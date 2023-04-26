package com.zk.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.intern.InternUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.dto.Result;
import com.zk.pojo.User;
import com.zk.pojo.Video;
import com.zk.mapper.VideoMapper;
import com.zk.service.UserService;
import com.zk.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Resource
    VideoMapper videoMapper;
    @Resource
    UserService userService;

    @Override
    public Result feed(String userId, String latestTime) {
        if (latestTime == null) {
            latestTime = String.valueOf(DateUtil.currentSeconds());
        }
        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        wrapper.apply("UNIX_TIMESTAMP(create_time) <= "+ latestTime )
                .orderByDesc("create_time");
        Page<Video> page = new Page<>(1,30);
        Page<Video> videoPage = videoMapper.selectPage(page, wrapper);
        List<Video> records = videoPage.getRecords();

        // 设置latestTime
        Long newLatestTime = null;
        for (Video video : records) {
            User user = userService.getById(video.getUserId());
            // TODO 数据从redis中取 此处使用的虚假数据  将视频一id 时间戳存入Zset
            //粉丝数
            user.setFollowerCount(1);
            //喜欢数
            user.setFavoriteCount(1);
            //关注数
            user.setFollowCount(1);
            //作品数
            user.setWorkCount(1);
            video.setUser(user);
            // 视频评论数
            video.setCommentCount(100);
            // 视频点赞数
            video.setFavoriteCount(100);
            // 是否点赞
            video.setIsFavorite(true);
            // 设置latestTime
            newLatestTime = video.getCreateTime().toInstant().getEpochSecond();
        }
        return Result.ok("video_list", records, newLatestTime.intValue());
    }
}

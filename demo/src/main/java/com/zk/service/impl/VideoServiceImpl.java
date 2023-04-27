package com.zk.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.intern.InternUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.dto.Result;
import com.zk.pojo.Favorite;
import com.zk.pojo.User;
import com.zk.pojo.Video;
import com.zk.mapper.VideoMapper;
import com.zk.service.FavoriteService;
import com.zk.service.UserService;
import com.zk.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
    FavoriteService favoriteService;

    @Override
    public Result feed(String curUserId, String latestTime) {
        if (latestTime == null) {
            latestTime = String.valueOf(DateUtil.currentSeconds());
        }
        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        wrapper.apply("UNIX_TIMESTAMP(create_time) < "+ latestTime )
                .orderByDesc("create_time");
        Page<Video> page = new Page<>(1,1);
        Page<Video> videoPage = videoMapper.selectPage(page, wrapper);
        List<Video> records = videoPage.getRecords();

        // 设置latestTime
        Integer newLatestTime = null;
        for (Video video : records) {
            setVideoPramByVid(video, video.getUserId(), Integer.parseInt(curUserId));
            // 设置latestTime
            newLatestTime = Integer.parseInt(String.valueOf(video.getCreateTime().toInstant().getEpochSecond()));
        }

        return Result.ok("video_list", records, newLatestTime);
    }

    @Override
    public Result listByUserId(Integer userId, Integer curUserId) {
        List<Video> videoList = query().eq("user_id", userId).list();
        videoList.stream().forEach(video -> {
            setVideoPramByVid(video, userId, curUserId);
        });
        return Result.ok("video_list",videoList);
    }

    @Override
    public Result favoriteList(Integer curUserId, int userId) {
        List<Favorite> favoriteList = favoriteService.query()
                .eq("user_id", userId).list();
        List<Video> videoList = new ArrayList<>(favoriteList.size());
        for (Favorite favorite : favoriteList) {
            videoList.add(getById(favorite.getVideoId()));
        }
        for (Video video : videoList) {
            setVideoPramByVid(video, userId, curUserId);
        }
        return Result.ok("video_list",videoList);
    }


    public void setVideoPramByVid(Video video, Integer userId, Integer curUserId) {
        User user = userService.getById(video.getUserId());
        videoWithUser(user, userId, curUserId);
        // TODO 数据从redis中取 此处使用的虚假数据  将视频一id 时间戳存入Zset
        video.setUser(user);
        // 视频评论数
        video.setCommentCount(100);
        // 视频点赞数
        video.setFavoriteCount(100);
        // 是否点赞
        video.setIsFavorite(true);
    }

    public void videoWithUser(User user, Integer userId,  Integer curUserId) {
        //粉丝数
        user.setFollowerCount(1);
        //喜欢数
        user.setFavoriteCount(1);
        //关注数
        user.setFollowCount(1);
        //作品数
        user.setWorkCount(1);
        // 是否关注
        user.setFollow(true);
    }
}

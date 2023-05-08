package com.zk.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.intern.InternUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.dto.Result;
import com.zk.pojo.Favorite;
import com.zk.pojo.User;
import com.zk.pojo.Video;
import com.zk.mapper.VideoMapper;
import com.zk.service.CommentService;
import com.zk.service.FavoriteService;
import com.zk.service.UserService;
import com.zk.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zk.utils.RedisConstants.*;

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
    @Lazy
    UserService userService;
    @Resource
    FavoriteService favoriteService;
    @Resource
    CommentService commentService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 热点数据部署
     */
    public void loadVideo() {
        List<Video> videoList = list();
        for (Video video : videoList) {
            stringRedisTemplate.opsForZSet()
                    .add(VIDEO_FEED_KEY,
                            video.getId().toString(),
                            video.getCreateTime().toInstant().getEpochSecond());
        }
    }

    @Override
    public Result feed(String curUserId, String latestTime) {
        int offset = 1;
        if (latestTime == null) {
            latestTime = String.valueOf(DateUtil.currentSeconds());
            offset = 0;
        }

        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(VIDEO_FEED_KEY,
                        0,Long.valueOf(latestTime),
                        offset, 30);
        // 判空
        if (typedTuples.isEmpty() || typedTuples == null) {
            return Result.fail();
        }

        // 设置latestTime
        Long newLatestTime = null;
        List<Integer> ids = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            ids.add(Integer.valueOf(typedTuple.getValue()));
            newLatestTime =typedTuple.getScore().longValue();
        }

        // 根据id查询video
        String idsStr = StrUtil.join(",", ids);
        List<Video> videos = query().in("id", ids)
                .last("order by field(id," + idsStr + ")").list();

        for (Video video : videos) {
            setVideoPramByVid(video, video.getUserId(), Integer.parseInt(curUserId));
        }

        return Result.ok("video_list", videos, Integer.valueOf(newLatestTime.toString()));
    }


//    @Override
//    public Result feed(String curUserId, String latestTime) {
//        if (latestTime == null) {
//            latestTime = String.valueOf(DateUtil.currentSeconds());
//        }
//        QueryWrapper<Video> wrapper = new QueryWrapper<>();
//        wrapper.apply("UNIX_TIMESTAMP(create_time) < "+ latestTime )
//                .orderByDesc("create_time");
//        Page<Video> page = new Page<>(1,30);
//        Page<Video> videoPage = videoMapper.selectPage(page, wrapper);
//        List<Video> records = videoPage.getRecords();
//
//        // 设置latestTime
//        Integer newLatestTime = null;
//        for (Video video : records) {
//            setVideoPramByVid(video, video.getUserId(), Integer.parseInt(curUserId));
//            // 设置latestTime
//            newLatestTime = Integer.parseInt(String.valueOf(video.getCreateTime().toInstant().getEpochSecond()));
//        }
//
//        return Result.ok("video_list", records, newLatestTime);

//    }

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
        List<Integer> videoIds = favoriteService.query()
                .eq("user_id", userId)
                .eq("deleted", 0).list()
                .stream().map(Favorite::getVideoId)
                .collect(Collectors.toList());
        if (videoIds == null || videoIds.isEmpty()) {
            return Result.ok("video_list",null);
    }
        List<Video> videoList = query().in("id",videoIds).list();
        if (videoList == null || videoList.isEmpty()) {
            return Result.ok("video_list", null);
        }
        for (Video video : videoList) {
            setVideoPramByVid(video, userId, curUserId);
        }
        return Result.ok("video_list",videoList);
    }


    public void setVideoPramByVid(Video video, Integer userId, Integer curUserId) {
        User user = userService.getById(video.getUserId());
        userService.userInfo(user, userId, curUserId);
        video.setUser(user);
        // 视频评论数
        video.setCommentCount(
                stringRedisTemplate.opsForSet()
                        .size(VIDEO_COMMENT_KEY + video.getId()).intValue()
        );
        // 视频点赞数
        video.setFavoriteCount(
                stringRedisTemplate.opsForSet()
                        .size(VIDEO_FAVORITE_KEY + video.getId()).intValue()
        );
        // 是否点赞
        Boolean isFavorite = stringRedisTemplate.opsForSet()
                .isMember(VIDEO_FAVORITE_KEY + video.getId(),
                        curUserId.toString());
        video.setIsFavorite(BooleanUtil.isTrue(isFavorite));
    }

}

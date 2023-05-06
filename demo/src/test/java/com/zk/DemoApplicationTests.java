package com.zk;

import cn.hutool.core.date.DateTime;
import com.zk.pojo.*;
import com.zk.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zk.utils.RedisConstants.*;

@SpringBootTest
class DemoApplicationTests {

    @Resource
    UserService userService;

    @Test
    void contextLoads() {
        User user = new User();
        user.setUsername("zk");
        user.setPassword("123123");
        userService.save(user);
    }

    @Test
    void deleted() {
        boolean b = userService.removeById(1);
        System.out.println(b);
    }


    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    VideoService videoService;
    @Test
    public void loadVideo() {
        List<Video> videoList = videoService.list();
        for (Video video : videoList) {
            stringRedisTemplate.opsForZSet()
                    .add(VIDEO_FEED_KEY,
                            video.getId().toString(),
                            video.getCreateTime().toInstant().getEpochSecond());
        }
    }

    @Test
    public void test() {
//        Set<String> top10 = stringRedisTemplate
//                .opsForZSet()
//                .reverseRange(VIDEO_FEED_KEY, 0, 9);
//        top10.stream().map(Integer::valueOf).forEach(System.out::println);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(VIDEO_FEED_KEY,
                        0, 1683359118, 0, 3);
        typedTuples.stream().map(typedTuple -> {
            return typedTuple.getValue();
        }).forEach(System.out::println);
    }


    @Resource
    FollowService followService;

    /**
     * 关注列表  粉丝列表 热点数据部署
     */
    @Test
    public void loadFollow() {
        List<Follow> followList = followService
                .query().eq("deleted", 0).list();
        for (Follow follow : followList) {
            Integer userId = follow.getUserId();
            Integer followedUserId = follow.getFollowedUserId();
            String key1 = FOLLOWS_KEY + userId;
            String key2 = FOLLOWERS_KEY + followedUserId;
            stringRedisTemplate.opsForSet().add(key1, followedUserId.toString());
            stringRedisTemplate.opsForSet().add(key2, userId.toString());
        }
    }

    @Resource
    FavoriteService favoriteService;

    /**
     * 部署视频点赞热点数据
     */
    @Test
    public void loadFavorite() {
        List<Favorite> favoriteList =
                favoriteService.query().eq("deleted", 0).list();
        for (Favorite favorite : favoriteList) {
            Integer videoId = favorite.getVideoId();
            Integer userId = favorite.getUserId();
//            stringRedisTemplate.opsForSet().add(VIDEO_FAVORITE_KEY + videoId,
//                    userId.toString());
            stringRedisTemplate.opsForSet()
                    .add(USER_FAVORITE_KEY + userId,
                            videoId.toString());
        }
    }

    /**
     * 部署用户上传视频的数据
     */

    @Test
    public void loadUserVideo() {
        List<Video> videoList = videoService.list();
        for (Video video : videoList) {
            Integer userId = video.getUserId();
            Integer videoId = video.getId();
            stringRedisTemplate.opsForSet()
                    .add(USER_VIDEO_KEY + userId, videoId.toString());
        }
    }

    /**
     * 视频评论数据部署
     */
    @Resource
    CommentService commentService;
    @Test
    public void loadComment() {
        List<Comment> commentList = commentService.list();
        for (Comment comment : commentList) {
            Integer commentId = comment.getId();
            Integer videoId = comment.getVideoId();
            stringRedisTemplate.opsForSet()
                    .add(VIDEO_COMMENT_KEY + videoId,
                            commentId.toString());
        }
    }

}

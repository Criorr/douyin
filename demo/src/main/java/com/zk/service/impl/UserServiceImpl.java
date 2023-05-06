package com.zk.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.zk.pojo.Follow;
import com.zk.pojo.User;
import com.zk.mapper.UserMapper;
import com.zk.pojo.Video;
import com.zk.service.FavoriteService;
import com.zk.service.FollowService;
import com.zk.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.service.VideoService;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    FollowService followService;
    @Resource
    FavoriteService favoriteService;
    @Resource
    VideoService videoService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     *
     * @param user 需要完善的用户的对象
     * @param userId 查看用户的id
     * @param curUserId 当前用户的id
     */
    @Override
    public void userInfo(User user, Integer userId, Integer curUserId) {
//        int followerCount = 0;
//        int count = 0;
//        int followCount = 0;

//        for (Follow follow : followService.list()) {
//            Integer uid = follow.getUserId();
//            Integer followedUserId = follow.getFollowedUserId();
//            if (uid.equals(curUserId) && followedUserId.equals(user.getId())) {
//                count++;
//            }
//            if (uid.equals(userId)) {
//                followCount++;
//            }
//            if (followedUserId.equals(userId)) {
//                followerCount++;
//            }
//        }
        String key1 = FOLLOWERS_KEY + user.getId();
        String key2 = FOLLOWS_KEY + user.getId();

        int followerCount = stringRedisTemplate.opsForSet().size(key1).intValue();
        int followCount = stringRedisTemplate.opsForSet().size(key2).intValue();
        Boolean isFollow =
                stringRedisTemplate.opsForSet().isMember(FOLLOWS_KEY + curUserId, user.getId().toString());
        //粉丝数
        user.setFollowerCount(followerCount);
        //关注数
        user.setFollowCount(followCount);
        // 是否关注
        user.setFollow(BooleanUtil.isTrue(isFollow));
        //喜欢数
        Integer favoriteCount = stringRedisTemplate
                .opsForSet()
                .size(USER_FAVORITE_KEY + user.getId()).intValue();
//                favoriteService.query().eq("user_id", user.getId()).count();
        user.setFavoriteCount(favoriteCount);

        // 用户作品id列表
        Set<String> videoIds = stringRedisTemplate.opsForSet()
                .members(USER_VIDEO_KEY + user.getId());
//        List<Integer> videoIdWithUser = videoService.query()
//                .eq("user_id", user.getId()).list()
//                .stream().map(Video::getUserId).collect(Collectors.toList());
        //作品数
//        Integer workCount = videoIds.size();
        user.setWorkCount(videoIds.size());
        // 获赞数量
        Integer totalFavorited = 0;
        for (String videoId : videoIds) {
            totalFavorited +=
                    stringRedisTemplate.opsForSet()
                    .size(VIDEO_FAVORITE_KEY + videoId).intValue();
        }
        user.setTotalFavorited(totalFavorited);
    }
}

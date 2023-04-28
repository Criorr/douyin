package com.zk.service.impl;

import com.zk.pojo.Follow;
import com.zk.pojo.User;
import com.zk.mapper.UserMapper;
import com.zk.pojo.Video;
import com.zk.service.FavoriteService;
import com.zk.service.FollowService;
import com.zk.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.service.VideoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     *
     * @param user 需要完善的用户的对象
     * @param userId 查看用户的id
     * @param curUserId 当前用户的id
     */
    @Override
    public void userInfo(User user, Integer userId, Integer curUserId) {
        int followerCount = 0;
        int count = 0;
        int followCount = 0;

        for (Follow follow : followService.list()) {
            Integer uid = follow.getUserId();
            Integer followedUserId = follow.getFollowedUserId();
            if (uid.equals(curUserId) && followedUserId.equals(user.getId())) {
                count++;
            }
            if (uid.equals(userId)) {
                followCount++;
            }
            if (followedUserId.equals(userId)) {
                followerCount++;
            }
        }

        //粉丝数
        user.setFollowerCount(followerCount);
        //关注数
        user.setFollowCount(followCount);
        // 是否关注
        user.setFollow(count > 0 ? true : false);
        //喜欢数
        Integer favoriteCount = favoriteService.query().eq("user_id", userId).count();
        user.setFavoriteCount(favoriteCount);

        List<Integer> videoIdWithUser = videoService.query()
                .eq("user_id", userId).list()
                .stream().map(Video::getUserId).collect(Collectors.toList());
        //作品数
        Integer workCount = videoIdWithUser.size();
        user.setWorkCount(workCount);
        // 获赞数量
        Integer totalFavorited = favoriteService.query().in("video_id", videoIdWithUser).count();
        user.setTotalFavorited(totalFavorited);
    }
}

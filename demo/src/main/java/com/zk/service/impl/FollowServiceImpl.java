package com.zk.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zk.dto.Result;
import com.zk.pojo.Follow;
import com.zk.mapper.FollowMapper;
import com.zk.pojo.User;
import com.zk.pojo.Video;
import com.zk.service.FollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zk.utils.RedisConstants.FOLLOWERS_KEY;
import static com.zk.utils.RedisConstants.FOLLOWS_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    @Resource
    @Lazy
    UserService userService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public Result relationAction(Integer userId, Integer toUserId, String actionType) {
        if (userId.equals(toUserId)) {
            return Result.fail("无法关注自己");
        }
        String key1 = FOLLOWS_KEY + userId;
        String key2 = FOLLOWERS_KEY + toUserId;
        if ("1".equals(actionType)) {
            // 关注
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowedUserId(toUserId);
            follow.setDeleted(0);
            QueryWrapper<Follow> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId).eq("followed_user_id",toUserId);
            boolean isSuccess = saveOrUpdate(follow, wrapper);
            if (isSuccess) {
                stringRedisTemplate.opsForSet().add(key1, toUserId.toString());
                stringRedisTemplate.opsForSet().add(key2, userId.toString());
            }
        } else if ("2".equals(actionType)) {
            // 取消关注
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowedUserId(toUserId);
            follow.setDeleted(1);
            QueryWrapper<Follow> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId).eq("followed_user_id",toUserId);
            boolean isSuccess = update(follow, wrapper);
            if (isSuccess) {
                stringRedisTemplate.opsForSet().remove(key1, toUserId.toString());
                stringRedisTemplate.opsForSet().remove(key2, userId.toString());
            }
        } else {
            return Result.fail();
        }
        return Result.ok();
    }

    /**
     *  获取查看用户的关注列表
     * @param curUserId 当前用户id
     * @param userId  查看用户id
     * @return
     */
    @Override
    public Result followList(Integer curUserId, Integer userId) {
        String key = FOLLOWS_KEY + userId;
        // 用户关注列表id
        Set<String> ids = stringRedisTemplate.opsForSet().members(key);
//        List<Follow> followList = query()
//                .eq("user_id", userId)
//                .eq("deleted", 0).list();
        List<User> userList = userService.query().in("id", ids).list();
        for (User user : userList) {
            // 填充该用户的信息
            userService.userInfo(user, userId, curUserId);
        }
//        // 遍历关注列表
//        for (Follow follow : followList) {
//            // 关注的用户
//            User user = userService.getById(follow.getFollowedUserId());
//            // 填充该用户的信息
//            userService.userInfo(user, userId, curUserId);
//            userList.add(user);
//        }
        return Result.ok("user_list", userList);
    }

    /**
     * 获取关注查看用户的列表
     * @param curUserId 当前用户id
     * @param userId  查看用户id
     * @return
     */
    @Override
    public Result followerList(Integer curUserId, Integer userId) {
        List<Follow> followerList = query()
                .eq("followed_user_id", userId)
                .eq("deleted", 0).list();
        List<User> userList = new ArrayList<>();
        for (Follow follow : followerList) {
            User user = userService.getById(follow.getUserId());
            userService.userInfo(user, userId, curUserId);
            userList.add(user);
        }
        return Result.ok("user_list", userList);
    }

    @Override
    public Result friendList(Integer curUserId, Integer userId) {
        // userId的关注id列表
        List<Integer> followList = query()
                .eq("user_id", userId).list()
                .stream().map(Follow::getFollowedUserId)
                .collect(Collectors.toList());
        // userId的粉丝id列表
        List<Integer> followerList = query()
                .eq("followed_user_id", userId).list()
                .stream().map(Follow::getUserId)
                .collect(Collectors.toList());;
        // 相互关注的用户id列表
        List<Integer> friendList = followerList.stream()
                .filter(followList::contains)
                .collect(Collectors.toList());
        List<User> userList = new ArrayList<>();
        for (Integer uid : friendList) {
            User user = userService.getById(uid);
            userService.userInfo(user, uid, curUserId);
            userList.add(user);
        }
        return Result.ok("user_list", userList);
    }
}

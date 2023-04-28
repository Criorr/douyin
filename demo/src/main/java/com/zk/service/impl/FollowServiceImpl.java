package com.zk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zk.dto.Result;
import com.zk.pojo.Follow;
import com.zk.mapper.FollowMapper;
import com.zk.pojo.User;
import com.zk.service.FollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.service.UserService;
import org.springframework.context.annotation.Lazy;
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
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    @Resource
    @Lazy
    UserService userService;
    @Override
    public Result relationAction(Integer userId, Integer toUserId, String actionType) {
        if (userId.equals(toUserId)) {
            return Result.fail("无法关注自己");
        }
        if ("1".equals(actionType)) {
            // 关注
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowedUserId(toUserId);
            follow.setDeleted(0);
            QueryWrapper<Follow> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId).eq("followed_user_id",toUserId);
            saveOrUpdate(follow,wrapper);
        } else if ("2".equals(actionType)) {
            // 取消关注
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowedUserId(toUserId);
            follow.setDeleted(1);
            QueryWrapper<Follow> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId).eq("followed_user_id",toUserId);
            update(follow, wrapper);
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
        List<Follow> followList = query()
                .eq("user_id", userId)
                .eq("deleted", 0).list();
        List<User> userList = new ArrayList<>();
        // 遍历关注列表
        for (Follow follow : followList) {
            // 关注的用户
            User user = userService.getById(follow.getFollowedUserId());
            // 填充该用户的信息
            userService.userInfo(user, userId, curUserId);
            userList.add(user);
        }
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

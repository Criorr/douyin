package com.zk.controller;


import com.zk.dto.Result;
import com.zk.service.FollowService;
import com.zk.utils.JWTUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@RestController
@RequestMapping("/douyin/relation")
public class FollowController {
    @Resource
    FollowService followService;
    @PostMapping("/action")
    public Result relationAction(@RequestParam("token") String token,
                                 @RequestParam("to_user_id") String toUserId,
                                 @RequestParam("action_type") String actionType) {
        Integer userId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return followService.relationAction(userId, Integer.parseInt(toUserId), actionType);
    }

    @GetMapping("/follow/list")
    public Result followList(@RequestParam("token") String token,
                             @RequestParam("user_id") String userId) {
        Integer curUserId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return followService.followList(curUserId, Integer.parseInt(userId));
    }

    @GetMapping("/follower/list")
    public Result followerList(@RequestParam("token") String token,
                             @RequestParam("user_id") String userId) {
        Integer curUserId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return followService.followerList(curUserId, Integer.parseInt(userId));
    }

    @GetMapping("/friend/list")
    public Result friendList(@RequestParam("token") String token,
                             @RequestParam("user_id") String userId) {
        Integer curUserId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return followService.friendList(curUserId, Integer.parseInt(userId));
    }

}


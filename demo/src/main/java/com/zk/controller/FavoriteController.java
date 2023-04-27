package com.zk.controller;


import com.zk.dto.Result;
import com.zk.service.FavoriteService;
import com.zk.service.VideoService;
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
@RequestMapping("/douyin/favorite")
public class FavoriteController {

    @Resource
    FavoriteService favoriteService;

    @Resource
    VideoService videoService;
    @PostMapping("/action")
    public Result action(@RequestParam("token") String token,
                         @RequestParam("video_id") String videoId,
                         @RequestParam("action_type") String actionType) {
        Integer userId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return favoriteService.action(userId, Integer.parseInt(videoId), actionType);
    }

    @GetMapping("/list")
    public Result list(@RequestParam("token") String token,
                       @RequestParam("user_id") String userId) {
        Integer curUserId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return videoService.favoriteList(curUserId, Integer.parseInt(userId));
    }
}


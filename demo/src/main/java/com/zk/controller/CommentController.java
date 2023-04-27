package com.zk.controller;


import com.zk.dto.Result;
import com.zk.service.CommentService;
import com.zk.utils.InterceptJWT;
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
@RequestMapping("/douyin/comment")
public class CommentController {
    @Resource
    CommentService commentService;

    @GetMapping("/list")
    public Result commentList(@RequestParam("video_id") String videoId,
                              @RequestParam("token") String token) {
        Integer userId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return commentService.commentList(userId, Integer.parseInt(videoId));
    }

    @PostMapping("/action")
    public Result commentAction(@RequestParam("token") String token,
                                @RequestParam("video_id") String videoId,
                                @RequestParam("action_type") String actionType,
                                @RequestParam(value = "comment_text", required = false) String commentText,
                                @RequestParam(value = "comment_id", required = false) String commentId
                                ) {
        Integer userId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return commentService.commentAction(userId, videoId, actionType, commentText, commentId);
    }
}


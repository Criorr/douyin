package com.zk.controller;


import com.zk.dto.Result;
import com.zk.service.MessageService;
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
@RequestMapping("/douyin/message")
public class MessageController {
    @Resource
    MessageService messageService;
    @PostMapping("/action")
    public Result messageAction(@RequestParam("token") String token,
                                @RequestParam("to_user_id") String toUserId,
                                @RequestParam("action_type") String actionType,
                                @RequestParam("content") String content) {
        Integer curUserId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return messageService.messageAction(curUserId, Integer.parseInt(toUserId), actionType, content);
    }

    @GetMapping("/chat")
    public Result chat(@RequestParam("token") String token,
                       @RequestParam("to_user_id") String toUserId,
                       @RequestParam("pre_msg_time") Integer preMsgTime) {
        Integer curUserId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return messageService.chat(curUserId, Integer.parseInt(toUserId), preMsgTime);
    }

}


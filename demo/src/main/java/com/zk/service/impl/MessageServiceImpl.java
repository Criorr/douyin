package com.zk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zk.dto.Result;
import com.zk.pojo.Message;
import com.zk.mapper.MessageMapper;
import com.zk.pojo.Video;
import com.zk.service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public Result messageAction(Integer curUserId, Integer toUserId, String actionType, String content) {
        if ("1".equals(actionType)) {
            Message message = new Message();
            message.setFromUserId(curUserId);
            message.setToUserId(toUserId);
            message.setContent(content);
            save(message);
        } else {
            return Result.fail();
        }
        return Result.ok();
    }

    @Override
    public Result chat(Integer curUserId, Integer toUserId, Integer preMsgTime) {
        List<Message> messageList = query().eq("from_user_id", toUserId)
                .eq("to_user_id", curUserId)
                .apply("UNIX_TIMESTAMP(create_time) > "+ preMsgTime)
                .orderByAsc("create_time")
                .list();
        messageList.stream().forEach(message -> {
            message.setCreate_time(message.getCreateTime().toInstant().getEpochSecond());
        });
        return Result.ok("message_list", messageList);
    }
}

package com.zk.service;

import com.zk.dto.Result;
import com.zk.pojo.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
public interface MessageService extends IService<Message> {

    Result messageAction(Integer curUserId, Integer toUserId, String actionType, String content);


    Result chat(Integer curUserId, Integer toUserId, Integer preMsgTime);

}

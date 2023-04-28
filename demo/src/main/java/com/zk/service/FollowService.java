package com.zk.service;

import com.zk.dto.Result;
import com.zk.pojo.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
public interface FollowService extends IService<Follow> {

    Result relationAction(Integer userId, Integer toUserId, String actionType);

    Result followList(Integer curUserId, Integer userId);

    Result followerList(Integer curUserId, Integer userId);

    Result friendList(Integer curUserId, Integer userId);
}

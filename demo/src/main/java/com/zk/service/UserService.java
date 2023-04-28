package com.zk.service;

import com.zk.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
public interface UserService extends IService<User> {

    void userInfo(User user, Integer userId, Integer curUserId);
}

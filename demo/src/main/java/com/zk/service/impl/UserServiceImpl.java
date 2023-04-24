package com.zk.service.impl;

import com.zk.pojo.User;
import com.zk.mapper.UserMapper;
import com.zk.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

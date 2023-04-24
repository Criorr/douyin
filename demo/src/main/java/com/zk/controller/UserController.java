package com.zk.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zk.dto.UserDTO;
import com.zk.pojo.User;
import com.zk.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@RestController
@RequestMapping("/douyin/user")
public class UserController {
    @Resource
    UserService userService;

    @PostMapping("/register")
    public UserDTO register(User user) {
        if (user == null) {
            return new UserDTO(10, "注册失败，数据为空！", null, 0);
        }
        int count = userService.count(
                new QueryWrapper<User>().
                        eq("username", user.getUsername())
        );
        if (count > 0) {
            return new UserDTO(10, "注册失败，用户名相同", null, 0);
        }
        userService.save(user);
        String token = UUID.randomUUID().toString();
        return new UserDTO(0, "注册成功",token, user.getId());
    }

    @PostMapping("/login")
    public UserDTO login(User user) {
        if (user == null) {
            return new UserDTO(10, "登录失败，数据为空！", null, 0);
        }
        User queryUser = userService.getOne(
                new QueryWrapper<User>().
                        eq("username", user.getUsername())
                        .eq("password", user.getPassword())
        );
        if (queryUser == null) {
            return new UserDTO(10, "用户名密码错误！", null, 0);
        }
        String token = UUID.randomUUID().toString();
        return new UserDTO(0, "登录成功",token, queryUser.getId());
    }

}


package com.zk.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zk.dto.Result;
import com.zk.dto.UserDTO;
import com.zk.pojo.User;
import com.zk.service.UserService;
import com.zk.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@RestController
@Slf4j
@RequestMapping("/douyin/user")
public class UserController {
    @Resource
    UserService userService;

    @PostMapping("/register")
    public UserDTO register(User user) {
        if (user == null) {
            return new UserDTO(1, "注册失败，数据为空！", null, 0);
        }
        int count = userService.count(
                new QueryWrapper<User>().
                        eq("username", user.getUsername())
        );
        if (count > 0) {
            return new UserDTO(1, "注册失败，用户名相同", null, 0);
        }
        userService.save(user);
        String token = JWTUtils.getJwtToken(user.getId().toString());
        return new UserDTO(0, "注册成功",token, user.getId());
    }

    @PostMapping("/login")
    public UserDTO login(User user, HttpServletRequest req) {
        if (user == null) {
            return new UserDTO(1, "登录失败，数据为空！", null, 0);
        }
        User queryUser = userService.getOne(
                new QueryWrapper<User>().
                        eq("username", user.getUsername())
                        .eq("password", user.getPassword())
        );
        if (queryUser == null) {
            return new UserDTO(1, "用户名密码错误！", null, 0);
        }
        log.info("userId:" + queryUser.getId().toString());
        req.getSession().setAttribute("userId", queryUser.getId().toString());
        String token = JWTUtils.getJwtToken(queryUser.getId().toString());
        return new UserDTO(0, "登录成功",token, queryUser.getId());
    }

    @GetMapping
    public Result userInfo(@RequestParam("user_id") Integer userId) {
        System.out.println(userId);
        User user = userService.getById(userId);
        // TODO 数据从redis中获取
        //粉丝数
        user.setFollowerCount(1);
        //喜欢数
        user.setFavoriteCount(1);
        //关注数
        user.setFollowCount(1);
        //作品数
        user.setWorkCount(1);
        // 是否关注
        user.setFollow(true);
        return Result.ok("user", user);
    }

}


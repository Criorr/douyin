package com.zk;

import com.zk.pojo.User;
import com.zk.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DemoApplicationTests {

    @Resource
    UserService userService;

    @Test
    void contextLoads() {
        User user = new User();
        user.setUsername("zk");
        user.setPassword("123123");
        userService.save(user);
    }

    @Test
    void deleted() {
        boolean b = userService.removeById(1);
        System.out.println(b);
    }

}

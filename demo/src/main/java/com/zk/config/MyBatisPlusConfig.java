package com.zk.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatisPlusConfig
 *
 * @author ZhengKai
 * @date 2023/4/23
 */


// 扫描我们的 mapper 文件夹
@MapperScan("com.zk.mapper")
@EnableTransactionManagement
@Configuration // 配置类
public class MyBatisPlusConfig {

}

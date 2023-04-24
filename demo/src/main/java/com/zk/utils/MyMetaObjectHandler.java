package com.zk.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * MyMetaObjectHandler
 *
 * @author ZhengKai
 * @date 2023/4/23
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill.....");
        // setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject
        this.setFieldValByName("createTime",new Timestamp(System.currentTimeMillis()),metaObject);
        this.setFieldValByName("updateTime",new Timestamp(System.currentTimeMillis()),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill.....");
        this.setFieldValByName("updateTime",new Timestamp(System.currentTimeMillis()),metaObject);
    }
}

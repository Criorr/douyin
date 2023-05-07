package com.zk.product;

import com.zk.enums.FavoriteActionEnum;
import com.zk.callback.MyCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.time.LocalDateTime;

import static com.zk.config.RabbitmqConfig.*;

/**
 * Product
 *
 * @author ZhengKai
 * @date 2023/5/7
 */
@Component
@Slf4j
public class Product {
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    MyCallBack myCallBack;
    @PostConstruct
    public void init() {
        //消息发送失败返回到队列中, yml需要配置 publisher-returns: true
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(myCallBack);
        rabbitTemplate.setReturnCallback(myCallBack);
    }
    public void send(String msg, FavoriteActionEnum type) {
        log.info("当前时间{},生产者产生消息----", LocalDateTime.now());
        switch (type) {
            case LIKE:
                rabbitTemplate.convertAndSend(
                        FAVORITE_EXCHANGE_NAME,
                        LIKE_ROUTING_KEY, msg);
                break;
            case NOLIKE:
                rabbitTemplate.convertAndSend(
                        FAVORITE_EXCHANGE_NAME,
                        NO_LIKE_ROUTING_KEY, msg);
                break;
            default:
        }
    }
}

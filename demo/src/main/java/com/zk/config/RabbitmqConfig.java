package com.zk.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitmqConfig
 *
 * @author ZhengKai
 * @date 2023/5/7
 */
@Configuration
public class RabbitmqConfig {
    public static final String FAVORITE_EXCHANGE_NAME = "favorite.exchange";
    public static final String LIKE_QUEUE_NAME = "like.queue";
    public static final String NO_LIKE_QUEUE_NAME = "no.like.queue";
    public static final String NO_LIKE_ROUTING_KEY = "noLike.routingkey";
    public static final String LIKE_ROUTING_KEY = "like.routingkey";

    //声明交换机
    @Bean("favoriteExchange")
    public DirectExchange favoriteExchange() {
        return new DirectExchange(FAVORITE_EXCHANGE_NAME,true, false);
    }

    //声明点赞队列
    @Bean("likeQueue")
    public Queue likeQueue() {
        return new Queue(LIKE_QUEUE_NAME,true,false,false);
    }

    //声明取消点赞队列
    @Bean("noLikeQueue")
    public Queue noLikeQueue() {
        return new Queue(NO_LIKE_QUEUE_NAME,true,false,false);
    }

    //声明点赞队列绑定交换机
    @Bean
    public Binding bindingLikeQueueExchange(@Qualifier("likeQueue") Queue likeQueue,
                                            @Qualifier("favoriteExchange") DirectExchange favoriteExchange) {
        return BindingBuilder.bind(likeQueue).to(favoriteExchange).with(LIKE_ROUTING_KEY);
    }

    //声明取消点赞队列绑定交换机
    @Bean
    public Binding bindingNoLikeQueueExchange(@Qualifier("noLikeQueue") Queue noLikeQueue,
                                            @Qualifier("favoriteExchange") DirectExchange favoriteExchange) {
        return BindingBuilder.bind(noLikeQueue).to(favoriteExchange).with(NO_LIKE_ROUTING_KEY);
    }



}

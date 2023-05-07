package com.zk.consumer;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import com.zk.pojo.Favorite;
import com.zk.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.zk.config.RabbitmqConfig.LIKE_QUEUE_NAME;
import static com.zk.utils.RedisConstants.USER_FAVORITE_KEY;
import static com.zk.utils.RedisConstants.VIDEO_FAVORITE_KEY;

/**
 * LikeConsumer
 *
 * @author ZhengKai
 * @date 2023/5/7
 */

/**
 * 在 RabbitMQ 中，每个消费者只会消费一条消息，
 * 因此在同一时刻只有一个消费者在处理消息。
 * 这样可以避免并发写入操作导致的数据不一致问题。
 * 此外，RabbitMQ 的消息是有序投递的，也就是说，
 * 同一个队列中的消息按照先进先出的顺序依次投递给消费者。
 * 这样保证了消息处理的顺序性。
 * 因此，在使用 RabbitMQ 的情况下，
 * 可以不用加锁和事务来解决并发安全问题。
 * 但是需要注意的是，如果多个消费者同时处理同一个消息，
 * 还是需要考虑并发安全的问题。可以通过在消息中添加一些唯一标识，
 * 避免重复处理同一个消息。此外，在消费者端可以使用分布式锁等机制来避免并发问题。
 */
@Component
@Slf4j
public class LikeConsumer {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FavoriteService favoriteService;
    @RabbitListener(queues = LIKE_QUEUE_NAME)
    public void receiveMsg(Message message, Channel channel) {
        try {
            /**
             * 确认一条消息：<br>
             * channel.basicAck(deliveryTag, false); <br>
             * deliveryTag:该消息的index <br>
             * multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息 <br>
             */
            String msg = new String(message.getBody());
            Favorite favorite = JSONUtil.toBean(msg, Favorite.class);
            favorite.setDeleted(0);
            Integer userId = favorite.getUserId();
            Integer videoId = favorite.getVideoId();
            QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId).eq("video_id", videoId);
            boolean isSuccess = favoriteService.saveOrUpdate(favorite,wrapper);
            if (isSuccess) {
                stringRedisTemplate.opsForSet()
                        .add(VIDEO_FAVORITE_KEY + videoId,
                                userId.toString());
                stringRedisTemplate.opsForSet()
                        .add(USER_FAVORITE_KEY + userId,
                                videoId.toString());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("当前时间{},消息应答成功，点赞 u:{} --> v:{}", LocalDateTime.now(),userId,videoId);

        } catch (Exception e) {
            //消费者处理出了问题，需要告诉队列信息消费失败
            /**
             * 拒绝确认消息:<br>
             * channel.basicNack(long deliveryTag, boolean multiple, boolean requeue) ; <br>
             * deliveryTag:该消息的index<br>
             * multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。<br>
             * requeue：被拒绝的是否重新入队列 <br>
             */
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                        false, true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            log.error("当前时间{},消息消费失败", LocalDateTime.now());

            /**
             * 拒绝一条消息：<br>
             * channel.basicReject(long deliveryTag, boolean requeue);<br>
             * deliveryTag:该消息的index<br>
             * requeue：被拒绝的是否重新入队列
             */
            //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }
}

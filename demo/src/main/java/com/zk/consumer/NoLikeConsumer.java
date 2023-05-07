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

import static com.zk.config.RabbitmqConfig.NO_LIKE_QUEUE_NAME;
import static com.zk.utils.RedisConstants.USER_FAVORITE_KEY;
import static com.zk.utils.RedisConstants.VIDEO_FAVORITE_KEY;

/**
 * NoLikeConsumer
 *
 * @author ZhengKai
 * @date 2023/5/7
 */
@Component
@Slf4j
public class NoLikeConsumer {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FavoriteService favoriteService;
    @RabbitListener(queues = NO_LIKE_QUEUE_NAME)
    public void receiveMsg(Message message, Channel channel) {
        try {
            String msg = new String(message.getBody());
            Favorite favorite = JSONUtil.toBean(msg, Favorite.class);
            favorite.setDeleted(1);
            Integer userId = favorite.getUserId();
            Integer videoId = favorite.getVideoId();
            QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId).eq("video_id", videoId);
            boolean isSuccess = favoriteService.update(favorite,wrapper);
            if (isSuccess) {
                stringRedisTemplate.opsForSet()
                        .remove(VIDEO_FAVORITE_KEY + videoId,
                                userId.toString());
                stringRedisTemplate.opsForSet()
                        .remove(USER_FAVORITE_KEY + userId,
                                videoId.toString());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("当前时间{},消息应答成功，取消点赞 u:{} --> v:{}", LocalDateTime.now(),userId,videoId);

        } catch (Exception e) {
            //消费者处理出了问题，需要告诉队列信息消费失败
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                        false, true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            log.error("当前时间{},消息消费失败", LocalDateTime.now());
        }


    }

}

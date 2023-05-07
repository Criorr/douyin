package com.zk.service.impl;

import cn.hutool.json.JSONUtil;
import com.zk.enums.FavoriteActionEnum;
import com.zk.dto.Result;
import com.zk.pojo.Favorite;
import com.zk.mapper.FavoriteMapper;
import com.zk.product.Product;
import com.zk.service.FavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    Product product;
    @Override
    public Result action(Integer userId, Integer videoId, String actionType) {
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setVideoId(videoId);
        //发送消息交由消息队列处理
        product.send(JSONUtil.toJsonStr(favorite),
                FavoriteActionEnum.getFavoriteActionEnum(actionType));
        return Result.ok();
    }


}

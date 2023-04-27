package com.zk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zk.dto.Result;
import com.zk.pojo.Favorite;
import com.zk.mapper.FavoriteMapper;
import com.zk.service.FavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

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

    @Override
    public Result action(Integer userId, Integer videoId, String actionType) {
        if ("1".equals(actionType)) {
            //点赞
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setVideoId(videoId);
            favorite.setDeleted(0);
            QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId).eq("video_id", videoId);
            boolean isSuccess = saveOrUpdate(favorite,wrapper);
            //TODO 如果数据库操作成功 在Redis中存一份
        } else if ("2".equals(actionType)) {
            //取消点赞
            QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setVideoId(videoId);
            favorite.setDeleted(1);
            wrapper.eq("user_id",userId).eq("video_id", videoId);
            boolean isSuccess = update(favorite, wrapper);
        } else {
            return Result.fail();
        }
        return Result.ok();
    }


}

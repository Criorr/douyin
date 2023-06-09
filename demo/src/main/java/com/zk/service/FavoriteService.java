package com.zk.service;

import com.zk.dto.Result;
import com.zk.pojo.Favorite;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
public interface FavoriteService extends IService<Favorite> {

    Result action(Integer userId, Integer i, String actionType);

}

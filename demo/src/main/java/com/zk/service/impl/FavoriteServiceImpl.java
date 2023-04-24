package com.zk.service.impl;

import com.zk.pojo.Favorite;
import com.zk.mapper.FavoriteMapper;
import com.zk.service.FavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}

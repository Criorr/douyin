package com.zk.service;

import com.zk.dto.Result;
import com.zk.pojo.Video;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
public interface VideoService extends IService<Video> {

    Result feed(String userId, String latestTime);
}

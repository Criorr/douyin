package com.zk.service;

import com.zk.dto.Result;
import com.zk.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zk.utils.InterceptJWT;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
public interface CommentService extends IService<Comment> {

    Result commentList(Integer userId, Integer videoId);

    Result commentAction(Integer userId, String videoId, String actionType, String commentText, String commentId);
}

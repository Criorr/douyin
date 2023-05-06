package com.zk.service.impl;

import com.zk.dto.Result;
import com.zk.pojo.Comment;
import com.zk.mapper.CommentMapper;
import com.zk.pojo.User;
import com.zk.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.zk.utils.RedisConstants.VIDEO_COMMENT_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    @Lazy
    UserService userService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Result commentList(Integer curUserId, Integer videoId) {
        List<Comment> commentList = query().eq("video_id", videoId).list();
        for (Comment comment : commentList) {
            User user = userService.getById(comment.getUserId());
            userService.userInfo(user, comment.getUserId(), curUserId);
            comment.setUser(user);
        }
        return Result.ok("comment_list", commentList);
    }

    @Override
    public Result commentAction(Integer userId, String videoId, String actionType, String commentText, String commentId) {
        if ("1".equals(actionType)) {
            // 发送评论
            Comment comment = new Comment();
            comment.setUserId(userId);
            comment.setComment(commentText);
            comment.setVideoId(Integer.parseInt(videoId));
            boolean isSuccess = save(comment);
            if (isSuccess) {
                stringRedisTemplate.opsForSet()
                        .add(VIDEO_COMMENT_KEY + videoId,
                                comment.getId().toString());
            }
            User user = userService.getById(comment.getUserId());
            userService.userInfo(user, comment.getUserId(), userId);
            comment.setUser(user);
            return Result.ok("comment", comment);
        } else if ("2".equals(actionType)) {
            // 删除评论
            boolean isSuccess = removeById(commentId);
            if (isSuccess) {
                stringRedisTemplate.opsForSet()
                        .remove(VIDEO_COMMENT_KEY + videoId,
                                commentId.toString());
            }
            return Result.ok("comment", null);
        } else {
            return Result.fail();
        }
    }

}

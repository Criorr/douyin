package com.zk.service.impl;

import com.zk.dto.Result;
import com.zk.pojo.Comment;
import com.zk.mapper.CommentMapper;
import com.zk.pojo.User;
import com.zk.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    UserService userService;
    @Override
    public Result commentList(Integer curUserId, Integer videoId) {
        List<Comment> commentList = query().eq("video_id", videoId).list();
        for (Comment comment : commentList) {
            User user = userService.getById(comment.getUserId());
            commentWithUser(user, comment.getUserId(), curUserId);
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
            save(comment);
            User user = userService.getById(comment.getUserId());
            commentWithUser(user, comment.getUserId(), userId);
            comment.setUser(user);
            return Result.ok("comment", comment);
        } else if ("2".equals(actionType)) {
            // 删除评论
            removeById(commentId);
            return Result.ok("comment", null);
        } else {
            return Result.fail();
        }
    }

    public void commentWithUser(User user, Integer userId, Integer curUserId) {
        //粉丝数
        user.setFollowerCount(1);
        //喜欢数
        user.setFavoriteCount(1);
        //关注数
        user.setFollowCount(1);
        //作品数
        user.setWorkCount(1);
        // 是否关注
        user.setFollow(true);
    }
}

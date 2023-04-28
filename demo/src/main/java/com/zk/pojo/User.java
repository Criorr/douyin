package com.zk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.annotation.Resource;

/**
 * <p>
 * 
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @JsonProperty("name")
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 最近登陆时间
     */
    private Date loginTime;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户首页背景图
     */
    @JsonProperty("background_image")
    private String backgroundImage;

    /**
     * 用户个人简介
     */
    private String signature;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 关注总数
     */
    @JsonProperty("follow_count")
    @TableField(exist = false)
    private Integer followCount;

    /**
     * 粉丝总数
     */
    @JsonProperty("follower_count")
    @TableField(exist = false)
    private Integer followerCount;

    /**
     * 是否关注
     */
    @JsonProperty("is_follow")
    @TableField(exist = false)
    private boolean isFollow;


    /**
     * 作品数
     */
    @JsonProperty("work_count")
    @TableField(exist = false)
    private Integer workCount;


    /**
     * 喜欢数
     */
    @JsonProperty("favorite_count")
    @TableField(exist = false)
    private Integer favoriteCount;

    /**
     * 获赞数量
     */
    @JsonProperty("total_favorited")
    @TableField(exist = false)
    private Integer totalFavorited;
}

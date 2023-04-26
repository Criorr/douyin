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
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 视频id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 视频地址
     */
    @JsonProperty("play_url")
    private String playUrl;

    /**
     * 视频第一帧图片地址
     */
    @JsonProperty("cover_url")
    private String coverUrl;

    /**
     * 视频名称
     */
    private String title;

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
     * 作者信息
     */
    @JsonProperty("author")
    @TableField(exist = false)
    private User user;

    /**
     *  喜欢数
     */
    @TableField(exist = false)
    @JsonProperty("favorite_count")
    private Integer favoriteCount;

    /**
     *  评论数
     */
    @TableField(exist = false)
    @JsonProperty("comment_count")
    private Integer commentCount;

    /**
     *  是否点赞
     */
    @TableField(exist = false)
    @JsonProperty("is_favorite")
    private Boolean isFavorite;

}

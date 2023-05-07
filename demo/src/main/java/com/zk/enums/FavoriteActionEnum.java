package com.zk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum FavoriteActionEnum {
    LIKE("1"), // 点赞
    NOLIKE("2"); // 取消点赞

    private String type;

    public static FavoriteActionEnum getFavoriteActionEnum(String type) {
        if (Objects.equals("1", type)) {
            return LIKE;
        }
        if (Objects.equals("2", type)) {
            return NOLIKE;
        }
        return null;
    }


}

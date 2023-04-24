package com.zk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result
 *
 * @author ZhengKai
 * @date 2023/4/23
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int status_code;

    private String status_msg;

    private Object data;


    public static Result ok() {
        return new Result(0, null, null);
    }
    public static Result ok(Object data) {
        return new Result(0, null, data);
    }

    public static Result ok(String msg, Object data) {
        return new Result(0, msg, data);
    }

    public static Result fail() {
        return new Result(100, null, null);
    }
    public static Result fail(Object data) {
        return new Result(100, null, data);
    }

    public static Result fail(String msg, Object data) {
        return new Result(100, msg, data);
    }


}

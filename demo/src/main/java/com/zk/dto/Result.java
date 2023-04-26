package com.zk.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

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

    public static final int OK = 0;
    public static final int ERROR = 1;

    private int status_code;

    private String status_msg;

    /**
     * 自定义data数据名
     */
    @JsonIgnore
    private String dataName;

    @JsonIgnore
    private Object data;

    private Integer next_time;

    public Result(int status_code, String status_msg, String dataName, Object data) {
        this.status_code = status_code;
        this.status_msg = status_msg;
        this.dataName = dataName;
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getSonsMap() {
        return Collections.singletonMap(dataName, data);
    }


    public static Result ok() {
        return new Result(Result.OK, null,"data", null);
    }
    public static Result ok(String dataName, Object data) {
        return new Result(Result.OK, null,dataName, data);
    }

    public static Result ok(String dataName, Object data, Integer next_time) {
        return new Result(Result.OK, null,dataName, data, next_time);
    }

    public static Result fail() {
        return new Result(Result.ERROR, null, "data",null);
    }
    public static Result fail(String msg) {
        return new Result(Result.ERROR, msg, "data",null);
    }


}

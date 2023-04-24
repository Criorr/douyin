package com.zk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserDTO
 *
 * @author ZhengKai
 * @date 2023/4/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int status_code;
    private String status_msg;
    private String token;
    private long user_id;
}

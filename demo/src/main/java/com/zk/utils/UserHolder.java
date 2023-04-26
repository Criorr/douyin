package com.zk.utils;

import com.zk.dto.UserDTO;

/**
 * UserHolder
 *
 * @author ZhengKai
 * @date 2023/4/24
 */
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    public static UserDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}

package com.zk.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * JWTUtils
 *
 * @author ZhengKai
 * @date 2023/4/24
 */
public class JWTUtils {
    /**
     * 两个常量： 过期时间；秘钥
     */
    public static final long EXPIRE = 1000*60*60*24;
    public static final String SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    /**
     * 生成token字符串的方法
     * @param id
     * @return
     */
    public static String getJwtToken(String id){
        String JwtToken = Jwts.builder()
                //JWT头信息
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //设置分类；设置过期时间 一个当前时间，一个加上设置的过期时间常量
                .setSubject("lin-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                //设置token主体信息，存储用户信息
                .claim("id", id)
//                .signWith(SignatureAlgorithm.ES256, SECRET)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * @Param jwtToken
     */
    public static boolean checkToken(String jwtToken){
        if (StringUtils.isEmpty(jwtToken)){
            return false;
        }
        try{
            //验证token
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwtToken);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 根据token获取会员id
     * @Param request
     */
    public static String getMemberIdByJwtToken(String token){
        if (StringUtils.isEmpty(token)){
            return "";
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        return (String) body.get("id");
    }
}
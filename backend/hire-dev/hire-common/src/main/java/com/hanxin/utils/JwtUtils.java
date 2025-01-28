package com.hanxin.utils;

import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.result.ResponseStatusEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
@RefreshScope
public class JwtUtils {
    public static final String at = "@";

    @Autowired
    private JwtProperties jwtProperties;

    @Value("${jwt.key}")
    public String JWT_KEY;

    public String createJWTWithPrefix(String body, String prefix) {
        return prefix + at + dealJWT(body, null);
    }

    public String createJWTWithPrefix(String body, Long expireTimes, String prefix) {
        if (expireTimes == null) {
            ExceptionWrapper.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }

        return prefix + at + dealJWT(body, expireTimes);
    }

    public String createJWT(String body) {
        return dealJWT(body, null);
    }

    public String createJWT(String body, Long expireTimes) {
        if (expireTimes == null) {
            ExceptionWrapper.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }

        return dealJWT(body, expireTimes);
    }

    public String dealJWT(String body, Long expireTimes) {
        //String userKey = jwtProperties.getKey();
        String userKey = JWT_KEY;
        log.info("Nacos jwt: " + userKey);

        String base64 = Base64.getEncoder().encodeToString(userKey.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        String jwt = "";
        if (expireTimes != null) {
            jwt = generateJWT(body, expireTimes, secretKey);
        } else {
            jwt = generateJWT(body, secretKey);
        }

        System.out.println(jwt);

        return jwt;
    }

    public String generateJWT(String body, SecretKey secretKey) {
        String jwtToken = Jwts.builder()
                .subject(body)
                .signWith(secretKey)
                .compact();

        return jwtToken;
    }

    public String generateJWT(String body, Long expireTimes ,SecretKey secretKey) {
        Date expirationDate = new Date(System.currentTimeMillis() + expireTimes);

        String jwtToken = Jwts.builder()
                .subject(body)
                .signWith(secretKey)
                .expiration(expirationDate)
                .compact();

        return jwtToken;
    }

    public String checkJWT(String pendingJWT) {

        //String userKey = jwtProperties.getKey();
        String userKey = JWT_KEY;
        log.info("Nacos jwt key = " + userKey);

        // 1. 对秘钥进行base64编码
        String base64 = Base64.getEncoder().encodeToString(userKey.getBytes());

        // 2. 对base64生成一个秘钥的对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        // 3. 校验jwt
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();       // 构造解析器
        // 解析成功，可以获得Claims，从而去get相关的数据，如果此处抛出异常，则说明解析不通过，也就是token失效或者被篡改
        Jws<Claims> jws = jwtParser.parseSignedClaims(pendingJWT);      // 解析jwt

        String body = jws.getPayload().getSubject();

        return body;
    }
}

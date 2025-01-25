package com.hanxin.utils;

import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.result.ResponseStatusEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    public static final String at = "@";

    @Autowired
    private JwtProperties jwtProperties;

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
        String userKey = jwtProperties.getKey();
        //String base64 = new BASE64Encoder().encode(userKey.getBytes());
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
}

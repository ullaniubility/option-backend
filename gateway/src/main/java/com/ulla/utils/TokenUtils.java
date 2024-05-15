package com.ulla.utils;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @Description Token工具
 * @author zhuyongdong
 * @since 2023-01-29 11:05:22
 */
@Component
public class TokenUtils {

    private static final String SIGN_KEY = "STREAM_TOKEN";

    /**
     * jwt 有效期为30天
     */
    public static final Long JWT_EXPIRE_TIME_LONG = 1000 * 60 * 60 * 24 * 30L;

    private TokenUtils() {

    }

    public static String createToken(String str) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = getCurrentTime();
        Date now = new Date(nowMillis);
        SecretKey secretKey = createSecretKey();

        JwtBuilder builder = Jwts.builder().setId(UUID.randomUUID().toString()).setIssuedAt(now).setSubject(str)
            .signWith(signatureAlgorithm, secretKey);

        long expMillis = getCurrentTime() + JWT_EXPIRE_TIME_LONG;
        Date exp = new Date(expMillis);

        // token过期时间
        builder.setExpiration(exp);

        return builder.compact();
    }

    private static SecretKey createSecretKey() {
        byte[] encodedKey = Base64.decodeBase64(SIGN_KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    public static Claims getClaim(String token) {
        SecretKey secretKey = createSecretKey();
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (Exception ignored) {
            return null;
        }
    }

    /** 获取当前时间戳（13位整数） */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static boolean isNotExpire(String token) {
        Date exp = getClaim(token).getExpiration();
        return exp.getTime() > getCurrentTime();
    }

    public static boolean isExpire(String token) {
        Date exp = getClaim(token).getExpiration();
        return exp.getTime() < getCurrentTime();
    }
}

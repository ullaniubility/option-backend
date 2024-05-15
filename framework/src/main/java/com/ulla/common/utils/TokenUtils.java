package com.ulla.common.utils;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSON;
import com.ulla.constant.UserConstant;
import com.ulla.modules.auth.mo.LoginUserMo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description Token工具
 * @since 2023-01-29 11:05:22
 */
@Slf4j
public final class TokenUtils {

    private static final String SIGN_KEY = "STREAM_TOKEN";

    private static final String ENCODED_PASSWORD = "GfUCs*FQ8G";

    /**
     * jwt 有效期为30天
     */
    public static final Long JWT_EXPIRE_TIME_LONG = 1000 * 60 * 60 * 24 * 30L;

    public static String createToken(LoginUserMo loginUser) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        SecretKey secretKey = createSecretKey();
        JwtBuilder builder = Jwts.builder().setId(UUID.randomUUID().toString()).setIssuedAt(now)
            .setSubject(JSON.toJSONString(loginUser)).signWith(signatureAlgorithm, secretKey);
        long expMillis = System.currentTimeMillis() + JWT_EXPIRE_TIME_LONG;
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

    public static LoginUserMo getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = request.getHeader(UserConstant.TOKEN);
        // log.info("获取到的token：{}", token);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = getClaim(token);
                // 解析对应的权限以及用户信息
                String subject = Objects.requireNonNull(claims).getSubject();
                // log.info("解析对应的权限以及用户信息subject：{}", subject);
                return JSON.parseObject(subject, LoginUserMo.class);
            } catch (Exception e) {
                log.info("解析失败");
                log.error(e.getMessage());
            }
        }
        log.info("未获取到token");
        return null;
    }

    public static LoginUserMo getLoginUser(String token) {
        // 获取请求携带的令牌
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = getClaim(token);
                // 解析对应的权限以及用户信息
                String subject = Objects.requireNonNull(claims).getSubject();
                return JSON.parseObject(subject, LoginUserMo.class);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * 编码
     * 
     * @param str
     * @return
     */
    public static String encoded(String str) {
        return strToHex(encodedString(str, ENCODED_PASSWORD));
    }

    /**
     * 转换
     * 
     * @param str
     * @param password
     * @return
     */
    private static String encodedString(String str, String password) {
        char[] pwd = password.toCharArray();
        int pwdLen = pwd.length;

        char[] strArray = str.toCharArray();
        for (int i = 0; i < strArray.length; i++) {
            strArray[i] = (char)(strArray[i] ^ pwd[i % pwdLen] ^ pwdLen);
        }
        return new String(strArray);
    }

    private static String strToHex(String s) {
        return bytesToHexStr(s.getBytes());
    }

    private static String bytesToHexStr(byte[] bytesArray) {
        StringBuilder builder = new StringBuilder();
        String hexStr;
        for (byte bt : bytesArray) {
            hexStr = Integer.toHexString(bt & 0xFF);
            if (hexStr.length() == 1) {
                builder.append("0");
                builder.append(hexStr);
            } else {
                builder.append(hexStr);
            }
        }
        return builder.toString();
    }

    /**
     * 解码
     * 
     * @param str
     * @return
     */
    public static String decoded(String str) {
        String hexStr = null;
        try {
            hexStr = hexStrToStr(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hexStr != null) {
            hexStr = encodedString(hexStr, ENCODED_PASSWORD);
        }
        return hexStr;
    }

    private static String hexStrToStr(String hexStr) {
        return new String(hexStrToBytes(hexStr));
    }

    private static byte[] hexStrToBytes(String hexStr) {
        String hex;
        int val;
        byte[] btHexStr = new byte[hexStr.length() / 2];
        for (int i = 0; i < btHexStr.length; i++) {
            hex = hexStr.substring(2 * i, 2 * i + 2);
            val = Integer.valueOf(hex, 16);
            btHexStr[i] = (byte)val;
        }
        return btHexStr;
    }

    public static void main(String[] args) {
        String token =
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlM2FlYmNmYS05NjI3LTRiNGMtYTk0NS04N2Y1ZDgzNDNmNTIiLCJpYXQiOjE2ODIwNTUzNjAsInN1YiI6IntcImlkXCI6ODEsXCJsb2dpblRpbWVcIjoxNjgyMDU1MzYwOTc3LFwib3BlbklkXCI6XCI0OTMtNTQ4LTIyMlwiLFwicGVybWlzc2lvbnNcIjpcIm1lbWJlclwiLFwidWlkXCI6Njk2MTY4MTM2Mjg2NjIyMH0iLCJleHAiOjE2ODQ2NDczNjB9.n9yfBgS6iHuHfh_kwLJTLJbE5LF-19Eq6rxx83EVNO8";
        // log.info("获取到的token：{}", token);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = getClaim(token);
                // 解析对应的权限以及用户信息
                String subject = Objects.requireNonNull(claims).getSubject();
                // log.info("解析对应的权限以及用户信息subject：{}", subject);
                System.out.println(JSON.parseObject(subject, LoginUserMo.class));;
            } catch (Exception e) {
                log.info("解析失败");
                log.error(e.getMessage());
            }
        }
        log.info("未获取到token");

    }

}

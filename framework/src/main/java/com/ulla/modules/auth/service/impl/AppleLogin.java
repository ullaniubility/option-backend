package com.ulla.modules.auth.service.impl;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ulla.modules.auth.enums.AuthCommonSource;
import com.ulla.modules.auth.mo.ThirdConfigMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.utils.HttpUtils;
import com.ulla.modules.auth.utils.UrlBuilder;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 1
 */
@Slf4j
public class AppleLogin extends AbstractThirdPartyLogin {

    public AppleLogin(ThirdConfigMo config) {
        super(AuthCommonSource.APPLE, config);
    }

    @Override
    public String authorize() {
        return UrlBuilder.fromBaseUrl(source.authorize()).queryParam("response_type", "code")
            .queryParam("client_id", config.getClientId()).queryParam("redirect_uri", config.getRedirectUri()).build();
    }

    /**
     * 这里返回的实际上是id_token
     *
     * @param code
     * @return
     */
    @Override
    public String getAccessToken(String code) {
        config.setClientSecret(generateClientSecret());
        String response = new HttpUtils().post(accessTokenUrl(code)).getBody();
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        checkResponse(accessTokenObject);
        return accessTokenObject.getString("id_token");
    }

    /**
     * 由于后续没有其他使用access_token，这里传入的token实际上是id_token进行验证，并不获取用户信息
     *
     * @param idToken
     * @return
     */
    @Override
    public UserMo getUserInfo(String idToken) {
        // 验证idToken
        if (!verify(idToken)) {
            throw new RuntimeException("id_token验证失败");
        }
        return null;
    }

    /**
     * 生成clientSecret
     *
     * @return
     */
    public String generateClientSecret() {
        Map<String, Object> header = new HashMap<>(2);
        header.put("kid", config.getKid());
        long second = System.currentTimeMillis() / 1000;
        // 将private key字符串转换成PrivateKey 对象
        PrivateKey privateKey = null;
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec =
                new PKCS8EncodedKeySpec(Base64.decodeBase64(config.getPrivateKey()));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("生成clientSecret失败");
        }
        // 此处只需privateKey
        Algorithm algorithm = Algorithm.ECDSA256(null, (ECPrivateKey)privateKey);
        // 生成JWT格式的client_secret
        return JWT.create().withHeader(header).withClaim("iss", config.getTeamId()).withClaim("iat", second)
            .withClaim("exp", 86400 * 180 + second).withClaim("aud", "https://appleid.apple.com")
            .withClaim("sub", config.getClientId()).sign(algorithm);
    }

    private JSONArray getAuthKeys() {
        String response =
            new HttpUtils().get(UrlBuilder.fromBaseUrl(AuthCommonSource.APPLE.userInfo()).build()).getBody();
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        checkResponse(accessTokenObject);
        return accessTokenObject.getJSONArray("keys");
    }

    private Boolean verify(String jwt) {
        JSONArray arr = getAuthKeys();
        if (arr == null) {
            return false;
        }
        // 先取苹果第一个key进行校验
        if (verifyExc(jwt, arr.getJSONObject(0))) {
            return true;
        } else {
            // 再取第二个key校验
            if (verifyExc(jwt, arr.getJSONObject(1))) {
                return true;
            } else {
                // 再取第三个key校验
                return verifyExc(jwt, arr.getJSONObject(2));
            }
        }

    }

    /**
     * 对前端传来的idToken进行验证
     *
     * @param idToken
     *            对应前端传来的 idToken
     * @param authKey
     *            苹果的公钥 authKey
     * @return
     * @throws Exception
     */
    private Boolean verifyExc(String idToken, JSONObject authKey) {
        Jwk jwa = Jwk.fromValues(authKey);
        PublicKey publicKey = null;
        try {
            publicKey = jwa.getPublicKey();
        } catch (InvalidPublicKeyException e) {
            log.error("publicKey error", e);
            throw new RuntimeException("publicKey error");
        }

        String aud = "";
        String sub = "";
        if (idToken.split("\\.").length > 1) {
            String claim = new String(Base64.decodeBase64(idToken.split("\\.")[1]));
            aud = JSONObject.parseObject(claim).get("aud").toString();
            sub = JSONObject.parseObject(claim).get("sub").toString();
        }
        JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey);
        jwtParser.requireIssuer("https://appleid.apple.com");
        jwtParser.requireAudience(aud);
        jwtParser.requireSubject(sub);

        try {
            Jws<Claims> claim = jwtParser.parseClaimsJws(idToken);
            return claim != null && claim.getBody().containsKey("auth_time");
        } catch (ExpiredJwtException e) {
            log.error("apple identityToken expired", e);
        } catch (Exception e) {
            log.error("apple identityToken illegal", e);
        }
        return false;
    }

    /**
     * 对前端传来的JWT字符串idToken的第二部分进行解码 主要获取其中的aud和sub，aud大概对应ios前端的包名，sub大概对应当前用户的授权的openID
     *
     * @param identityToken
     * @return { "aud":"com.xkj.****", "sub":"000***.8da764d3f9e34d2183e8da08a1057***.0***",
     *         "c_hash":"UsKAuEoI-****","email_verified":"true", "auth_time":1574673481,
     *         "iss":"https://appleid.apple.com", "exp":1574674081, "iat":1574673481, "email":"****@qq.com" }
     */
    private JSONObject parserIdentityToken(String identityToken) {
        String[] arr = identityToken.split("\\.");
        String decode = new String(Base64.decodeBase64(arr[1]));
        String substring = decode.substring(0, decode.indexOf("}") + 1);
        return JSON.parseObject(substring);
    }

}

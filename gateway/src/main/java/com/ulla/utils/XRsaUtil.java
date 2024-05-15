package com.ulla.utils;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import jodd.net.URLDecoder;

/**
 * @Description RSA加解密工具
 * @author zhuyongdong
 * @since 2023-04-22 12:46:56
 */
public class XRsaUtil {

    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";

    public static final String KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String RSA_ALGORITHM_SIGN = "SHA256WithRSA";

    private static RSAPublicKey publicKey;
    private static RSAPrivateKey privateKey;

    // 可以调用 createKeys 方法自己生成，替换一下

    // 解密加密
    public static String setRsaPublicKey(Object json) {
        String publicKeyValue =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAILxLkw91qVRcID_sDpbVSkK1B7mnBpE_eOkU6bK3t-BH7iWgByvsmmwgrIDU2B1m1v7pNJYu4mljHzpDj0XYNECAwEAAQ";
        String en = publicEncrypt(json.toString(), getRSAPublicKey(publicKeyValue));
        return en;
    }

    // 解密
    public static String getData(String json) {
        String privateKeyValue =
            "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAgvEuTD3WpVFwgP-wOltVKQrUHuacGkT946RTpsre34EfuJaAHK-yabCCsgNTYHWbW_uk0li7iaWMfOkOPRdg0QIDAQABAkASlVIBxgDxg2ZZGHCVR6MFaSEDpazf2YzCwu6QTFhnFcMK3z-VXOziZhMw0KYUFMzwQEQu4cKK7olcFFN8poRBAiEAyXi3bI91gkVM3zdbk_8fu5QrEI6kLrj0ydWY6MtHeJkCIQCmYbfCehWMAhFoja3AL5NVovRgNnC4LIISX5gpKTQ0-QIgKxse86VGGRdGuUOY3nNpkLLE_Afo7O45wa1nx_cmVZECIQCRghY2Q5TCfDCDUpyo3jKpCzlTR2ku-OXMccPeA4X_6QIgOl1a5cIxSvXyBnuYGx5ZKS-Yst_BoyMQxMVRgr4bmv4";
        String de = XRsaUtil.privateDecrypt(json, getRSAPrivateKey(privateKeyValue));
        return de;
    }

    public XRsaUtil(String publicKey, String privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

            // 通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));

            this.publicKey = (RSAPublicKey)keyFactory.generatePublic(x509KeySpec);
            // 通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            this.privateKey = (RSAPrivateKey)keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (Exception e) {
            throw new RuntimeException("Unsupported key: ", e);
        }
    }

    public static RSAPublicKey getRSAPublicKey(String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            // 通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            return (RSAPublicKey)keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new RuntimeException("getRSAPublicKey,Unsupported key: ", e);
        }
    }

    public static RSAPrivateKey getRSAPrivateKey(String privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            // 通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            return (RSAPrivateKey)keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (Exception e) {
            throw new RuntimeException("getRSAPrivateKey,Unsupported key: ", e);
        }
    }

    public static Map<String, String> createKeys(int keySize) {
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;

        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm -> [" + RSA_ALGORITHM + "]");
        }

        // 初始化KeyPairGenerator对象,不要被initialize()源码表面上欺骗,其实这里声明的size是生效的
        kpg.initialize(keySize);

        // 生成秘钥对
        KeyPair keyPair = kpg.generateKeyPair();

        // 得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());

        // 得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    /**
     * 公钥加密
     */
    public String publicEncrypt(String data) {
        return publicKeyEncrypt(data, publicKey);
    }

    public static String publicEncrypt(String data, RSAPublicKey rsaPublicKey) {
        return publicKeyEncrypt(data, rsaPublicKey);
    }

    private static String publicKeyEncrypt(String data, RSAPublicKey rsaPublicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);

            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET),
                rsaPublicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("Unsupported key[" + data + "]", e);
        }
    }

    /**
     * 私钥解密
     */
    public String privateDecrypt(String data) {
        return privateKeyDecrypt(data, privateKey, publicKey.getModulus());
    }

    public static String privateDecrypt(String data, RSAPrivateKey rsaPrivateKey) {
        return privateKeyDecrypt(data, rsaPrivateKey, rsaPrivateKey.getModulus());
    }

    private static String privateKeyDecrypt(String data, RSAPrivateKey rsaPrivateKey, BigInteger modulus) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            return new String(
                rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), modulus.bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("Unsupported key[" + data + "]", e);
        }
    }

    /**
     * 私钥加密
     */
    public String privateEncrypt(String data) {
        return privateKeyEncrypt(data, privateKey, publicKey.getModulus());
    }

    public static String privateEncrypt(String data, RSAPrivateKey rsaPrivateKey) {
        return privateKeyEncrypt(data, rsaPrivateKey, rsaPrivateKey.getModulus());
    }

    private static String privateKeyEncrypt(String data, RSAPrivateKey rsaPrivateKey, BigInteger modulus) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKey);

            return Base64.encodeBase64URLSafeString(
                rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), modulus.bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("Unsupported key[" + data + "]", e);
        }
    }

    /**
     * 公钥解密
     */
    public String publicDecrypt(String data) {
        return publicKeyDecrypt(data, publicKey);
    }

    public static String publicDecrypt(String data, RSAPublicKey rsaPublicKey) {
        return publicKeyDecrypt(data, rsaPublicKey);
    }

    private static String publicKeyDecrypt(String data, RSAPublicKey rsaPublicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);

            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data),
                rsaPublicKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("Unsupported key[" + data + "]", e);
        }
    }

    /**
     * 私钥签名
     */
    public String sign(String data) {
        return getSign(data, privateKey);
    }

    public static String sign(String data, RSAPrivateKey privateKey) {
        return getSign(data, privateKey);
    }

    private static String getSign(String data, RSAPrivateKey privateKey) {
        try {
            String encodeStr = DigestUtils.md5Hex(data);
            Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
            signature.initSign(privateKey);
            signature.update(encodeStr.getBytes(CHARSET));

            return Base64.encodeBase64URLSafeString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("Unsupported key[" + data + "]", e);
        }
    }

    /**
     * 公钥验签，验证sign
     */
    public boolean verify(String data, String sign) {
        return verifySign(data, sign, publicKey);
    }

    public static boolean verify(String data, String sign, RSAPublicKey rsaPublicKey) {
        return verifySign(data, sign, rsaPublicKey);
    }

    public static boolean verifySign(String data, String sign, RSAPublicKey rsaPublicKey) {
        try {
            String encodeStr = DigestUtils.md5Hex(data);
            Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
            signature.initVerify(rsaPublicKey);
            signature.update(encodeStr.getBytes(CHARSET));

            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            throw new RuntimeException("[" + data + "]", e);
        }
    }

    /**
     * 分段加解密
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;

        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = (keySize / 8) - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;

        try {
            while (datas.length > offSet) {
                if ((datas.length - offSet) > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }

                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unsupported key[" + maxBlock + "]", e);
        }

        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

    public static void main(String[] args) {

        try {
            /*String publicKey =
                "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAILxLkw91qVRcID_sDpbVSkK1B7mnBpE_eOkU6bK3t-BH7iWgByvsmmwgrIDU2B1m1v7pNJYu4mljHzpDj0XYNECAwEAAQ";
            String privateKey =
                "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAgvEuTD3WpVFwgP-wOltVKQrUHuacGkT946RTpsre34EfuJaAHK-yabCCsgNTYHWbW_uk0li7iaWMfOkOPRdg0QIDAQABAkASlVIBxgDxg2ZZGHCVR6MFaSEDpazf2YzCwu6QTFhnFcMK3z-VXOziZhMw0KYUFMzwQEQu4cKK7olcFFN8poRBAiEAyXi3bI91gkVM3zdbk_8fu5QrEI6kLrj0ydWY6MtHeJkCIQCmYbfCehWMAhFoja3AL5NVovRgNnC4LIISX5gpKTQ0-QIgKxse86VGGRdGuUOY3nNpkLLE_Afo7O45wa1nx_cmVZECIQCRghY2Q5TCfDCDUpyo3jKpCzlTR2ku-OXMccPeA4X_6QIgOl1a5cIxSvXyBnuYGx5ZKS-Yst_BoyMQxMVRgr4bmv4";*/
            String publicKey =
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZFW5LyHaR0kkr8GtvyMCptXbfOPX86hRZyVEqxXqpp+wsGNIUWLw6FJDFtWlgQpvc7xngilEOPzOfbpgjEEVaAM5dDfmwbSd1pfc2AfjXQErYSeWLEiiMN22RgLqZMTeb6894PdH+VahT9CwNafM9hSgdhiiJxIN67roQ12ujiQIDAQAB";
            String privateKey =
                "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJkVbkvIdpHSSSvwa2/IwKm1dt849fzqFFnJUSrFeqmn7CwY0hRYvDoUkMW1aWBCm9zvGeCKUQ4/M59umCMQRVoAzl0N+bBtJ3Wl9zYB+NdASthJ5YsSKIw3bZGAupkxN5vrz3g90f5VqFP0LA1p8z2FKB2GKInEg3ruuhDXa6OJAgMBAAECgYAH1jQGRyXiwywhxrYJS/Ko/XQva0AUiXsvOYIhOWjVJJaPj1m51u0T4BelN2tElURbZBYkC5CotQ2UfNnm+Z9DLukiP7GiX8VrNt6U1Mw4Gf8aJEPFzUMjZaGTnydFOh5pkuNWmqcxT0BLQl+qKd2JYF+g6koT8f70B6ONsOg0HQJBANmyvZPMfXXtI3tTpaHElWgQ/VrU/cRGuLJb70OIbmz0fEKYhvGVh5XkoNENy/sv6ODWhybud/uVr1uuNGjxy5cCQQC0BGoT3J3pvODVRTLf217mZytUjjg4+ro0V1s4eS1Uzz3HNXSKl5LfFFBs1WuqJoHlonKXdqMP5j044wNjRG3fAkARfg1BnQLPwPhC5pu+fd9Ld2IENG0Xol8g5cY59PJ6isKZT0w4iT1VzAMoCSXEzzq1JkfB1xOJAaQPh9XqQNONAkEAgozRASZ1vxUjWIeepYnKAP3BBsk9LNVoRJ01onb+0QqamuXBZuEVuoJY8RS1x+e1PHva7s0y5EztCVxZEJyHsQJAS/V99EYtBKRcQmz2nMdpxq3tAZURKD1Wx6Zs1hVvEWFYJEjNYwaTabALnrOf3etEwglHsAwewA1fww39VzeB8g==";
            RSAPublicKey rsaPublicKey = XRsaUtil.getRSAPublicKey(publicKey);
            RSAPrivateKey rsaPrivateKey = XRsaUtil.getRSAPrivateKey(privateKey);

            String json =
                "{\"mail\":\"41277749@qq.com\",\"name\" :\"朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东朱永东\",\"password\":\"zgyl5202\",\"phone\":\"19974866455\"}";

            /*  // 私钥加密得到sign
            String sign = XRsaUtil.sign(json, rsaPrivateKey);
            System.out.println("sign:" + sign);
            boolean b = XRsaUtil.verifySign(json, sign, rsaPublicKey);
            System.out.println("验证签名:" + b);*/

            String en = XRsaUtil.publicEncrypt(json, rsaPublicKey);
            System.out.println("公钥(public)加密,私钥(private)解密---------");
            System.out.println("公钥加密json数据:" + en);
            String de = XRsaUtil.privateDecrypt(en, rsaPrivateKey);
            System.out.println("私钥解密:" + de);

            String de3 = XRsaUtil.privateDecrypt(en, rsaPrivateKey);
            System.out.println("私钥解密:" + de3);
            String de2 = XRsaUtil.privateDecrypt(
                "cEeWIMK0UVec4cixGzdMHytPAd2oEDejqPtPOPdl/Yv1csqSjPDG5KI 3i3C73Fvs3aAbCZTs6K1c3716UCpCvxIR6A3X0TxY9AJarVX7d97MXIGMVart4fPdp xfOqPgM5SYYbLxFI0CydkZCOTiBOBzMxHWYv0e6r61ZOjeZQ=",
                rsaPrivateKey);
            System.out.println("私钥解密2:" + URLDecoder.decode(de2));

            en = XRsaUtil.privateEncrypt(json, rsaPrivateKey);
            de = XRsaUtil.publicDecrypt(en, rsaPublicKey);

            System.out.println("私钥(private)加密，公钥(public)解密---------");
            System.out.println("私钥加密json数据:" + en);
            System.out.println("公钥解密:" + de);

            System.out.println("--------------------------------------------------------------------------------");

        } catch (Exception e) {
            System.out.println("Exception thrown: " + e);
        }

    }
}

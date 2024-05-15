package com.ulla.common.utils;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

/**
 * @Description 后台专用解密工具
 * @author zhuyongdong
 * @since 2023-05-16 14:13:34
 */
public class DecryptStringUtil {

    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";

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

    public static String getDecryptString(String jsonStr) {
        String privateKey =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJkVbkvIdpHSSSvwa2/IwKm1dt849fzqFFnJUSrFeqmn7CwY0hRYvDoUkMW1aWBCm9zvGeCKUQ4/M59umCMQRVoAzl0N+bBtJ3Wl9zYB+NdASthJ5YsSKIw3bZGAupkxN5vrz3g90f5VqFP0LA1p8z2FKB2GKInEg3ruuhDXa6OJAgMBAAECgYAH1jQGRyXiwywhxrYJS/Ko/XQva0AUiXsvOYIhOWjVJJaPj1m51u0T4BelN2tElURbZBYkC5CotQ2UfNnm+Z9DLukiP7GiX8VrNt6U1Mw4Gf8aJEPFzUMjZaGTnydFOh5pkuNWmqcxT0BLQl+qKd2JYF+g6koT8f70B6ONsOg0HQJBANmyvZPMfXXtI3tTpaHElWgQ/VrU/cRGuLJb70OIbmz0fEKYhvGVh5XkoNENy/sv6ODWhybud/uVr1uuNGjxy5cCQQC0BGoT3J3pvODVRTLf217mZytUjjg4+ro0V1s4eS1Uzz3HNXSKl5LfFFBs1WuqJoHlonKXdqMP5j044wNjRG3fAkARfg1BnQLPwPhC5pu+fd9Ld2IENG0Xol8g5cY59PJ6isKZT0w4iT1VzAMoCSXEzzq1JkfB1xOJAaQPh9XqQNONAkEAgozRASZ1vxUjWIeepYnKAP3BBsk9LNVoRJ01onb+0QqamuXBZuEVuoJY8RS1x+e1PHva7s0y5EztCVxZEJyHsQJAS/V99EYtBKRcQmz2nMdpxq3tAZURKD1Wx6Zs1hVvEWFYJEjNYwaTabALnrOf3etEwglHsAwewA1fww39VzeB8g==";
        RSAPrivateKey rsaPrivateKey = DecryptStringUtil.getRSAPrivateKey(privateKey);
        return privateDecrypt(jsonStr, rsaPrivateKey);
    }

}

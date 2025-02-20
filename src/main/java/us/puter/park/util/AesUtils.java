package us.puter.park.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesUtils {

    /**
     * AES 암호화
     * @param str
     * @param key
     * @param iv
     * @param useBase64
     * @return
     */
    public static String encrypt(String str, String key, String iv, boolean useBase64) {
        if (StringUtils.isBlank(str)) {
            return "";
        }

        if (StringUtils.isBlank(iv)) {
            iv = key.substring(0, 16);
        }

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(key.getBytes(), "AES"),
                    new IvParameterSpec(iv.getBytes())
            );
            byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return useBase64 ? Base64.getEncoder().encodeToString(encrypted) : new String(Hex.encode(encrypted));
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * AES 복호화
     * @param encStr
     * @param key
     * @param iv
     * @param useBase64
     * @return
     */
    public static String decrypt(String encStr, String key, String iv, boolean useBase64) {
        if (StringUtils.isBlank(iv)) {
            iv = key.substring(0, 16);
        }

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(key.getBytes(), "AES"),
                    new IvParameterSpec(iv.getBytes())
            );
            byte[] encrypted = useBase64 ? Base64.getDecoder().decode(encStr) : Hex.decode(encStr);
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encStr;
        }
    }

}

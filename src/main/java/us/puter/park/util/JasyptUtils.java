package us.puter.park.util;

import org.apache.commons.lang3.StringUtils;
import us.puter.park.common.config.JasyptConfig;

public class JasyptUtils {

    /**
     * Jasypt 암호화
     * @param plainString
     * @return 암호화된 값
     */
    public static String encStringByJasypt(String plainString) {
        JasyptConfig jasyptConfig = new JasyptConfig();
        return jasyptConfig.stringEncryptor().encrypt(plainString);
    }

    /**
     * Jasypt 복호화
     * @param encString
     * @return 복호화된 값
     */
    public static String decStringByJasypt(String encString) {
        if (StringUtils.contains(encString, "ENC(")) {
            encString = encString.substring(4, encString.length() - 1);
        }
        JasyptConfig jasyptConfig = new JasyptConfig();
        return jasyptConfig.stringEncryptor().decrypt(encString);
    }
}

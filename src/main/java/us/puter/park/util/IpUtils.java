package us.puter.park.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    private static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    private static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    private static final String X_REAL_IP = "X-Real-IP";
    private static final String X_REALIP = "X-RealIP";

    /**
     * 접근 IP 확인
     * @param request
     * @return
     */
    public static String getRemoteIP(HttpServletRequest request) {
        // IP 주소 획득을 위한 헤더 목록
        String[] headers = {
                X_FORWARDED_FOR,
                PROXY_CLIENT_IP,
                WL_PROXY_CLIENT_IP,
                HTTP_CLIENT_IP,
                HTTP_X_FORWARDED_FOR,
                X_REAL_IP,
                X_REALIP
        };

        // 헤더 목록에서 IP 주소 획득
        return Arrays.stream(headers)
                .map(request::getHeader)
                .filter(ip -> StringUtils.isNotBlank(ip) && !StringUtils.equalsIgnoreCase(UNKNOWN, ip))
                .findFirst()
                .orElseGet(request::getRemoteAddr); // 찾지 못할 시 기본 IP 주소 반환
    }
}

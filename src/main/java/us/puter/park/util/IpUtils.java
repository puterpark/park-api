package us.puter.park.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    /**
     * 접근 IP 확인
     * @param request
     * @return
     */
    public static String getRemoteIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}

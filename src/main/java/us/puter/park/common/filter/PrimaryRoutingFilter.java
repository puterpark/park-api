package us.puter.park.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PrimaryRoutingFilter extends OncePerRequestFilter {

    public static final String X_FORCED_PRIMARY = "X-Forced-Primary";
    private static final ThreadLocal<Boolean> forcedPrimary = new ThreadLocal<>();

    public static boolean isForcedPrimary() {
        return Boolean.TRUE.equals(forcedPrimary.get());
    }

    public static void clear() {
        forcedPrimary.remove();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 헤더 확인 후 primary 강제 여부 설정
            String headerValue = request.getHeader(X_FORCED_PRIMARY);
            if (StringUtils.equalsIgnoreCase("true", headerValue)) {
                forcedPrimary.set(true);
            }

            filterChain.doFilter(request, response);
        } finally {
            clear();
        }
    }
}

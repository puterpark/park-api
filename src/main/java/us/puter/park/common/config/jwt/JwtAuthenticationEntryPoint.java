package us.puter.park.common.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import us.puter.park.common.exception.ErrorCode;
import us.puter.park.common.http.CustomResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("ErrorCode");
        if (errorCode == null) {
            errorCode = ErrorCode.UNAUTHORIZED;
        }
        CustomResponse.setResponse(response, errorCode);
    }
}

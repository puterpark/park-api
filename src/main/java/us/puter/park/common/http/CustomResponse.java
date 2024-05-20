package us.puter.park.common.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import us.puter.park.common.exception.ErrorCode;

import java.io.IOException;

public class CustomResponse {

    public static void setResponse(HttpServletResponse response, ErrorCode errorCode) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getStatus());

        HttpResponseDto<?> dto = new HttpResponseDto<>(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode(), null);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(dto);
            response.getWriter().write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package us.puter.park.common.http;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import us.puter.park.common.exception.ErrorCode;

public class HttpApiResponse {

    public static <T> HttpResponseDto<T> getSuccessResponse(T data) {
        return new HttpResponseDto<T>("OK", 200, "S0000", data);
    }

    public static CommonHttpResultDto getSuccessResponse() {
        return new CommonHttpResultDto("OK", 200, "S0000");
    }

    public static CommonHttpResultDto getSuccessResponse(String message) {
        return new CommonHttpResultDto(message, 200, "S0000");
    }

    public static CommonHttpResultDto getFailureResponse(ErrorCode errorCode, String message) {
        return new CommonHttpResultDto(message, errorCode.getStatus(), errorCode.getCode());
    }

    public static <T> HttpResponseDto<T> getFailureResponse(ErrorCode errorCode, T data) {
        return new HttpResponseDto<T>(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode(), data);
    }

    public static HttpResponseDto<?> errorOf(final ErrorCode errorCode) {
        return errorOf(errorCode, errorCode.getMessage());
    }

    public static HttpResponseDto<?> errorOf(final ErrorCode errorCode, String message) {
        return new HttpResponseDto<>(errorCode, message);
    }

    public static ResponseEntity<CommonHttpResultDto> buildResponse(ErrorCode errorCode, String errorMessage) {
        if (StringUtils.isBlank(errorMessage)) {
            return ResponseEntity.ok(getSuccessResponse());
        } else {
            return ResponseEntity
                    .status(errorCode.getStatus())
                    .body(getFailureResponse(errorCode, errorMessage));
        }
    }

    public static ResponseEntity<CommonHttpResultDto> buildResponse(ErrorCode errorCode, String errorMessage, Object data) {
        if (StringUtils.isBlank(errorMessage)) {
            if (data != null) {
                return ResponseEntity.ok(HttpApiResponse.getSuccessResponse(data));
            } else {
                return ResponseEntity.ok(HttpApiResponse.getSuccessResponse());
            }
        } else {
            return ResponseEntity
                    .status(errorCode.getStatus())
                    .body(HttpApiResponse.getFailureResponse(errorCode, errorMessage));
        }
    }
}

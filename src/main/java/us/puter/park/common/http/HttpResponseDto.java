package us.puter.park.common.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import us.puter.park.common.exception.ErrorCode;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class HttpResponseDto<T> extends CommonHttpResultDto {

    private T data;

    public HttpResponseDto(final ErrorCode errorCode) {
        super.setMessage(errorCode.getMessage());
        super.setStatus(errorCode.getStatus());
        super.setCode(errorCode.getCode());
    }

    public HttpResponseDto(final ErrorCode errorCode, String message) {
        super.setMessage(message);
        super.setStatus(errorCode.getStatus());
        super.setCode(errorCode.getCode());
    }

    public HttpResponseDto(final ErrorCode errorCode, T data) {
        super.setMessage(errorCode.getMessage());
        super.setStatus(errorCode.getStatus());
        super.setCode(errorCode.getCode());
        this.data = data;
    }

    public HttpResponseDto(String message, int status, String code, T data) {
        super.setMessage(message);
        super.setStatus(status);
        super.setCode(code);
        this.data = data;
    }
}

package us.puter.park.common.exception;

import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Exception 전용
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<HttpResponseDto<?>> handleException(final Exception e) {
        log.error("{} Exception Occurred.", getStackStrace(e), e);
        ErrorCode errorCode = ErrorCode.ROOT_EXCEPTION;
        HttpResponseDto<?> responseDto = HttpApiResponse.errorOf(errorCode);
        return new ResponseEntity<>(responseDto, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * RuntimeException 전용
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<HttpResponseDto<?>> handleRuntimeException(final RuntimeException e) {
        log.error("{} RuntimeException Occurred.", getStackStrace(e), e);
        ErrorCode errorCode = ErrorCode.RUNTIME_EXCEPTION;
        HttpResponseDto<?> responseDto = HttpApiResponse.errorOf(errorCode);
        return new ResponseEntity<>(responseDto, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * ConstraintViolationException 전용 (@Validated 실패 시 작동)
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<HttpResponseDto<?>> handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("ConstraintViolationException Occurred [{}]", e.getMessage());

        ErrorCode errorCode = ErrorCode.VALIDATION_FAIL;

        StringBuilder sb = new StringBuilder();
        sb.append(errorCode.getMessage()).append(" [");
        e.getConstraintViolations().forEach(error -> {
            Stream<Path.Node> stream = StreamSupport.stream(error.getPropertyPath().spliterator(), false);
            List<Path.Node> list = stream.toList();
            String field = list.getLast().getName();
            sb.append(field).append("|");
        });
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        HttpResponseDto<?> responseDto = HttpApiResponse.errorOf(errorCode, sb.toString());
        return new ResponseEntity<>(responseDto, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * MethodArgumentNotValidException 전용 (@Valid 실패 시 작동)
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<HttpResponseDto<?>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException Occurred [{}]", e.getMessage());

        ErrorCode errorCode = ErrorCode.VALIDATION_FAIL;

        StringBuilder sb = new StringBuilder();
        sb.append(errorCode.getMessage()).append(" [");
        e.getBindingResult().getFieldErrors().forEach(error -> {
            sb.append(error.getField()).append("|");
        });
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        HttpResponseDto<?> responseDto = HttpApiResponse.errorOf(errorCode, sb.toString());
        return new ResponseEntity<>(responseDto, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * BusinessException 전용 (주로 사용)
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<HttpResponseDto<?>> handleBusinessException(final BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        StringBuilder sb = new StringBuilder();
        sb.append(getStackStrace(e)).append(" ");
        sb.append("BusinessException Occurred [").append(errorCode.getMessage()).append("]");
        if (e.getData() != null && !errorCode.getMessage().equals(e.getData())) {
            sb.append(", ").append(e.getData());
        }
        log.error(sb.toString());

        HttpResponseDto<?> responseDto = null;

        if (errorCode.equals(ErrorCode.VALIDATION_FAIL)) {
            sb = new StringBuilder();
            sb.append(errorCode.getMessage()).append(" [").append(e.getData()).append("]");
            responseDto = HttpApiResponse.errorOf(errorCode, sb.toString());
        } else {
            responseDto = HttpApiResponse.getFailureResponse(errorCode, e.getData());
        }

        return new ResponseEntity<>(responseDto, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * HttpRequestMethodNotSupportedException 전용
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<HttpResponseDto<?>> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.error("{} HttpRequestMethodNotSupportedException Occurred [{}]", getStackStrace(e), e.getMessage());
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        HttpResponseDto<?> responseDto = HttpApiResponse.errorOf(errorCode);
        return new ResponseEntity<>(responseDto, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * HttpMessageNotReadableException 전용 (request dto의 enum 매핑 실패 시 작동)
     * @param e
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<HttpResponseDto<?>> handleHttpMessageNotReadableException(final BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("{} HttpMessageNotReadableException Occurred [{}], {}", getStackStrace(e), errorCode.getMessage(), e.getData());
        HttpResponseDto<?> responseDto = HttpApiResponse.getFailureResponse(errorCode, e.getData());
        return new ResponseEntity<>(responseDto, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * NoResourceFoundException 전용
     * @param e
     * @return
     */
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<HttpResponseDto<?>> handleNoResourceFoundException(final NoResourceFoundException e) {
        ErrorCode errorCode = ErrorCode.NOT_FOUND_RESOURCE;
        HttpResponseDto<?> responseDto = HttpApiResponse.errorOf(errorCode);
        return new ResponseEntity<>(responseDto, HttpStatus.valueOf(errorCode.getStatus()));
    }

    /**
     * exception 발생 위치
     * @param e
     * @return
     */
    private String getStackStrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement stackTraceElement = stackTrace[0];
            sb.append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber()).append(" -");
        }
        return sb.toString();
    }
}

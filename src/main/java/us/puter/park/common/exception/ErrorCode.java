package us.puter.park.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 5XX
    RUNTIME_EXCEPTION(500, "E0001", "Runtime Exception")
    , ROOT_EXCEPTION(500, "E0002", "Root Exception")
    , INTERNAL_SERVER_ERROR(500, "E0003", "Internal Server Error")

    // 4XX - 공통
    , UNAUTHORIZED(401, "F0001", "Unauthorized")
    , AUTH_FAIL(401, "F0002", "Authorized Failed")
    , FORBIDDEN(403, "F0003", "Forbidden")
    , METHOD_NOT_ALLOWED(405, "F0004", "HTTP Method Not Allowed")
    , DENY_AUTHORIZATION(401, "F0005", "Deny Authorization")
    , ACCESS_TOKEN_EXPIRED(401, "F0006", "Access Token Expired")
    , REFRESH_TOKEN_EXPIRED(401, "F0007", "Refresh Token Expired")
    , NOT_FOUND_RESOURCE(404, "F0008", "Not Found Resource")

    // 4XX - 커스텀
    , INVALID_REQ_PARAM(400, "F1001", "Invalid Request Parameter")
    , VALIDATION_FAIL(400, "F1002", "Validation Failed")
    , ALREADY_EXISTS_DATA(400, "F1003", "Already Exists Data")
    , NOT_FOUND_DATA(400, "F1004", "Not Found Data")
    , NOT_FOUND_MEMBER(400, "F1005", "Not Found Member")
    , INVALID_ENUM_VALUE(400, "F1006", "Invalid Enum Value")
    , NOT_FOUND_REFRESH_TOKEN(400, "F1007", "Not Found Refresh Token")
    ;

    private final int status;
    private final String code;
    private final String message;
}

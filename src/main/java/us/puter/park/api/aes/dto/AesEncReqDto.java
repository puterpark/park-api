package us.puter.park.api.aes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import us.puter.park.common.config.CommonPatterns;

public record AesEncReqDto(
        // 평문
        @NotBlank
        String plainText,
        // 비밀키
        @NotBlank
        @Pattern(regexp = CommonPatterns.SECRET_KEY)
        String secretKey,
        // iv
        @Pattern(regexp = "^([A-Za-z0-9!@#$%^&*()_+={}\\[\\]:;\"'<>,.?/~`-]{16})?$")
        String iv,
        // base64 사용여부
        @NotBlank
        @Pattern(regexp = CommonPatterns.YN)
        String useBase64
) {
}

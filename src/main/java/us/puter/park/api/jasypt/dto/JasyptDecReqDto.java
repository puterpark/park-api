package us.puter.park.api.jasypt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import us.puter.park.common.config.CommonPatterns;

public record JasyptDecReqDto(
        // 암호화된 문자
        @NotBlank
        String encText,
        // 비밀키
        @NotBlank
        @Pattern(regexp = CommonPatterns.SECRET_KEY)
        String secretKey
) {
}

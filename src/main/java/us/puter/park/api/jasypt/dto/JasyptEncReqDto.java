package us.puter.park.api.jasypt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import us.puter.park.common.config.CommonPatterns;

public record JasyptEncReqDto(
        // 평문
        @NotBlank
        String plainText,
        // 비밀키
        @NotBlank
        @Pattern(regexp = CommonPatterns.SECRET_KEY)
        String secretKey
) {
}

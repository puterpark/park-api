package us.puter.park.api.jasypt.dto;

import jakarta.validation.constraints.NotBlank;

public record JasyptDecReqDto(
        @NotBlank
        String encText,
        @NotBlank
        String secretKey
) {
}

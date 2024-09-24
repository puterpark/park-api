package us.puter.park.api.jasypt.dto;

import jakarta.validation.constraints.NotBlank;

public record JasyptEncReqDto(
        @NotBlank
        String plainText,
        @NotBlank
        String secretKey
) {
}

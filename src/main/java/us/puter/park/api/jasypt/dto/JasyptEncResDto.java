package us.puter.park.api.jasypt.dto;

import lombok.Builder;

@Builder
public record JasyptEncResDto(
        String encText
) {
}

package us.puter.park.api.member.dto;

import lombok.Builder;

@Builder
public record TokenResDto(
        String accessToken,
        String refreshToken
) {
}

package us.puter.park.api.member.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginReqDto(
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}

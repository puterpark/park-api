package us.puter.park.api.member.dto;

import jakarta.validation.constraints.NotBlank;
import us.puter.park.api.member.enums.MemberRole;
import us.puter.park.api.member.enums.MemberStatus;

public record MemberCreateReqDto(
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotBlank
        String name,
        MemberRole role,
        MemberStatus status
) {
}

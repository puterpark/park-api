package us.puter.park.api.member.dto;

import us.puter.park.api.member.enums.MemberRole;

import java.util.UUID;

public record MemberTokenInfoDto(
        UUID id,
        String username,
        MemberRole role
) {
}

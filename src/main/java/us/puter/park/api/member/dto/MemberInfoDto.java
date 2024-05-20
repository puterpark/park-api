package us.puter.park.api.member.dto;

import java.util.UUID;

public record MemberInfoDto(
        UUID id,
        String username,
        String password,
        String role,
        String status
) {
}

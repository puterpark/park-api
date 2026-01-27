package us.puter.park.api.ip.dto;

import lombok.Builder;

@Builder
public record IpResDto(
        String ip
) {
}

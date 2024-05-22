package us.puter.park.api.shortenurl.dto;

import lombok.Builder;

@Builder
public record ShortenUrlCreateResDto(
        String shortenUri
) {
}

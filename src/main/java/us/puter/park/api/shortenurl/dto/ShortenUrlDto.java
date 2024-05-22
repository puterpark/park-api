package us.puter.park.api.shortenurl.dto;

import java.util.UUID;

public record ShortenUrlDto(
        UUID id,
        String orgUrl
) {
}

package us.puter.park.api.shortenurl.dto;

public record AdminShortenUrlResDto(
        // 단축URI
        String shortenUri,
        // 원본URL
        String orgUrl
) {
}

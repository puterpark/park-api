package us.puter.park.api.shortenurl.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AdminShortenUrlStatisticResDto(
        // 오늘 리다이렉트 수
        Integer todayRedirectCount,
        // 오늘 가장 많이 접속한 IP
        String todayMostAccessIp,
        // 7일 전부터 많이 접근한 URI/COUNT 목록 5개
        List<ShortenUrlCountDto> top5day7,
        // 30일 전부터 많이 접근한 URI/COUNT 목록 5개
        List<ShortenUrlCountDto> top5day30
) {
}

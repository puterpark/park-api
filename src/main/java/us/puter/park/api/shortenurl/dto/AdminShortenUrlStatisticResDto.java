package us.puter.park.api.shortenurl.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AdminShortenUrlStatisticResDto(
        // 오늘 리다이렉트 수
        Integer todayRedirectCount,
        // 오늘 가장 많이 접속한 IP
        String todayMostAccessIp,
        // 오늘 기준 - 많이 접근한 URI/COUNT 목록 5개
        List<ShortenUrlCountDto> todayTop5,
        // 1주 기준 - 많이 접근한 URI/COUNT 목록 5개
        List<ShortenUrlCountDto> weekTop5,
        // 한달 기준 - 많이 접근한 URI/COUNT 목록 5개
        List<ShortenUrlCountDto> monthTop5
) {
}

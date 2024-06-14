package us.puter.park.api.shortenurl.dto;

import java.util.List;

public record AdminShortenUrlListResDto(
        // 목록
        List<ShortenUrlResDto> list,
        // 전체 갯수
        Long totalCount,
        // 전체 페이지 수
        Integer totalPage,
        // 현재 페이지
        Integer currentPage
) {
}

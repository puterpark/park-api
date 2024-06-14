package us.puter.park.api.shortenurl.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdminShortenUrlListReqDto(
        // 페이지 정보
        @NotNull
        @Min(1)
        Integer start,
        @NotNull
        @Min(50)
        @Max(500)
        Integer limit,

        // 검색어
        String searchWord
) {
}

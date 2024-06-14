package us.puter.park.api.shortenurl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShortenUrlResDto(
        // 고유번호
        UUID id,
        // 단축 링크 URI
        String shortenUri,
        // 원본 URL
        String orgUrl,
        // 생성일시
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime regDate,
        // 수정일시
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime modDate
) {
}

package us.puter.park.api.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.Date;

@Builder
public record HolidayResDto(
        // 휴일
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        Date date,
        // 휴일명
        String name
) {
}

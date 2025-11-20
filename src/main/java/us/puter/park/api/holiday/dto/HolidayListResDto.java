package us.puter.park.api.holiday.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HolidayListResDto(
        Integer totalCount,
        List<HolidayResDto> list
) {
}

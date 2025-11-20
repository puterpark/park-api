package us.puter.park.api.holiday.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.holiday.dto.HolidayListResDto;
import us.puter.park.api.holiday.service.HolidayService;
import us.puter.park.common.http.CommonHttpResultDto;
import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;

@RestController
@RequiredArgsConstructor
@Validated
public class HolidayController {

    private final HolidayService holidayService;

    // 연도별 휴일 조회
    @GetMapping(value = "/api/v1/holiday/{year}")
    public HttpResponseDto<HolidayListResDto> getHolidayYear(
            @PathVariable String year
    ) {
        return HttpApiResponse.getSuccessResponse(holidayService.getHolidayByYear(year));
    }

    // 휴일 새로고침 (관리자)
    @PostMapping(value = "/api/v1/admin/holiday/refresh")
    public CommonHttpResultDto postAdminHolidayRefresh() {
        holidayService.refreshHoliday();
        return HttpApiResponse.getSuccessResponse();
    }

}

package us.puter.park.api.holiday.service;

import biweekly.component.VEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import us.puter.park.api.holiday.dto.HolidayListResDto;
import us.puter.park.api.holiday.dto.HolidayResDto;
import us.puter.park.common.config.CommonPatterns;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;
import us.puter.park.util.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

    private final HolidayParser holidayParser;

    /**
     * 휴일 목록 조회
     * @param year
     * @return
     */
    public HolidayListResDto getHolidayByYear(String year) {
        // year가 없으면 오늘 날짜의 연도 설정
        if (StringUtils.isBlank(year)) {
            year = DateUtils.toString(LocalDate.now(), "yyyy");
        }

        // 연도 정규식 검증
        if (!year.matches(CommonPatterns.YYYY)) {
            throw new BusinessException(ErrorCode.INVALID_REQ_PARAM, CommonPatterns.YYYY);
        }

        // 사용가능한 연도 조회 및 검증
        Set<Integer> availableYears = holidayParser.getAvailableYears();
        if (!availableYears.contains(Integer.parseInt(year))) {
            throw new BusinessException(ErrorCode.INVALID_REQ_PARAM, availableYears);
        }

        // 특정 연도의 휴일 조회
        List<VEvent> events = holidayParser.getEventsByYear(Integer.parseInt(year));
        List<HolidayResDto> holidayList = events.stream()
                .map(event -> HolidayResDto.builder()
                        .date(event.getDateStart().getValue())
                        .name(event.getSummary().getValue())
                        .build())
                .toList();

        return HolidayListResDto.builder()
                .totalCount(holidayList.size())
                .list(holidayList)
                .build();
    }

    /**
     * 휴일 새로고침
     */
    public void refreshHoliday() {
        holidayParser.forceRefresh();
    }
}

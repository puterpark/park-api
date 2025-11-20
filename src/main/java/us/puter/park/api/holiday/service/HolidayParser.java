package us.puter.park.api.holiday.service;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class HolidayParser {

    private Map<Integer, List<VEvent>> eventsByYear;
    private long lastUpdated;

    public HolidayParser() {
        this.eventsByYear = new HashMap<>();
        loadAndIndex();
    }

    /**
     * ICS 파일을 로드하여 연도별로 인덱싱
     * 이벤트를 연도별로 분류하여 Map에 저장하고, 마지막 업데이트 시간 기록
     */
    private void loadAndIndex() {
        String icsUrl = "https://raw.githubusercontent.com/puterpark/holiday-in-korea/main/holiday.ics";

        try {
            URI uri = new URI(icsUrl);
            try (InputStream inputStream = uri.toURL().openStream()) {
                ICalendar ical = Biweekly.parse(inputStream).first();

                Map<Integer, List<VEvent>> newIndex = new HashMap<>();
                Calendar cal = Calendar.getInstance();

                for (VEvent event : ical.getEvents()) {
                    if (event.getDateStart() != null && event.getDateStart().getValue() != null) {
                        cal.setTime(event.getDateStart().getValue());
                        int year = cal.get(Calendar.YEAR);

                        newIndex.computeIfAbsent(year, k -> new ArrayList<>()).add(event);
                    }
                }

                this.eventsByYear = newIndex;
                this.lastUpdated = System.currentTimeMillis();
            }
        } catch (IOException e1) {
            log.error("error[IOException]", e1);
            throw new BusinessException(ErrorCode.ROOT_EXCEPTION);
        } catch (URISyntaxException e2) {
            log.error("error[URISyntaxException]", e2);
            throw new BusinessException(ErrorCode.ROOT_EXCEPTION);
        }
    }

    /**
     * 캐시 만료 검증
     * @return
     */
    private boolean isCacheExpired() {
        long ttl = 24 * 60 * 60 * 1000; // 24시간
        return (System.currentTimeMillis() - lastUpdated) > ttl;
    }

    /**
     * 특정 연도의 휴일 목록 조회
     * @param year
     * @return
     */
    public List<VEvent> getEventsByYear(int year) {
        if (isCacheExpired()) {
            log.info("cache expired, reloading holiday");
            loadAndIndex();
        }

        return eventsByYear.getOrDefault(year, Collections.emptyList());
    }

    /**
     * 휴일 정보가 존재하는 모든 연도 조회
     * @return
     */
    public Set<Integer> getAvailableYears() {
        if (isCacheExpired()) {
            log.info("cache expired, reloading holiday");
            loadAndIndex();
        }

        return eventsByYear.keySet();
    }

    /**
     * 휴일 데이터 강제 새로고침
     */
    public void forceRefresh() {
        log.info("force refreshing holiday");
        loadAndIndex();
    }
}

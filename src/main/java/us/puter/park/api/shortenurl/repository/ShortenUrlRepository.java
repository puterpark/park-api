package us.puter.park.api.shortenurl.repository;

import generated.jooq.obj.tables.ShortenUrl;
import generated.jooq.obj.tables.ShortenUrlAccess;
import generated.jooq.obj.tables.daos.ShortenUrlDao;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import us.puter.park.api.shortenurl.dto.ShortenUrlCountDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ShortenUrlRepository extends ShortenUrlDao {

    private final DSLContext dslContext;
    private static final ShortenUrl SHORTEN_URL = ShortenUrl.SHORTEN_URL;
    private static final ShortenUrlAccess SHORTEN_URL_ACCESS = ShortenUrlAccess.SHORTEN_URL_ACCESS;

    public ShortenUrlRepository(DSLContext dslContext) {
        super(dslContext.configuration());
        this.dslContext = dslContext;
    }

    /**
     * orgUrl > shortenUri 조회
     * @param orgUrl
     * @return
     */
    public String findShortenUriByOrgUrl(String orgUrl) {
        return dslContext
                .select(SHORTEN_URL.SHORTEN_URI)
                .from(SHORTEN_URL)
                .where(SHORTEN_URL.ORG_URL.eq(orgUrl))
                .fetchOneInto(String.class);
    }

    /**
     * shortenUri > 존재 여부
     * @param shortenUri
     * @return
     */
    public boolean existsByShortenUri(String shortenUri) {
        return dslContext.fetchExists(SHORTEN_URL, SHORTEN_URL.SHORTEN_URI.eq(shortenUri));
    }

    /**
     * shortenUri > id, orgUrl 조회
     * @param shortenUri
     * @return
     */
    public ShortenUrlDto findOrgUrlByShortenUri(String shortenUri) {
        return dslContext
                .select(
                        SHORTEN_URL.ID,
                        SHORTEN_URL.ORG_URL
                )
                .from(SHORTEN_URL)
                .where(SHORTEN_URL.SHORTEN_URI.eq(shortenUri))
                .fetchOneInto(ShortenUrlDto.class);
    }

    /**
     * (오늘 - day) 날짜부터 접근 기록 수가 가장 많은 상위 5개 조회
     * @param day
     * @return
     */
    public List<ShortenUrlCountDto> findShortenUrlCountTop5(int day) {
        return dslContext
                .select(
                        SHORTEN_URL.SHORTEN_URI.as("x"),
                        DSL.count(SHORTEN_URL_ACCESS.ID).as("y")
                )
                .from(SHORTEN_URL)
                .join(SHORTEN_URL_ACCESS).on(SHORTEN_URL.ID.eq(SHORTEN_URL_ACCESS.SHORTEN_URL_ID))
                .where(SHORTEN_URL_ACCESS.REG_DATE.greaterOrEqual(LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(day)))
                .groupBy(SHORTEN_URL.ID)
                .orderBy(DSL.count(SHORTEN_URL_ACCESS.ID).desc())
                .limit(5)
                .fetchInto(ShortenUrlCountDto.class);
    }

}

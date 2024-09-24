package us.puter.park.api.shortenurl.repository;

import generated.jooq.obj.tables.ShortenUrl;
import generated.jooq.obj.tables.ShortenUrlAccess;
import generated.jooq.obj.tables.daos.ShortenUrlDao;
import io.micrometer.common.util.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlListReqDto;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlResDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCountDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlResDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
     * shortenUri > 존재 여부 (특정 id 제외)
     * @param shortenUri
     * @param id
     * @return
     */
    public boolean existsByShortenUriNotInId(String shortenUri, String id) {
        return dslContext.fetchExists(
                SHORTEN_URL,
                SHORTEN_URL.SHORTEN_URI.eq(shortenUri)
                        .and(SHORTEN_URL.ID.ne(UUID.fromString(id)))
        );
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

    /**
     * 단축 링크 목록 조회
     * @param reqDto
     * @return
     */
    public Page<ShortenUrlResDto> getShortenUrlList(AdminShortenUrlListReqDto reqDto) {
        Pageable pageable = PageRequest.of(reqDto.start() - 1, reqDto.limit());

        Condition condition = DSL.noCondition();

        if (StringUtils.isNotBlank(reqDto.searchWord())) {
            String searchWord = "%" + reqDto.searchWord() + "%";
            condition = condition.and(
                    SHORTEN_URL.ORG_URL.like(searchWord)
                            .or(SHORTEN_URL.SHORTEN_URI.like(searchWord))
            );
        }

        Integer totalCount = Optional.ofNullable(
                dslContext
                        .selectCount()
                        .from(SHORTEN_URL)
                        .where(condition)
                        .fetchOneInto(Integer.class)
        ).orElse(0);

        List<ShortenUrlResDto> list = dslContext
                .select(
                        SHORTEN_URL.ID,
                        SHORTEN_URL.SHORTEN_URI,
                        SHORTEN_URL.ORG_URL,
                        DSL.max(SHORTEN_URL_ACCESS.REG_DATE).as("lastAccessDate"),
                        SHORTEN_URL.REG_DATE,
                        SHORTEN_URL.MOD_DATE
                )
                .from(SHORTEN_URL)
                .leftJoin(SHORTEN_URL_ACCESS).on(SHORTEN_URL.ID.eq(SHORTEN_URL_ACCESS.SHORTEN_URL_ID))
                .where(condition)
                .groupBy(SHORTEN_URL.ID, SHORTEN_URL.SHORTEN_URI, SHORTEN_URL.ORG_URL, SHORTEN_URL.REG_DATE, SHORTEN_URL.MOD_DATE)
                .orderBy(SHORTEN_URL.ID.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchInto(ShortenUrlResDto.class);

        return new PageImpl<>(list, pageable, totalCount);
    }

    /**
     * id > 단축 링크 조회 (관리자)
     * @param id
     * @return
     */
    public AdminShortenUrlResDto findById(String id) {
        return dslContext
                .select(
                        SHORTEN_URL.SHORTEN_URI,
                        SHORTEN_URL.ORG_URL
                )
                .from(SHORTEN_URL)
                .where(
                        SHORTEN_URL.ID.eq(UUID.fromString(id))
                )
                .fetchOneInto(AdminShortenUrlResDto.class);
    }

    /**
     * 단축 링크 수정
     * @param id
     * @param updateFields
     */
    public void updateShortenUrl(String id, Map<Field<?>, Object> updateFields) {
        dslContext
                .update(SHORTEN_URL)
                .set(updateFields)
                .where(SHORTEN_URL.ID.eq(UUID.fromString(id)))
                .execute();
    }
}

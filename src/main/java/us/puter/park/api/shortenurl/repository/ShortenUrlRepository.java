package us.puter.park.api.shortenurl.repository;

import generated.jooq.obj.tables.ShortenUrl;
import generated.jooq.obj.tables.daos.ShortenUrlDao;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import us.puter.park.api.shortenurl.dto.ShortenUrlDto;

@Repository
public class ShortenUrlRepository extends ShortenUrlDao {

    private final DSLContext dslContext;
    private static final ShortenUrl SHORTEN_URL = ShortenUrl.SHORTEN_URL;

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
}

package us.puter.park.api.shortenurl.repository;

import generated.jooq.obj.tables.ShortenUrlAccess;
import generated.jooq.obj.tables.daos.ShortenUrlAccessDao;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class ShortenUrlAccessRepository extends ShortenUrlAccessDao {

    private final DSLContext dslContext;
    private static final ShortenUrlAccess SHORTEN_URL_ACCESS = ShortenUrlAccess.SHORTEN_URL_ACCESS;

    public ShortenUrlAccessRepository(DSLContext dslContext) {
        super(dslContext.configuration());
        this.dslContext = dslContext;
    }

    /**
     * 오늘 기준 리다이렉트 수 조회
     * @return
     */
    public Integer countTodayRedirect() {
        Integer count =  dslContext
                .selectCount()
                .from(SHORTEN_URL_ACCESS)
                .where(SHORTEN_URL_ACCESS.REG_DATE.greaterOrEqual(LocalDateTime.now().toLocalDate().atStartOfDay()))
                .fetchOneInto(Integer.class);
        return count == null ? 0 : count;
    }

    /**
     * 오늘 기준 가장 접근이 많은 IP 조회
     * @return
     */
    public String findMostAccessIp() {
        String accessIp = dslContext
                .select(
                        SHORTEN_URL_ACCESS.ACCESS_IP
                )
                .from(SHORTEN_URL_ACCESS)
                .where(SHORTEN_URL_ACCESS.REG_DATE.greaterOrEqual(LocalDateTime.now().toLocalDate().atStartOfDay()))
                .groupBy(SHORTEN_URL_ACCESS.ACCESS_IP)
                .limit(1)
                .fetchOneInto(String.class);
        return StringUtils.isBlank(accessIp) ? "x.x.x.x" : accessIp;
    }
}

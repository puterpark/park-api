package us.puter.park.api.shortenurl.repository;

import generated.jooq.obj.tables.ShortenUrlAccess;
import generated.jooq.obj.tables.daos.ShortenUrlAccessDao;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class ShortenUrlAccessRepository extends ShortenUrlAccessDao {

    private final DSLContext dslContext;
    private static final ShortenUrlAccess SHORTEN_URL_ACCESS = ShortenUrlAccess.SHORTEN_URL_ACCESS;

    public ShortenUrlAccessRepository(DSLContext dslContext) {
        super(dslContext.configuration());
        this.dslContext = dslContext;
    }
}

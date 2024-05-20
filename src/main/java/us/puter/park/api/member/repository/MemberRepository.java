package us.puter.park.api.member.repository;

import generated.jooq.obj.tables.Member;
import generated.jooq.obj.tables.daos.MemberDao;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import us.puter.park.api.member.dto.MemberInfoDto;

import java.util.UUID;

@Repository
public class MemberRepository extends MemberDao {

    private final DSLContext dslContext;
    private static final Member MEMBER = Member.MEMBER;

    public MemberRepository(DSLContext dslContext) {
        super(dslContext.configuration());
        this.dslContext = dslContext;
    }

    /**
     * username > 회원 기본 정보 조회
     * @param username
     * @return
     */
    public MemberInfoDto findByUsername(String username) {
        return dslContext
                .select(
                        MEMBER.ID,
                        MEMBER.USERNAME,
                        MEMBER.PASSWORD,
                        MEMBER.ROLE,
                        MEMBER.STATUS
                )
                .from(MEMBER)
                .where(MEMBER.USERNAME.eq(username))
                .fetchOneInto(MemberInfoDto.class);
    }

    /**
     * 고유번호 > 회원 기본 정보 조회
     * @param id
     * @return
     */
    public MemberInfoDto findById(String id) {
        return dslContext
                .select(
                        MEMBER.ID,
                        MEMBER.USERNAME,
                        MEMBER.PASSWORD,
                        MEMBER.ROLE,
                        MEMBER.STATUS
                )
                .from(MEMBER)
                .where(MEMBER.ID.eq(UUID.fromString(id)))
                .fetchOneInto(MemberInfoDto.class);
    }
}

package us.puter.park.api.member.repository;

import generated.jooq.obj.tables.Member;
import generated.jooq.obj.tables.daos.MemberDao;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;
import us.puter.park.api.member.dto.MemberInfoDto;
import us.puter.park.api.member.dto.MemberTokenInfoDto;

import java.util.Map;
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

    /**
     * username > 중복 여부
     * @param username
     * @return
     */
    public boolean existsByUsername(String username) {
        return dslContext.fetchExists(MEMBER, MEMBER.USERNAME.eq(username));
    }

    /**
     * 회원 수정
     * @param id
     * @param updateField
     */
    public void update(String id, Map<Field<?>, Object> updateField) {
        dslContext.update(MEMBER)
                .set(updateField)
                .where(MEMBER.ID.eq(UUID.fromString(id)))
                .execute();
    }

    /**
     * refreshToken > 고유번호, username, role 조회
     * @param refreshToken
     * @return
     */
    public MemberTokenInfoDto findByRefreshToken(String refreshToken) {
        return dslContext
                .select(
                        MEMBER.ID,
                        MEMBER.USERNAME,
                        MEMBER.ROLE
                )
                .from(MEMBER)
                .where(MEMBER.REFRESH_TOKEN.eq(refreshToken))
                .fetchOneInto(MemberTokenInfoDto.class);
    }
}

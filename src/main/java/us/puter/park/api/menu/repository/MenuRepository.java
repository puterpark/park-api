package us.puter.park.api.menu.repository;

import generated.jooq.obj.tables.Menu;
import generated.jooq.obj.tables.daos.MenuDao;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import us.puter.park.api.menu.dto.MenuDto;

import java.util.List;

import static org.jooq.impl.DSL.max;

@Repository
public class MenuRepository extends MenuDao {

    private final DSLContext dslContext;
    private static final Menu MENU = Menu.MENU;

    public MenuRepository(DSLContext dslContext) {
        super(dslContext.configuration());
        this.dslContext = dslContext;
    }

    /**
     * uri > 존재 여부
     * @param uri
     * @return
     */
    public boolean existsByUri(String uri) {
        return dslContext.fetchExists(MENU, MENU.URI.eq(uri));
    }

    /**
     * 정렬순서 현재 최댓값 조회
     * @return
     */
    public Integer findMaxSortOrder() {
        Integer maxSortOrder = dslContext
                .select(max(MENU.SORT_ORDER))
                .from(MENU)
                .fetchOneInto(Integer.class);
        return maxSortOrder == null ? 9999 : maxSortOrder;
    }

    /**
     * useYn > 메뉴 목록 조회
     * @param useYn
     * @return
     */
    public List<MenuDto> findAllByUseYn(String useYn) {
        return dslContext
                .select(
                        MENU.NAME,
                        MENU.MODE,
                        MENU.URI,
                        MENU.ICON
                )
                .from(MENU)
                .where(MENU.USE_YN.eq(useYn))
                .orderBy(MENU.SORT_ORDER.asc())
                .fetchInto(MenuDto.class);
    }

    /**
     * mode > 존재 여부
     * @param mode
     * @return
     */
    public boolean existsByMode(String mode) {
        return dslContext.fetchExists(MENU, MENU.MODE.eq(mode));
    }
}

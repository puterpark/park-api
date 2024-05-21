package us.puter.park.api.menu.service;

import com.github.f4b6a3.uuid.UuidCreator;
import generated.jooq.obj.tables.pojos.Menu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import us.puter.park.api.menu.dto.MenuCreateReqDto;
import us.puter.park.api.menu.dto.MenuDto;
import us.puter.park.api.menu.repository.MenuRepository;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;

    /**
     * 메뉴 추가
     * @param reqDto
     */
    @Transactional
    public void insertMenu(MenuCreateReqDto reqDto) {
        // uri 중복 확인
        if (menuRepository.existsByUri(reqDto.uri())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_DATA, "uri");
        }

        // 메뉴 추가
        Menu menu = Menu.builder()
                .id(UuidCreator.getTimeOrdered())
                .name(reqDto.name())
                .mode(reqDto.mode())
                .uri(reqDto.uri())
                .icon(reqDto.icon())
                .sortOrder(menuRepository.findMaxSortOrder() + 1)
                .useYn(reqDto.useYn())
                .regDate(LocalDateTime.now())
                .build();
        menuRepository.insert(menu);
        log.info("insert menu: name[{}], uri[{}]", menu.name(), menu.uri());
    }

    // 메뉴 목록 > model 설정
    @Transactional(readOnly = true)
    public void setModelFromMenu(Model model, String mode) {
        List<MenuDto> menuList = menuRepository.findAllByUseYn("Y");
        model.addAttribute("menuList", menuList);
        model.addAttribute("mode", mode);
    }

}

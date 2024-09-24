package us.puter.park.api.menu.service;

import com.github.f4b6a3.uuid.UuidCreator;
import generated.jooq.obj.tables.pojos.Menu;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import us.puter.park.api.menu.dto.MenuCreateReqDto;
import us.puter.park.api.menu.dto.MenuDto;
import us.puter.park.api.menu.repository.MenuRedisRepository;
import us.puter.park.api.menu.repository.MenuRepository;
import us.puter.park.common.config.CommonVariables;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;
import us.puter.park.util.IpUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuRedisRepository menuRedisRepository;

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
    public void setModelFromMenu(HttpServletRequest request, Model model, String mode) {
        List<MenuDto> menuList;
        if (StringUtils.equals("prod", CommonVariables.profile)) {
            // redis에서 먼저 메뉴 목록 조회
            menuList = menuRedisRepository.findMenuList();
            if (CollectionUtils.isEmpty(menuList)) {
                // redis에 없으면 DB에서 조회
                menuList = menuRepository.findAllByUseYn("Y");
                menuRedisRepository.cacheMenuList(menuList); // 캐시 저장
            }
        } else {
            menuList = menuRepository.findAllByUseYn("Y");
        }

        model.addAttribute("menuList", menuList);
        model.addAttribute("mode", mode);
        model.addAttribute("title", CommonVariables.title);
        model.addAttribute("ip", IpUtils.getRemoteIP(request));
    }

    // 도구 페이지 설정
    @Transactional(readOnly = true)
    public String setToolsPage(String mode, Model model, HttpServletRequest request) {
        // mode 존재 여부
        if(!menuRepository.existsByMode(mode)) {
            return "redirect:/error/404";
        }

        setModelFromMenu(request, model, mode);

        if (StringUtils.equals("qrcode", mode)) {
            model.addAttribute("qrCodeSize", CommonVariables.qrCodeSize);
        }

        return "tools/" + mode;
    }

}

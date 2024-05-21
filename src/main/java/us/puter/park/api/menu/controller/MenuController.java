package us.puter.park.api.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.menu.dto.MenuCreateReqDto;
import us.puter.park.api.menu.service.MenuService;
import us.puter.park.common.http.CommonHttpResultDto;
import us.puter.park.common.http.HttpApiResponse;

@RestController
@RequiredArgsConstructor
@Validated
public class MenuController {

    private final MenuService menuService;

    // 메뉴 추가
    @PostMapping(value = "/api/v1/admin/menu")
    public CommonHttpResultDto postAdminMenu(
            @RequestBody @Valid MenuCreateReqDto reqDto
    ) {
        menuService.insertMenu(reqDto);
        return HttpApiResponse.getSuccessResponse();
    }
}

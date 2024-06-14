package us.puter.park.api.page.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import us.puter.park.api.menu.service.MenuService;
import us.puter.park.common.config.jwt.TokenProvider;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final MenuService menuService;
    private final TokenProvider tokenProvider;

    // 메인 페이지
    @GetMapping("/")
    public String getIndex(
            Model model
            , HttpServletRequest request
    ) {
        menuService.setModelFromMenu(request, model, "home");
        return "index/index";
    }

    // 도구 페이지
    @GetMapping("/tools/{mode}")
    public String getToolsMode(
            @PathVariable String mode
            , Model model
            , HttpServletRequest request
    ) {
        return menuService.setToolsPage(mode, model, request);
    }

    // robots.txt 페이지
    @GetMapping("/robots.txt")
    public String getRobotsTxt() {
        return "robots.txt";
    }

    // 관리자 로그인 페이지
    @GetMapping("/admin")
    public String getAdmin(
            Model model
            , HttpServletRequest request
    ) {
        String accessToken = tokenProvider.getCookieValue(request, "accessToken");
        if (tokenProvider.validateToken(accessToken, request, "/admin")) {
            return "redirect:/admin/shorten-url";
        }

        menuService.setModelFromMenu(request, model, null);
        return "admin/login";
    }

    // 관리자 단축 링크 통계 페이지
    @GetMapping("/admin/shorten-url")
    public String getAdminTest(
            Model model
            , HttpServletRequest request
    ) {
        String accessToken = tokenProvider.getCookieValue(request, "accessToken");
        if (!tokenProvider.validateToken(accessToken, request, "/admin/shorten-url")) {
            return "redirect:/";
        }

        menuService.setModelFromMenu(request, model, null);
        return "admin/shorten-url";
    }
}

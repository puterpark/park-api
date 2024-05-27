package us.puter.park.api.page.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import us.puter.park.api.menu.service.MenuService;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final MenuService menuService;

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
}

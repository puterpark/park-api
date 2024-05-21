package us.puter.park.api.page.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import us.puter.park.common.config.CommonVariables;

@Controller
public class CustomErrorController implements ErrorController {

    // 500 에러 전용
    @GetMapping(value = "/error")
    public String getError(
            Model model
    ) {
        model.addAttribute("title", CommonVariables.title);
        return "error/500";
    }

    // 404 에러 전용
    @GetMapping(value = "/error/404")
    public String getError404(
            Model model
    ) {
        model.addAttribute("title", CommonVariables.title);
        return "error/404";
    }
}

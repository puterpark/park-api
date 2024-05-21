package us.puter.park.api.page.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import us.puter.park.common.config.CommonVariables;
import us.puter.park.util.IpUtils;

@Controller
@RequiredArgsConstructor
public class PageController {

    @GetMapping("/")
    public String getIndex(Model model, HttpServletRequest request) {

        model.addAttribute("title", CommonVariables.title);
        model.addAttribute("mode", "home");
        model.addAttribute("ip", IpUtils.getRemoteIP(request));

        return "index/index";
    }

}

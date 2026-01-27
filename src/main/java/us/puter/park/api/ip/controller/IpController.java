package us.puter.park.api.ip.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.ip.dto.IpResDto;
import us.puter.park.api.ip.service.IpService;
import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;

@RestController
@RequiredArgsConstructor
@Validated
public class IpController {

    private final IpService ipService;

    // request IP 조회
    @GetMapping(value = "/api/v1/ip")
    public HttpResponseDto<IpResDto> getIp(
            HttpServletRequest request
    ) {
        return HttpApiResponse.getSuccessResponse(ipService.getIp(request));
    }
}

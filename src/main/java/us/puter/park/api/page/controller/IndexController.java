package us.puter.park.api.page.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.page.dto.HealthCheckResDto;
import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@RestController
public class IndexController {

    // 헬스 체크
    @GetMapping("/health-check")
    public HttpResponseDto<HealthCheckResDto> index() throws UnknownHostException {
        return HttpApiResponse.getSuccessResponse(
                HealthCheckResDto.builder()
                        .now(LocalDateTime.now())
                        .hostname(InetAddress.getLocalHost().getHostName())
                        .greeting("Hello, park-api!")
                        .build()
        );
    }
}

package us.puter.park.api.shortenurl.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlStatisticResDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCreateReqDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCreateResDto;
import us.puter.park.api.shortenurl.service.ShortenUrlService;
import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;

@RestController
@RequiredArgsConstructor
@Validated
public class ShortenUrlController {

    private final ShortenUrlService shortenUrlService;

    // 짧은 링크 추가
    @PostMapping(value = "/api/v1/shorten-url")
    public HttpResponseDto<ShortenUrlCreateResDto> postShortenUrl(
            @RequestBody @Valid ShortenUrlCreateReqDto reqDto
    ) {
        return HttpApiResponse.getSuccessResponse(shortenUrlService.insertShortenUrl(reqDto));
    }

    // orgUrl 리다이렉트
    @GetMapping(value = "/{shortenUri}")
    public void getShortenUri(
            @PathVariable String shortenUri
            , HttpServletRequest req
            , HttpServletResponse res
    ) {
        shortenUrlService.redirectOrgUrl(shortenUri, req, res);
    }

    // 짧은 링크 통계 (관리자)
    @GetMapping(value = "/api/v1/admin/shorten-url/statistic")
    public HttpResponseDto<AdminShortenUrlStatisticResDto> getAdminShortenUrlStatistic() {
        return HttpApiResponse.getSuccessResponse(shortenUrlService.getShortenUrlStatistic());
    }
}

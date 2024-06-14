package us.puter.park.api.shortenurl.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlListReqDto;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlListResDto;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlResDto;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlStatisticResDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCreateReqDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCreateResDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlUpdateReqDto;
import us.puter.park.api.shortenurl.service.ShortenUrlService;
import us.puter.park.common.config.CommonPatterns;
import us.puter.park.common.http.CommonHttpResultDto;
import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;

@RestController
@RequiredArgsConstructor
@Validated
public class ShortenUrlController {

    private final ShortenUrlService shortenUrlService;

    // 단축 링크 추가
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

    // 단축 링크 통계 (관리자)
    @GetMapping(value = "/api/v1/admin/shorten-url/statistic")
    public HttpResponseDto<AdminShortenUrlStatisticResDto> getAdminShortenUrlStatistic() {
        return HttpApiResponse.getSuccessResponse(shortenUrlService.getShortenUrlStatistic());
    }

    // 단축 링크 목록 조회 (관리자)
    @GetMapping(value = "/api/v1/admin/shorten-url/list")
    public HttpResponseDto<AdminShortenUrlListResDto> getAdminShortenUrlList(
            @Valid AdminShortenUrlListReqDto reqDto
    ) {
        return HttpApiResponse.getSuccessResponse(shortenUrlService.getShortenUrlList(reqDto));
    }

    // 단축 링크 조회 (관리자)
    @GetMapping(value = "/api/v1/admin/shorten-url/{id}")
    public HttpResponseDto<AdminShortenUrlResDto> getAdminShortenUrlId(
            @PathVariable @Valid @NotBlank @Pattern(regexp = CommonPatterns.UUID) String id
    ) {
        return HttpApiResponse.getSuccessResponse(shortenUrlService.getShortenUrl(id));
    }

    // 단축 링크 수정 (관리자)
    @PatchMapping(value = "/api/v1/admin/shorten-url/{id}")
    public CommonHttpResultDto patchAdminShortenUrlId(
            @PathVariable @Valid @NotBlank @Pattern(regexp = CommonPatterns.UUID) String id,
            @RequestBody @Valid ShortenUrlUpdateReqDto reqDto
    ) {
        shortenUrlService.updateShortenUrl(id, reqDto);
        return HttpApiResponse.getSuccessResponse();
    }

    // 단축 링크 삭제 (관리자)
    @DeleteMapping(value = "/api/v1/admin/shorten-url/{id}")
    public CommonHttpResultDto deleteAdminShortenUrlId(
            @PathVariable @Valid @NotBlank @Pattern(regexp = CommonPatterns.UUID) String id
    ) {
        shortenUrlService.deleteShortenUrl(id);
        return HttpApiResponse.getSuccessResponse();
    }
}

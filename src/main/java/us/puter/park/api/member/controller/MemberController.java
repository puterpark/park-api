package us.puter.park.api.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.member.dto.LoginReqDto;
import us.puter.park.api.member.dto.TokenResDto;
import us.puter.park.api.member.dto.MemberCreateReqDto;
import us.puter.park.api.member.service.MemberService;
import us.puter.park.common.http.CommonHttpResultDto;
import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;

@RestController
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    // 회원 추가
    @PostMapping(value = "/api/v1/admin/member")
    public CommonHttpResultDto postMember(
            @RequestBody @Valid MemberCreateReqDto reqDto
    ) {
        memberService.insertMember(reqDto);
        return HttpApiResponse.getSuccessResponse();
    }

    // 회원 로그인
    @PostMapping(value = "/api/v1/member/login")
    public HttpResponseDto<TokenResDto> postMemberLogin(
            @RequestBody @Valid LoginReqDto reqDto
            , HttpServletResponse response
    ) {
        return HttpApiResponse.getSuccessResponse(memberService.login(reqDto, response));
    }

    // accessToken 재발급
    @PostMapping(value = "/api/v1/member/refresh")
    public HttpResponseDto<TokenResDto> postMemberRefresh(
            HttpServletRequest request
            , HttpServletResponse response
    ) {
        return HttpApiResponse.getSuccessResponse(memberService.refreshAccessToken(request, response));
    }
}

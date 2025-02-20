package us.puter.park.api.jasypt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.jasypt.dto.JasyptDecReqDto;
import us.puter.park.api.jasypt.dto.JasyptDecResDto;
import us.puter.park.api.jasypt.dto.JasyptEncReqDto;
import us.puter.park.api.jasypt.dto.JasyptEncResDto;
import us.puter.park.api.jasypt.service.JasyptService;
import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;

@RestController
@RequiredArgsConstructor
@Validated
public class JasyptController {

    private final JasyptService jasyptService;

    // jasypt 암호화
    @PostMapping("/api/v1/jasypt/encrypt")
    public HttpResponseDto<JasyptEncResDto> postJasyptEncrypt(
            @RequestBody @Valid JasyptEncReqDto reqDto
    ) {
        return HttpApiResponse.getSuccessResponse(jasyptService.encrypt(reqDto));
    }

    // jasypt 복호화
    @PostMapping("/api/v1/jasypt/decrypt")
    public HttpResponseDto<JasyptDecResDto> postJasyptDecrypt(
            @RequestBody @Valid JasyptDecReqDto reqDto
    ) {
        return HttpApiResponse.getSuccessResponse(jasyptService.decrypt(reqDto));
    }

}

package us.puter.park.api.aes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.puter.park.api.aes.dto.AesDecReqDto;
import us.puter.park.api.aes.dto.AesDecResDto;
import us.puter.park.api.aes.dto.AesEncReqDto;
import us.puter.park.api.aes.dto.AesEncResDto;
import us.puter.park.api.aes.service.AesService;
import us.puter.park.common.http.HttpApiResponse;
import us.puter.park.common.http.HttpResponseDto;

@RestController
@RequiredArgsConstructor
@Validated
public class AesController {

    private final AesService aesService;

    // AES256 암호화
    @PostMapping("/api/v1/aes/encrypt")
    public HttpResponseDto<AesEncResDto> postAesEncrypt(
            @RequestBody @Valid AesEncReqDto reqDto
    ) {
        return HttpApiResponse.getSuccessResponse(aesService.encrypt(reqDto));
    }

    // AES256 복호화
    @PostMapping("/api/v1/aes/decrypt")
    public HttpResponseDto<AesDecResDto> postAesDecrypt(
            @RequestBody @Valid AesDecReqDto reqDto
    ) {
        return HttpApiResponse.getSuccessResponse(aesService.decrypt(reqDto));
    }

}

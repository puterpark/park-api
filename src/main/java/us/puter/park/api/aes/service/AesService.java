package us.puter.park.api.aes.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import us.puter.park.api.aes.dto.AesDecReqDto;
import us.puter.park.api.aes.dto.AesDecResDto;
import us.puter.park.api.aes.dto.AesEncReqDto;
import us.puter.park.api.aes.dto.AesEncResDto;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;
import us.puter.park.util.AesUtils;

@Service
@Slf4j
public class AesService {

    /**
     * aes 암호화
     * @param reqDto
     * @return
     */
    public AesEncResDto encrypt(AesEncReqDto reqDto) {
        String secretKey = reqDto.secretKey();
        int secretKeyLength = secretKey.length();
        if (secretKeyLength != 16 && secretKeyLength != 24 && secretKeyLength != 32) {
            throw new BusinessException(ErrorCode.VALIDATION_FAIL, "secretKey");
        }

        String encText = AesUtils.encrypt(reqDto.plainText(), secretKey, reqDto.iv(), StringUtils.equals("Y", reqDto.useBase64()));

        return AesEncResDto.builder()
                .encText(encText)
                .build();
    }

    /**
     * aes 복호화
     * @param reqDto
     * @return
     */
    public AesDecResDto decrypt(AesDecReqDto reqDto) {
        String secretKey = reqDto.secretKey();
        int secretKeyLength = secretKey.length();
        if (secretKeyLength != 16 && secretKeyLength != 24 && secretKeyLength != 32) {
            throw new BusinessException(ErrorCode.VALIDATION_FAIL, "secretKey");
        }

        String decText = AesUtils.decrypt(reqDto.encText(), secretKey, reqDto.iv(), StringUtils.equals("Y", reqDto.useBase64()));

        return AesDecResDto.builder()
                .decText(decText)
                .build();
    }
}


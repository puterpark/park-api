package us.puter.park.api.jasypt.service;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.stereotype.Service;
import us.puter.park.api.jasypt.dto.JasyptDecReqDto;
import us.puter.park.api.jasypt.dto.JasyptDecResDto;
import us.puter.park.api.jasypt.dto.JasyptEncReqDto;
import us.puter.park.api.jasypt.dto.JasyptEncResDto;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;

@Service
@Slf4j
public class JasyptService {

    /**
     * jasypt 초기화
     * @param secretKey
     * @return
     */
    private StringEncryptor stringEncryptor(String secretKey) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(secretKey);
        config.setPoolSize("1");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setStringOutputType("base64");
        config.setKeyObtentionIterations("1000");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        encryptor.setConfig(config);
        return encryptor;
    }

    /**
     * jasypt 암호화
     * @param reqDto
     * @return
     */
    public JasyptEncResDto encrypt(JasyptEncReqDto reqDto) {
        return JasyptEncResDto.builder()
                .encText(stringEncryptor(reqDto.secretKey()).encrypt(reqDto.plainText()))
                .build();
    }

    /**
     * jasypt 복호화
     * @param reqDto
     * @return
     */
    public JasyptDecResDto decrypt(JasyptDecReqDto reqDto) {
        try {
            return JasyptDecResDto.builder()
                    .decText(stringEncryptor(reqDto.secretKey()).decrypt(reqDto.encText()))
                    .build();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_SECRET_KEY);
        }
    }
}

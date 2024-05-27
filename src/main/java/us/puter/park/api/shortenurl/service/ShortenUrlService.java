package us.puter.park.api.shortenurl.service;

import com.github.f4b6a3.uuid.UuidCreator;
import generated.jooq.obj.tables.pojos.ShortenUrl;
import generated.jooq.obj.tables.pojos.ShortenUrlAccess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.puter.park.api.shortenurl.dto.IpInfoDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCreateReqDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCreateResDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlDto;
import us.puter.park.api.shortenurl.repository.ShortenUrlAccessRepository;
import us.puter.park.api.shortenurl.repository.ShortenUrlRepository;
import us.puter.park.common.config.CommonVariables;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;
import us.puter.park.util.IpUtils;
import us.puter.park.util.RestClientUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortenUrlService {

    private final RestClientUtils restClientUtils;
    private final ShortenUrlRepository shortenUrlRepository;
    private final ShortenUrlAccessRepository shortenUrlAccessRepository;

    /**
     * shortenUrl 추가
     * @param reqDto
     * @return
     */
    @Transactional
    public ShortenUrlCreateResDto insertShortenUrl(ShortenUrlCreateReqDto reqDto) {
        String originalUrl = reqDto.orgUrl();

        // orgUrl 중복 확인
        String shortenUri = shortenUrlRepository.findShortenUriByOrgUrl(originalUrl);
        if (StringUtils.isBlank(shortenUri)) {
            // shortenUri 생성
            do {
                shortenUri = RandomStringUtils.randomAlphanumeric(CommonVariables.shortenUriLength);
            } while (shortenUrlRepository.existsByShortenUri(shortenUri));

            // shortenUrl 추가
            ShortenUrl shortenUrl = ShortenUrl.builder()
                    .id(UuidCreator.getTimeOrdered())
                    .orgUrl(originalUrl)
                    .shortenUri(shortenUri)
                    .regDate(LocalDateTime.now())
                    .build();
            shortenUrlRepository.insert(shortenUrl);
            log.info("insert shortenUrl: shortenUri[{}], orgUrl[{}]", shortenUri, originalUrl);
        }

        return ShortenUrlCreateResDto.builder()
                .shortenUri(shortenUri)
                .build();
    }

    /**
     * orgUrl redirect
     * @param shortenUri
     * @param req
     * @param res
     */
    @Transactional
    public void redirectOrgUrl(String shortenUri, HttpServletRequest req, HttpServletResponse res) {
        // 짧은 링크 기본 정보 조회
        ShortenUrlDto urlDto = shortenUrlRepository.findOrgUrlByShortenUri(shortenUri);
        if (urlDto == null || StringUtils.isBlank(urlDto.orgUrl())) {
            log.info("not found shortenUri[{}]", shortenUri);
            try {
                res.sendRedirect("/error/404");
                return;
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        String orgUrl = urlDto.orgUrl();
        String ip = IpUtils.getRemoteIP(req);
        
        // IP 정보 조회
        String url = MessageFormat.format("http://ip-api.com/json/{0}?fields=50943", ip);
        IpInfoDto ipDto = restClientUtils.get(url, IpInfoDto.class);

        // 짧은링크접근 기본 정보 설정
        ShortenUrlAccess.ShortenUrlAccessBuilder builder = ShortenUrlAccess.builder()
                .id(UuidCreator.getTimeOrdered())
                .shortenUrlId(urlDto.id())
                .accessIp(ip)
                .regDate(LocalDateTime.now());

        // IP 정보 설정
        String status = ipDto.status();
        if (StringUtils.equals("success", status)) {
            builder.country(ipDto.country())
                    .countryCode(ipDto.countryCode())
                    .region(ipDto.region())
                    .regionName(ipDto.regionName())
                    .city(ipDto.city())
                    .zip(ipDto.zip())
                    .lat(ipDto.lat())
                    .lon(ipDto.lon())
                    .isp(ipDto.isp())
                    .org(ipDto.org());
        }
        shortenUrlAccessRepository.insert(builder.build());

        try {
            res.sendRedirect(orgUrl);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

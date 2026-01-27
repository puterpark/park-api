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
import org.jooq.Field;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlListReqDto;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlListResDto;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlResDto;
import us.puter.park.api.shortenurl.dto.AdminShortenUrlStatisticResDto;
import us.puter.park.api.shortenurl.dto.IpInfoDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCreateReqDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlCreateResDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlResDto;
import us.puter.park.api.shortenurl.dto.ShortenUrlUpdateReqDto;
import us.puter.park.api.shortenurl.repository.ShortenUrlAccessRepository;
import us.puter.park.api.shortenurl.repository.ShortenUrlRedisRepository;
import us.puter.park.api.shortenurl.repository.ShortenUrlRepository;
import us.puter.park.common.config.CommonVariables;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;
import us.puter.park.util.IpUtils;
import us.puter.park.util.RestClientUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortenUrlService {

    private final RestClientUtils restClientUtils;
    private final ShortenUrlRepository shortenUrlRepository;
    private final ShortenUrlAccessRepository shortenUrlAccessRepository;
    private final ShortenUrlRedisRepository shortenUrlRedisRepository;

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
        // 단축 링크 기본 정보 조회
        ShortenUrlDto urlDto = shortenUrlRedisRepository.findByShortenUri(shortenUri)
                .orElseGet(() -> {
                    ShortenUrlDto shortenUrlDto = shortenUrlRepository.findOrgUrlByShortenUri(shortenUri);
                    if (shortenUrlDto != null) {
                        shortenUrlRedisRepository.save(shortenUri, shortenUrlDto);
                    }
                    return shortenUrlDto;
                });

        if (urlDto == null || StringUtils.isBlank(urlDto.orgUrl())) {
            log.info("not found shortenUri[{}]", shortenUri);
            try {
                res.sendRedirect("https://puter.us/error/404");
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

        // 단축링크접근 기본 정보 설정
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

    /**
     * 단축 링크 통계 조회
     * @return
     */
    @Transactional(readOnly = true)
    public AdminShortenUrlStatisticResDto getShortenUrlStatistic() {
        return AdminShortenUrlStatisticResDto.builder()
                .todayRedirectCount(shortenUrlAccessRepository.countTodayRedirect())
                .todayMostAccessIp(shortenUrlAccessRepository.findMostAccessIp())
                .todayTop5(shortenUrlRepository.findShortenUrlCountTop5(0))
                .weekTop5(shortenUrlRepository.findShortenUrlCountTop5(-7))
                .monthTop5(shortenUrlRepository.findShortenUrlCountTop5(-30))
                .build();
    }

    /**
     * 단축 링크 목록 조회
     * @param reqDto
     * @return
     */
    @Transactional(readOnly = true)
    public AdminShortenUrlListResDto getShortenUrlList(AdminShortenUrlListReqDto reqDto) {
        Page<ShortenUrlResDto> page = shortenUrlRepository.getShortenUrlList(reqDto);

        return new AdminShortenUrlListResDto(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber() + 1
        );
    }

    /**
     * 단축 링크 조회
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public AdminShortenUrlResDto getShortenUrl(String id) {
        AdminShortenUrlResDto resDto = shortenUrlRepository.findById(id);
        if (resDto == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_SHORTEN_URL);
        }

        return resDto;
    }

    /**
     * 단축 링크 수정
     * @param id
     * @param reqDto
     */
    @Transactional
    public void updateShortenUrl(String id, ShortenUrlUpdateReqDto reqDto) {
        // 단축 링크 조회
        ShortenUrl shortenUrl = shortenUrlRepository.findById(UUID.fromString(id));
        if (shortenUrl == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_SHORTEN_URL);
        }

        String shortenUri = reqDto.shortenUri();
        String orgUrl = reqDto.orgUrl();

        // 단축 링크 중복 검증
        if (shortenUrlRepository.existsByShortenUriNotInId(shortenUri, id)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_DATA, "shortenUri");
        }

        // 단축 링크 수정
        final generated.jooq.obj.tables.ShortenUrl SHORTEN_URL = generated.jooq.obj.tables.ShortenUrl.SHORTEN_URL;
        Map<Field<?>, Object> updateFields = new HashMap<>();
        updateFields.put(SHORTEN_URL.SHORTEN_URI, shortenUri);
        updateFields.put(SHORTEN_URL.ORG_URL, orgUrl);
        updateFields.put(SHORTEN_URL.MOD_DATE, LocalDateTime.now());
        shortenUrlRepository.updateShortenUrl(id, updateFields);
        log.info("update shortenUrl: shortenUri[{}], orgUrl[{}]", shortenUri, orgUrl);

        // redis cache 삭제
        shortenUrlRedisRepository.delete(shortenUrl.shortenUri());
    }

    /**
     * 단축 링크 삭제
     * @param id
     */
    @Transactional
    public void deleteShortenUrl(String id) {
        UUID shortenUrlId = UUID.fromString(id);

        // 단축 링크 조회
        ShortenUrl shortenUrl = shortenUrlRepository.findById(shortenUrlId);
        if (shortenUrl == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_SHORTEN_URL);
        }

        // 단축 링크 삭제
        shortenUrlRepository.deleteById(shortenUrlId);
        log.info("delete shortenUrl: id[{}]", id);

        // redis cache 삭제
        shortenUrlRedisRepository.delete(shortenUrl.shortenUri());
    }
}

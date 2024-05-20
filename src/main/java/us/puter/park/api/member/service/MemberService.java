package us.puter.park.api.member.service;

import com.github.f4b6a3.uuid.UuidCreator;
import generated.jooq.obj.tables.pojos.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.puter.park.api.member.domain.PrincipalDetails;
import us.puter.park.api.member.dto.LoginReqDto;
import us.puter.park.api.member.dto.MemberCreateReqDto;
import us.puter.park.api.member.dto.MemberTokenInfoDto;
import us.puter.park.api.member.dto.TokenDto;
import us.puter.park.api.member.dto.TokenResDto;
import us.puter.park.api.member.repository.MemberRepository;
import us.puter.park.common.config.CommonVariables;
import us.puter.park.common.config.jwt.TokenProvider;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    /**
     * 회원 추가
     * @param reqDto
     */
    @Transactional
    public void insertMember(MemberCreateReqDto reqDto) {
        String username = reqDto.username();
        if (memberRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_DATA, "username");
        }

        // 회원 추가
        Member member = Member.builder()
                .id(UuidCreator.getTimeOrdered())
                .username(username)
                .password(passwordEncoder.encode(reqDto.password()))
                .name(reqDto.name())
                .role(reqDto.role().getValue())
                .status(reqDto.status().getValue())
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();
        memberRepository.insert(member);
        log.info("insert member: username[{}]", member.username());
    }

    /**
     * 회원 로그인
     * @param reqDto
     * @param response
     * @return
     */
    @Transactional
    public TokenResDto login(LoginReqDto reqDto, HttpServletResponse response) {
        String username = reqDto.username();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, reqDto.password());
        Authentication authentication = setAuthentication(authenticationToken);
        if (authentication == null) {
            throw new BusinessException(ErrorCode.AUTH_FAIL, MessageFormat.format("username[{0}]", username));
        }

        // 토큰 발급
        PrincipalDetails details = (PrincipalDetails) authentication.getPrincipal();
        TokenDto tokenDto = tokenProvider.generateTokenDto(details);

        String refreshToken = tokenDto.refreshToken();

        // refreshToken 저장
        final generated.jooq.obj.tables.Member MEMBER = generated.jooq.obj.tables.Member.MEMBER;
        Map<Field<?>, Object> updateField = new HashMap<>();
        updateField.put(MEMBER.REFRESH_TOKEN, refreshToken);
        updateField.put(MEMBER.LAST_LOGIN_DATE, LocalDateTime.now());
        updateField.put(MEMBER.MOD_DATE, LocalDateTime.now());
        memberRepository.update(details.id(), updateField);

        setRefreshTokenAtCookie(response, refreshToken); // httpOnly secure로 쿠키로 저장

        return TokenResDto.builder()
                .accessToken(tokenDto.accessToken())
                .build();
    }

    /**
     * UsernamePasswordAuthenticationToken을 통한 Authentication 설정
     * @param authenticationToken
     * @return
     */
    private Authentication setAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        Authentication authentication = null;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            log.error("wrong id/pw: id[{}]", authenticationToken.getName());
        } catch (Exception e) {
            log.error("error[set authentication]", e);
        }
        return authentication;
    }

    /**
     * 쿠키에 refreshToken 설정
     * @param response
     * @param refreshToken
     */
    private void setRefreshTokenAtCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie responseCookie =
                ResponseCookie.from("refreshToken", refreshToken)
                        .maxAge(CommonVariables.refreshTokenValidSecond)
                        .path("/")
                        .secure(true)
                        .httpOnly(true)
                        .sameSite("None")
                        .build();

        response.setHeader("Set-Cookie", responseCookie.toString());
    }

    /**
     * accessToken 재발급
     * @param request
     * @param response
     * @return
     */
    @Transactional(readOnly = true)
    public TokenResDto refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);

        // refreshToken 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            // 만료 처리
            Cookie refreshTokenCookie = new Cookie("refreshToken", "");
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // refreshToken > username, role 조회
        MemberTokenInfoDto infoDto = memberRepository.findByRefreshToken(refreshToken);
        if (infoDto == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 재발급
        String accessToken = tokenProvider.recreateAccessToken(infoDto.id().toString(), infoDto.role().getValue());

        return TokenResDto.builder()
                .accessToken(accessToken)
                .build();
    }

    /**
     * request > refreshToken 조회
     * @param request
     * @return
     */
    private String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null || request.getCookies().length == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .map(Cookie::getValue).findFirst()
                .orElse("");

        if (StringUtils.isBlank(refreshToken)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        return refreshToken;
    }
}

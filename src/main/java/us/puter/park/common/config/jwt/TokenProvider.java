package us.puter.park.common.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import us.puter.park.api.member.domain.PrincipalDetails;
import us.puter.park.api.member.dto.TokenDto;
import us.puter.park.api.member.service.CustomUserDetailsService;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private Key key;

    private final String secret;
    private final long accessTokenValidSecond;
    private final long refreshTokenValidSecond;
    private final CustomUserDetailsService customUserDetailsService;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-valid-second}") long accessTokenValidSecond,
            @Value("${jwt.refresh-token-valid-second}") long refreshTokenValidSecond,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.secret = secret;
        this.accessTokenValidSecond = accessTokenValidSecond * 1000;
        this.refreshTokenValidSecond = refreshTokenValidSecond * 1000;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * accessToken, refreshToken 발급
     * @param details
     * @return
     */
    public TokenDto generateTokenDto(PrincipalDetails details) {
        return createTokens(details.id(), details.role());
    }

    /**
     * accessToken 발급
     * @param id
     * @param authorities
     * @return
     */
    private TokenDto createTokens(String id, String authorities) {
        String accessToken = createAccessToken(id, authorities);
        String refreshToken = createRefreshToken(id);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * accessToken 재발급
     * @param id
     * @param authorities
     * @return
     */
    public String recreateAccessToken(String id, String authorities) {
        return createAccessToken(id, authorities);
    }

    /**
     * accessToken 생성
     * @param id
     * @param authorities
     * @return
     */
    private String createAccessToken(String id, String authorities) {
        long now = (new Date()).getTime();
        Date accessTokenValidity = new Date(now + this.accessTokenValidSecond);
        
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(accessTokenValidity)
                .compact();
    }

    /**
     * refreshToken 생성
     * @param id
     * @return
     */
    private String createRefreshToken(String id) {
        long now = (new Date()).getTime();
        Date refreshTokenValidity = new Date(now + this.refreshTokenValidSecond);
        
        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(refreshTokenValidity)
                .compact();
    }

    /**
     * accessToken > 인가 정보 조회
     * @param accessToken
     * @return
     */
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UserDetails principal = customUserDetailsService.loadUserById(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    /**
     * 토큰 검증
     * @param token
     * @param request
     * @param requestURI
     * @return
     */
    public boolean validateToken(String token, HttpServletRequest request, String requestURI) {
        ErrorCode errorCode = null;
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("malformed token: uri[{}]", requestURI);
            errorCode = ErrorCode.AUTH_FAIL;
        } catch (ExpiredJwtException e) {
            log.info("expired token: uri[{}]", requestURI);
            errorCode = ErrorCode.ACCESS_TOKEN_EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.info("unsupported token: uri[{}]", requestURI);
            errorCode = ErrorCode.AUTH_FAIL;
        } catch (IllegalArgumentException e) {
            log.info("illegal token: uri[{}]", requestURI);
            errorCode = ErrorCode.AUTH_FAIL;
        }
        request.setAttribute("ErrorCode", errorCode);
        return false;
    }

    /**
     * 토큰 검증
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("malformed token");
        } catch (ExpiredJwtException e) {
            log.info("expired token");
        } catch (UnsupportedJwtException e) {
            log.info("unsupported token");
        } catch (IllegalArgumentException e) {
            log.info("illegal token");
        }
        return false;
    }

    /**
     * 토큰 파싱
     * @param accessToken
     * @return
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * cookie 값 조회
     * @param request
     * @param name
     * @return
     */
    public String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}

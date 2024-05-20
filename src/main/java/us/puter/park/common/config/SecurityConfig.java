package us.puter.park.common.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import us.puter.park.api.member.enums.MemberRole;
import us.puter.park.common.config.jwt.JwtAccessDeniedHandler;
import us.puter.park.common.config.jwt.JwtAuthenticationEntryPoint;
import us.puter.park.common.config.jwt.JwtFilter;
import us.puter.park.common.config.jwt.TokenProvider;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Value("${spring.profiles.active}")
    String profile;

    @Value("${system.cors-allowed-origin}")
    List<String> corsAllowedOriginList;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(
                        cors -> cors
                                .configurationSource(corsConfigurationSource())
                )
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(
                        header -> header
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                                .disable()
                )
                .exceptionHandling(
                        exception -> exception
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(
                                        "/admin/**"
                                        , "/api/v1/admin/**"
                                )
                                .hasAuthority(MemberRole.ADMIN.getValue())
                                .requestMatchers(
                                        "/**"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                );

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        if (!StringUtils.equalsIgnoreCase("prod", profile)) {
            corsAllowedOriginList.add("127.0.0.1");
            corsAllowedOriginList.add("localhost");
        }

        corsConfig.setAllowCredentials(true);
        for (String allowedOrigin : corsAllowedOriginList) {
            if (StringUtils.isNotBlank(allowedOrigin)) {
                corsConfig.addAllowedOriginPattern("*" + allowedOrigin + "*");
            }
        }

        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod(HttpMethod.GET);
        corsConfig.addAllowedMethod(HttpMethod.POST);
        corsConfig.addAllowedMethod(HttpMethod.PUT);
        corsConfig.addAllowedMethod(HttpMethod.PATCH);
        corsConfig.addAllowedMethod(HttpMethod.DELETE);

        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();
        corsConfigSource.registerCorsConfiguration("/**", corsConfig);

        return corsConfigSource;
    }
}

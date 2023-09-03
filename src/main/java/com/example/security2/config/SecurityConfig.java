package com.example.security2.config;

import com.example.security2.jwt.JwtAccessDeniedHandler;
import com.example.security2.jwt.JwtAuthenticationEntryPoint;
import com.example.security2.jwt.TokenProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(CorsFilter corsFilter, TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.corsFilter = corsFilter;
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // csrf, cors 일단 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable)
//                .cors(AbstractHttpConfigurer::disable)

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                // jwt 예외핸들러 등록
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // 세션을 사용하지 않기 때문에 "STATELESS"로 설정
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests((requests) -> requests
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers(
                            new AntPathRequestMatcher("/api/v1/auth"),
                            new AntPathRequestMatcher("/api/v1/user/hello"),
                            new AntPathRequestMatcher("/api/v1/user/signup")
                    ).permitAll()
                    .anyRequest().authenticated()
                )
                // enable h2 console
                .headers(header -> header
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                // JwtFilter 적용
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }
}

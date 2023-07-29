package com.example.security2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity // 스프링 필터체인에 등록
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // deprecated
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)         // 권한 처리, Secured 어노테이션 활성화. preAuthorize, postAuthorize 어노테이션 활성화
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // csrf, cors 일단 비활성화
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/user/**").authenticated() // 인증만 되면 들어갈 수 있는 주소.
                .requestMatchers("/manager/**").access(
                        new WebExpressionAuthorizationManager("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                )
                .requestMatchers("/admin/**").access(
                        new WebExpressionAuthorizationManager("hasRole('ROLE_ADMIN')")
                )
                .anyRequest().permitAll())              // 예제 구현을 위해 위 /user, /manager, /admin 관련 api를 제외하고 전부 허용함.
                .formLogin(form -> form
                        .loginPage("/loginForm")
                        .usernameParameter("email")     // loadUserByUsername 메서드가 호출될 때 username 파라미터를 세팅해주기 위함. default는 username으로 매핑됨.
                        .loginProcessingUrl("/login")   // login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해준다. 컨트롤러에 /login 안만들어도됨.
                        .defaultSuccessUrl("/")         // 디폴트는 /로 보내주지만 사용자가 특정주소로 요청을 하면(/user) 로그인 성공시 거기로 보내줌.
                );
//        http.httpBasic(withDefaults());

        return http.build();
    }
}

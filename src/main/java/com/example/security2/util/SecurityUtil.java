package com.example.security2.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {}

    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username;
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            username = springSecurityUser.getUsername();
        } else {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
}

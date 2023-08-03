package com.example.security2.controller;

import com.example.security2.auth.PrincipalDetails;
import com.example.security2.entity.User;
import com.example.security2.enums.AuthType;
import com.example.security2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * View
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class ViewController {

    // 간편구현을 위해 레포지토리만 생성
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping({"", "/"})
    public String index() {
        return "index";
    }

    // @AuthenticationPrincipal 어노테이션으로 인증객체 내 User정보 확인 가능
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("principalDetails: {}", principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        user.setRole(AuthType.ROLE_USER.name());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-only")
    public @ResponseBody String accessOnlyAdmin() {
        return "어드민 권한만 접근 가능";
    }

    /**
     * 권한 하나로만 걸고 싶으면 @Secured로 처리하기 쉽고, 여러개면 @PreAuthorize로 가능
     *
     * @PreAuthorize 함수 실행 전 실행되는 어노테이션
     * @PostAuthorize 함수 실행 후 실행되는 어노테이션 -> 후처리는 잘 사용하지 않음
     */
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/manager-and-admin")
    public @ResponseBody String accessManagerAndAdmin() {
        return "매니저, 관리자 권한만 접근 허용";
    }
}

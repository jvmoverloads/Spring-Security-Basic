package com.example.security2.controller;

import com.example.security2.entity.User;
import com.example.security2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
        // 뷰리졸버 설정: templates(prefix), .mustache(suffix) -> 의존성 추가 시 기본으로 잡힘.
        // src/main/resources/templates/index.mustache
        return "index";
    }

    // 페이지 만들기 귀찮아서 응답 body 바로 리턴
    @GetMapping("/user")
    public @ResponseBody String user() {
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

    // 스프링 시큐리티가 기본적으로 해당 요척을 낚아채지만, SecurityConfig 설정 이후 내 설정 먹음
    /*@GetMapping("/login")
    public @ResponseBody String login() {
        return "login";
    }*/

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
        log.info("user: {}", user.getName());

        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user); // 시큐리티로 로그인 할 수 없음(패스워드가 암호화되지 않아서)

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

package com.example.security2.auth;

import com.example.security2.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 * 로그인이 진행이 완료가 되면 시큐리티 session을 만들어준다.(SecurityContextHolder)
 * 오브젝트 => Authentication 타입의 객체
 * Authentication 안에 User 정보가 있어야 됨.
 * User 객체는 UserDetails 타입 객체(Security)
 *
 * Security Session => Authentication => UserDetails(PrincipalDetails)
 */
@Getter
@Setter
public class PrincipalDetails implements UserDetails, OAuth2User {

    // composition
    private final User user;
    private Map<String, Object> attributes;

    // 일반 로그인
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth 로그인
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this(user);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 해당 User의 권한을 리턴하는 곳!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) user::getRole);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 계정이 만료되지 않은지(true: 만료되지 않음 / false: 만료됨)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않은지(true: 잠기지 않음 / false: 잠김)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호가 만료되지 않은지(true: 만료안됨 / false: 만료)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화되어 있는지(true: 활성화 / false: 비활)
    @Override
    public boolean isEnabled() {
        /**
         * TODO
         * 만일 우리 사이트의 정책이 1년동안 로그인하지 않은 회원을 휴면처리 하기로 했다면?
         * 현재 서버시간 - 마지막 로그인 시간 => 1년을 초과하면 return false;
         * System.currentTimeMillis() - user.getLastLoginDate();
         */

        return true;
    }

    @Override
    public String getName() {
        return (String) attributes.get("sub");
    }
}

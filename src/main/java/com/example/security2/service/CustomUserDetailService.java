package com.example.security2.service;

import com.example.security2.entity.User;
import com.example.security2.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("userDetailsService")
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + " > DB에서 찾을 수 없음."));
    }

    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if (!user.isActivated())
            throw new RuntimeException(username + " > 활성화된 유저가 아닙니다.");

        Collection<? extends GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                grantedAuthorities
        );
    }
}

package com.example.security2.oauth;

import com.example.security2.auth.PrincipalDetails;
import com.example.security2.entity.User;
import com.example.security2.enums.AuthType;
import com.example.security2.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public PrincipalOauth2UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    // 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("attr: {}", attributes.toString());

        String email = oAuth2User.getAttribute("email");
        Optional<User> optionalUser = userRepository.findByEmail(email);

        User userEntity;
        if (optionalUser.isEmpty()) {
            userEntity = createUserEntityByOAuth2User(userRequest, oAuth2User);
            userRepository.save(userEntity);
        } else {
            userEntity = optionalUser.get();
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }

    private User createUserEntityByOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String providerId = oAuth2User.getAttribute("sub");
        String userName = String.format("%s_%s", provider, providerId);
        String password = passwordEncoder.encode(randomPassword());
        String email = oAuth2User.getAttribute("email");
        String role = AuthType.ROLE_USER.name();

        return User.builder()
                .email(email)
                .name(userName)
                .password(password)
                .role(role)
                .provider(provider)
                .providerId(providerId)
                .build();
    }

    private String randomPassword() {
        // 랜덤 비밀번호(0 ~ z까지 20자리 랜덤 비밀번호 생성)
        Random random = new Random();
        int leftLimit = 97;     // letter '0'
        int rightLimit = 122;   // letter 'z'
        int targetStringLength = 20;

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

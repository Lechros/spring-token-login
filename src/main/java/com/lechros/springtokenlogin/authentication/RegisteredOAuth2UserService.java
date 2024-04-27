package com.lechros.springtokenlogin.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lechros.springtokenlogin.user.User;
import com.lechros.springtokenlogin.user.UserRepository;
import com.lechros.springtokenlogin.user.UserSocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisteredOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String ID_TOKEN = "id_token";

    private final UserRepository userRepository;
    private final UserSocialLoginService userSocialLoginService;

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("apple".equals(registrationId)) {
            // Apple ID 로그인일 경우 id_token에서 sub 추출
            String idToken = userRequest.getAdditionalParameters().get(ID_TOKEN).toString();
            Map<String, Object> claims = parseClaims(idToken);
            String providerId = (String) claims.get(JwtClaimNames.SUB);
            User user = findOrCreateUser(registrationId, providerId);

            Map<String, Object> attributes = Map.of(JwtClaimNames.SUB, providerId);
            Set<GrantedAuthority> authorities = Set.of(new OAuth2UserAuthority(attributes));

            return new RegisteredOAuth2User(authorities, attributes, user);
        } else {
            // 토큰 정보 API에서 사용자 정보 받아오기
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            String oauth2UserName;
            // Spring Security 6.3.0-M2부터 nested user-name-attribute 설정 가능, until then...
            // https://github.com/spring-projects/spring-security/pull/14265
            if ("naver".equals(registrationId)) {
                oauth2UserName = (String) ((Map<String, Object>) oauth2User.getAttributes().get("response")).get("id");
            } else {
                oauth2UserName = oauth2User.getName();
            }
            // 받아온 정보의 sub로 필요 시 DB에 저장하고 user 가져오기
            User user = findOrCreateUser(registrationId, oauth2UserName);

            return new RegisteredOAuth2User(oauth2User.getAuthorities(), oauth2User.getAttributes(), user);
        }
    }

    @Transactional
    protected User findOrCreateUser(String provider, String providerId) {
        Optional<User> optionalUser = userSocialLoginService.findUser(provider, providerId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        User user = User.withUsername(String.format("new %s user name", provider));
        user = userRepository.save(user);
        userSocialLoginService.addUserSocialLogin(user, provider, providerId);

        return user;
    }

    private Map<String, Object> parseClaims(String jwt) {
        // JwtParser를 사용하면 alg, exp 검사 과정이 포함되기 때문에 단순히 payload만 변환
        Charset chs = StandardCharsets.UTF_8;
        String[] parts = jwt.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1].getBytes(chs)), chs);
        try {
            return new ObjectMapper().readValue(payload, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

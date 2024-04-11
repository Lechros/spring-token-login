package com.lechros.springtokenlogin.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lechros.springtokenlogin.user.User;
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisteredOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String ID_TOKEN = "id_token";

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
            User user = userSocialLoginService.findOrRegister(registrationId, providerId);

            Map<String, Object> attributes = Map.of(JwtClaimNames.SUB, providerId);
            Set<GrantedAuthority> authorities = Set.of(new OAuth2UserAuthority(attributes));

            return new RegisteredOAuth2User(authorities, attributes, user);
        } else {
            // 토큰 정보 API에서 사용자 정보 받아오기
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            // 받아온 정보의 sub로 필요 시 DB에 저장하고 user 가져오기
            User user = userSocialLoginService.findOrRegister(registrationId, oauth2User.getName());

            return new RegisteredOAuth2User(oauth2User.getAuthorities(), oauth2User.getAttributes(), user);
        }
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

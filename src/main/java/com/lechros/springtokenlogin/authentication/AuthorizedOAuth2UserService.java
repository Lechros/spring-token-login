package com.lechros.springtokenlogin.authentication;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthorizedOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    static final String ATTRIBUTE_DELIMITER = "/";

    private final AuthorizedUserService authorizedUserService;

    private final CustomOAuth2UserService delegate = new CustomOAuth2UserService();

    @Override
    public AuthorizedUser loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        return authorizedUserService.getAuthorizedUser(userRequest, oauth2User);
    }

    @PostConstruct
    private void init() {
        delegate.setAttributesConverter((request) -> (attributes) -> {
            String userNameAttributeName = getUserNameAttributeName(request);

            // ex) user-name-attribute가 "response/id"일 경우, "response" -> "id" 경로의 값을 "response/id" 키로 저장
            if (userNameAttributeName.contains(ATTRIBUTE_DELIMITER)) {
                String[] keys = userNameAttributeName.split(ATTRIBUTE_DELIMITER);
                String userName = getNestedValue(attributes, keys);

                attributes.put(userNameAttributeName, userName);
            }

            return attributes;
        });
    }

    private String getUserNameAttributeName(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    }

    private static <T> T getNestedValue(Map<String, Object> map, String[] keys) {
        Map<String, Object> cur = map;
        for (String key : keys) {
            Object o = cur.get(key);
            if (o == null) {
                return null;
            }
            if (o instanceof Map) {
                cur = (Map<String, Object>) o;
            } else {
                return (T) o;
            }
        }
        return null;
    }
}

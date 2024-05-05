package com.lechros.springtokenlogin.authentication;

import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.List;
import java.util.Set;

public class RedirectUriParameterOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String REDIRECT_URI_PARAMETER_NAME = "redirect_uri";

    private static final String REGISTRATION_ID_ATTRIBUTE_NAME = "registration_id";

    private final DefaultOAuth2AuthorizationRequestResolver delegate;
    private final Set<String> allowedRegistrationIds;

    public RedirectUriParameterOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                                                  String authorizationRequestBaseUri,
                                                                  List<String> allowedRegistrationIds) {
        Assert.notNull(allowedRegistrationIds, "allowedRegistrationIds cannot be null");
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);
        this.allowedRegistrationIds = Set.copyOf(allowedRegistrationIds);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request);
        return resolveRedirectUri(authorizationRequest, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request, clientRegistrationId);
        return resolveRedirectUri(authorizationRequest, request);
    }

    private OAuth2AuthorizationRequest resolveRedirectUri(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        if (authorizationRequest == null) {
            return null;
        }
        String registrationId = authorizationRequest.getAttribute(REGISTRATION_ID_ATTRIBUTE_NAME);
        if (allowedRegistrationIds.contains(registrationId)) {
            String redirectUri = request.getParameter(REDIRECT_URI_PARAMETER_NAME);
            if (redirectUri != null) {
                return withRedirectUri(authorizationRequest, redirectUri);
            }
        }
        return authorizationRequest;
    }

    private OAuth2AuthorizationRequest withRedirectUri(OAuth2AuthorizationRequest authorizationRequest, String redirectUri) {
        return OAuth2AuthorizationRequest.from(authorizationRequest)
            .redirectUri(redirectUri)
            .build();
    }
}

package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.util.ExpiringHashMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.Assert;

import java.util.Map;

public final class InMemoryOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // 30분 내에 로그인을 완료하지 않으면 요청 삭제
    private final Map<String, OAuth2AuthorizationRequest> authorizationRequests = new ExpiringHashMap<>(30 * 60 * 1000);

    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");
        String stateParameter = this.getStateParameter(request);
        if (stateParameter == null) {
            return null;
        } else {
            OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequests.get(stateParameter);
            return authorizationRequest != null && stateParameter.equals(authorizationRequest.getState()) ? authorizationRequest : null;
        }
    }

    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        if (authorizationRequest == null) {
            this.removeAuthorizationRequest(request, response);
        } else {
            String state = authorizationRequest.getState();
            Assert.hasText(state, "authorizationRequest.state cannot be empty");
            this.authorizationRequests.put(state, authorizationRequest);
        }
    }

    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(response, "response cannot be null");
        OAuth2AuthorizationRequest authorizationRequest = this.loadAuthorizationRequest(request);
        if (authorizationRequest != null) {
            this.authorizationRequests.remove(authorizationRequest.getState());
        }

        return authorizationRequest;
    }

    private String getStateParameter(HttpServletRequest request) {
        return request.getParameter("state");
    }
}

package com.lechros.springtokenlogin.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {

    @Value("${authorization.issuer}")
    private String issuer;

    @Bean
    public SecretKeySpec secretKeySpec() {
        // TODO: key를 파일로 분리해서 관리
        String key = "12345678123456781234567812345678";
        return new SecretKeySpec(key.getBytes(), "HmacSHA256");
    }

    @Bean
    public JwsHeader jwsHeader() { // JWT 발급에 사용
        return JwsHeader.with(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() { // JWT 발급에 사용
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKeySpec()));
    }

    @Bean
    public JwtDecoder jwtDecoder() { // OAuth2 Resource Server에서 사용
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec()).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        jwtDecoder.setJwtValidator(withIssuer);

        return jwtDecoder;
    }
}

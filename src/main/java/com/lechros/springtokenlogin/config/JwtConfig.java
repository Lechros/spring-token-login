package com.lechros.springtokenlogin.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {

    @Bean
    public JwtEncoder jwtEncoder() { // JWT 발급에 사용
        ImmutableSecret<SecurityContext> immutableSecret = new ImmutableSecret<>(secretKeySpec());
        return new NimbusJwtEncoder(immutableSecret);
    }

    @Bean
    public JwtDecoder jwtDecoder() { // OAuth2 Resource Server에서 사용
        return NimbusJwtDecoder.withSecretKey(secretKeySpec()).build();
    }

    @Bean
    public SecretKeySpec secretKeySpec() {
        // TODO: key를 파일로 분리해서 관리
        String key = "12345678123456781234567812345678";
        return new SecretKeySpec(key.getBytes(), "HmacSHA256");
    }
}

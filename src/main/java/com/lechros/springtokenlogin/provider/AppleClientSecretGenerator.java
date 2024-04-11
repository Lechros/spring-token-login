package com.lechros.springtokenlogin.provider;

import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

public class AppleClientSecretGenerator implements ClientSecretGenerator {

    @Getter
    private final String registrationId = "apple";
    private final String serverUrl = "https://appleid.apple.com";
    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.apple.team-id}")
    private String teamId;
    @Value("${spring.security.oauth2.client.registration.apple.key-id}")
    private String keyId;
    @Value("${spring.security.oauth2.client.registration.apple.key-path}")
    private String keyPath;

    @Override
    public String generate() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(5));

        // @formatter:off
        return Jwts.builder()
            .header().empty().keyId(keyId).and()
            .issuer(teamId)
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiresAt))
            .audience().add(serverUrl).and()
            .subject(clientId)
            .signWith(generatePrivateKey())
            .compact();
        // @formatter:on
    }

    private PrivateKey generatePrivateKey() {
        try {
            ClassPathResource keyFile = new ClassPathResource(keyPath);
            byte[] keyBytes = keyFile.getInputStream().readAllBytes();
            String keyString = new String(keyBytes, StandardCharsets.UTF_8).replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyString));
            return KeyFactory.getInstance("EC").generatePrivate(keySpec);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

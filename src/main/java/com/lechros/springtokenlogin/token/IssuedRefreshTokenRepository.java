package com.lechros.springtokenlogin.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssuedRefreshTokenRepository extends JpaRepository<IssuedRefreshToken, Long> {

    Optional<IssuedRefreshToken> findByTokenValue(String tokenValue);
}

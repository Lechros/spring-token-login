package com.lechros.springtokenlogin.repository;

import com.lechros.springtokenlogin.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);
}

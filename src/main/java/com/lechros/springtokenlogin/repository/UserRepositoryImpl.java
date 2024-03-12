package com.lechros.springtokenlogin.repository;

import com.lechros.springtokenlogin.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private ConcurrentMap<Long, User> map = new ConcurrentHashMap<>();

    private long ai = 1;

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(ai++);
        }
        map.put(user.getId(), user);
        return user;
    }
}

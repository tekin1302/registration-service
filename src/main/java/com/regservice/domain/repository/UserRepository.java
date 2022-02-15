package com.regservice.domain.repository;

import com.regservice.domain.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class UserRepository {

    private Map<String, User> users;

    public UserRepository() {
        this.users = new ConcurrentHashMap<>();
    }

    public Mono<User> save(User user) {
        users.put(user.getEmail(), user);
        return Mono.justOrEmpty(user);
    }

    public Mono<User> findByEmail(String email) {
        return Mono.justOrEmpty(users.get(email));
    }
}

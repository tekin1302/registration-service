package com.regservice.domain.service;

import com.regservice.domain.model.User;
import com.regservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Mono<Void> signup(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user)
                .then();
    }

    public Mono<Boolean> areCredentialsValid(User user) {
        return userRepository.findByEmail(user.getEmail())
                .map(User::getPassword)
                .map(encodedPassword -> passwordEncoder.matches(user.getPassword(), encodedPassword));
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

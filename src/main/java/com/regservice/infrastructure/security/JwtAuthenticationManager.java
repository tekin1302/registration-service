package com.regservice.infrastructure.security;

import com.regservice.domain.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String credentials = ((String) authentication.getCredentials());
        return Mono.just(credentials)
                .map(jwtService::validateJwt)
                .onErrorResume(e -> {
                    log.error("Invalid token");
                    return Mono.empty();
                })
                .map(jwt -> new UsernamePasswordAuthenticationToken(
                        jwt.getBody().getSubject(),
                        credentials,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
    }
}

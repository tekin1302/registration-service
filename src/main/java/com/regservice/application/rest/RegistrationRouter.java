package com.regservice.application.rest;

import com.regservice.application.dto.UserDTO;
import com.regservice.application.mapper.UserMapper;
import com.regservice.domain.service.AuthService;
import com.regservice.domain.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RegistrationRouter {

    private final UserMapper userMapper;
    private final AuthService authService;
    private final JwtService jwtService;

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route(POST("/signup"), this::signup)
                .and(route(POST("/login"), this::login))
                .and(route(GET("/user-details"), this::getUserDetails));
    }

    private Mono<ServerResponse> signup(ServerRequest req) {
        return req.bodyToMono(UserDTO.class)
                .map(userMapper::fromDTO)
                .flatMap(authService::signup)
                .then(ServerResponse.noContent().build());
    }

    private Mono<ServerResponse> login(ServerRequest req) {
        return req.bodyToMono(UserDTO.class)
                .map(userMapper::fromDTO)
                .flatMap(user -> authService
                        .areCredentialsValid(user)
                        .flatMap(ok -> ok ? Mono.just(user) : Mono.empty()))
                .map(user -> jwtService.getJwt(user.getEmail()))
                .map(jwt -> ResponseCookie.fromClientResponse("X-Auth", jwt)
                        .maxAge(3600)
                        .httpOnly(true)
                        .path("/")
                        .secure(false)
                        .build())
                .flatMap(cookie -> ServerResponse.noContent().cookie(cookie).build())
                .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

    private Mono<ServerResponse> getUserDetails(ServerRequest req) {
        return req.principal()
                .map(Principal::getName)
                .flatMap(authService::getUserByEmail)
                .map(userMapper::toDTO)
                .flatMap(name -> ServerResponse.ok().bodyValue(name));
    }
}

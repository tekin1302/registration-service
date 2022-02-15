package com.regservice.domain.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);

    public String getJwt(String username) {
        return Jwts.builder()
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setSubject(username)
                .setIssuer("identity")
                .setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(15))))
                .setIssuedAt(Date.from(Instant.now()))
                .compact();
    }

    public Jws<Claims> validateJwt(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(jwt);
    }
}

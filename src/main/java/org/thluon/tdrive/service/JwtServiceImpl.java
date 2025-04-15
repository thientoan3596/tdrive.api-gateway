package org.thluon.tdrive.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;
    @PostConstruct
    public void init(){
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Claims extractAllClaims(@NonNull String token) throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException, NullPointerException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

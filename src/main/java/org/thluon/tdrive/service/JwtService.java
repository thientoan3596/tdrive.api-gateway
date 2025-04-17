package org.thluon.tdrive.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.NonNull;

import java.security.SignatureException;

public interface JwtService {
    default Claims extractAllClaims(@NonNull String token)
            throws ExpiredJwtException, MalformedJwtException, SignatureException,IllegalArgumentException,NullPointerException{
        throw new IllegalStateException("Not implemented");
    }
}

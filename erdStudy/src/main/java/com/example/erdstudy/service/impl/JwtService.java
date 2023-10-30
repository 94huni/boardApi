package com.example.erdstudy.service.impl;

import com.example.erdstudy.domain.Member;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    boolean validateToken(String token, UserDetails userDetails);

    String generateToken(UserDetails userDetails);

    String getEmail(String token);
}

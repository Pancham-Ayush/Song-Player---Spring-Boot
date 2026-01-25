package com.example.SecurityMicroService.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Service
public class JWT_Token {

    @Value("${JWT.secret.key}")
    String secretKey;

    public String getSecretToken(String email, String role) {
        long expiry = System.currentTimeMillis() + (1000 * 60 * 60 * 24);
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public HashMap<String, String> getEmail_RoleFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        HashMap<String, String> response = new HashMap<>();
        response.put("email", claims.get("email", String.class));
        response.put("role", claims.get("role", String.class));
        return response;

    }
}

package com.example.demo.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // 生成安全的密钥
//    private SecretKey getSignKey() {
//        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    }
    public String generateToken(String username,Integer role){
        Date now=new Date();
        Date expiryTime=new Date(now.getTime()+expiration*1000);
        return Jwts.builder()
                .setSubject(username)
                .claim("role",role)
                .setIssuedAt(now)
                .setExpiration(expiryTime)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }catch(JwtException | IllegalArgumentException e ){
            return false;
        }
    }
    public String getUsernameFromToken(String token){
        Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public Integer getRoleFromToken(String token){
        Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.get("role",Integer.class);
    }
}

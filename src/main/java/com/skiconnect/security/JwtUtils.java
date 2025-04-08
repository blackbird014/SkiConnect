package com.skiconnect.security;

import com.skiconnect.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // TODO: Move to application.properties
    @Value("${skiconnect.app.jwtSecret:DefaultSecretKeyForSkiConnectApplicationDoNotUseInProduction}")
    private String jwtSecretString;

    // TODO: Move to application.properties
    @Value("${skiconnect.app.jwtExpirationMs:86400000}") // 24 hours
    private int jwtExpirationMs;

    private SecretKey jwtSecret;

    @jakarta.annotation.PostConstruct
    private void init() {
        // Ensure the secret key is strong enough for the HS512 algorithm
        if (jwtSecretString == null || jwtSecretString.length() < 64) {
            logger.warn("JWT Secret is not configured or too short, using a default secure key. PLEASE CONFIGURE a strong skiconnect.app.jwtSecret property.");
            this.jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        } else {
             byte[] keyBytes = jwtSecretString.getBytes();
             this.jwtSecret = Keys.hmacShaKeyFor(keyBytes);
        }
    }

    public String generateJwtToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .claim("roles", roles) // Add roles as a claim
                .claim("userId", userPrincipal.getId()) // Add user ID as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(jwtSecret, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
} 
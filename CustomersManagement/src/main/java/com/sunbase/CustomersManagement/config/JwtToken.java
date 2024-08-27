package com.sunBase.CustomersManagement.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

public class JwtToken {

    public static String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityDetails.JWT_KEY.getBytes());

        String authoritiesString = getValue(authorities);

        return Jwts.builder()
                .setIssuer("Radhikesh")
                .setSubject("JWT_Token")
                .claim("username", username)
                .claim("authorities", authoritiesString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000)) // 1 day expiration
                .signWith(key)
                .compact();
    }

    private static String getValue(Collection<? extends GrantedAuthority> collection) {
        Set<String> set = new HashSet<>();
        for (GrantedAuthority autho : collection) {
            String role = autho.getAuthority();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role; 
            }
            set.add(role);
        }
        return String.join(",", set); 
    }
}

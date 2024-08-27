//package com.sunBase.CustomersManagement.config;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.crypto.SecretKey;
//import java.io.IOException;
//
//public class JwtTokenValidatorFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String jwt = request.getHeader(SecurityDetails.JWT_HEADER);
//        if (jwt != null) {
//            try {
//                jwt = jwt.substring(7);
//                SecretKey key = Keys.hmacShaKeyFor(SecurityDetails.JWT_KEY.getBytes());
//                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
//                String username = String.valueOf(claims.get("username"));
//                String authorities = (String) claims.get("authorities");
//
//                Authentication auth = new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            } catch (Exception e) {
//                throw new BadCredentialsException("Invalid Token received.");
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String servletpath = request.getServletPath();
//        return servletpath.equals("/api/auth/login") || servletpath.equals("/api/auth/signup");
//    }
//}
//

package com.sunBase.CustomersManagement.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader(SecurityDetails.JWT_HEADER);
        if (jwt != null) {
            try {
                jwt = jwt.substring(7); // Remove "Bearer " prefix
                SecretKey key = Keys.hmacShaKeyFor(SecurityDetails.JWT_KEY.getBytes());
                
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();
                
              String username = String.valueOf(claims.get("username"));
              String authorities = (String) claims.get("authorities");

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // Throw BadCredentialsException to be handled by the global exception handler
                throw new BadCredentialsException("Invalid Token received.");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletpath = request.getServletPath();
        return servletpath.equals("/api/auth/login") || servletpath.equals("/api/auth/signup");
    }
}


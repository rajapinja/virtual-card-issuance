package com.laraid.vci.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    private static final Logger log
            = LoggerFactory. getLogger(CurrentUserService.class);

    public String getCurrentUserId() {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) token.getPrincipal();

        String userId = jwt.getSubject(); // UUID
        String email = jwt.getClaim("email");
        String username = jwt.getClaim("preferred_username");
        String fullName = jwt.getClaim("name");

        log.info("User ID: {} ", userId);
        log.info("Email: {}", email);
        log.info("Username: {}", username);
        log.info("Name: {}",fullName);

        return jwt.getSubject(); // Or use jwt.getClaim("email") if needed
    }
}


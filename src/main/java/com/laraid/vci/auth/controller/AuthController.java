package com.laraid.vci.auth.controller;

import com.laraid.vci.merchant.repo.MerchantRepository;
import com.laraid.vci.auth.service.AuthService;
import com.laraid.vci.auth.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log
            = LoggerFactory. getLogger(AuthController.class);

    private final MerchantRepository repo;
    private final AuthService authService;

    @GetMapping("/me")
    public JwtUtils.UserInfo getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return JwtUtils.extractUserInfo(jwt);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        log.info("login : {} : {}", email, password);

        // Step 1: Delegate authentication to Keycloak
        String accessToken;
        try {
            accessToken = authService.loginToKeycloak(email, password).block(); // returns JWT
            log.info("login : {} ",accessToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid credentials: " + e.getMessage());
        }

        // Step 2: Check if user exists in local DB
        return repo.findByEmail(email)
                .map(merchant -> ResponseEntity.ok("Login successful for " + email))
                .orElseThrow(() -> new UsernameNotFoundException("Merchant exists in Keycloak but not in local DB"));
    }
}

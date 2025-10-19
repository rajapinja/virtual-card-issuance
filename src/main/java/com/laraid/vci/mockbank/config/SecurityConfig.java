package com.laraid.vci.mockbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                //.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // 👈 This is where you add it
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/mock-bank/provision-card").permitAll()
                        .anyRequest().authenticated()
                );
//                .oauth2ResourceServer(oauth -> oauth
//                        .jwt(jwt -> jwt
//                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                        )
//                );

        return http.build();
    }

}

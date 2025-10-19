package com.laraid.vci.auth.util;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

public class JwtUtils {

    public record UserInfo(String email, String name, List<String> roles) {}

    public static UserInfo extractUserInfo(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name"); // or "preferred_username"
        List<String> roles = jwt.getClaimAsMap("realm_access") != null
                ? (List<String>) ((Map<String, Object>) jwt.getClaim("realm_access")).get("roles")
                : List.of();
        return new UserInfo(email, name, roles);
    }
}

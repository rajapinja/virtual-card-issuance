## SpringBoot and POSTMAN Setup
### SecurityConfig.java

    @EnableWebSecurity
    public class SecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/merchants/public/**").permitAll()
            .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        
                return http.build();
            }
    }
✅ Get Access Token from Keycloak
       From Postman or frontend app:
    
    Request Token:
        POST http://localhost:8080/realms/your-realm/protocol/openid-connect/token
        Content-Type: application/x-www-form-urlencoded
    
        grant_type=password
        client_id=your-client-id
        username=merchant@example.com
            password=123456
    Response:
    
    {
        "access_token": "eyJhbGciOiJSUzI1NiIsInR...",
        "expires_in": 300,
        ...
    }
    Use this token in your requests:
    
    Authorization: Bearer <access_token>

✅ Access Merchant Info from Token
    In your controller/service:

    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.oauth2.jwt.Jwt;
    
    @GetMapping("/me")
    public ResponseEntity<String> getMerchant(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        return ResponseEntity.ok("Logged in as: " + email);
    }

### Summary
    Concern     	    Where it's handled
    Password encryption	✅ Keycloak (BCrypt internally)
    JWT token	        ✅ Keycloak
    Email verification	✅ Keycloak
    Authentication	    ✅ Spring Security OAuth2
    Authorization	    ✅ Spring roles/scopes



### ✅ Step-by-Step: Keycloak Setup
    1. Access Keycloak Admin Console
        Open browser: http://localhost:8384 (or your Keycloak port).

        Login using admin credentials (set during Keycloak installation).
    
    2. Create a Realm
       A realm in Keycloak is like a tenant or security domain.
    
        📍 Admin Console → Realms → Add Realm    
            Realm Name: vci-realm    
            Click Create.
    
    3. Create a Client
       This represents your Spring Boot app that will consume the tokens.
    
    📍    Clients → Create
    
        Basic Settings:
        Client ID: springboot-api
        
        Client Protocol: openid-connect        
        Root URL: http://localhost:8080 (or your backend URL)        
        Click Save.
        
        After saving, update settings:
        Access Type: confidential (or public if no secret is needed — public is okay for browser-based apps or resource servers)
        
        Standard Flow Enabled: ✅ (for browser login flows)        
        Direct Access Grants Enabled: ✅ (for password grant – only if needed)        
        Service Accounts Enabled: ✅ (for machine-to-machine auth if needed)        
        Authorization Enabled: ✅ (if using RBAC policies via Keycloak)
        
        Click Save.
        
        Credentials Tab:
        Copy the Client Secret if confidential (you'll need it for Spring Boot login).
    
    4. Create Roles (Optional but Recommended)
       📍 Realm Roles → Add Role
    
        Examples:        
            merchant            
            admin            
            Click Save.
    
    5. Create a User
       📍 Users → Add User
    
        User Details:
        Username: rajapin        
        Email: raja@example.com        
        First Name: Raja        
        Last Name: Pinja        
        Email Verified: ✅        
        Click Save.
        
        Credentials Tab:
        Set a password: e.g. password123
        
        Temporary: ❌ (uncheck so it doesn’t ask for password reset)
        
        Click Set Password
        
        Role Mapping Tab:
        Assign realm roles (e.g., merchant, admin)
    
    6. Verify OIDC Discovery Endpoint Works
       Open:
  
        http://localhost:8384/realms/vci-realm/.well-known/openid-configuration
        You should see JSON config including:
        
        issuer        
        authorization_endpoint        
        token_endpoint        
        jwks_uri
    
    ✅ Use in Spring Boot
        In application.yml:
       
        spring:
            security:
                oauth2:
                    resourceserver:
                        jwt:issuer-uri: http://localhost:8384/realms/vci-realm
    
### To decode and inspect tokens, use:
  
    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('ROLE_merchant')")
    public String hello(@AuthenticationPrincipal Jwt jwt) {
        return "Hello " + jwt.getClaimAsString("preferred_username");
    }

### ✅ Recommended (Minimal) Configuration:

    spring:
        security:
            oauth2:
                resourceserver:
                    jwt:
                        issuer-uri: http://localhost:8384/realms/vci-realm
        Spring Security will automatically resolve the jwk-set-uri using the .well-known/openid-configuration endpoint exposed by Keycloak when issuer-uri is provided.
        
    🔍 What does issuer-uri do?
    It tells Spring Security to fetch metadata from:
    
    http://localhost:8384/realms/vci-realm/.well-known/openid-configuration
    This metadata includes the jwk-set-uri (used to validate tokens), among other things.

### Public 
    http://localhost:8384/realms/vci-realm/protocol/openid-connect/token
    x-www-form-urlencoded

    grant_type:password
    client_id:vci-springboot-api
    username:raja.pinja9@gmail.com
    password:Test1234

    {
        "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJNcHg0b29UZDFDYzEzeFp3aDhYRi1RLUwxWWRzVlc5bEVlYTN0dXYtTURJIn0.eyJleHAiOjE3NTMwOTcyNjAsImlhdCI6MTc1MzA5Njk2MCwianRpIjoiZTg1ODA0YzItMTQ3NC00YzczLWIxYzctNGZhZDQ5YWIxZTNlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4Mzg0L3JlYWxtcy92Y2ktcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZDg2ZjVlZjctYTMxYS00MzQwLWFjNmMtYjQ2ZTBhM2RjNDgwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidmNpLXNwcmluZ2Jvb3QtYXBpIiwic2Vzc2lvbl9zdGF0ZSI6ImMwNTBmYWQ2LTg5NjEtNGY1YS05ZDYwLTY0ZThhMDYyN2UyOCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo5NzkxIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImFkbWluIiwibWVyY2hhbnQiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtdmNpLXJlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiYzA1MGZhZDYtODk2MS00ZjVhLTlkNjAtNjRlOGEwNjI3ZTI4IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJSYWphIFBpbmphIiwicHJlZmVycmVkX3VzZXJuYW1lIjoicmFqYXBpbmphIiwiZ2l2ZW5fbmFtZSI6IlJhamEiLCJmYW1pbHlfbmFtZSI6IlBpbmphIiwiZW1haWwiOiJyYWphLnBpbmphOUBnbWFpbC5jb20ifQ.zucXW_OmL5RDaYkMFG6bo333-DhTyCF5ttuT5rGFMnhjPG7ge-qIX_Pc-DvIHbQ1bOAbCadjPgsVhO66exEsHm6s16Tlh0qmdS_2vjxF8irUSjoztIM9_387bKAmDT3QhHFLqfJocUa0gXZ8w0BnDgQI4UdT8oLb_s539wpdb2b67-CCN4hA_mEyK5RzwQRPwECMa9CAok3VPK0N108Y8MD-swMkm6it7TyqM6Dt5aKZaotpWlMkM_ClE8dbVx_T2GYv_vGHNnXct_eiKUnNxCyUrcmsZdYoLpNO0w5JefaYFv_O-xGpLqXIK33xY-_fKr0SQpjD4QT2Zy_qtTxpvQ",
        "expires_in": 300,
        "refresh_expires_in": 1800,
        "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJkMDA3OGVhOC02YjA3LTRkMzYtYjljNC1hOWU1ZmQzMGYwY2EifQ.eyJleHAiOjE3NTMwOTg3NjAsImlhdCI6MTc1MzA5Njk2MCwianRpIjoiY2VkZTAwOTUtNDg2NS00ZWVmLTk1NGMtZjU3M2JlNjUzMTc3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4Mzg0L3JlYWxtcy92Y2ktcmVhbG0iLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgzODQvcmVhbG1zL3ZjaS1yZWFsbSIsInN1YiI6ImQ4NmY1ZWY3LWEzMWEtNDM0MC1hYzZjLWI0NmUwYTNkYzQ4MCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJ2Y2ktc3ByaW5nYm9vdC1hcGkiLCJzZXNzaW9uX3N0YXRlIjoiYzA1MGZhZDYtODk2MS00ZjVhLTlkNjAtNjRlOGEwNjI3ZTI4Iiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiYzA1MGZhZDYtODk2MS00ZjVhLTlkNjAtNjRlOGEwNjI3ZTI4In0.EwOxZsaUKTxzd-VJ8x0ShyL9dCOy0oYjyquCK3h4kme4ukvdUXm-tNGIX4Jn5Y7ib7uGLGICjk8_v_MggdJBLw",
        "token_type": "Bearer",
        "not-before-policy": 0,
        "session_state": "c050fad6-8961-4f5a-9d60-64e8a0627e28",
        "scope": "profile email"
    }

### Confidential

    ❗ If client is confidential, use this format instead:

    -d "client_secret=YOUR_SECRET"
    Or in Postman, include:
    
    client_id    
    client_secret

### ✅ To create a new user in Keycloak via Admin REST API:
    You must use:
    
    POST http://localhost:8384/admin/realms/vci-realm/users
    This is the correct method to create a user.

🔁 Other relevant Keycloak Admin API methods:
    Purpose	                    Method	        Endpoint
-----------------------------------------------------------------------------
    Create a new user	        POST	        /admin/realms/{realm}/users
    Get all users	            GET	            /admin/realms/{realm}/users
    Get a specific user by ID	GET	            /admin/realms/{realm}/users/{id}
    Update user	                PUT	            /admin/realms/{realm}/users/{id}
    Delete user	                DELETE	        /admin/realms/{realm}/users/{id}

✅ Example: Creating a user with POST

    curl -X POST http://localhost:8384/admin/realms/vci-realm/users \
    -H "Authorization: Bearer <access_token>" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser",
        "email": "test@example.com",
        "enabled": true
    }'

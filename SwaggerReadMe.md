### SpringBoot Dependency
    ✅ Recommended Swagger Dependency (Springdoc OpenAPI)

    🔧 Add this to your pom.xml (for Spring Boot 3+):    
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>
    🔁 For Spring Boot 2.x, use version 1.7.0 instead.

### 🚀 Accessing Swagger UI:
    http://localhost:9791/swagger-ui/index.html 
    or 
    http://localhost:9791/swagger-ui.html

### 🧩 Optional Customization (application.yml)

    springdoc:
        api-docs:
            path: /v3/api-docs
        swagger-ui:
            path: /swagger-ui.html
            operationsSorter: method
            tagsSorter: alpha

### ✏️ Add Metadata using @OpenAPIDefinition (Optional)
    @OpenAPIDefinition(
    info = @Info(title = "My API", version = "1.0", description = "Demo API"),
    servers = {@Server(url = "/", description = "Default Server")}
    )
    @SpringBootApplication
    public class MyApp { ... }

### 🧪 Annotate Your Controllers
    @RestController
    @RequestMapping("/api/users")
    public class UserController {
    
        @Operation(summary = "Get user by ID")
        @ApiResponse(responseCode = "200", description = "User found")
        @GetMapping("/{id}")
        public User getUser(@PathVariable Long id) {
            return new User(id, "John");
        }
    }

### 🆚 springfox (Deprecated)
    Avoid using springfox-swagger2 and springfox-swagger-ui unless you're on very old projects. It's no longer maintained well and often breaks with Spring Boot 3+.

### 📦 Summary
    Purpose	        Tool/Dependency
-------------------------------------------------------------
    Swagger UI	    springdoc-openapi-starter-webmvc-ui
    Spec format	    OpenAPI 3.0 (YAML/JSON)
    Annotations	    @Operation, @Parameter, @OpenAPIDefinition


### ✅ Quick Checklist to Keep It Smooth:
    📘 @OpenAPIDefinition (optional): Add global info like title, version, security globally.    
    🔐 Token Auth: You can configure Swagger to accept Bearer tokens under Authorize button.    
    🔎 @SecurityRequirement: Add per-controller if you want to show auth needs in Swagger UI.   
    ⚙️ Custom Grouping/Tagging: Use @Tag(name="...") on your controllers for better grouping.

    @Configuration
    public class SwaggerConfig {
    
        @Bean
        public OpenAPI customOpenAPI() {
            final String securitySchemeName = "bearerAuth";
            return new OpenAPI()
                    .info(new Info()
                            .title("Virtual|Physical Card Issuance App")
                            .version("1.0")
                            .description("Collection of APIs for different modules"))
                    .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                    .components(new Components().addSecuritySchemes(securitySchemeName,
                            new SecurityScheme()
                                    .name(securitySchemeName)
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")));
        }
    }

### ✅ 1. Swagger JSON for Import in Postman
    You can download the OpenAPI (Swagger) JSON like this:
        URL: http://localhost:8080/v3/api-docs
        Or if grouped: http://localhost:8080/v3/api-docs/public-api or admin-api

### 🔽 To Import into Postman:
    Open Postman.    
    Click "Import" → "Link" or "File".    
    Paste the Swagger JSON URL or upload the exported .json file.    
    Postman auto-generates a collection with endpoints.
    
### 🎨 2. Custom UI Branding (Logo, Colors) for Swagger UI
    To fully customize Swagger UI, you have two options:    
        Option A: Minimal Swagger UI customizations
    Use application.yml:

    springdoc:
    swagger-ui:
    title: "Fintech Platform API Docs"
    path: /swagger-ui.html
    display-operation-id: true
    display-request-duration: true
    defaultModelsExpandDepth: -1
    persistAuthorization: true

    You can override the Swagger favicon/logo by adding a custom static file:

### Place your logo at:
    src/main/resources/static/swagger-ui/swagger-custom.css

    /* swagger-custom.css */
    .swagger-ui .topbar {
        background-color: #0d47a1; /* deep blue */
    }

    .swagger-ui .topbar-wrapper img {
        content: url('/swagger-ui/my-logo.svg'); /* your logo path */
        width: 150px;
    }
    And add this to application.yml:

    springdoc:
    swagger-ui:
    customCssUrl: /swagger-ui/swagger-custom.css
### ✅ Make sure your static files are served from /src/main/resources/static/swagger-ui/

    Option B: Full UI Customization (host your own Swagger UI)
    Download the Swagger UI static HTML bundle.
    Customize index.html — change logos, titles, colors.
    Deploy it as a static site (or from Spring static/).
    Set url = http://localhost:8080/v3/api-docs in index.html.
    
    This is best for production-style branded API portals.

### 🔐 3. Role-Based Swagger Endpoint Grouping
    If your API has roles like ROLE_ADMIN, ROLE_MERCHANT, ROLE_USER, and you want to separate their endpoints:
    Use GroupedOpenApi and @PreAuthorize filtering.
### ✳️ Controller Example:
    @RestController
    @RequestMapping("/api/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public class AdminController {
        @GetMapping("/dashboard")
        public String getDashboard() { return "Admin Dashboard"; }
    }
### ✳️ Group Configuration:

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
        .group("admin-api")
        .pathsToMatch("/api/admin/**")
        .addOpenApiMethodFilter(method ->
        method.isAnnotationPresent(PreAuthorize.class) &&
        method.getAnnotation(PreAuthorize.class).value().contains("ADMIN"))
        .build();
    }
    Repeat for other roles like "MERCHANT" and "USER".

### ✅ Summary
    Feature	                        Implementation
-----------------------------------------------------------------------------
    Import Swagger to Postman	    /v3/api-docs JSON
    Custom UI (logo/colors)	        swagger-custom.css, override logo
    Role-based endpoint grouping	GroupedOpenApi + @PreAuthorize filters
------------------------------------------------------------------------------


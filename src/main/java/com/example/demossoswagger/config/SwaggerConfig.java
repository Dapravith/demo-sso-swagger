package com.example.demossoswagger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Bean
    public OpenAPI customOpenAPI() {
        // Define the OAuth2 security scheme
        SecurityScheme oauth2Scheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl(keycloakAuthServerUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/auth")
                                .tokenUrl(keycloakAuthServerUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token")
                        ));

        // Define a security requirement with the OAuth2 scheme
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("OAuth2");

        return new OpenAPI()
                .info(new Info()
                        .title("Spring Keycloak Project API")
                        .version("1.0.0")
                        .description("API documentation for Spring Boot application with SSO Keycloak integration"))
                .addServersItem(new Server().url(apiBaseUrl))
                .addSecurityItem(securityRequirement)  // Apply security globally
                .schemaRequirement("OAuth2", oauth2Scheme); // Register OAuth2 security scheme
    }
}

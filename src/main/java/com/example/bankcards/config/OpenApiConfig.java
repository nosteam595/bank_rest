package com.example.bankcards.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.example.bankcards.util.AppConstants.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = OPEN_API_SECURITY_SCHEME_NAME;
        return new OpenAPI()
                .info(new Info()
                        .title(OPEN_API_TITLE)
                        .version(OPEN_API_VERSION)
                        .description(OPEN_API_DESCRIPTION))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme(OPEN_API_SECURITY_SCHEME)
                                        .bearerFormat(OPEN_API_SECURITY_SCHEME_BEARER_FORMAT)));
    }
}

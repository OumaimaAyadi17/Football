package com.api.football.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour la documentation OpenAPI/Swagger.
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nice Football API")
                        .description("API REST pour la gestion de l'équipe de football de Nice en Ligue 1")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nice Football API Team")
                                .email("contact@nice-football.com")
                                .url("https://www.ogcnice.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}


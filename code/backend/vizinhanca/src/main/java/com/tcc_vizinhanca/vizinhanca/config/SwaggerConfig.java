package com.tcc_vizinhanca.vizinhanca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vizinhança API")
                        .description("API do sistema de gerenciamento de condomínios Vizinhança")
                        .version("1.0.04.26")
                        .contact(new Contact()
                                .name("Leonardo Scotti")
                                .email("leonardo.scotti07@email.com")
                        )
                        .license(new License()
                                .name("MIT License")
                        )
                );
    }

}

package com.cwcdev.pix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pixOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pix API")
                        .description("API de gestão de Clientes e Chaves Pix")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Pix")
                                .email("cwcdev@hotmail.com")
                                .url("https://pixapi.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor Local"),
                        new Server().url("https://api.pix.com").description("Servidor Produção")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação Completa")
                        .url("https://pixapi.com/docs"));
    }
}

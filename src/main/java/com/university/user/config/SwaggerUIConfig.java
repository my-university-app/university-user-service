package com.university.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;


@Configuration
@OpenAPIDefinition
public class SwaggerUIConfig {

    @Bean
    public OpenAPI springUserServerAPI(){
        var server = new Server();
        server.url("/");

        return new OpenAPI()
                .servers(Collections.singletonList(server))
                .info(new Info().title("University - User Server")
                        .description("The server must get the user access the university system")
                        .version("v1.0.0")
                        .license(new License().name("All rights reserved")));
    }

    @Bean
    public GroupedOpenApi publicApi(){
        return GroupedOpenApi.builder()
                .group("universityapp-user-server")
                .packagesToScan("com.university.user.controller")
                .pathsToMatch("/**")
                .build();
    }

}

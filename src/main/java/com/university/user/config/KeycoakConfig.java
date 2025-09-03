package com.university.user.config;

import com.university.user.service.KeycloakAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycoakConfig {

    @Bean
    public KeycloakAPI keycloakAPI(
            @Value("${keycloak.server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret
    ){
        return new KeycloakAPI(serverUrl, realm, clientId, clientSecret);
    }
}

package com.university.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
              http
                .csrf(csrf -> csrf.disable())
                      .authorizeHttpRequests(auth -> auth
                              .requestMatchers(
                                      "auth/**",
                                      "/ping",
                                      "/actuator/**",
                                      "/api/swagger/**",
                                      "/v3/api-docs/**"
                              ).permitAll()
                              .anyRequest().authenticated());
        // Configura o resource server para usar o seu conversor de roles
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtRoleConverter())));
                return http.build();
    }
}

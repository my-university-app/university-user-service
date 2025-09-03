package com.university.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.user.dto.UserRequest;
import com.university.user.dto.keycloak.Credentials;
import com.university.user.dto.keycloak.User;
import com.university.user.util.ConverterJSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

public class KeycloakAPI {
    private static String URI_TOKEN = "/realms/%s/protocol/openid-connect/token";
    private static String BODY_TOKEN = "grant_type=client_credentials&client_id={CLIENT_ID}&client_secret={CLIENT_SECRET}";
    private static String URI_USER = "/admin/realms/{REALM}/users";

    private static String AUTHORIZATION_TOKEN = "Bearer {TOKEN}";

    private final WebClient webClient;
    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    public KeycloakAPI(
            @Value("${keycloak.server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret
    ) {
        this.webClient = WebClient.builder().baseUrl(serverUrl).build();
        this.serverUrl = serverUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAdminAccessToken()
    {
        String tokenUrl = String.format(URI_TOKEN, realm);
        String body = BODY_TOKEN
                .replace("{CLIENT_ID}", clientId)
                .replace("{CLIENT_SECRET}", clientSecret);
        JsonNode res = executeAdminPost(tokenUrl,body);
        if (res == null || res.get("access_token") == null) throw new RuntimeException("Não foi possível obter token admin do Keycloak");
        return res.get("access_token").asText();
    }

    public void createUser(String accessToken, UserRequest userRequest) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Credentials> list = new ArrayList<>();
        String url = URI_USER.replace("{REALM}", realm);
        String authorizationToken = AUTHORIZATION_TOKEN.replace("{TOKEN}", accessToken);

        Credentials credentials = Credentials.builder()
                .type("password ")
                .value(userRequest.getPassword())
                .temporary(false).build();
        list.add(credentials);

        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .enabled(true)
                .credentials(list)
                .build();

        System.out.println("USer JSON: "+ ConverterJSON.toString(user));
        JsonNode res =  executePost(url, authorizationToken, ConverterJSON.toString(user));
        System.out.println("JSON Resposta: "+ mapper.writeValueAsString(res));
    }


    private JsonNode executeAdminPost(String uri, String body){
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    private JsonNode executePost(String uri,String token, String body){
        return webClient.post()
                .uri(uri)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}

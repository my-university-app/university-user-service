package com.university.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.user.dto.UserRequest;
import com.university.user.dto.keycloak.*;
import com.university.user.util.ConverterJSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


public class KeycloakAPI {
    private static final Logger log = LoggerFactory.getLogger(KeycloakAPI.class);
    private static String URI_TOKEN = "/realms/%s/protocol/openid-connect/token";
    private static String BODY_TOKEN = "grant_type=client_credentials&client_id=%s&client_secret=%s";
    private static String URI_USER = "/admin/realms/%s/users";
    private static String URI_ROLES_SEARCH = "/admin/realms/%s/roles/%s";
    private static String URL_ROLES_ADD_USER = "/admin/realms/%s/users/%s/role-mappings/realm";
    private static String URL_LOGIN_USER = "/realms/%s/protocol/openid-connect/token";

    private static String AUTHORIZATION_TOKEN = "Bearer %s";

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
        log.info("KeycloakAPI initialized");
        this.webClient = WebClient.builder().baseUrl(serverUrl).build();
        this.serverUrl = serverUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAdminAccessToken()
    {
        String tokenUrl = String.format(URI_TOKEN, realm);
        String body = String.format(BODY_TOKEN, clientId,clientSecret);
        JsonNode res = executeTokenPost(tokenUrl,body);
        if (res == null || res.get("access_token") == null) throw new RuntimeException("Não foi possível obter token admin do Keycloak");
        return res.get("access_token").asText();
    }

    public ResponseEntity<String> createUser(String accessToken, UserRequest userRequest) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<CredentialsDTO> list = new ArrayList<>();

        CredentialsDTO credentialsDTO = CredentialsDTO.builder()
                .type("password ")
                .value(userRequest.getPassword())
                .temporary(false).build();
        list.add(credentialsDTO);

        UserDTO userDTO = UserDTO.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .enabled(true)
                .credentials(list)
                .build();

        Mono<ResponseEntity<String>> monoResponse = post(getRealmFromURL(),
                accessToken,
                ConverterJSON.toString(userDTO))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    return  Mono.just(ResponseEntity
                            .status(ex.getRawStatusCode())
                            .headers(ex.getHeaders())
                            .body(ex.getResponseBodyAsString()));
                });

        ResponseEntity<String> response =  monoResponse.block();
        log.info("HTTP Status (from ResponseEntity): " + response.getStatusCode());
        log.info("Headers (from ResponseEntity): " + response.getHeaders());
        log.info("Response Body (from ResponseEntity): " + response.getBody());
        return response;
    }

    public ResponseEntity<String> setupRoleByUser(String accessToken, String userId) {
        RoleDTO roleDTO = null;
        ResponseEntity<String> responseRole = getRolesBySearch(accessToken);

        if(responseRole.getStatusCode().is2xxSuccessful()){
            JsonNode json = ConverterJSON.toJSONObject(responseRole.getBody());
            roleDTO = ConverterJSON.toEntity(responseRole.getBody(), RoleDTO.class);
            log.info("Converted to roleDTO: "+ roleDTO.toString());
        }

        RoleMappingDTO roleMappingDTO = RoleMappingDTO.builder()
                .id(roleDTO.getId())
                .name(roleDTO.getName())
                .build();

        List<RoleMappingDTO> rolesMapping = new ArrayList<>();
        rolesMapping.add(roleMappingDTO);

        Mono<ResponseEntity<String>> monoResponse = post(
                String.format(URL_ROLES_ADD_USER,realm, userId),
                accessToken,
                ConverterJSON.toString(rolesMapping));

        return monoResponse.block();
    }

    public ResponseEntity<String> getTokenLoginByUser(String accessToken, UserLoginRequest userLoginRequest){

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret",clientSecret);
        formData.add("username", userLoginRequest.getUsername());
        formData.add("password", userLoginRequest.getPassword());

        Mono<ResponseEntity<String>> monoResponse = post(String.format(URL_LOGIN_USER, realm), accessToken, formData);
        return monoResponse.block();
    }

    private ResponseEntity<String> getRolesBySearch(String accessToken){
        return  getRolesBySearch(accessToken, "student");
    }

    private ResponseEntity<String>  getRolesBySearch(String accessToken, String roleName){
      Mono<ResponseEntity<String>> mono = get(String.format(URI_ROLES_SEARCH, this.realm, roleName), accessToken);
      return mono.block();
    }


    private JsonNode executeTokenPost(String uri, String body){
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    private Mono<ResponseEntity<String>> post(String uri, String token, String body){
        return webClient.post()
                .uri(uri)
                .header("Authorization", setBearerToken(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(bodyStr -> ResponseEntity
                                        .status(response.statusCode())
                                        .headers(response.headers().asHttpHeaders())
                                        .body(bodyStr))
                );
    }

    private Mono<ResponseEntity<String>> post(String uri,String token, MultiValueMap<String, String> formData){
        return webClient.post()
                .uri(uri)
                .header("Authorization", setBearerToken(token))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(bodyStr -> ResponseEntity
                                        .status(response.statusCode())
                                        .headers(response.headers().asHttpHeaders())
                                        .body(bodyStr))
                );
    }


    public Mono<ResponseEntity<String>> get(String uri,String token, MultiValueMap<String, String> params) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParams(params)
                        .path(uri)
                        .build())
                .header("Authorization", setBearerToken(token))
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(bodyStr -> ResponseEntity
                                        .status(response.statusCode())
                                        .headers(response.headers().asHttpHeaders())
                                        .body(bodyStr))
                );
    }

    public Mono<ResponseEntity<String>> get(String uri,String token) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .build())
                .header("Authorization", setBearerToken(token))
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(bodyStr -> ResponseEntity
                                        .status(response.statusCode())
                                        .headers(response.headers().asHttpHeaders())
                                        .body(bodyStr))
                );
    }

    private String getRealmFromURL() {
        return String.format(URI_USER, realm);
    }

    private String setBearerToken(String token) {
        return String.format(AUTHORIZATION_TOKEN,token);
    }

}

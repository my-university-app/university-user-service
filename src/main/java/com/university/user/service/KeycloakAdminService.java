package com.university.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.university.user.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
@RequiredArgsConstructor
public class KeycloakAdminService {
    final Logger log =  LoggerFactory.getLogger(KeycloakAdminService.class);

   private final KeycloakAPI keycloakAPI;

   public void createStudentUser(UserRequest userRequest) throws JsonProcessingException {

       log.info("Starting the create new user");
       String token = keycloakAPI.getAdminAccessToken();
       log.info("Generate new token of the keycloack: "+ token);
       log.info("The new user : "+ userRequest.toString());
       keycloakAPI.createUser(token, userRequest);
    }


//    public void assignRoleToUser(String userId, String roleName) {
//        String token = obtainAdminAccessToken();
//        // obter representação do role
//        JsonNode role = webClient.get()
//                .uri("/admin/realms/" + realm + "/roles/" + roleName)
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .block();
//        if (role == null) throw new RuntimeException("Role não encontrado: " + roleName);
//
//        // o payload para atribuição é a representação do role
//        webClient.post()
//                .uri("/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
//                .header("Authorization", "Bearer " + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(List.of(role))
//                .retrieve()
//                .toBodilessEntity()
//                .block();
//    }
//
//    private String findUserIdByUsername(String username, String token) {
//        JsonNode res = webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/admin/realms/" + realm + "/users")
//                        .queryParam("username", username)
//                        .queryParam("exact", true)
//                        .build())
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .block();
//        if (res != null && res.isArray() && res.size() > 0) {
//            return res.get(0).get("id").asText();
//        }
//        return null;
//    }
}

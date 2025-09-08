package com.university.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.university.user.dto.UserRequest;
import com.university.user.exception.UserExistException;
import com.university.user.util.ConverterJSON;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class KeycloakAdminService {
    final Logger log =  LoggerFactory.getLogger(KeycloakAdminService.class);
   private final KeycloakAPI keycloakAPI;

   public void createStudentUser(UserRequest userRequest) throws JsonProcessingException, UserExistException {
       ResponseEntity<String> entityResponse;
       HttpStatusCode status;
       log.info("Starting the create new user");
       String token = keycloakAPI.getAdminAccessToken();
       log.info("Generate new token of the keycloack: "+ token);
       log.info("The new user : "+ userRequest.toString());

       entityResponse =  keycloakAPI.createUser(token, userRequest);

       status = entityResponse.getStatusCode();

       if (status.is4xxClientError()){
           JsonNode body =  ConverterJSON.toJSONObject(entityResponse.getBody());
           throw new UserExistException(Objects.requireNonNull(body).get("errorMessage").asText());
       }

       String userId = null;
       String location;
       if(status.is2xxSuccessful()){
           location = entityResponse.getHeaders().getFirst("Location");
           userId = location != null ? location.substring(location.lastIndexOf("/") + 1) : null;
       }
       log.info("User Id: "+userId);

       entityResponse = keycloakAPI.setupRoleByUser(token, userId);

       status = entityResponse.getStatusCode();
       if(status.is2xxSuccessful()){
           log.info(
                   String.format("UserId[%s] atualizado roles com sucesso",
                           userId)
           );
       }

    }
}

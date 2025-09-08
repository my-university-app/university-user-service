package com.university.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.university.user.dto.UserRequest;
import com.university.user.dto.UserResponse;
import com.university.user.service.KeycloakAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@Tag(name = "User", description = "Endpoints for user access the university app.")
public class UserController {
    @Autowired
    KeycloakAdminService keycloakAdminService;


    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The user was create"),
            @ApiResponse(responseCode = "500", description = "the error server intern")
    })
    @PostMapping()
    public ResponseEntity<?> register(@Validated @RequestBody UserRequest userRequest) throws JsonProcessingException {
        keycloakAdminService.createStudentUser(userRequest);
        return ResponseEntity.ok(UserResponse.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .build());
    }

}

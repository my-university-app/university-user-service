package com.university.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.university.user.dto.UserRequest;
import com.university.user.dto.UserResponse;
import com.university.user.dto.ValidateTokenRequest;
import com.university.user.dto.keycloak.RefreshTokenRequest;
import com.university.user.dto.AuthLoginRequest;
import com.university.user.service.KeycloakAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "auth", description = "Endpoints for user access the university app.")
public class AuthController {
    @Autowired
    KeycloakAdminService keycloakAdminService;


    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The user was create"),
            @ApiResponse(responseCode = "500", description = "the error server intern")
    })

    @PostMapping("user")
    public ResponseEntity<?> register(@Validated @RequestBody UserRequest userRequest) throws JsonProcessingException {
        keycloakAdminService.createStudentUser(userRequest);
        return ResponseEntity.ok(UserResponse.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .build());
    }

    @PostMapping("login")
    public ResponseEntity<?> loginUser(@Validated @RequestBody AuthLoginRequest authLoginRequest) {
        return ResponseEntity.ok(keycloakAdminService.getLoginByUser(authLoginRequest));
    }

    @PostMapping("logout")
    public ResponseEntity<?> logoutUser(@Validated @RequestBody RefreshTokenRequest refreshTokenRequest){
        keycloakAdminService.logoutByUser(refreshTokenRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("validate")
    public ResponseEntity<?> validateToken(@RequestBody @Valid ValidateTokenRequest validateTokenRequest) {
      return ResponseEntity.ok(keycloakAdminService.validateToken(validateTokenRequest.getToken()));
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(keycloakAdminService.refreshTokenByUser(refreshTokenRequest));
    }

}

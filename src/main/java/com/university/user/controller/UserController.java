package com.university.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.university.user.dto.UserRequest;
import com.university.user.service.KeycloakAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    KeycloakAdminService keycloakAdminService;

    @PostMapping()
    public ResponseEntity<?> register(@Validated @RequestBody UserRequest userRequest) throws JsonProcessingException {
        keycloakAdminService.createStudentUser(userRequest);
        return ResponseEntity.ok(userRequest);
    }

}

package com.university.user.dto.keycloak;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRequest {
    public String username;
    public String password;
}

package com.university.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthLoginRequest {
    public String username;
    public String password;
}

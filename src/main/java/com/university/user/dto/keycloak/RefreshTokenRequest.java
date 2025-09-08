package com.university.user.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenRequest {
    @NotBlank
    @JsonProperty("refresh_token")
    private String refreshToken;
}

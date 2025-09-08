package com.university.user.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
//@Builder
public class UserTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_expires_in")
    private int refreshExpiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("not-before-policy")
    private int notBeforePolicy;

    @JsonProperty("session_state")
    private String sessionState;

    @JsonProperty("scope")
    private String scope;

//    @JsonPOJOBuilder(withPrefix = "")
//    public static class RoleDTOBuilder {}
}

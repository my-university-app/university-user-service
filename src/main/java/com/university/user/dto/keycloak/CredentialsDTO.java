package com.university.user.dto.keycloak;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredentialsDTO {
    @NotBlank
    private String type;
    @NotBlank
    private String value;
    @NotBlank
    private Boolean temporary;
}

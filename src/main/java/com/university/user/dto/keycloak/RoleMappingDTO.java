package com.university.user.dto.keycloak;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleMappingDTO {
    private String id;
    private String name;
}

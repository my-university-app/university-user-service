package com.university.user.dto.keycloak;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonDeserialize(builder = RoleDTO.RoleDTOBuilder.class)
public class RoleDTO {
    private String id;
    private String name;
    private Boolean composite;
    private Boolean clientRole;
    private String containerId;


    @JsonPOJOBuilder(withPrefix = "")
    public static class RoleDTOBuilder {}
}

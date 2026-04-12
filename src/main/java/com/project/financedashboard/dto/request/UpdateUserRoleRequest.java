package com.project.financedashboard.dto.request;

import com.project.financedashboard.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for updating a user role")
public record UpdateUserRoleRequest(

        @Schema(example = "ANALYST",
                description = "VIEWER / ANALYST / ADMIN")
        @NotNull(message = "Role is required")
        Role role
) {}
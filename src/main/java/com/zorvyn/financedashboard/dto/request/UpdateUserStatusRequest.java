package com.zorvyn.financedashboard.dto.request;

import com.zorvyn.financedashboard.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for activating or deactivating a user")
public record UpdateUserStatusRequest(

        @Schema(example = "INACTIVE",
                description = "ACTIVE or INACTIVE")
        @NotNull(message = "Status is required")
        UserStatus status
) {}
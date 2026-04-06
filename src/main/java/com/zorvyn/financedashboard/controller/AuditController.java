package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.response.ApiResponse;
import com.zorvyn.financedashboard.dto.response.AuditLogResponse;
import com.zorvyn.financedashboard.enums.AuditAction;
import com.zorvyn.financedashboard.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;


import java.util.UUID;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs",
        description = "Admin only -> complete trail of all data changes")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all audit logs (Admin only)",
            description = "Full paginated audit trail of every " +
                    "create, update, and delete in the system",
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            description = "Page number (0-based)",
                            example = "0",
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            description = "Number of records per page",
                            example = "20",
                            schema = @Schema(type = "integer", defaultValue = "20")
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            description = "Sort field and direction",
                            example = "createdAt,desc",
                            schema = @Schema(type = "string", defaultValue = "createdAt,desc")
                    )
            }
    )
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAll(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Audit logs retrieved successfully",
                        auditService.getAll(pageable)));
    }

    @GetMapping("/entity/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get audit history for a specific record",
            description = "Returns full change history for one transaction by ID",
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            example = "0",
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            example = "20",
                            schema = @Schema(type = "integer", defaultValue = "20")
                    )
            }
    )
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getByEntity(

            @PathVariable UUID entityId,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Audit history retrieved successfully",
                        auditService.getByEntityId(entityId, pageable)));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get audit logs by user",
            description = "All actions performed by a specific user",
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            example = "0",
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            example = "20",
                            schema = @Schema(type = "integer", defaultValue = "20")
                    )
            }
    )
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getByUser(

            @PathVariable UUID userId,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User audit logs retrieved successfully",
                        auditService.getByUser(userId, pageable)));
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get audit logs by action type",
            description = "Filter by CREATED, UPDATED, or DELETED",
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            example = "0",
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            example = "20",
                            schema = @Schema(type = "integer", defaultValue = "20")
                    )
            }
    )
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getByAction(
            @PathVariable AuditAction action,
            @ParameterObject

            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Action audit logs retrieved successfully",
                        auditService.getByAction(action, pageable)));
    }
}
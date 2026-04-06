package com.zorvyn.financedashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Service health check - no token required")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Returns servic status.No authentication required."
    )
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status",    "UP",
                "version",   "1.0.0",
                "service",   "Finance Dashboard API",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
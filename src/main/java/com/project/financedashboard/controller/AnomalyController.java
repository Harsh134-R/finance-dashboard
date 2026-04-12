package com.project.financedashboard.controller;

import com.project.financedashboard.dto.response.AnomalyResponse;
import com.project.financedashboard.dto.response.AnomalySummaryResponse;
import com.project.financedashboard.dto.response.ApiResponse;
import com.project.financedashboard.service.AnomalyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
@Tag(name = "Anomaly Detection",
        description = "Identifies unusual transactions based on " +
                "category spending patterns")
public class AnomalyController {

    private final AnomalyService anomalyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Detect all anomalous transactions",
            description = """
                Flags transactions that deviate significantly from their
                category average.

                Severity levels:
                - WARNING  → 50%-200% above category average
                - CRITICAL → more than 200% above category average

                Returns a summary with total counts and full list
                of flagged transactions with deviation details.
                """
    )
    public ResponseEntity<ApiResponse<AnomalySummaryResponse>> detectAnomalies() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Anomaly detection complete",
                        anomalyService.detectAnomalies()));
    }

    @GetMapping("/critical")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Get critical anomalies only",
            description = "Returns only transactions more than 200% " +
                    "above their category average"
    )
    public ResponseEntity<ApiResponse<List<AnomalyResponse>>> getCritical() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Critical anomalies retrieved",
                        anomalyService.getCriticalOnly()));
    }
}
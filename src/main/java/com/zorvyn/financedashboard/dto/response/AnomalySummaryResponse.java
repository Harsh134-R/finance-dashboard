package com.zorvyn.financedashboard.dto.response;

import com.zorvyn.financedashboard.enums.AnomalySeverity;

import java.util.List;
import java.util.Map;

public record AnomalySummaryResponse(
        int totalFlagged,
        int criticalCount,
        int warningCount,
        Map<String, Long> byCategory,
        List<AnomalyResponse> anomalies
) {}
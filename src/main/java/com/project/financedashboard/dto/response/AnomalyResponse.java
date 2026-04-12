package com.project.financedashboard.dto.response;

import com.project.financedashboard.enums.AnomalySeverity;

import java.math.BigDecimal;

public record AnomalyResponse(
        TransactionResponse transaction,
        BigDecimal categoryAverage,
        BigDecimal deviationAmount,
        double deviationPercent,
        AnomalySeverity severity,
        String reason
) {}
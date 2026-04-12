package com.project.financedashboard.dto.response;

import java.math.BigDecimal;

public record MonthlyTrendResponse(
        int year,
        int month,
        String monthName,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {}
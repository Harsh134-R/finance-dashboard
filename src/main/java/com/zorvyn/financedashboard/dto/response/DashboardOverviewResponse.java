package com.zorvyn.financedashboard.dto.response;

import java.math.BigDecimal;

public record DashboardOverviewResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance,
        long totalTransactions,
        long incomeCount,
        long expenseCount
) {}
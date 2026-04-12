package com.project.financedashboard.dto.response;

import com.project.financedashboard.enums.Category;
import com.project.financedashboard.enums.TransactionType;

import java.math.BigDecimal;

public record CategorySummaryResponse(
        Category category,
        TransactionType type,
        BigDecimal total,
        long count
) {}
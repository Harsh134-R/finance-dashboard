package com.zorvyn.financedashboard.dto.response;

import com.zorvyn.financedashboard.enums.Category;
import com.zorvyn.financedashboard.enums.TransactionType;

import java.math.BigDecimal;

public record CategorySummaryResponse(
        Category category,
        TransactionType type,
        BigDecimal total,
        long count
) {}
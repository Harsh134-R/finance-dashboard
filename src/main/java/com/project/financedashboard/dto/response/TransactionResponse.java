package com.project.financedashboard.dto.response;

import com.project.financedashboard.entity.Transaction;
import com.project.financedashboard.enums.Category;
import com.project.financedashboard.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        BigDecimal amount,
        TransactionType type,
        Category category,
        LocalDate transactionDate,
        String notes,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getType(),
                t.getCategory(),
                t.getTransactionDate(),
                t.getNotes(),
                t.getCreatedBy().getFullName(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
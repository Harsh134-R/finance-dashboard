package com.zorvyn.financedashboard.dto.request;

import com.zorvyn.financedashboard.enums.Category;
import com.zorvyn.financedashboard.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request body for creating or updating a transaction")
public record TransactionRequest(

        @Schema(example = "75000.00",
                description = "Transaction amount — must be greater than 0")
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 13, fraction = 2,
                message = "Amount must have at most 13 digits and 2 decimal places")
        BigDecimal amount,

        @Schema(example = "INCOME",
                description = "INCOME or EXPENSE")
        @NotNull(message = "Transaction type is required")
        TransactionType type,

        @Schema(example = "SALARY",
                description = "SALARY / INVESTMENT / FOOD / RENT / UTILITIES / ENTERTAINMENT / OTHER")
        @NotNull(message = "Category is required")
        Category category,

        @Schema(example = "2026-01-15",
                description = "Date of transaction — cannot be in the future (yyyy-MM-dd)")
        @NotNull(message = "Transaction date is required")
        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate transactionDate,

        @Schema(example = "Monthly salary payment",
                description = "Optional notes — max 500 characters")
        @Size(max = 500, message = "Notes must not exceed 500 characters")
        String notes
) {}
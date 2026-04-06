package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.request.TransactionRequest;
import com.zorvyn.financedashboard.dto.response.ApiResponse;
import com.zorvyn.financedashboard.dto.response.TransactionResponse;
import com.zorvyn.financedashboard.enums.Category;
import com.zorvyn.financedashboard.enums.TransactionType;
import com.zorvyn.financedashboard.service.CsvExportService;
import com.zorvyn.financedashboard.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions",
        description = "Create, view, update, and delete financial records")
public class TransactionController {

    private final TransactionService transactionService;

    private final CsvExportService csvExportService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create transaction (Admin only)")
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponse response = transactionService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Transaction created successfully", response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "List all transactions",
            description = "Supports filtering by type, category, date range, " +
                    "and keyword search in notes. All params are optional.",
            parameters = {
                    @Parameter(
                            name = "type",
                            in = ParameterIn.QUERY,
                            description = "Filter by transaction type",
                            example = "INCOME",
                            schema = @Schema(type = "string",
                                    allowableValues = {"INCOME", "EXPENSE"})
                    ),
                    @Parameter(
                            name = "category",
                            in = ParameterIn.QUERY,
                            description = "Filter by category",
                            example = "SALARY",
                            schema = @Schema(type = "string",
                                    allowableValues = {
                                            "SALARY","INVESTMENT","FOOD",
                                            "RENT","UTILITIES","ENTERTAINMENT","OTHER"
                                    })
                    ),
                    @Parameter(
                            name = "startDate",
                            in = ParameterIn.QUERY,
                            description = "From date (yyyy-MM-dd)",
                            example = "2026-01-01",
                            schema = @Schema(type = "string", format = "date")
                    ),
                    @Parameter(
                            name = "endDate",
                            in = ParameterIn.QUERY,
                            description = "To date (yyyy-MM-dd)",
                            example = "2026-12-31",
                            schema = @Schema(type = "string", format = "date")
                    ),
                    @Parameter(
                            name = "keyword",
                            in = ParameterIn.QUERY,
                            description = "Search in notes",
                            example = "salary"
                    ),
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            description = "Page number (0-based)",
                            example = "0",
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            description = "Number of records per page",
                            example = "10",
                            schema = @Schema(type = "integer", defaultValue = "10")
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            description = "Sort field and direction",
                            example = "transactionDate,desc",
                            schema = @Schema(type = "string",
                                    defaultValue = "transactionDate,desc")
                    )
            }
    )
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAll(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @ParameterObject
            @PageableDefault(size = 10, sort = "transactionDate",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<TransactionResponse> transactions = transactionService.getAll(
                type, category, startDate, endDate, keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Transactions retrieved successfully", transactions));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(
            @PathVariable UUID id) {

        TransactionResponse response = transactionService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Transaction retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update transaction (Admin only)")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponse response = transactionService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Transaction updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete transaction (Admin only)",
            description = "Marks transaction as deleted - data is preserved")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        transactionService.softDelete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Transaction deleted successfully"));
    }


    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Export transactions to CSV",
            description = """
            Downloads a CSV file of transactions with the same
            filter options as the list endpoint.
            All parameters are optional-no filters returns all records.

            Parameters:
            - type: INCOME or EXPENSE
            - category: SALARY, FOOD, RENT, UTILITIES,
                        INVESTMENT, ENTERTAINMENT, OTHER
            - startDate: yyyy-MM-dd
            - endDate: yyyy-MM-dd
            - keyword: search in notes
            """
    )
    public void exportCsv(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            HttpServletResponse response) throws IOException {

        csvExportService.exportTransactions(
                type, category, startDate, endDate, keyword, response);
    }

}
package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.entity.Transaction;
import com.zorvyn.financedashboard.enums.Category;
import com.zorvyn.financedashboard.enums.TransactionType;
import com.zorvyn.financedashboard.repository.TransactionRepository;
import com.zorvyn.financedashboard.repository.TransactionSpecification;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void exportTransactions(
            TransactionType type,
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            HttpServletResponse response) throws IOException {

        // Build filename with timestamp
        String filename = "transactions_"
                + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".csv";

        // Set response headers for file download
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + "\"");
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");

        // Apply same filters as transaction list endpoint
        Specification<Transaction> spec = Specification.allOf(
                TransactionSpecification.notDeleted(),
                TransactionSpecification.hasType(type),
                TransactionSpecification.hasCategory(category),
                TransactionSpecification.fromDate(startDate),
                TransactionSpecification.toDate(endDate),
                TransactionSpecification.notesContains(keyword)
        );

        List<Transaction> transactions = transactionRepository
                .findAll(spec);

        PrintWriter writer = response.getWriter();

        // BOM for Excel UTF-8 compatibility
        writer.print('\uFEFF');

        // Header row
        writer.println(
                "ID," +
                        "Amount," +
                        "Type," +
                        "Category," +
                        "Transaction Date," +
                        "Notes," +
                        "Created By," +
                        "Created At"
        );

        // Data rows
        transactions.forEach(t -> writer.println(
                escapeCsv(t.getId().toString()) + "," +
                        t.getAmount() + "," +
                        t.getType().name() + "," +
                        t.getCategory().name() + "," +
                        t.getTransactionDate().format(DATE_FMT) + "," +
                        escapeCsv(t.getNotes()) + "," +
                        escapeCsv(t.getCreatedBy().getFullName()) + "," +
                        t.getCreatedAt().format(DATETIME_FMT)
        ));

        writer.flush();

        log.info("CSV export completed — {} records exported",
                transactions.size());
    }

    // Handles commas, quotes, and newlines inside field values
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
package com.project.financedashboard.service;

import com.project.financedashboard.dto.response.AnomalyResponse;
import com.project.financedashboard.dto.response.AnomalySummaryResponse;
import com.project.financedashboard.dto.response.TransactionResponse;
import com.project.financedashboard.entity.Transaction;
import com.project.financedashboard.enums.AnomalySeverity;
import com.project.financedashboard.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnomalyService {

    private final TransactionRepository transactionRepository;

    // Threshold - flag anything above 1.5x category average
    private static final double BASE_THRESHOLD = 1.2;

    public AnomalySummaryResponse detectAnomalies() {

        List<Object[]> rawResults = transactionRepository
                .findAnomalousTransactions(BASE_THRESHOLD);

        List<AnomalyResponse> anomalies = rawResults.stream()
                .map(row -> {

                    UUID id = (UUID) row[0];
                    BigDecimal amount = (BigDecimal) row[1];
                    BigDecimal avgAmount = (BigDecimal) row[2];

                    Transaction transaction = transactionRepository
                            .findByIdAndIsDeletedFalse(id)
                            .orElse(null);

                    if (transaction == null) return null;

                    return buildAnomalyResponse(transaction, avgAmount);
                })
                .filter(a -> a != null)
                .collect(Collectors.toList());

        long criticalCount = anomalies.stream()
                .filter(a -> a.severity() == AnomalySeverity.CRITICAL)
                .count();

        long warningCount = anomalies.stream()
                .filter(a -> a.severity() == AnomalySeverity.WARNING)
                .count();

        Map<String, Long> byCategory = anomalies.stream()
                .collect(Collectors.groupingBy(
                        a -> a.transaction().category().name(),
                        Collectors.counting()
                ));

        return new AnomalySummaryResponse(
                anomalies.size(),
                (int) criticalCount,
                (int) warningCount,
                byCategory,
                anomalies
        );
    }

    public List<AnomalyResponse> getCriticalOnly() {
        return detectAnomalies().anomalies().stream()
                .filter(a -> a.severity() == AnomalySeverity.CRITICAL)
                .collect(Collectors.toList());
    }



    private AnomalyResponse buildAnomalyResponse(
            Transaction transaction, BigDecimal categoryAverage) {

        BigDecimal amount = transaction.getAmount();

        // if deviation amount above average
        BigDecimal deviationAmount = amount.subtract(categoryAverage)
                .setScale(2, RoundingMode.HALF_UP);

        // deviation as percentage
        double deviationPercent = categoryAverage.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : amount.subtract(categoryAverage)
                .divide(categoryAverage, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        AnomalySeverity severity = classifySeverity(deviationPercent);

        String reason = buildReason(
                transaction, categoryAverage, deviationPercent, severity);

        return new AnomalyResponse(
                TransactionResponse.from(transaction),
                categoryAverage.setScale(2, RoundingMode.HALF_UP),
                deviationAmount,
                Math.round(deviationPercent * 100.0) / 100.0,
                severity,
                reason
        );
    }

    private AnomalySeverity classifySeverity(double deviationPercent) {
        if (deviationPercent >= 200.0) return AnomalySeverity.CRITICAL;
        if (deviationPercent >= 50.0)  return AnomalySeverity.WARNING;
        return AnomalySeverity.NORMAL;
    }

    private String buildReason(Transaction transaction,
                               BigDecimal categoryAverage,
                               double deviationPercent,
                               AnomalySeverity severity) {
        return String.format(
                "%s transaction of %.2f is %.1f%% above the %s category " +
                        "average of %.2f — classified as %s",
                transaction.getType().name(),
                transaction.getAmount(),
                deviationPercent,
                transaction.getCategory().name(),
                categoryAverage,
                severity.name()
        );
    }
}
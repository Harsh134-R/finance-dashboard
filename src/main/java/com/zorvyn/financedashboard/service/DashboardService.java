package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.response.CategorySummaryResponse;
import com.zorvyn.financedashboard.dto.response.DashboardOverviewResponse;
import com.zorvyn.financedashboard.dto.response.MonthlyTrendResponse;
import com.zorvyn.financedashboard.dto.response.TransactionResponse;
import com.zorvyn.financedashboard.enums.TransactionType;
import com.zorvyn.financedashboard.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardOverviewResponse getOverview() {
        BigDecimal totalIncome  = transactionRepository
                .sumByType(TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository
                .sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance   = totalIncome.subtract(totalExpense);

        long totalTransactions  = transactionRepository.countActive();
        long incomeCount        = transactionRepository
                .countByType(TransactionType.INCOME);
        long expenseCount       = transactionRepository
                .countByType(TransactionType.EXPENSE);

        return new DashboardOverviewResponse(
                totalIncome,
                totalExpense,
                netBalance,
                totalTransactions,
                incomeCount,
                expenseCount
        );
    }

    public List<CategorySummaryResponse> getCategoryWise() {
        return transactionRepository.getCategoryWiseSummary();
    }

    public List<TransactionResponse> getRecentActivity(int limit) {
        int safeLimit = Math.min(limit, 50); // cap at 50
        return transactionRepository
                .findRecentTransactions(PageRequest.of(0, safeLimit))
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    public List<MonthlyTrendResponse> getMonthlyTrends() {
        List<Object[]> raw = transactionRepository.getMonthlyRawTrends();

        return raw.stream().map(row -> {
            int year  = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            BigDecimal income  = row[2] != null
                    ? (BigDecimal) row[2] : BigDecimal.ZERO;
            BigDecimal expense = row[3] != null
                    ? (BigDecimal) row[3] : BigDecimal.ZERO;
            BigDecimal net = income.subtract(expense);
            String monthName = Month.of(month).name();

            return new MonthlyTrendResponse(
                    year, month, monthName, income, expense, net);
        }).toList();
    }
}
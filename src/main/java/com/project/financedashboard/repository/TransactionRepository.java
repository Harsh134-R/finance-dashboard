package com.project.financedashboard.repository;

import com.project.financedashboard.dto.response.CategorySummaryResponse;
import com.project.financedashboard.entity.Transaction;
import com.project.financedashboard.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, UUID>,
        JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndIsDeletedFalse(UUID id);

    Page<Transaction> findAllByIsDeletedFalse(Pageable pageable);

    // Dashboard queries

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.type = :type
            AND t.isDeleted = false
            """)
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("""
            SELECT COUNT(t)
            FROM Transaction t
            WHERE t.type = :type
            AND t.isDeleted = false
            """)
    long countByType(@Param("type") TransactionType type);

    @Query("""
            SELECT COUNT(t)
            FROM Transaction t
            WHERE t.isDeleted = false
            """)
    long countActive();

    @Query("""
            SELECT new com.project.financedashboard.dto.response.CategorySummaryResponse(
                t.category,
                t.type,
                SUM(t.amount),
                COUNT(t)
            )
            FROM Transaction t
            WHERE t.isDeleted = false
            GROUP BY t.category, t.type
            ORDER BY SUM(t.amount) DESC
            """)
    List<CategorySummaryResponse> getCategoryWiseSummary();

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.isDeleted = false
            ORDER BY t.transactionDate DESC
            """)
    List<Transaction> findRecentTransactions(Pageable pageable);

    @Query("""
            SELECT
                EXTRACT(YEAR FROM t.transactionDate),
                EXTRACT(MONTH FROM t.transactionDate),
                SUM(CASE WHEN t.type = com.project.financedashboard.enums.TransactionType.INCOME
                         THEN t.amount ELSE 0 END),
                SUM(CASE WHEN t.type = com.project.financedashboard.enums.TransactionType.EXPENSE
                         THEN t.amount ELSE 0 END)
            FROM Transaction t
            WHERE t.isDeleted = false
            GROUP BY
                EXTRACT(YEAR FROM t.transactionDate),
                EXTRACT(MONTH FROM t.transactionDate)
            ORDER BY
                EXTRACT(YEAR FROM t.transactionDate) DESC,
                EXTRACT(MONTH FROM t.transactionDate) DESC
            """)
    List<Object[]> getMonthlyRawTrends();

    //Anomaly detection

    @Query("""
            SELECT t.category, AVG(t.amount)
            FROM Transaction t
            WHERE t.isDeleted = false
            GROUP BY t.category
            """)
    List<Object[]> getCategoryAverages();

    @Query(value = """
            SELECT t.id, t.amount, cat_avg.avg_amount
            FROM transactions t
            JOIN (
                SELECT category,
                       AVG(amount) AS avg_amount
                FROM transactions
                WHERE is_deleted = false
                GROUP BY category
            ) cat_avg ON t.category = cat_avg.category
            WHERE t.is_deleted = false
              AND t.amount > cat_avg.avg_amount * :threshold
            ORDER BY (t.amount / cat_avg.avg_amount) DESC
            """,
            nativeQuery = true)
    List<Object[]> findAnomalousTransactions(
            @Param("threshold") double threshold);
}
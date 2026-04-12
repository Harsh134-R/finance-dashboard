package com.project.financedashboard.repository;

import com.project.financedashboard.entity.Transaction;
import com.project.financedashboard.enums.Category;
import com.project.financedashboard.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TransactionSpecification {

    private TransactionSpecification() {}

    public static Specification<Transaction> notDeleted() {
        return (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));
    }

    public static Specification<Transaction> hasType(TransactionType type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction()
                        : cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> hasCategory(Category category) {
        return (root, query, cb) ->
                category == null ? cb.conjunction()
                        : cb.equal(root.get("category"), category);
    }

    public static Specification<Transaction> fromDate(LocalDate startDate) {
        return (root, query, cb) ->
                startDate == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(
                        root.get("transactionDate"), startDate);
    }

    public static Specification<Transaction> toDate(LocalDate endDate) {
        return (root, query, cb) ->
                endDate == null ? cb.conjunction()
                        : cb.lessThanOrEqualTo(
                        root.get("transactionDate"), endDate);
    }

    public static Specification<Transaction> notesContains(String keyword) {
        return (root, query, cb) ->
                keyword == null || keyword.isBlank() ? cb.conjunction()
                        : cb.like(cb.lower(root.get("notes")),
                        "%" + keyword.toLowerCase() + "%");
    }
}
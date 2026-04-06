package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.request.TransactionRequest;
import com.zorvyn.financedashboard.dto.response.TransactionResponse;
import com.zorvyn.financedashboard.entity.Transaction;
import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.enums.AuditAction;
import com.zorvyn.financedashboard.enums.Category;
import com.zorvyn.financedashboard.enums.TransactionType;
import com.zorvyn.financedashboard.exception.ResourceNotFoundException;
import com.zorvyn.financedashboard.repository.TransactionRepository;
import com.zorvyn.financedashboard.repository.TransactionSpecification;
import com.zorvyn.financedashboard.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final HttpServletRequest httpRequest;

    public TransactionResponse create(TransactionRequest request) {
        User currentUser = getCurrentUser();

        Transaction transaction = Transaction.builder()
                .amount(request.amount())
                .type(request.type())
                .category(request.category())
                .transactionDate(request.transactionDate())
                .notes(request.notes())
                .createdBy(currentUser)
                .isDeleted(false)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Audit log - no old value on create
        auditService.log(
                "TRANSACTION",
                saved.getId(),
                AuditAction.CREATED,
                null,
                TransactionResponse.from(saved),
                getClientIp()
        );

        return TransactionResponse.from(saved);
    }

    public Page<TransactionResponse> getAll(
            TransactionType type,
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Pageable pageable) {

        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "startDate must not be after endDate");
        }

        Specification<Transaction> spec = Specification.allOf(
                TransactionSpecification.notDeleted(),
                TransactionSpecification.hasType(type),
                TransactionSpecification.hasCategory(category),
                TransactionSpecification.fromDate(startDate),
                TransactionSpecification.toDate(endDate),
                TransactionSpecification.notesContains(keyword)
        );

        return transactionRepository.findAll(spec, pageable)
                .map(TransactionResponse::from);
    }

    public TransactionResponse getById(UUID id) {
        return TransactionResponse.from(findActiveById(id));
    }

    public TransactionResponse update(UUID id, TransactionRequest request) {
        Transaction transaction = findActiveById(id);

        // Snapshot before change for audit
        TransactionResponse oldSnapshot = TransactionResponse.from(transaction);

        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setCategory(request.category());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setNotes(request.notes());

        Transaction saved = transactionRepository.save(transaction);

        // Audit log - old and new value
        auditService.log(
                "TRANSACTION",
                saved.getId(),
                AuditAction.UPDATED,
                oldSnapshot,
                TransactionResponse.from(saved),
                getClientIp()
        );

        return TransactionResponse.from(saved);
    }

    public void softDelete(UUID id) {
        Transaction transaction = findActiveById(id);

        // Snapshot before delete
        TransactionResponse oldSnapshot = TransactionResponse.from(transaction);

        transaction.setIsDeleted(true);
        transactionRepository.save(transaction);

        // Audit log - no new value on delete
        auditService.log(
                "TRANSACTION",
                id,
                AuditAction.DELETED,
                oldSnapshot,
                null,
                getClientIp()
        );
    }


    private Transaction findActiveById(UUID id) {
        return transactionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));
    }

    private String getClientIp() {
        String forwarded = httpRequest.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : httpRequest.getRemoteAddr();
    }
}
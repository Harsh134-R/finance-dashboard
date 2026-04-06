package com.zorvyn.financedashboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zorvyn.financedashboard.dto.response.AuditLogResponse;
import com.zorvyn.financedashboard.entity.AuditLog;
import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.enums.AuditAction;
import com.zorvyn.financedashboard.exception.ResourceNotFoundException;
import com.zorvyn.financedashboard.repository.AuditLogRepository;
import com.zorvyn.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // Called from TransactionService on every write operation
    public void log(String entityType,
                    UUID entityId,
                    AuditAction action,
                    Object oldValue,
                    Object newValue,
                    String ipAddress) {

        User currentUser = getCurrentUser();

        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .performedBy(currentUser.getId())
                .performedByEmail(currentUser.getEmail())
                .oldValue(toJson(oldValue))
                .newValue(toJson(newValue))
                .ipAddress(ipAddress)
                .build();

        auditLogRepository.save(auditLog);
    }

    // Query methods

    public Page<AuditLogResponse> getAll(Pageable pageable) {
        return auditLogRepository
                .findAllByOrderByCreatedAtDesc(pageable)
                .map(AuditLogResponse::from);
    }

    public Page<AuditLogResponse> getByEntityId(
            UUID entityId, Pageable pageable) {
        return auditLogRepository
                .findByEntityIdOrderByCreatedAtDesc(entityId, pageable)
                .map(AuditLogResponse::from);
    }

    public Page<AuditLogResponse> getByUser(
            UUID userId, Pageable pageable) {
        return auditLogRepository
                .findByPerformedByOrderByCreatedAtDesc(userId, pageable)
                .map(AuditLogResponse::from);
    }

    public Page<AuditLogResponse> getByAction(
            AuditAction action, Pageable pageable) {
        return auditLogRepository
                .findByActionOrderByCreatedAtDesc(action, pageable)
                .map(AuditLogResponse::from);
    }

    //Helpers

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize audit value: {}", e.getMessage());
            return obj.toString();
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));
    }
}
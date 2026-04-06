package com.zorvyn.financedashboard.repository;

import com.zorvyn.financedashboard.entity.AuditLog;
import com.zorvyn.financedashboard.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AuditLog> findByEntityIdOrderByCreatedAtDesc(
            UUID entityId, Pageable pageable);

    Page<AuditLog> findByPerformedByOrderByCreatedAtDesc(
            UUID performedBy, Pageable pageable);

    Page<AuditLog> findByActionOrderByCreatedAtDesc(
            AuditAction action, Pageable pageable);

    Page<AuditLog> findByEntityTypeOrderByCreatedAtDesc(
            String entityType, Pageable pageable);
}
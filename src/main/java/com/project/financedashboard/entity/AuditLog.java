package com.project.financedashboard.entity;

import com.project.financedashboard.enums.AuditAction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_entity_id",   columnList = "entity_id"),
        @Index(name = "idx_audit_performed_by", columnList = "performed_by"),
        @Index(name = "idx_audit_action",       columnList = "action"),
        @Index(name = "idx_audit_created_at",   columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;          // like "TRANSACTION"

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action;         // CREATED, UPDATED, DELETED

    @Column(name = "performed_by", nullable = false)
    private UUID performedBy;

    @Column(name = "performed_by_email", nullable = false)
    private String performedByEmail;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;            // JSON snapshot before change

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;            // JSON snapshot after change

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
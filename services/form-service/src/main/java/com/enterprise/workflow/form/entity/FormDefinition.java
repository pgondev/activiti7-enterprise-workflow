package com.enterprise.workflow.form.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity for Form Definition storage.
 * Stores form.io JSON schema in PostgreSQL JSONB column.
 */
@Entity
@Table(name = "form_definitions", indexes = {
    @Index(name = "idx_form_key", columnList = "form_key"),
    @Index(name = "idx_form_category", columnList = "category")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "form_key", nullable = false)
    private String key;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private String category;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(nullable = false)
    private boolean published = false;

    @Column(columnDefinition = "text")
    private String schema;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (version == null) version = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

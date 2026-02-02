package com.enterprise.workflow.form.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity for Application Bundle storage.
 * Represents a packaged deployment unit containing BPMN processes, forms, DMN
 * tables, etc.
 */
@Entity
@Table(name = "app_bundles", indexes = {
        @Index(name = "idx_bundle_key", columnList = "bundle_key"),
        @Index(name = "idx_bundle_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "bundle_key", nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(length = 2000)
    private String description;

    private String author;

    private String category;

    @Column(name = "manifest_json", columnDefinition = "TEXT", nullable = false)
    private String manifestJson;

    /**
     * ZIP file content stored as BLOB
     * For large bundles, consider storing in object storage (S3, MinIO)
     */
    @Lob
    @Column(name = "bundle_data")
    private byte[] bundleData;

    /**
     * Size of the bundle in bytes
     */
    @Column(name = "bundle_size")
    private Long bundleSize;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BundleStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deployed_at")
    private LocalDateTime deployedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "deployment_id")
    private String deploymentId;

    @Column(name = "deployment_error", length = 4000)
    private String deploymentError;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = BundleStatus.CREATED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Deployment status of the bundle
     */
    public enum BundleStatus {
        CREATED, // Bundle created but not deployed
        VALIDATING, // Bundle is being validated
        VALID, // Bundle passed validation
        INVALID, // Bundle failed validation
        DEPLOYING, // Deployment in progress
        DEPLOYED, // Successfully deployed
        FAILED, // Deployment failed
        ARCHIVED // Bundle archived (replaced by newer version)
    }
}

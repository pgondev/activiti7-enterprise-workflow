package com.enterprise.workflow.form.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity for Form Submission storage.
 */
@Entity
@Table(name = "form_submissions", indexes = {
    @Index(name = "idx_submission_form", columnList = "form_id"),
    @Index(name = "idx_submission_process", columnList = "process_instance_id"),
    @Index(name = "idx_submission_task", columnList = "task_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "form_id", nullable = false)
    private String formId;

    @Column(name = "form_version")
    private Integer formVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data;

    @Column(name = "submitted_by")
    private String submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "process_instance_id")
    private String processInstanceId;

    @Column(name = "task_id")
    private String taskId;

    @PrePersist
    protected void onSubmit() {
        submittedAt = LocalDateTime.now();
    }
}

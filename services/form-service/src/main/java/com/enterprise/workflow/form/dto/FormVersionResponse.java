package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response for form version info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormVersionResponse {
    private String id;
    private String formId;
    private Integer version;
    private Map<String, Object> schema;
    private LocalDateTime createdAt;
    private String createdBy;
}

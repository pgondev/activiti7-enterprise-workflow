package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for form definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDefinitionResponse {
    private String id;
    private String key;
    private String name;
    private String description;
    private String category;
    private Integer version;
    private boolean published;
    private Map<String, Object> schema;  // form.io JSON schema
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private List<String> linkedProcesses;
}

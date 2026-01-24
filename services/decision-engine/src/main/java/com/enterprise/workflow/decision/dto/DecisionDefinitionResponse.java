package com.enterprise.workflow.decision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for decision definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDefinitionResponse {
    private String id;
    private String key;
    private String name;
    private String description;
    private Integer version;
    private String deploymentId;
    private String resourceName;
    private String tenantId;
    private LocalDateTime deployedAt;
}

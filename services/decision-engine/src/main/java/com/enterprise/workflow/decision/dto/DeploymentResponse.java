package com.enterprise.workflow.decision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response for deployment operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentResponse {
    private String deploymentId;
    private String name;
    private LocalDateTime deployedAt;
    private List<String> decisionKeys;
}

package com.enterprise.workflow.decision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response for decision execution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionExecutionResponse {
    private String executionId;
    private String decisionKey;
    private String decisionId;
    private Integer version;
    private Map<String, Object> inputs;
    private List<Map<String, Object>> outputs;
    private LocalDateTime executedAt;
    private long executionTimeMs;
}

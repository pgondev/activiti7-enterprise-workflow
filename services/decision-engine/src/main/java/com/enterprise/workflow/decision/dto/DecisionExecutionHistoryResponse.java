package com.enterprise.workflow.decision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response for decision execution history.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionExecutionHistoryResponse {
    private String id;
    private String decisionKey;
    private String decisionName;
    private Integer version;
    private Map<String, Object> inputs;
    private List<Map<String, Object>> outputs;
    private String processInstanceId;
    private String activityId;
    private String executedBy;
    private LocalDateTime executedAt;
    private long executionTimeMs;
    private boolean successful;
    private String errorMessage;
}

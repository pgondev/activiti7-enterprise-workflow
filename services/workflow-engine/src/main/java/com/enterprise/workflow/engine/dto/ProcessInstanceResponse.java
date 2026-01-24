package com.enterprise.workflow.engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for process instance information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceResponse {
    
    private String id;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer processDefinitionVersion;
    private String businessKey;
    private String name;
    private String status;
    private String startUserId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, Object> variables;
    private String tenantId;
}

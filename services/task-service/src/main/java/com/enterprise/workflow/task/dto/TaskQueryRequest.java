package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Query request for filtering and searching tasks.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskQueryRequest {
    
    private String processInstanceId;
    private String processDefinitionKey;
    private String assignee;
    private Boolean unassignedOnly;
    private String candidateUser;
    private String candidateGroup;
    private String nameLike;
    private Integer priority;
    private Integer priorityGreaterThan;
    private Integer priorityLessThan;
    private LocalDateTime dueDateBefore;
    private LocalDateTime dueDateAfter;
    private LocalDateTime createDateBefore;
    private LocalDateTime createDateAfter;
    private List<String> taskIds;
    private String businessKey;
    private Boolean includeVariables;
    private Map<String, Object> variableEquals;
}

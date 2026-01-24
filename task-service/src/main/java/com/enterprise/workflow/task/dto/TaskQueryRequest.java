package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Task query request with comprehensive filters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskQueryRequest {
    
    private String assignee;
    private String candidateUser;
    private String candidateGroup;
    private List<String> candidateGroups;
    private String processInstanceId;
    private String processDefinitionKey;
    private String processDefinitionId;
    private String caseInstanceId;
    private String caseDefinitionKey;
    private String taskDefinitionKey;
    private String nameLike;
    private String descriptionLike;
    private Integer priority;
    private Integer priorityMin;
    private Integer priorityMax;
    private LocalDateTime dueBefore;
    private LocalDateTime dueAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime createdAfter;
    private Boolean unassigned;
    private Boolean includeVariables;
    private String tenantId;
    private String category;
    private String parentTaskId;
    private Boolean withoutParentTask;
}

package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for user task information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    
    private String id;
    private String name;
    private String description;
    private String assignee;
    private String owner;
    private String formKey;
    private Integer priority;
    private String status;
    
    // Process context
    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private String businessKey;
    
    // Candidates
    private List<String> candidateUsers;
    private List<String> candidateGroups;
    
    // Dates
    private LocalDateTime createTime;
    private LocalDateTime dueDate;
    private LocalDateTime claimTime;
    
    // Variables (loaded on demand)
    private Map<String, Object> taskVariables;
    private Map<String, Object> processVariables;
    
    // Form
    private String formDefinitionId;
    private boolean hasForm;
    
    // Comments count
    private int commentsCount;
    private int attachmentsCount;
}

package com.enterprise.workflow.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for Task representation.
 * Combines features from Camunda, Flowable, and Activiti task models.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    /**
     * Unique task identifier
     */
    private String id;

    /**
     * Task name/title
     */
    private String name;

    /**
     * Detailed task description
     */
    private String description;

    /**
     * Current assignee user ID
     */
    private String assignee;

    /**
     * Task owner (original creator)
     */
    private String owner;

    /**
     * Task priority (0-100, higher = more important)
     */
    private Integer priority;

    /**
     * Due date for task completion
     */
    private LocalDateTime dueDate;

    /**
     * Follow-up date for reminders
     */
    private LocalDateTime followUpDate;

    /**
     * Task creation timestamp
     */
    private LocalDateTime createTime;

    /**
     * Task claim timestamp
     */
    private LocalDateTime claimTime;

    /**
     * Parent process instance ID
     */
    private String processInstanceId;

    /**
     * Process definition ID
     */
    private String processDefinitionId;

    /**
     * Process definition key
     */
    private String processDefinitionKey;

    /**
     * Process definition name
     */
    private String processDefinitionName;

    /**
     * Execution ID within the process
     */
    private String executionId;

    /**
     * Case instance ID (for CMMN tasks)
     */
    private String caseInstanceId;

    /**
     * Case definition ID
     */
    private String caseDefinitionId;

    /**
     * Parent task ID (for subtasks)
     */
    private String parentTaskId;

    /**
     * Form key for task form
     */
    private String formKey;

    /**
     * Task category/type
     */
    private String category;

    /**
     * Tenant ID for multi-tenancy
     */
    private String tenantId;

    /**
     * Task status (CREATED, ASSIGNED, COMPLETED, DELEGATED, etc.)
     */
    private TaskStatus status;

    /**
     * Delegation state
     */
    private DelegationState delegationState;

    /**
     * Task variables
     */
    private Map<String, Object> variables;

    /**
     * Candidate users who can claim the task
     */
    private java.util.List<String> candidateUsers;

    /**
     * Candidate groups who can claim the task
     */
    private java.util.List<String> candidateGroups;

    /**
     * Task status enumeration
     */
    public enum TaskStatus {
        CREATED,
        ASSIGNED,
        CLAIMED,
        DELEGATED,
        RESOLVED,
        COMPLETED,
        CANCELLED
    }

    /**
     * Delegation state enumeration
     */
    public enum DelegationState {
        PENDING,
        RESOLVED
    }
}

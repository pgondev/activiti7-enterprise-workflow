package com.enterprise.workflow.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for Process Instance representation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceDTO {

    /**
     * Unique process instance identifier
     */
    private String id;

    /**
     * Business key for business correlation
     */
    private String businessKey;

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
     * Process definition version
     */
    private Integer processDefinitionVersion;

    /**
     * Process instance name
     */
    private String name;

    /**
     * Process instance description
     */
    private String description;

    /**
     * User who started the process
     */
    private String startUserId;

    /**
     * Process start timestamp
     */
    private LocalDateTime startTime;

    /**
     * Process end timestamp (null if still running)
     */
    private LocalDateTime endTime;

    /**
     * Current process status
     */
    private ProcessStatus status;

    /**
     * Parent process instance ID (for call activities)
     */
    private String parentProcessInstanceId;

    /**
     * Root process instance ID
     */
    private String rootProcessInstanceId;

    /**
     * Super execution ID
     */
    private String superExecutionId;

    /**
     * Tenant ID for multi-tenancy
     */
    private String tenantId;

    /**
     * Process variables
     */
    private Map<String, Object> variables;

    /**
     * Whether the process is suspended
     */
    private Boolean suspended;

    /**
     * Process status enumeration
     */
    public enum ProcessStatus {
        RUNNING,
        COMPLETED,
        SUSPENDED,
        CANCELLED,
        TERMINATED
    }
}

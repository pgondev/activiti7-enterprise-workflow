package com.enterprise.workflow.runtime.service;

import com.enterprise.workflow.common.dto.ProcessInstanceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Service interface for process instance management.
 * Provides operations for process execution lifecycle.
 */
public interface ProcessService {

    /**
     * Start a new process instance.
     *
     * @param processDefinitionKey The process definition key
     * @param businessKey Optional business key for correlation
     * @param variables Initial process variables
     * @return The created process instance
     */
    ProcessInstanceDTO startProcess(String processDefinitionKey, String businessKey, 
                                    Map<String, Object> variables);

    /**
     * Get a process instance by ID.
     *
     * @param processInstanceId The process instance ID
     * @return The process instance
     */
    ProcessInstanceDTO getProcessInstance(String processInstanceId);

    /**
     * Get process instances with optional filters.
     *
     * @param processDefinitionKey Optional filter by process definition key
     * @param status Optional filter by status
     * @param pageable Pagination parameters
     * @return Page of process instances
     */
    Page<ProcessInstanceDTO> getProcessInstances(String processDefinitionKey,
                                                  ProcessInstanceDTO.ProcessStatus status,
                                                  Pageable pageable);

    /**
     * Cancel a running process instance.
     *
     * @param processInstanceId The process instance ID
     * @param reason Optional cancellation reason
     */
    void cancelProcess(String processInstanceId, String reason);

    /**
     * Suspend a running process instance.
     *
     * @param processInstanceId The process instance ID
     * @return The updated process instance
     */
    ProcessInstanceDTO suspendProcess(String processInstanceId);

    /**
     * Activate a suspended process instance.
     *
     * @param processInstanceId The process instance ID
     * @return The updated process instance
     */
    ProcessInstanceDTO activateProcess(String processInstanceId);

    /**
     * Get all variables for a process instance.
     *
     * @param processInstanceId The process instance ID
     * @return Map of variable names to values
     */
    Map<String, Object> getVariables(String processInstanceId);

    /**
     * Set variables on a process instance.
     *
     * @param processInstanceId The process instance ID
     * @param variables Variables to set
     */
    void setVariables(String processInstanceId, Map<String, Object> variables);

    /**
     * Send a signal to a process instance.
     *
     * @param processInstanceId The process instance ID
     * @param signalName The signal name
     * @param variables Optional signal variables
     */
    void sendSignal(String processInstanceId, String signalName, Map<String, Object> variables);

    /**
     * Send a message to a process instance.
     *
     * @param processInstanceId The process instance ID
     * @param messageName The message name
     * @param variables Optional message variables
     */
    void sendMessage(String processInstanceId, String messageName, Map<String, Object> variables);
}

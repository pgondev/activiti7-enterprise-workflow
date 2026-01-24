package com.enterprise.workflow.engine.service;

import com.enterprise.workflow.engine.dto.ProcessStartRequest;
import com.enterprise.workflow.engine.dto.ProcessInstanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Service interface for Process Instance management.
 */
public interface ProcessService {

    /**
     * Start a new process instance.
     */
    ProcessInstanceResponse startProcess(ProcessStartRequest request);

    /**
     * Get process instances with optional filtering.
     */
    Page<ProcessInstanceResponse> getProcessInstances(
            String processDefinitionKey, 
            String status, 
            Pageable pageable);

    /**
     * Get a process instance by ID.
     */
    ProcessInstanceResponse getProcessInstance(String processInstanceId);

    /**
     * Cancel a process instance.
     */
    void cancelProcess(String processInstanceId, String reason);

    /**
     * Suspend a process instance.
     */
    ProcessInstanceResponse suspendProcess(String processInstanceId);

    /**
     * Activate a suspended process instance.
     */
    ProcessInstanceResponse activateProcess(String processInstanceId);

    /**
     * Get process variables.
     */
    Map<String, Object> getVariables(String processInstanceId);

    /**
     * Set process variables.
     */
    void setVariables(String processInstanceId, Map<String, Object> variables);

    /**
     * Send a signal to a process instance.
     */
    void sendSignal(String processInstanceId, String signalName, Map<String, Object> variables);

    /**
     * Send a message to a process instance.
     */
    void sendMessage(String processInstanceId, String messageName, Map<String, Object> variables);
}

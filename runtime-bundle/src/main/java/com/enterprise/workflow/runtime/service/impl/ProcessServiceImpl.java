package com.enterprise.workflow.runtime.service.impl;

import com.enterprise.workflow.common.dto.ProcessInstanceDTO;
import com.enterprise.workflow.common.exception.ResourceNotFoundException;
import com.enterprise.workflow.common.security.SecurityUtils;
import com.enterprise.workflow.runtime.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RuntimeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Implementation of ProcessService using Activiti7 Cloud Runtime.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRuntime processRuntime;
    private final RuntimeService runtimeService;
    private final SecurityUtils securityUtils;

    @Override
    public ProcessInstanceDTO startProcess(String processDefinitionKey, String businessKey,
            Map<String, Object> variables) {
        log.info("Starting process: {} with businessKey: {}", processDefinitionKey, businessKey);

        ProcessInstance processInstance = processRuntime.start(
                ProcessPayloadBuilder.start()
                        .withProcessDefinitionKey(processDefinitionKey)
                        .withBusinessKey(businessKey)
                        .withVariables(variables)
                        .build());

        log.info("Process started with ID: {}", processInstance.getId());
        return mapToDTO(processInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessInstanceDTO getProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = processRuntime.processInstance(processInstanceId);
        if (processInstance == null) {
            throw new ResourceNotFoundException("ProcessInstance", processInstanceId);
        }
        return mapToDTO(processInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProcessInstanceDTO> getProcessInstances(
            String processDefinitionKey,
            ProcessInstanceDTO.ProcessStatus status,
            org.springframework.data.domain.Pageable pageable) {

        Page<ProcessInstance> instances = processRuntime.processInstances(
                Pageable.of(pageable.getPageNumber(), pageable.getPageSize()));

        return new org.springframework.data.domain.PageImpl<>(
                instances.getContent().stream()
                        .filter(pi -> processDefinitionKey == null ||
                                pi.getProcessDefinitionKey().equals(processDefinitionKey))
                        .map(this::mapToDTO)
                        .toList(),
                pageable,
                instances.getTotalItems());
    }

    @Override
    public void cancelProcess(String processInstanceId, String reason) {
        log.info("Cancelling process: {} with reason: {}", processInstanceId, reason);

        processRuntime.delete(
                ProcessPayloadBuilder.delete()
                        .withProcessInstanceId(processInstanceId)
                        .withReason(reason)
                        .build());

        log.info("Process cancelled: {}", processInstanceId);
    }

    @Override
    public ProcessInstanceDTO suspendProcess(String processInstanceId) {
        log.info("Suspending process: {}", processInstanceId);

        ProcessInstance processInstance = processRuntime.suspend(
                ProcessPayloadBuilder.suspend()
                        .withProcessInstanceId(processInstanceId)
                        .build());

        log.info("Process suspended: {}", processInstanceId);
        return mapToDTO(processInstance);
    }

    @Override
    public ProcessInstanceDTO activateProcess(String processInstanceId) {
        log.info("Activating process: {}", processInstanceId);

        ProcessInstance processInstance = processRuntime.resume(
                ProcessPayloadBuilder.resume()
                        .withProcessInstanceId(processInstanceId)
                        .build());

        log.info("Process activated: {}", processInstanceId);
        return mapToDTO(processInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVariables(String processInstanceId) {
        return runtimeService.getVariables(processInstanceId);
    }

    @Override
    public void setVariables(String processInstanceId, Map<String, Object> variables) {
        log.info("Setting {} variables on process: {}", variables.size(), processInstanceId);

        processRuntime.setVariables(
                ProcessPayloadBuilder.setVariables()
                        .withProcessInstanceId(processInstanceId)
                        .withVariables(variables)
                        .build());
    }

    @Override
    public void sendSignal(String processInstanceId, String signalName, Map<String, Object> variables) {
        log.info("Sending signal '{}' to process: {}", signalName, processInstanceId);

        processRuntime.signal(
                ProcessPayloadBuilder.signal()
                        .withName(signalName)
                        .withVariables(variables)
                        .build());
    }

    @Override
    public void sendMessage(String processInstanceId, String messageName, Map<String, Object> variables) {
        log.info("Sending message '{}' to process: {}", messageName, processInstanceId);

        // runtimeService.createMessageCorrelation(messageName)
        // .processInstanceId(processInstanceId)
        // .setVariables(variables)
        // .correlate();
        throw new UnsupportedOperationException(
                "Message correlation not supported in Activiti 8.0.0 without Cloud Streams");
    }

    /**
     * Map Activiti ProcessInstance to DTO.
     */
    private ProcessInstanceDTO mapToDTO(ProcessInstance processInstance) {
        return ProcessInstanceDTO.builder()
                .id(processInstance.getId())
                .businessKey(processInstance.getBusinessKey())
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .processDefinitionKey(processInstance.getProcessDefinitionKey())
                .processDefinitionVersion(processInstance.getProcessDefinitionVersion())
                .name(processInstance.getName())
                .startUserId(processInstance.getInitiator())
                .startTime(processInstance.getStartDate() != null
                        ? LocalDateTime.ofInstant(processInstance.getStartDate().toInstant(),
                                ZoneId.systemDefault())
                        : null)
                .status(mapStatus(processInstance.getStatus()))
                .parentProcessInstanceId(processInstance.getParentId())
                .build();
    }

    /**
     * Map Activiti process status to DTO status.
     */
    private ProcessInstanceDTO.ProcessStatus mapStatus(ProcessInstance.ProcessInstanceStatus status) {
        if (status == null)
            return ProcessInstanceDTO.ProcessStatus.RUNNING;

        return switch (status) {
            case RUNNING -> ProcessInstanceDTO.ProcessStatus.RUNNING;
            case COMPLETED -> ProcessInstanceDTO.ProcessStatus.COMPLETED;
            case SUSPENDED -> ProcessInstanceDTO.ProcessStatus.SUSPENDED;
            case CANCELLED -> ProcessInstanceDTO.ProcessStatus.CANCELLED;
            default -> ProcessInstanceDTO.ProcessStatus.RUNNING;
        };
    }
}

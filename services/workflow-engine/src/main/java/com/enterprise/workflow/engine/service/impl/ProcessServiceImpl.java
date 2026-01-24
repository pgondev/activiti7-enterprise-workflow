package com.enterprise.workflow.engine.service.impl;

import com.enterprise.workflow.engine.dto.ProcessStartRequest;
import com.enterprise.workflow.engine.dto.ProcessInstanceResponse;
import com.enterprise.workflow.engine.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RuntimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ProcessService using Activiti7 Runtime.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRuntime processRuntime;
    private final RuntimeService runtimeService;

    @Override
    public ProcessInstanceResponse startProcess(ProcessStartRequest request) {
        log.debug("Starting process with key: {}", request.getProcessDefinitionKey());
        
        var payloadBuilder = ProcessPayloadBuilder.start()
                .withProcessDefinitionKey(request.getProcessDefinitionKey());
        
        if (request.getBusinessKey() != null) {
            payloadBuilder.withBusinessKey(request.getBusinessKey());
        }
        if (request.getName() != null) {
            payloadBuilder.withName(request.getName());
        }
        if (request.getVariables() != null) {
            payloadBuilder.withVariables(request.getVariables());
        }
        
        ProcessInstance instance = processRuntime.start(payloadBuilder.build());
        return mapToResponse(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProcessInstanceResponse> getProcessInstances(
            String processDefinitionKey, 
            String status, 
            org.springframework.data.domain.Pageable pageable) {
        
        var page = processRuntime.processInstances(
                Pageable.of(pageable.getPageNumber(), pageable.getPageSize()));
        
        List<ProcessInstanceResponse> responses = page.getContent().stream()
                .filter(pi -> processDefinitionKey == null || 
                        pi.getProcessDefinitionKey().equals(processDefinitionKey))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, page.getTotalItems());
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessInstanceResponse getProcessInstance(String processInstanceId) {
        ProcessInstance instance = processRuntime.processInstance(processInstanceId);
        return mapToResponse(instance);
    }

    @Override
    public void cancelProcess(String processInstanceId, String reason) {
        log.debug("Cancelling process: {} with reason: {}", processInstanceId, reason);
        processRuntime.delete(ProcessPayloadBuilder.delete()
                .withProcessInstanceId(processInstanceId)
                .withReason(reason)
                .build());
    }

    @Override
    public ProcessInstanceResponse suspendProcess(String processInstanceId) {
        log.debug("Suspending process: {}", processInstanceId);
        ProcessInstance instance = processRuntime.suspend(
                ProcessPayloadBuilder.suspend()
                        .withProcessInstanceId(processInstanceId)
                        .build());
        return mapToResponse(instance);
    }

    @Override
    public ProcessInstanceResponse activateProcess(String processInstanceId) {
        log.debug("Activating process: {}", processInstanceId);
        ProcessInstance instance = processRuntime.resume(
                ProcessPayloadBuilder.resume()
                        .withProcessInstanceId(processInstanceId)
                        .build());
        return mapToResponse(instance);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVariables(String processInstanceId) {
        return runtimeService.getVariables(processInstanceId);
    }

    @Override
    public void setVariables(String processInstanceId, Map<String, Object> variables) {
        runtimeService.setVariables(processInstanceId, variables);
    }

    @Override
    public void sendSignal(String processInstanceId, String signalName, Map<String, Object> variables) {
        log.debug("Sending signal {} to process {}", signalName, processInstanceId);
        if (variables != null) {
            runtimeService.signalEventReceived(signalName, processInstanceId, variables);
        } else {
            runtimeService.signalEventReceived(signalName, processInstanceId);
        }
    }

    @Override
    public void sendMessage(String processInstanceId, String messageName, Map<String, Object> variables) {
        log.debug("Sending message {} to process {}", messageName, processInstanceId);
        var execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .messageEventSubscriptionName(messageName)
                .singleResult();
        
        if (execution != null) {
            if (variables != null) {
                runtimeService.messageEventReceived(messageName, execution.getId(), variables);
            } else {
                runtimeService.messageEventReceived(messageName, execution.getId());
            }
        }
    }

    private ProcessInstanceResponse mapToResponse(ProcessInstance instance) {
        return ProcessInstanceResponse.builder()
                .id(instance.getId())
                .processDefinitionId(instance.getProcessDefinitionId())
                .processDefinitionKey(instance.getProcessDefinitionKey())
                .processDefinitionVersion(instance.getProcessDefinitionVersion())
                .businessKey(instance.getBusinessKey())
                .name(instance.getName())
                .status(instance.getStatus().name())
                .startTime(instance.getStartDate() != null ? 
                        instance.getStartDate().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime() : null)
                .build();
    }
}

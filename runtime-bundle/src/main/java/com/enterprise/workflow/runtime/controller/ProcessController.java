package com.enterprise.workflow.runtime.controller;

import com.enterprise.workflow.common.dto.ProcessInstanceDTO;
import com.enterprise.workflow.runtime.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API for managing process instances.
 * Provides endpoints for starting, querying, and managing process execution.
 */
@RestController
@RequestMapping("/api/v1/process-instances")
@RequiredArgsConstructor
@Tag(name = "Process Instances", description = "Process instance management API")
public class ProcessController {

    private final ProcessService processService;

    @PostMapping("/start/{processDefinitionKey}")
    @Operation(summary = "Start a new process instance")
    public ResponseEntity<ProcessInstanceDTO> startProcess(
            @Parameter(description = "Process definition key") 
            @PathVariable String processDefinitionKey,
            @Parameter(description = "Business key for correlation") 
            @RequestParam(required = false) String businessKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        ProcessInstanceDTO instance = processService.startProcess(
                processDefinitionKey, businessKey, variables);
        return ResponseEntity.status(HttpStatus.CREATED).body(instance);
    }

    @GetMapping
    @Operation(summary = "Get all process instances")
    public ResponseEntity<Page<ProcessInstanceDTO>> getProcessInstances(
            @Parameter(description = "Process definition key filter")
            @RequestParam(required = false) String processDefinitionKey,
            @Parameter(description = "Status filter")
            @RequestParam(required = false) ProcessInstanceDTO.ProcessStatus status,
            Pageable pageable) {
        
        Page<ProcessInstanceDTO> instances = processService.getProcessInstances(
                processDefinitionKey, status, pageable);
        return ResponseEntity.ok(instances);
    }

    @GetMapping("/{processInstanceId}")
    @Operation(summary = "Get a specific process instance")
    public ResponseEntity<ProcessInstanceDTO> getProcessInstance(
            @PathVariable String processInstanceId) {
        
        ProcessInstanceDTO instance = processService.getProcessInstance(processInstanceId);
        return ResponseEntity.ok(instance);
    }

    @DeleteMapping("/{processInstanceId}")
    @Operation(summary = "Cancel a process instance")
    public ResponseEntity<Void> cancelProcess(
            @PathVariable String processInstanceId,
            @RequestParam(required = false) String reason) {
        
        processService.cancelProcess(processInstanceId, reason);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{processInstanceId}/suspend")
    @Operation(summary = "Suspend a process instance")
    public ResponseEntity<ProcessInstanceDTO> suspendProcess(
            @PathVariable String processInstanceId) {
        
        ProcessInstanceDTO instance = processService.suspendProcess(processInstanceId);
        return ResponseEntity.ok(instance);
    }

    @PostMapping("/{processInstanceId}/activate")
    @Operation(summary = "Activate a suspended process instance")
    public ResponseEntity<ProcessInstanceDTO> activateProcess(
            @PathVariable String processInstanceId) {
        
        ProcessInstanceDTO instance = processService.activateProcess(processInstanceId);
        return ResponseEntity.ok(instance);
    }

    @GetMapping("/{processInstanceId}/variables")
    @Operation(summary = "Get process instance variables")
    public ResponseEntity<Map<String, Object>> getVariables(
            @PathVariable String processInstanceId) {
        
        Map<String, Object> variables = processService.getVariables(processInstanceId);
        return ResponseEntity.ok(variables);
    }

    @PutMapping("/{processInstanceId}/variables")
    @Operation(summary = "Set process instance variables")
    public ResponseEntity<Void> setVariables(
            @PathVariable String processInstanceId,
            @RequestBody Map<String, Object> variables) {
        
        processService.setVariables(processInstanceId, variables);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{processInstanceId}/signal/{signalName}")
    @Operation(summary = "Send a signal to a process instance")
    public ResponseEntity<Void> sendSignal(
            @PathVariable String processInstanceId,
            @PathVariable String signalName,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        processService.sendSignal(processInstanceId, signalName, variables);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{processInstanceId}/message/{messageName}")
    @Operation(summary = "Send a message to a process instance")
    public ResponseEntity<Void> sendMessage(
            @PathVariable String processInstanceId,
            @PathVariable String messageName,
            @RequestBody(required = false) Map<String, Object> variables) {
        
        processService.sendMessage(processInstanceId, messageName, variables);
        return ResponseEntity.ok().build();
    }
}

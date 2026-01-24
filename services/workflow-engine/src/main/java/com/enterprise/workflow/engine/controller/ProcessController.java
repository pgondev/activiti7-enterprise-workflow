package com.enterprise.workflow.engine.controller;

import com.enterprise.workflow.engine.dto.ProcessStartRequest;
import com.enterprise.workflow.engine.dto.ProcessInstanceResponse;
import com.enterprise.workflow.engine.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * REST API for Process Instance Management.
 * Provides endpoints for starting, querying, and managing BPMN process instances.
 */
@RestController
@RequestMapping("/api/v1/processes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Process Instances", description = "BPMN Process instance management API")
public class ProcessController {

    private final ProcessService processService;

    @PostMapping("/start")
    @Operation(summary = "Start a new process instance")
    public ResponseEntity<ProcessInstanceResponse> startProcess(
            @Valid @RequestBody ProcessStartRequest request) {
        log.info("Starting process: {} with businessKey: {}", 
                request.getProcessDefinitionKey(), request.getBusinessKey());
        ProcessInstanceResponse response = processService.startProcess(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all process instances")
    public ResponseEntity<Page<ProcessInstanceResponse>> getProcessInstances(
            @RequestParam(required = false) String processDefinitionKey,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(processService.getProcessInstances(
                processDefinitionKey, status, pageable));
    }

    @GetMapping("/{processInstanceId}")
    @Operation(summary = "Get process instance by ID")
    public ResponseEntity<ProcessInstanceResponse> getProcessInstance(
            @PathVariable String processInstanceId) {
        return ResponseEntity.ok(processService.getProcessInstance(processInstanceId));
    }

    @DeleteMapping("/{processInstanceId}")
    @Operation(summary = "Cancel a process instance")
    public ResponseEntity<Void> cancelProcess(
            @PathVariable String processInstanceId,
            @RequestParam(required = false) String reason) {
        log.info("Cancelling process: {} with reason: {}", processInstanceId, reason);
        processService.cancelProcess(processInstanceId, reason);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{processInstanceId}/suspend")
    @Operation(summary = "Suspend a process instance")
    public ResponseEntity<ProcessInstanceResponse> suspendProcess(
            @PathVariable String processInstanceId) {
        return ResponseEntity.ok(processService.suspendProcess(processInstanceId));
    }

    @PostMapping("/{processInstanceId}/activate")
    @Operation(summary = "Activate a suspended process instance")
    public ResponseEntity<ProcessInstanceResponse> activateProcess(
            @PathVariable String processInstanceId) {
        return ResponseEntity.ok(processService.activateProcess(processInstanceId));
    }

    @GetMapping("/{processInstanceId}/variables")
    @Operation(summary = "Get process variables")
    public ResponseEntity<Map<String, Object>> getVariables(
            @PathVariable String processInstanceId) {
        return ResponseEntity.ok(processService.getVariables(processInstanceId));
    }

    @PutMapping("/{processInstanceId}/variables")
    @Operation(summary = "Update process variables")
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

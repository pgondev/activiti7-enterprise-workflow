package com.enterprise.workflow.decision.controller;

import com.enterprise.workflow.decision.dto.*;
import com.enterprise.workflow.decision.service.DecisionService;
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
import java.util.List;
import java.util.Map;

/**
 * REST API for DMN Decision Management.
 * Provides decision table deployment, execution, and management.
 */
@RestController
@RequestMapping("/api/v1/decisions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Decision Tables", description = "DMN decision management API")
public class DecisionController {

    private final DecisionService decisionService;

    // ==================== DECISION DEFINITIONS ====================

    @GetMapping
    @Operation(summary = "Get all decision definitions")
    public ResponseEntity<Page<DecisionDefinitionResponse>> getDecisions(Pageable pageable) {
        return ResponseEntity.ok(decisionService.getDecisions(pageable));
    }

    @GetMapping("/{decisionId}")
    @Operation(summary = "Get decision definition by ID")
    public ResponseEntity<DecisionDefinitionResponse> getDecision(@PathVariable String decisionId) {
        return ResponseEntity.ok(decisionService.getDecision(decisionId));
    }

    @GetMapping("/key/{decisionKey}")
    @Operation(summary = "Get decision definition by key")
    public ResponseEntity<DecisionDefinitionResponse> getDecisionByKey(@PathVariable String decisionKey) {
        return ResponseEntity.ok(decisionService.getDecisionByKey(decisionKey));
    }

    @GetMapping("/{decisionId}/xml")
    @Operation(summary = "Get DMN XML for a decision")
    public ResponseEntity<String> getDecisionXml(@PathVariable String decisionId) {
        return ResponseEntity.ok(decisionService.getDecisionXml(decisionId));
    }

    // ==================== DEPLOYMENT ====================

    @PostMapping("/deploy")
    @Operation(summary = "Deploy a DMN decision table")
    public ResponseEntity<DeploymentResponse> deployDecision(
            @Valid @RequestBody DeployDecisionRequest request) {
        log.info("Deploying decision: {}", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(decisionService.deployDecision(request));
    }

    @DeleteMapping("/deployments/{deploymentId}")
    @Operation(summary = "Delete a deployment")
    public ResponseEntity<Void> deleteDeployment(@PathVariable String deploymentId) {
        log.info("Deleting deployment: {}", deploymentId);
        decisionService.deleteDeployment(deploymentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== EXECUTION ====================

    @PostMapping("/key/{decisionKey}/execute")
    @Operation(summary = "Execute a decision by key")
    public ResponseEntity<DecisionExecutionResponse> executeDecision(
            @PathVariable String decisionKey,
            @RequestBody Map<String, Object> variables) {
        log.info("Executing decision: {}", decisionKey);
        return ResponseEntity.ok(decisionService.executeDecision(decisionKey, variables));
    }

    @PostMapping("/{decisionId}/execute")
    @Operation(summary = "Execute a decision by ID")
    public ResponseEntity<DecisionExecutionResponse> executeDecisionById(
            @PathVariable String decisionId,
            @RequestBody Map<String, Object> variables) {
        log.info("Executing decision by ID: {}", decisionId);
        return ResponseEntity.ok(decisionService.executeDecisionById(decisionId, variables));
    }

    @PostMapping("/key/{decisionKey}/execute/batch")
    @Operation(summary = "Execute decision for multiple inputs")
    public ResponseEntity<List<DecisionExecutionResponse>> executeBatch(
            @PathVariable String decisionKey,
            @RequestBody List<Map<String, Object>> inputsList) {
        log.info("Batch executing decision {} for {} inputs", decisionKey, inputsList.size());
        return ResponseEntity.ok(decisionService.executeBatch(decisionKey, inputsList));
    }

    // ==================== HISTORY ====================

    @GetMapping("/history/executions")
    @Operation(summary = "Get decision execution history")
    public ResponseEntity<Page<DecisionExecutionHistoryResponse>> getExecutionHistory(
            @RequestParam(required = false) String decisionKey,
            @RequestParam(required = false) String processInstanceId,
            Pageable pageable) {
        return ResponseEntity.ok(decisionService.getExecutionHistory(decisionKey, processInstanceId, pageable));
    }

    @GetMapping("/history/executions/{executionId}")
    @Operation(summary = "Get specific execution details")
    public ResponseEntity<DecisionExecutionHistoryResponse> getExecution(@PathVariable String executionId) {
        return ResponseEntity.ok(decisionService.getExecution(executionId));
    }

    // ==================== VALIDATION ====================

    @PostMapping("/validate")
    @Operation(summary = "Validate DMN XML without deploying")
    public ResponseEntity<DecisionValidationResponse> validateDecision(
            @RequestBody String dmnXml) {
        return ResponseEntity.ok(decisionService.validateDecision(dmnXml));
    }
}

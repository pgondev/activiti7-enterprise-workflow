package com.enterprise.workflow.engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Deployments
 */
@RestController
@RequestMapping("/api/v1/deployments")
@Tag(name = "Deployments", description = "BPMN/DMN Deployment operations")
@CrossOrigin(origins = "*")
public class DeploymentController {

    private final RepositoryService repositoryService;

    public DeploymentController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping
    @Operation(summary = "List all deployments")
    public ResponseEntity<List<Map<String, Object>>> listDeployments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<Deployment> deployments = repositoryService.createDeploymentQuery()
                .orderByDeploymenTime().desc()
                .listPage(page * size, size);

        List<Map<String, Object>> result = deployments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Deploy a BPMN/DMN process")
    public ResponseEntity<Map<String, Object>> deploy(@RequestBody DeployRequest request) {
        try {
            if (request.bpmnXml == null || request.bpmnXml.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "BPMN XML cannot be empty"));
            }

            DeploymentBuilder builder = repositoryService.createDeployment()
                    .name(request.name != null ? request.name : "Deployment")
                    .addString(
                            request.resourceName != null ? request.resourceName : "process.bpmn20.xml",
                            request.bpmnXml);

            Deployment deployment = builder.deploy();

            Map<String, Object> result = toDto(deployment);
            result.put("message", "Deployment successful");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Deployment failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a deployment")
    public ResponseEntity<Void> deleteDeployment(
            @PathVariable String id,
            @RequestParam(defaultValue = "false") boolean cascade) {

        repositoryService.deleteDeployment(id, cascade);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Get count of deployments")
    public ResponseEntity<Map<String, Long>> countDeployments() {
        long count = repositoryService.createDeploymentQuery().count();
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toDto(Deployment deployment) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", deployment.getId());
        dto.put("name", deployment.getName());
        dto.put("deploymentTime", deployment.getDeploymentTime());
        dto.put("category", deployment.getCategory());
        dto.put("key", deployment.getKey());
        return dto;
    }

    public static class DeployRequest {
        public String name;
        public String bpmnXml;
        public String resourceName;
    }
}

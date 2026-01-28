package com.enterprise.workflow.engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Process Definitions
 */
@RestController
@RequestMapping("/api/v1/process-definitions")
@Tag(name = "Process Definitions", description = "BPMN Process Definition operations")
public class ProcessDefinitionController {

    private final RepositoryService repositoryService;

    public ProcessDefinitionController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping
    @Operation(summary = "List all process definitions")
    public ResponseEntity<List<Map<String, Object>>> listDefinitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .listPage(page * size, size);

        List<Map<String, Object>> result = definitions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get process definition by ID")
    public ResponseEntity<Map<String, Object>> getDefinition(@PathVariable String id) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(id)
                .singleResult();

        if (definition == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDto(definition));
    }

    @GetMapping(value = "/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Get process definition XML")
    public ResponseEntity<String> getDefinitionXml(@PathVariable String id) {
        try {
            ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(id)
                    .singleResult();

            if (definition == null) {
                return ResponseEntity.notFound().build();
            }

            try (InputStream resourceStream = repositoryService.getResourceAsStream(
                    definition.getDeploymentId(), 
                    definition.getResourceName())) {
                String xml = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
                return ResponseEntity.ok(xml);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/key/{key}")
    @Operation(summary = "Get latest process definition by key")
    public ResponseEntity<Map<String, Object>> getDefinitionByKey(@PathVariable String key) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .latestVersion()
                .singleResult();

        if (definition == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDto(definition));
    }

    @GetMapping("/count")
    @Operation(summary = "Get count of process definitions")
    public ResponseEntity<Map<String, Long>> countDefinitions() {
        long count = repositoryService.createProcessDefinitionQuery().latestVersion().count();
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toDto(ProcessDefinition definition) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", definition.getId());
        dto.put("key", definition.getKey());
        dto.put("name", definition.getName());
        dto.put("version", definition.getVersion());
        dto.put("deploymentId", definition.getDeploymentId());
        dto.put("description", definition.getDescription());
        dto.put("category", definition.getCategory());
        return dto;
    }
}

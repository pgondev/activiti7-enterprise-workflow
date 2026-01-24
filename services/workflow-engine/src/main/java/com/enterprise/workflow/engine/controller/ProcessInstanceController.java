package com.enterprise.workflow.engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Process Instances
 */
@RestController
@RequestMapping("/api/v1/process-instances")
@Tag(name = "Process Instances", description = "Process Instance management operations")
public class ProcessInstanceController {

    private final RuntimeService runtimeService;

    public ProcessInstanceController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @GetMapping
    @Operation(summary = "List all running process instances")
    public ResponseEntity<List<Map<String, Object>>> listInstances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
                .listPage(page * size, size);

        List<Map<String, Object>> result = instances.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get process instance by ID")
    public ResponseEntity<Map<String, Object>> getInstance(@PathVariable String id) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(id)
                .singleResult();

        if (instance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDto(instance));
    }

    @PostMapping("/key/{processKey}/start")
    @Operation(summary = "Start a new process instance")
    public ResponseEntity<Map<String, Object>> startProcess(
            @PathVariable String processKey,
            @RequestBody(required = false) Map<String, Object> variables) {

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                processKey,
                variables != null ? variables : new HashMap<>());

        return ResponseEntity.ok(toDto(instance));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (terminate) a process instance")
    public ResponseEntity<Void> deleteInstance(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {

        runtimeService.deleteProcessInstance(id, reason != null ? reason : "Terminated by user");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Get count of running process instances")
    public ResponseEntity<Map<String, Long>> countInstances() {
        long count = runtimeService.createProcessInstanceQuery().count();
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toDto(ProcessInstance instance) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", instance.getId());
        dto.put("processDefinitionId", instance.getProcessDefinitionId());
        dto.put("processDefinitionKey", instance.getProcessDefinitionKey());
        dto.put("businessKey", instance.getBusinessKey());
        dto.put("suspended", instance.isSuspended());
        dto.put("ended", instance.isEnded());
        return dto;
    }
}

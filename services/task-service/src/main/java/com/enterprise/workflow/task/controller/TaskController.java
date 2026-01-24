package com.enterprise.workflow.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Task management operations")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "List tasks")
    public ResponseEntity<List<Map<String, Object>>> listTasks(
            @RequestParam(required = false) String assignee,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var query = taskService.createTaskQuery();
        if (assignee != null) {
            query.taskAssignee(assignee);
        }

        List<Task> tasks = query.orderByTaskCreateTime().desc().listPage(page * size, size);

        return ResponseEntity.ok(tasks.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable String id) {
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        if (task == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(task));
    }

    @PostMapping("/{id}/claim")
    @Operation(summary = "Claim a task")
    public ResponseEntity<Void> claimTask(@PathVariable String id, @RequestParam String userId) {
        taskService.claim(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a task")
    public ResponseEntity<Void> completeTask(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> variables) {

        taskService.complete(id, variables);
        return ResponseEntity.ok().build();
    }

    // Endpoint for batch claim (simplified)
    @PostMapping("/batch/claim")
    @Operation(summary = "Claim multiple tasks")
    public ResponseEntity<Void> batchClaim(@RequestBody List<String> taskIds, @RequestParam String userId) {
        for (String id : taskIds) {
            taskService.claim(id, userId);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/claimable")
    @Operation(summary = "Get tasks available to claim")
    public ResponseEntity<List<Map<String, Object>>> getClaimableTasks() {
        // Unassigned tasks
        List<Task> tasks = taskService.createTaskQuery().taskUnassigned().list();
        return ResponseEntity.ok(tasks.stream().map(this::toDto).collect(Collectors.toList()));
    }

    private Map<String, Object> toDto(Task task) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", task.getId());
        dto.put("name", task.getName());
        dto.put("assignee", task.getAssignee());
        dto.put("createTime", task.getCreateTime());
        dto.put("processInstanceId", task.getProcessInstanceId());
        dto.put("processDefinitionId", task.getProcessDefinitionId());
        dto.put("priority", task.getPriority() > 50 ? "HIGH" : (task.getPriority() < 50 ? "LOW" : "MEDIUM"));
        dto.put("description", task.getDescription());
        return dto;
    }
}

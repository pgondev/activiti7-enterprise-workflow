package com.enterprise.workflow.task.controller;

import com.enterprise.workflow.task.dto.*;
import com.enterprise.workflow.task.service.TaskManagementService;
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
 * REST API for User Task Management.
 * Provides endpoints for task inbox, operations, comments, and attachments.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Tasks", description = "User task management API")
public class TaskController {

    private final TaskManagementService taskService;

    // ==================== TASK QUERIES ====================

    @GetMapping
    @Operation(summary = "Get all tasks with filtering")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @Valid TaskQueryRequest query,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(query, pageable));
    }

    @GetMapping("/inbox")
    @Operation(summary = "Get tasks assigned to current user")
    public ResponseEntity<Page<TaskResponse>> getMyTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getMyTasks(pageable));
    }

    @GetMapping("/claimable")
    @Operation(summary = "Get tasks available to claim")
    public ResponseEntity<Page<TaskResponse>> getClaimableTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getClaimableTasks(pageable));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTask(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getTask(taskId));
    }

    // ==================== TASK OPERATIONS ====================

    @PostMapping("/{taskId}/claim")
    @Operation(summary = "Claim a task")
    public ResponseEntity<TaskResponse> claimTask(@PathVariable String taskId) {
        log.info("Claiming task: {}", taskId);
        return ResponseEntity.ok(taskService.claimTask(taskId));
    }

    @PostMapping("/{taskId}/unclaim")
    @Operation(summary = "Release a claimed task")
    public ResponseEntity<TaskResponse> unclaimTask(@PathVariable String taskId) {
        log.info("Unclaiming task: {}", taskId);
        return ResponseEntity.ok(taskService.unclaimTask(taskId));
    }

    @PostMapping("/{taskId}/delegate")
    @Operation(summary = "Delegate task to another user")
    public ResponseEntity<TaskResponse> delegateTask(
            @PathVariable String taskId,
            @Valid @RequestBody DelegateTaskRequest request) {
        log.info("Delegating task {} to user {}", taskId, request.getUserId());
        return ResponseEntity.ok(taskService.delegateTask(taskId, request.getUserId()));
    }

    @PostMapping("/{taskId}/assign")
    @Operation(summary = "Assign task to a user")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable String taskId,
            @Valid @RequestBody AssignTaskRequest request) {
        log.info("Assigning task {} to user {}", taskId, request.getAssignee());
        return ResponseEntity.ok(taskService.assignTask(taskId, request.getAssignee()));
    }

    @PostMapping("/{taskId}/complete")
    @Operation(summary = "Complete a task with optional output variables")
    public ResponseEntity<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        log.info("Completing task: {}", taskId);
        taskService.completeTask(taskId, variables);
        return ResponseEntity.noContent().build();
    }

    // ==================== TASK VARIABLES ====================

    @GetMapping("/{taskId}/variables")
    @Operation(summary = "Get task variables")
    public ResponseEntity<Map<String, Object>> getVariables(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getVariables(taskId));
    }

    @PutMapping("/{taskId}/variables")
    @Operation(summary = "Update task variables")
    public ResponseEntity<Void> setVariables(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {
        taskService.setVariables(taskId, variables);
        return ResponseEntity.ok().build();
    }

    // ==================== COMMENTS ====================

    @GetMapping("/{taskId}/comments")
    @Operation(summary = "Get task comments")
    public ResponseEntity<List<TaskCommentResponse>> getComments(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getComments(taskId));
    }

    @PostMapping("/{taskId}/comments")
    @Operation(summary = "Add a comment to task")
    public ResponseEntity<TaskCommentResponse> addComment(
            @PathVariable String taskId,
            @Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.addComment(taskId, request.getMessage()));
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String taskId,
            @PathVariable String commentId) {
        taskService.deleteComment(taskId, commentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== ATTACHMENTS ====================

    @GetMapping("/{taskId}/attachments")
    @Operation(summary = "Get task attachments")
    public ResponseEntity<List<TaskAttachmentResponse>> getAttachments(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.getAttachments(taskId));
    }

    @PostMapping("/{taskId}/attachments")
    @Operation(summary = "Add attachment to task")
    public ResponseEntity<TaskAttachmentResponse> addAttachment(
            @PathVariable String taskId,
            @Valid @RequestBody AddAttachmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.addAttachment(taskId, request));
    }

    @DeleteMapping("/{taskId}/attachments/{attachmentId}")
    @Operation(summary = "Delete an attachment")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable String taskId,
            @PathVariable String attachmentId) {
        taskService.deleteAttachment(taskId, attachmentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== BATCH OPERATIONS ====================

    @PostMapping("/batch/claim")
    @Operation(summary = "Claim multiple tasks")
    public ResponseEntity<List<TaskResponse>> batchClaim(@RequestBody List<String> taskIds) {
        return ResponseEntity.ok(taskService.batchClaim(taskIds));
    }

    @PostMapping("/batch/complete")
    @Operation(summary = "Complete multiple tasks")
    public ResponseEntity<Void> batchComplete(
            @Valid @RequestBody BatchCompleteRequest request) {
        taskService.batchComplete(request.getTaskIds(), request.getVariables());
        return ResponseEntity.noContent().build();
    }
}

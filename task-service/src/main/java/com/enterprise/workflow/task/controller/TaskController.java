package com.enterprise.workflow.task.controller;

import com.enterprise.workflow.common.dto.TaskDTO;
import com.enterprise.workflow.task.dto.*;
import com.enterprise.workflow.task.service.TaskManagementService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST API for comprehensive task management.
 * Implements features from Camunda Tasklist, Flowable Work, and Hyland Automate.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "User task management API")
public class TaskController {

    private final TaskManagementService taskService;

    // ==================== Task Queries ====================

    @GetMapping
    @Operation(summary = "Get tasks with filters")
    public ResponseEntity<Page<TaskDTO>> getTasks(
            @Parameter(description = "Filter by assignee")
            @RequestParam(required = false) String assignee,
            @Parameter(description = "Filter by candidate group")
            @RequestParam(required = false) String candidateGroup,
            @Parameter(description = "Filter by candidate user")
            @RequestParam(required = false) String candidateUser,
            @Parameter(description = "Filter by process instance ID")
            @RequestParam(required = false) String processInstanceId,
            @Parameter(description = "Filter by process definition key")
            @RequestParam(required = false) String processDefinitionKey,
            @Parameter(description = "Filter by task name (contains)")
            @RequestParam(required = false) String nameLike,
            @Parameter(description = "Filter unassigned tasks only")
            @RequestParam(required = false, defaultValue = "false") Boolean unassigned,
            @Parameter(description = "Include process variables")
            @RequestParam(required = false, defaultValue = "false") Boolean includeVariables,
            Pageable pageable) {
        
        TaskQueryRequest query = TaskQueryRequest.builder()
                .assignee(assignee)
                .candidateGroup(candidateGroup)
                .candidateUser(candidateUser)
                .processInstanceId(processInstanceId)
                .processDefinitionKey(processDefinitionKey)
                .nameLike(nameLike)
                .unassigned(unassigned)
                .includeVariables(includeVariables)
                .build();
        
        Page<TaskDTO> tasks = taskService.queryTasks(query, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-tasks")
    @Operation(summary = "Get tasks assigned to current user")
    public ResponseEntity<Page<TaskDTO>> getMyTasks(Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getMyTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/candidate-tasks")
    @Operation(summary = "Get tasks where current user is a candidate")
    public ResponseEntity<Page<TaskDTO>> getCandidateTasks(Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getCandidateTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/group-tasks")
    @Operation(summary = "Get tasks for current user's groups")
    public ResponseEntity<Page<TaskDTO>> getGroupTasks(Pageable pageable) {
        Page<TaskDTO> tasks = taskService.getGroupTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a specific task")
    public ResponseEntity<TaskDTO> getTask(@PathVariable String taskId) {
        TaskDTO task = taskService.getTask(taskId);
        return ResponseEntity.ok(task);
    }

    // ==================== Task Operations ====================

    @PostMapping("/{taskId}/claim")
    @Operation(summary = "Claim a task")
    public ResponseEntity<TaskDTO> claimTask(@PathVariable String taskId) {
        TaskDTO task = taskService.claimTask(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/{taskId}/unclaim")
    @Operation(summary = "Unclaim/release a task")
    public ResponseEntity<TaskDTO> unclaimTask(@PathVariable String taskId) {
        TaskDTO task = taskService.unclaimTask(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/{taskId}/complete")
    @Operation(summary = "Complete a task with variables")
    public ResponseEntity<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        taskService.completeTask(taskId, variables);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/delegate")
    @Operation(summary = "Delegate a task to another user")
    public ResponseEntity<TaskDTO> delegateTask(
            @PathVariable String taskId,
            @Valid @RequestBody DelegateTaskRequest request) {
        TaskDTO task = taskService.delegateTask(taskId, request.getDelegateUserId());
        return ResponseEntity.ok(task);
    }

    @PostMapping("/{taskId}/resolve")
    @Operation(summary = "Resolve a delegated task")
    public ResponseEntity<TaskDTO> resolveTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        TaskDTO task = taskService.resolveTask(taskId, variables);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}/assignee")
    @Operation(summary = "Assign task to a user")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable String taskId,
            @Valid @RequestBody AssignTaskRequest request) {
        TaskDTO task = taskService.assignTask(taskId, request.getAssignee());
        return ResponseEntity.ok(task);
    }

    // ==================== Task Properties ====================

    @PutMapping("/{taskId}/priority")
    @Operation(summary = "Update task priority")
    public ResponseEntity<TaskDTO> updatePriority(
            @PathVariable String taskId,
            @Valid @RequestBody UpdatePriorityRequest request) {
        TaskDTO task = taskService.updatePriority(taskId, request.getPriority());
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}/due-date")
    @Operation(summary = "Update task due date")
    public ResponseEntity<TaskDTO> updateDueDate(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateDueDateRequest request) {
        TaskDTO task = taskService.updateDueDate(taskId, request.getDueDate());
        return ResponseEntity.ok(task);
    }

    // ==================== Task Variables ====================

    @GetMapping("/{taskId}/variables")
    @Operation(summary = "Get task variables")
    public ResponseEntity<Map<String, Object>> getVariables(@PathVariable String taskId) {
        Map<String, Object> variables = taskService.getTaskVariables(taskId);
        return ResponseEntity.ok(variables);
    }

    @PutMapping("/{taskId}/variables")
    @Operation(summary = "Set task variables")
    public ResponseEntity<Void> setVariables(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {
        taskService.setTaskVariables(taskId, variables);
        return ResponseEntity.ok().build();
    }

    // ==================== Task Comments ====================

    @GetMapping("/{taskId}/comments")
    @Operation(summary = "Get task comments")
    public ResponseEntity<List<TaskCommentDTO>> getComments(@PathVariable String taskId) {
        List<TaskCommentDTO> comments = taskService.getComments(taskId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{taskId}/comments")
    @Operation(summary = "Add a comment to task")
    public ResponseEntity<TaskCommentDTO> addComment(
            @PathVariable String taskId,
            @Valid @RequestBody AddCommentRequest request) {
        TaskCommentDTO comment = taskService.addComment(taskId, request.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    @Operation(summary = "Delete a task comment")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String taskId,
            @PathVariable String commentId) {
        taskService.deleteComment(taskId, commentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Task Attachments ====================

    @GetMapping("/{taskId}/attachments")
    @Operation(summary = "Get task attachments")
    public ResponseEntity<List<TaskAttachmentDTO>> getAttachments(@PathVariable String taskId) {
        List<TaskAttachmentDTO> attachments = taskService.getAttachments(taskId);
        return ResponseEntity.ok(attachments);
    }

    @PostMapping("/{taskId}/attachments")
    @Operation(summary = "Upload an attachment to task")
    public ResponseEntity<TaskAttachmentDTO> addAttachment(
            @PathVariable String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description) {
        TaskAttachmentDTO attachment = taskService.addAttachment(taskId, file, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
    }

    @DeleteMapping("/{taskId}/attachments/{attachmentId}")
    @Operation(summary = "Delete a task attachment")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable String taskId,
            @PathVariable String attachmentId) {
        taskService.deleteAttachment(taskId, attachmentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Task Candidates ====================

    @PostMapping("/{taskId}/candidate-users")
    @Operation(summary = "Add candidate users to task")
    public ResponseEntity<Void> addCandidateUsers(
            @PathVariable String taskId,
            @RequestBody List<String> userIds) {
        taskService.addCandidateUsers(taskId, userIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/candidate-users/{userId}")
    @Operation(summary = "Remove candidate user from task")
    public ResponseEntity<Void> removeCandidateUser(
            @PathVariable String taskId,
            @PathVariable String userId) {
        taskService.removeCandidateUser(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/candidate-groups")
    @Operation(summary = "Add candidate groups to task")
    public ResponseEntity<Void> addCandidateGroups(
            @PathVariable String taskId,
            @RequestBody List<String> groupIds) {
        taskService.addCandidateGroups(taskId, groupIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/candidate-groups/{groupId}")
    @Operation(summary = "Remove candidate group from task")
    public ResponseEntity<Void> removeCandidateGroup(
            @PathVariable String taskId,
            @PathVariable String groupId) {
        taskService.removeCandidateGroup(taskId, groupId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Batch Operations ====================

    @PostMapping("/batch/claim")
    @Operation(summary = "Claim multiple tasks")
    public ResponseEntity<List<TaskDTO>> batchClaim(@RequestBody List<String> taskIds) {
        List<TaskDTO> tasks = taskService.batchClaim(taskIds);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/batch/complete")
    @Operation(summary = "Complete multiple tasks")
    public ResponseEntity<Void> batchComplete(@RequestBody BatchCompleteRequest request) {
        taskService.batchComplete(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch/assign")
    @Operation(summary = "Assign multiple tasks to a user")
    public ResponseEntity<List<TaskDTO>> batchAssign(@RequestBody BatchAssignRequest request) {
        List<TaskDTO> tasks = taskService.batchAssign(request);
        return ResponseEntity.ok(tasks);
    }
}

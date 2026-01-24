package com.enterprise.workflow.task.service.impl;

import com.enterprise.workflow.task.dto.*;
import com.enterprise.workflow.task.service.TaskManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of TaskManagementService using Activiti7 Runtime.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskManagementServiceImpl implements TaskManagementService {

    private final TaskRuntime taskRuntime;
    private final TaskService taskService; // Activiti Engine API

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(TaskQueryRequest query, Pageable pageable) {
        var page = taskRuntime.tasks(
                org.activiti.api.runtime.shared.query.Pageable.of(
                        pageable.getPageNumber(), pageable.getPageSize()));
        
        List<TaskResponse> responses = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, page.getTotalItems());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getMyTasks(Pageable pageable) {
        String currentUser = getCurrentUserId();
        var page = taskRuntime.tasks(
                org.activiti.api.runtime.shared.query.Pageable.of(
                        pageable.getPageNumber(), pageable.getPageSize()));
        
        List<TaskResponse> myTasks = page.getContent().stream()
                .filter(t -> currentUser.equals(t.getAssignee()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(myTasks, pageable, myTasks.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getClaimableTasks(Pageable pageable) {
        var page = taskRuntime.tasks(
                org.activiti.api.runtime.shared.query.Pageable.of(
                        pageable.getPageNumber(), pageable.getPageSize()));
        
        List<TaskResponse> claimable = page.getContent().stream()
                .filter(t -> t.getAssignee() == null)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(claimable, pageable, claimable.size());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(String taskId) {
        Task task = taskRuntime.task(taskId);
        return mapToResponse(task);
    }

    @Override
    public TaskResponse claimTask(String taskId) {
        log.debug("Claiming task: {}", taskId);
        Task task = taskRuntime.claim(TaskPayloadBuilder.claim()
                .withTaskId(taskId)
                .build());
        return mapToResponse(task);
    }

    @Override
    public TaskResponse unclaimTask(String taskId) {
        log.debug("Unclaiming task: {}", taskId);
        Task task = taskRuntime.release(TaskPayloadBuilder.release()
                .withTaskId(taskId)
                .build());
        return mapToResponse(task);
    }

    @Override
    public TaskResponse delegateTask(String taskId, String userId) {
        log.debug("Delegating task {} to user {}", taskId, userId);
        taskService.delegateTask(taskId, userId);
        return getTask(taskId);
    }

    @Override
    public TaskResponse assignTask(String taskId, String assignee) {
        log.debug("Assigning task {} to {}", taskId, assignee);
        taskService.setAssignee(taskId, assignee);
        return getTask(taskId);
    }

    @Override
    public void completeTask(String taskId, Map<String, Object> variables) {
        log.debug("Completing task: {}", taskId);
        var builder = TaskPayloadBuilder.complete().withTaskId(taskId);
        if (variables != null && !variables.isEmpty()) {
            builder.withVariables(variables);
        }
        taskRuntime.complete(builder.build());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVariables(String taskId) {
        return taskService.getVariables(taskId);
    }

    @Override
    public void setVariables(String taskId, Map<String, Object> variables) {
        taskService.setVariables(taskId, variables);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getComments(String taskId) {
        return taskService.getTaskComments(taskId).stream()
                .map(c -> TaskCommentResponse.builder()
                        .id(c.getId())
                        .taskId(c.getTaskId())
                        .userId(c.getUserId())
                        .message(c.getFullMessage())
                        .time(c.getTime() != null ? 
                                c.getTime().toInstant()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDateTime() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public TaskCommentResponse addComment(String taskId, String message) {
        log.debug("Adding comment to task {}", taskId);
        var comment = taskService.addComment(taskId, null, message);
        return TaskCommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .userId(comment.getUserId())
                .message(comment.getFullMessage())
                .time(comment.getTime() != null ? 
                        comment.getTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime() : null)
                .build();
    }

    @Override
    public void deleteComment(String taskId, String commentId) {
        log.debug("Deleting comment {} from task {}", commentId, taskId);
        taskService.deleteComment(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAttachmentResponse> getAttachments(String taskId) {
        return taskService.getTaskAttachments(taskId).stream()
                .map(a -> TaskAttachmentResponse.builder()
                        .id(a.getId())
                        .taskId(a.getTaskId())
                        .name(a.getName())
                        .description(a.getDescription())
                        .type(a.getType())
                        .url(a.getUrl())
                        .userId(a.getUserId())
                        .time(a.getTime() != null ? 
                                a.getTime().toInstant()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDateTime() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public TaskAttachmentResponse addAttachment(String taskId, AddAttachmentRequest request) {
        log.debug("Adding attachment {} to task {}", request.getName(), taskId);
        var attachment = taskService.createAttachment(
                request.getType(),
                taskId,
                null,
                request.getName(),
                request.getDescription(),
                request.getUrl());
        
        return TaskAttachmentResponse.builder()
                .id(attachment.getId())
                .taskId(attachment.getTaskId())
                .name(attachment.getName())
                .description(attachment.getDescription())
                .type(attachment.getType())
                .url(attachment.getUrl())
                .build();
    }

    @Override
    public void deleteAttachment(String taskId, String attachmentId) {
        log.debug("Deleting attachment {} from task {}", attachmentId, taskId);
        taskService.deleteAttachment(attachmentId);
    }

    @Override
    public List<TaskResponse> batchClaim(List<String> taskIds) {
        log.info("Batch claiming {} tasks", taskIds.size());
        return taskIds.stream()
                .map(this::claimTask)
                .collect(Collectors.toList());
    }

    @Override
    public void batchComplete(List<String> taskIds, Map<String, Object> variables) {
        log.info("Batch completing {} tasks", taskIds.size());
        taskIds.forEach(taskId -> completeTask(taskId, variables));
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .assignee(task.getAssignee())
                .priority(task.getPriority())
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .processInstanceId(task.getProcessInstanceId())
                .formKey(task.getFormKey())
                .createTime(task.getCreatedDate() != null ?
                        task.getCreatedDate().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime() : null)
                .dueDate(task.getDueDate() != null ?
                        task.getDueDate().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime() : null)
                .claimTime(task.getClaimedDate() != null ?
                        task.getClaimedDate().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime() : null)
                .build();
    }

    private String getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}

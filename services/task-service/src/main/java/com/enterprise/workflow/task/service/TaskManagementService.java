package com.enterprise.workflow.task.service;

import com.enterprise.workflow.task.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for User Task Management.
 */
public interface TaskManagementService {

    // ==================== TASK QUERIES ====================
    
    Page<TaskResponse> getTasks(TaskQueryRequest query, Pageable pageable);
    
    Page<TaskResponse> getMyTasks(Pageable pageable);
    
    Page<TaskResponse> getClaimableTasks(Pageable pageable);
    
    TaskResponse getTask(String taskId);

    // ==================== TASK OPERATIONS ====================
    
    TaskResponse claimTask(String taskId);
    
    TaskResponse unclaimTask(String taskId);
    
    TaskResponse delegateTask(String taskId, String userId);
    
    TaskResponse assignTask(String taskId, String assignee);
    
    void completeTask(String taskId, Map<String, Object> variables);

    // ==================== TASK VARIABLES ====================
    
    Map<String, Object> getVariables(String taskId);
    
    void setVariables(String taskId, Map<String, Object> variables);

    // ==================== COMMENTS ====================
    
    List<TaskCommentResponse> getComments(String taskId);
    
    TaskCommentResponse addComment(String taskId, String message);
    
    void deleteComment(String taskId, String commentId);

    // ==================== ATTACHMENTS ====================
    
    List<TaskAttachmentResponse> getAttachments(String taskId);
    
    TaskAttachmentResponse addAttachment(String taskId, AddAttachmentRequest request);
    
    void deleteAttachment(String taskId, String attachmentId);

    // ==================== BATCH OPERATIONS ====================
    
    List<TaskResponse> batchClaim(List<String> taskIds);
    
    void batchComplete(List<String> taskIds, Map<String, Object> variables);
}

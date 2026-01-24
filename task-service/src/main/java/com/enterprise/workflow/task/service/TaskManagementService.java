package com.enterprise.workflow.task.service;

import com.enterprise.workflow.common.dto.TaskDTO;
import com.enterprise.workflow.task.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for comprehensive task management.
 * Combines features from Camunda Tasklist, Flowable Work, and Hyland Automate.
 */
public interface TaskManagementService {

    // ==================== Task Queries ====================

    /**
     * Query tasks with filters.
     */
    Page<TaskDTO> queryTasks(TaskQueryRequest query, Pageable pageable);

    /**
     * Get tasks assigned to the current user.
     */
    Page<TaskDTO> getMyTasks(Pageable pageable);

    /**
     * Get tasks where the current user is a candidate.
     */
    Page<TaskDTO> getCandidateTasks(Pageable pageable);

    /**
     * Get tasks for the current user's groups.
     */
    Page<TaskDTO> getGroupTasks(Pageable pageable);

    /**
     * Get a specific task by ID.
     */
    TaskDTO getTask(String taskId);

    // ==================== Task Operations ====================

    /**
     * Claim a task for the current user.
     */
    TaskDTO claimTask(String taskId);

    /**
     * Unclaim/release a task.
     */
    TaskDTO unclaimTask(String taskId);

    /**
     * Complete a task with optional variables.
     */
    void completeTask(String taskId, Map<String, Object> variables);

    /**
     * Delegate a task to another user.
     */
    TaskDTO delegateTask(String taskId, String delegateUserId);

    /**
     * Resolve a delegated task.
     */
    TaskDTO resolveTask(String taskId, Map<String, Object> variables);

    /**
     * Assign a task to a specific user.
     */
    TaskDTO assignTask(String taskId, String assignee);

    // ==================== Task Properties ====================

    /**
     * Update task priority.
     */
    TaskDTO updatePriority(String taskId, Integer priority);

    /**
     * Update task due date.
     */
    TaskDTO updateDueDate(String taskId, LocalDateTime dueDate);

    // ==================== Task Variables ====================

    /**
     * Get all task variables.
     */
    Map<String, Object> getTaskVariables(String taskId);

    /**
     * Set task variables.
     */
    void setTaskVariables(String taskId, Map<String, Object> variables);

    // ==================== Task Comments ====================

    /**
     * Get all comments for a task.
     */
    List<TaskCommentDTO> getComments(String taskId);

    /**
     * Add a comment to a task.
     */
    TaskCommentDTO addComment(String taskId, String message);

    /**
     * Delete a comment.
     */
    void deleteComment(String taskId, String commentId);

    // ==================== Task Attachments ====================

    /**
     * Get all attachments for a task.
     */
    List<TaskAttachmentDTO> getAttachments(String taskId);

    /**
     * Add an attachment to a task.
     */
    TaskAttachmentDTO addAttachment(String taskId, MultipartFile file, String description);

    /**
     * Delete an attachment.
     */
    void deleteAttachment(String taskId, String attachmentId);

    // ==================== Task Candidates ====================

    /**
     * Add candidate users to a task.
     */
    void addCandidateUsers(String taskId, List<String> userIds);

    /**
     * Remove a candidate user from a task.
     */
    void removeCandidateUser(String taskId, String userId);

    /**
     * Add candidate groups to a task.
     */
    void addCandidateGroups(String taskId, List<String> groupIds);

    /**
     * Remove a candidate group from a task.
     */
    void removeCandidateGroup(String taskId, String groupId);

    // ==================== Batch Operations ====================

    /**
     * Claim multiple tasks.
     */
    List<TaskDTO> batchClaim(List<String> taskIds);

    /**
     * Complete multiple tasks.
     */
    void batchComplete(BatchCompleteRequest request);

    /**
     * Assign multiple tasks to a user.
     */
    List<TaskDTO> batchAssign(BatchAssignRequest request);
}

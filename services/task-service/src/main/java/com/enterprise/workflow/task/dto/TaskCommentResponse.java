package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Comment response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentResponse {
    private String id;
    private String taskId;
    private String userId;
    private String message;
    private LocalDateTime time;
}

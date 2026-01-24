package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Task comment DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentDTO {
    
    private String id;
    private String taskId;
    private String processInstanceId;
    private String userId;
    private String userFullName;
    private String message;
    private LocalDateTime createdAt;
}

package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Task attachment DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachmentDTO {
    
    private String id;
    private String taskId;
    private String processInstanceId;
    private String name;
    private String description;
    private String contentType;
    private Long size;
    private String url;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}

package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Attachment response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachmentResponse {
    private String id;
    private String taskId;
    private String name;
    private String description;
    private String type;
    private String url;
    private String userId;
    private LocalDateTime time;
    private Long size;
}

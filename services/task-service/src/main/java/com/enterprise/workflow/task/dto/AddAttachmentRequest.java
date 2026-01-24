package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for adding an attachment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddAttachmentRequest {
    private String name;
    private String description;
    private String type;
    private String url;
    private byte[] content;
}

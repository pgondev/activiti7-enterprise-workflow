package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for adding a comment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentRequest {
    private String message;
}

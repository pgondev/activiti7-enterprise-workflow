package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for assigning a task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTaskRequest {
    private String assignee;
}

package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Request for delegating a task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DelegateTaskRequest {
    private String userId;
}

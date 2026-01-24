package com.enterprise.workflow.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Request for batch completion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchCompleteRequest {
    private List<String> taskIds;
    private Map<String, Object> variables;
}

package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request for form submission.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmissionRequest {
    private Map<String, Object> data;
    private String processInstanceId;
    private String taskId;
}

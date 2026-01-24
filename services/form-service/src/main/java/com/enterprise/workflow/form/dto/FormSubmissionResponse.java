package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response for form submission.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmissionResponse {
    private String id;
    private String formId;
    private Integer formVersion;
    private Map<String, Object> data;
    private String submittedBy;
    private LocalDateTime submittedAt;
    private String processInstanceId;
    private String taskId;
}

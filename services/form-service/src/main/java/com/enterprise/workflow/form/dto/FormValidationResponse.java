package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response for form validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormValidationResponse {
    private boolean valid;
    private List<FieldError> errors;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private String type;
    }
}

package com.enterprise.workflow.decision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response for DMN validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionValidationResponse {
    private boolean valid;
    private List<ValidationError> errors;
    private List<String> decisionKeys;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String type;
        private String message;
        private Integer line;
        private Integer column;
    }
}

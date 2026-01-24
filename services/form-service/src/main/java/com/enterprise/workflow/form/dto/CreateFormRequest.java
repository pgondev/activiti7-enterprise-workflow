package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * Request for creating a form.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFormRequest {
    @NotBlank(message = "Form key is required")
    private String key;
    
    @NotBlank(message = "Form name is required")
    private String name;
    
    private String description;
    private String category;
    private Map<String, Object> schema;  // form.io JSON schema
}

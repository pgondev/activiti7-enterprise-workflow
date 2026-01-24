package com.enterprise.workflow.engine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for starting a new process instance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStartRequest {
    
    @NotBlank(message = "Process definition key is required")
    private String processDefinitionKey;
    
    private String businessKey;
    
    private String name;
    
    private Map<String, Object> variables;
}

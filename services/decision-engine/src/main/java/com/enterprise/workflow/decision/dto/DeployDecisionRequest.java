package com.enterprise.workflow.decision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Request for deploying a DMN decision.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployDecisionRequest {
    @NotBlank(message = "Decision name is required")
    private String name;
    
    @NotBlank(message = "DMN XML is required")
    private String dmnXml;
    
    private String tenantId;
    private String category;
}

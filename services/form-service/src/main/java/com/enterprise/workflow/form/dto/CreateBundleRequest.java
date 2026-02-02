package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating/exporting an application bundle
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBundleRequest {

    private String bundleKey;
    private String bundleName;
    private String version;
    private String description;
    private String author;
    private String category;

    /**
     * List of process definition IDs to include
     */
    private List<String> processDefinitionIds;

    /**
     * List of form keys to include
     */
    private List<String> formKeys;

    /**
     * List of decision definition IDs to include
     */
    private List<String> decisionDefinitionIds;

    /**
     * List of case definition IDs to include (optional)
     */
    private List<String> caseDefinitionIds;

    /**
     * Include extension files and resources
     */
    private boolean includeResources;

    /**
     * Additional metadata
     */
    private java.util.Map<String, Object> metadata;
}

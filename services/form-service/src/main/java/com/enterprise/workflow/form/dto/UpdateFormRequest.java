package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request for updating a form.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFormRequest {
    private String name;
    private String description;
    private String category;
    private Map<String, Object> schema;
}

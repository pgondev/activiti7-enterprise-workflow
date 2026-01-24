package com.enterprise.workflow.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for Form representation.
 * Supports dynamic form building similar to Camunda Forms and Flowable Forms.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDTO {

    /**
     * Unique form identifier
     */
    private String id;

    /**
     * Form key for process/task reference
     */
    private String key;

    /**
     * Form name
     */
    private String name;

    /**
     * Form description
     */
    private String description;

    /**
     * Form version
     */
    private Integer version;

    /**
     * Form fields/components
     */
    private List<FormField> fields;

    /**
     * Form creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last modification timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Created by user
     */
    private String createdBy;

    /**
     * Tenant ID for multi-tenancy
     */
    private String tenantId;

    /**
     * Whether the form is a public form (embeddable)
     */
    private Boolean isPublic;

    /**
     * Form layout configuration
     */
    private FormLayout layout;

    /**
     * Form field definition
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormField {
        private String id;
        private String label;
        private FieldType type;
        private String placeholder;
        private String defaultValue;
        private Boolean required;
        private Boolean readOnly;
        private Boolean disabled;
        private String description;
        private FieldValidation validation;
        private List<FieldOption> options; // For select, radio, checkbox
        private ConditionalVisibility conditional;
        private Map<String, Object> properties; // Additional custom properties
    }

    /**
     * Field type enumeration
     */
    public enum FieldType {
        TEXT,
        TEXTAREA,
        NUMBER,
        EMAIL,
        PASSWORD,
        DATE,
        DATETIME,
        TIME,
        CHECKBOX,
        RADIO,
        SELECT,
        MULTISELECT,
        FILE,
        IMAGE,
        RICH_TEXT,
        SIGNATURE,
        SEPARATOR,
        HEADING,
        PARAGRAPH,
        BUTTON,
        GROUP,
        TABLE
    }

    /**
     * Field validation rules
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldValidation {
        private Integer minLength;
        private Integer maxLength;
        private Double min;
        private Double max;
        private String pattern;
        private String patternMessage;
        private List<String> allowedExtensions; // For file uploads
        private Long maxFileSize; // In bytes
    }

    /**
     * Select/radio/checkbox options
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldOption {
        private String label;
        private String value;
    }

    /**
     * Conditional visibility rules
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionalVisibility {
        private String fieldId;
        private String operator; // equals, notEquals, contains, etc.
        private Object value;
    }

    /**
     * Form layout configuration
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormLayout {
        private Integer columns;
        private String direction; // vertical, horizontal
        private String alignment;
    }
}

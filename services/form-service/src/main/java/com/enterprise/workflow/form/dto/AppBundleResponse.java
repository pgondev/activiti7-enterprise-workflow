package com.enterprise.workflow.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for AppBundle
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppBundleResponse {

    private String id;
    private String key;
    private String name;
    private String version;
    private String description;
    private String author;
    private String category;
    private String status;

    private Long bundleSize;
    private Integer artifactCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deployedAt;

    private String createdBy;
    private String deploymentId;

    private ManifestSummary manifest;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManifestSummary {
        private Map<String, Integer> artifactCounts; // "processes": 2, "forms": 3, etc.
        private List<String> processKeys;
        private List<String> formKeys;
        private List<String> decisionKeys;
        private Map<String, Object> metadata;
    }
}

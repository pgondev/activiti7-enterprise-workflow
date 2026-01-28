package com.enterprise.workflow.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTOs for Reporting Service
 */
public class ReportingDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardDTO {
        private String id;
        private String name;
        private String description;
        private String processDefinitionKey;
        private String createdBy;
        private LocalDateTime createdAt;
        private List<WidgetDTO> widgets;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WidgetDTO {
        private String id;
        private String type;  // chart, table, metric, gauge
        private String title;
        private Map<String, Object> config;
        private Integer row;
        private Integer col;
        private Integer width;
        private Integer height;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessDashboardDTO {
        private String processDefinitionKey;
        private String processDefinitionName;
        private ProcessOverviewDTO overview;
        private List<TrendPointDTO> instanceTrend;
        private List<TaskMetricDTO> taskMetrics;
        private List<BottleneckDTO> topBottlenecks;
        private Map<String, Long> statusDistribution;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessOverviewDTO {
        private Long totalInstances;
        private Long runningInstances;
        private Long completedInstances;
        private Long failedInstances;
        private Long cancelledInstances;
        private Double avgDurationSeconds;
        private Double medianDurationSeconds;
        private Long instancesStartedToday;
        private Long instancesCompletedToday;
        private Double completionRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstanceStatisticsDTO {
        private Long total;
        private Map<String, Long> byStatus;
        private Map<String, Long> byVersion;
        private Map<String, Double> avgDurationByVersion;
        private List<PercentileDTO> durationPercentiles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PercentileDTO {
        private Integer percentile;
        private Double value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BottleneckDTO {
        private String activityId;
        private String activityName;
        private String activityType;
        private Double avgDurationSeconds;
        private Long instanceCount;
        private Double percentageOfTotal;
        private Integer rank;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowAnalysisDTO {
        private String processDefinitionKey;
        private List<FlowNodeDTO> nodes;
        private List<FlowEdgeDTO> edges;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowNodeDTO {
        private String id;
        private String name;
        private String type;
        private Long executionCount;
        private Double avgDuration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowEdgeDTO {
        private String sourceId;
        private String targetId;
        private Long transitionCount;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskAnalyticsDTO {
        private String processDefinitionKey;
        private Long totalTasks;
        private Long completedTasks;
        private Long pendingTasks;
        private Double avgCompletionTimeSeconds;
        private Map<String, TaskUserMetricsDTO> byUser;
        private Map<String, TaskUserMetricsDTO> byGroup;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskUserMetricsDTO {
        private String userId;
        private String userName;
        private Long tasksCompleted;
        private Long tasksAssigned;
        private Double avgCompletionTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskMetricDTO {
        private String taskDefinitionKey;
        private String taskName;
        private Long count;
        private Double avgDuration;
        private Double slaCompliance;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlaMetricsDTO {
        private Double overallCompliance;
        private Map<String, Double> complianceByTask;
        private List<SlaBreachDTO> recentBreaches;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlaBreachDTO {
        private String taskId;
        private String taskName;
        private String processInstanceId;
        private LocalDateTime breachTime;
        private Long overdueSeconds;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormDataInsightsDTO {
        private String processDefinitionKey;
        private String formKey;
        private Long submissionCount;
        private Map<String, FieldInsightDTO> fieldInsights;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldInsightDTO {
        private String fieldId;
        private String fieldLabel;
        private String fieldType;
        private Long responseCount;
        private Object mostCommonValue;
        private Double avgNumericValue;
        private Map<String, Long> valueDistribution;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldDistributionDTO {
        private String fieldId;
        private String fieldLabel;
        private Map<String, Long> distribution;
        private Long totalResponses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrossInstanceQueryDTO {
        private String formKey;
        private List<QueryFilterDTO> filters;
        private List<String> selectFields;
        private String groupBy;
        private String aggregation;
        private Integer limit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryFilterDTO {
        private String field;
        private String operator;
        private Object value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrossInstanceQueryResultDTO {
        private Long totalMatches;
        private List<Map<String, Object>> results;
        private Map<String, Object> aggregations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataDTO {
        private String metric;
        private String interval;
        private List<TrendPointDTO> dataPoints;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPointDTO {
        private LocalDateTime timestamp;
        private Double value;
        private String label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportRequestDTO {
        private String reportType;
        private String processDefinitionKey;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<String> columns;
        private Map<String, Object> filters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomReportDTO {
        private String id;
        private String name;
        private String description;
        private String processDefinitionKey;
        private String reportType;
        private Map<String, Object> configuration;
        private String createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isPublic;
    }
}

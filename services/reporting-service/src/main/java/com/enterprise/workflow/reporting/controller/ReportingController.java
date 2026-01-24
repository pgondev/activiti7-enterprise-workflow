package com.enterprise.workflow.reporting.controller;

import com.enterprise.workflow.reporting.dto.*;
import com.enterprise.workflow.reporting.service.DashboardService;
import com.enterprise.workflow.reporting.service.ReportExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API for Reporting and Dashboards.
 * Provides analytics across process instances and form data.
 */
@RestController
@RequestMapping("/api/v1/reporting")
@RequiredArgsConstructor
@Tag(name = "Reporting", description = "Dashboards and analytics API")
public class ReportingController {

    private final DashboardService dashboardService;
    private final ReportExportService exportService;

    // ==================== Dashboard APIs ====================

    @GetMapping("/dashboards")
    @Operation(summary = "Get available dashboards for current user")
    public ResponseEntity<List<DashboardDTO>> getDashboards() {
        return ResponseEntity.ok(dashboardService.getAvailableDashboards());
    }

    @GetMapping("/dashboards/{processDefinitionKey}")
    @Operation(summary = "Get dashboard for a specific process model")
    public ResponseEntity<ProcessDashboardDTO> getProcessDashboard(
            @PathVariable String processDefinitionKey,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        return ResponseEntity.ok(dashboardService.getProcessDashboard(
                processDefinitionKey, startDate, endDate));
    }

    // ==================== Process Analytics ====================

    @GetMapping("/analytics/processes/{processDefinitionKey}/overview")
    @Operation(summary = "Get process overview metrics")
    public ResponseEntity<ProcessOverviewDTO> getProcessOverview(
            @PathVariable String processDefinitionKey,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        return ResponseEntity.ok(dashboardService.getProcessOverview(
                processDefinitionKey, startDate, endDate));
    }

    @GetMapping("/analytics/processes/{processDefinitionKey}/instances")
    @Operation(summary = "Get instance statistics across all instances")
    public ResponseEntity<InstanceStatisticsDTO> getInstanceStatistics(
            @PathVariable String processDefinitionKey,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        return ResponseEntity.ok(dashboardService.getInstanceStatistics(
                processDefinitionKey, startDate, endDate));
    }

    @GetMapping("/analytics/processes/{processDefinitionKey}/bottlenecks")
    @Operation(summary = "Identify process bottlenecks and slow activities")
    public ResponseEntity<List<BottleneckDTO>> getBottlenecks(
            @PathVariable String processDefinitionKey) {
        
        return ResponseEntity.ok(dashboardService.identifyBottlenecks(processDefinitionKey));
    }

    @GetMapping("/analytics/processes/{processDefinitionKey}/flow-analysis")
    @Operation(summary = "Get flow analysis showing path frequencies")
    public ResponseEntity<FlowAnalysisDTO> getFlowAnalysis(
            @PathVariable String processDefinitionKey) {
        
        return ResponseEntity.ok(dashboardService.getFlowAnalysis(processDefinitionKey));
    }

    // ==================== Task Analytics ====================

    @GetMapping("/analytics/tasks/{processDefinitionKey}")
    @Operation(summary = "Get task completion metrics by user/group")
    public ResponseEntity<TaskAnalyticsDTO> getTaskAnalytics(
            @PathVariable String processDefinitionKey,
            @RequestParam(required = false) String groupBy) {
        
        return ResponseEntity.ok(dashboardService.getTaskAnalytics(
                processDefinitionKey, groupBy));
    }

    @GetMapping("/analytics/tasks/{processDefinitionKey}/sla")
    @Operation(summary = "Get SLA compliance metrics")
    public ResponseEntity<SlaMetricsDTO> getSlaMetrics(
            @PathVariable String processDefinitionKey) {
        
        return ResponseEntity.ok(dashboardService.getSlaMetrics(processDefinitionKey));
    }

    // ==================== Form Data Analytics ====================

    @GetMapping("/analytics/forms/{processDefinitionKey}")
    @Operation(summary = "Get aggregated form data insights")
    public ResponseEntity<FormDataInsightsDTO> getFormDataInsights(
            @PathVariable String processDefinitionKey,
            @RequestParam(required = false) String formKey,
            @RequestParam(required = false) List<String> fields) {
        
        return ResponseEntity.ok(dashboardService.getFormDataInsights(
                processDefinitionKey, formKey, fields));
    }

    @GetMapping("/analytics/forms/{processDefinitionKey}/field-distribution")
    @Operation(summary = "Get value distribution for a specific form field")
    public ResponseEntity<FieldDistributionDTO> getFieldDistribution(
            @PathVariable String processDefinitionKey,
            @RequestParam String formKey,
            @RequestParam String fieldId) {
        
        return ResponseEntity.ok(dashboardService.getFieldDistribution(
                processDefinitionKey, formKey, fieldId));
    }

    @PostMapping("/analytics/forms/{processDefinitionKey}/cross-instance-query")
    @Operation(summary = "Query form data across all process instances")
    public ResponseEntity<CrossInstanceQueryResultDTO> queryCrossInstance(
            @PathVariable String processDefinitionKey,
            @RequestBody CrossInstanceQueryDTO query) {
        
        return ResponseEntity.ok(dashboardService.executeCrossInstanceQuery(
                processDefinitionKey, query));
    }

    // ==================== Time-based Analytics ====================

    @GetMapping("/analytics/trends/{processDefinitionKey}")
    @Operation(summary = "Get trend data over time")
    public ResponseEntity<TrendDataDTO> getTrends(
            @PathVariable String processDefinitionKey,
            @RequestParam String metric,
            @RequestParam String interval,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        return ResponseEntity.ok(dashboardService.getTrends(
                processDefinitionKey, metric, interval, startDate, endDate));
    }

    // ==================== Export APIs ====================

    @PostMapping("/export/excel")
    @Operation(summary = "Export report data to Excel")
    public ResponseEntity<Resource> exportToExcel(@RequestBody ExportRequestDTO request) {
        Resource file = exportService.exportToExcel(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @PostMapping("/export/pdf")
    @Operation(summary = "Export report data to PDF")
    public ResponseEntity<Resource> exportToPdf(@RequestBody ExportRequestDTO request) {
        Resource file = exportService.exportToPdf(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    @PostMapping("/export/csv")
    @Operation(summary = "Export raw data to CSV")
    public ResponseEntity<Resource> exportToCsv(@RequestBody ExportRequestDTO request) {
        Resource file = exportService.exportToCsv(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"data.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }

    // ==================== Custom Reports ====================

    @GetMapping("/reports")
    @Operation(summary = "Get saved custom reports")
    public ResponseEntity<List<CustomReportDTO>> getSavedReports() {
        return ResponseEntity.ok(dashboardService.getSavedReports());
    }

    @PostMapping("/reports")
    @Operation(summary = "Save a custom report configuration")
    public ResponseEntity<CustomReportDTO> saveReport(@RequestBody CustomReportDTO report) {
        return ResponseEntity.ok(dashboardService.saveReport(report));
    }

    @DeleteMapping("/reports/{reportId}")
    @Operation(summary = "Delete a custom report")
    public ResponseEntity<Void> deleteReport(@PathVariable String reportId) {
        dashboardService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}

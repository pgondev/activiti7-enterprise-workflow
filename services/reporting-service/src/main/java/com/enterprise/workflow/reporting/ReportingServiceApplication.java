package com.enterprise.workflow.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Reporting & Analytics Service
 * 
 * Provides comprehensive reporting and dashboard capabilities:
 * - Per-process model dashboards
 * - Cross-instance data analysis
 * - Form data aggregation and insights
 * - KPI tracking and alerts
 * - Export to Excel/PDF
 * - Real-time metrics
 * 
 * Inspired by Camunda Optimize and Hyland Analytics.
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ReportingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingServiceApplication.class, args);
    }
}

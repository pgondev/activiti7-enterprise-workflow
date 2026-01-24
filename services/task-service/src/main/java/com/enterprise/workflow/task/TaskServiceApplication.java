package com.enterprise.workflow.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Task Service Application
 * 
 * Comprehensive user task management including:
 * - Task inbox with filtering and sorting
 * - Claim, unclaim, delegate, complete operations
 * - Comments and attachments
 * - Candidate users and groups
 * - Batch operations
 */
@SpringBootApplication(scanBasePackages = {
    "com.enterprise.workflow.task",
    "com.enterprise.workflow.common"
})
@EnableCaching
@EnableAsync
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}

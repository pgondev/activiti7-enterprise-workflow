package com.enterprise.workflow.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Task Service Application - Comprehensive User Task Management.
 * 
 * Provides features inspired by Camunda Tasklist and Flowable Work:
 * - Task inbox with filtering and search
 * - Claim, unclaim, delegate operations
 * - Task comments and attachments
 * - Candidate users and groups
 * - Due dates and priorities
 * - Task history and audit trail
 */
@SpringBootApplication
@EnableCaching
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}

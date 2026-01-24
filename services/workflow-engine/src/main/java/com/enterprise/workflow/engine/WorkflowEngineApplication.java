package com.enterprise.workflow.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Workflow Engine Application
 * 
 * BPMN 2.0 process execution engine based on Activiti7.
 * Provides process deployment, instance management, and signal handling.
 */
@SpringBootApplication(scanBasePackages = {
        "com.enterprise.workflow.engine",
        "com.enterprise.workflow.common"
})
@EnableAsync
public class WorkflowEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowEngineApplication.class, args);
    }
}

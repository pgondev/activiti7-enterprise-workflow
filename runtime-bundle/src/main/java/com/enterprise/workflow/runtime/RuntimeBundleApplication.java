package com.enterprise.workflow.runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runtime Bundle Application - BPMN 2.0 Process Execution Engine.
 * 
 * This is the core process engine based on Activiti7 Cloud that handles:
 * - Process definition deployment
 * - Process instance lifecycle management
 * - Task creation and management
 * - Event handling (signals, messages, timers)
 * - Variable management
 */
@SpringBootApplication
public class RuntimeBundleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuntimeBundleApplication.class, args);
    }
}

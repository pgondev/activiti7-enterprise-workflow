package com.enterprise.workflow.decision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Decision Engine Application
 * 
 * DMN 1.3 decision table execution engine.
 * Provides business rule evaluation and decision automation.
 */
@SpringBootApplication(scanBasePackages = {
    "com.enterprise.workflow.decision",
    "com.enterprise.workflow.common"
})
public class DecisionEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(DecisionEngineApplication.class, args);
    }
}

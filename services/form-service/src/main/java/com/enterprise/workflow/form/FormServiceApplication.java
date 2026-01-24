package com.enterprise.workflow.form;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Form Service Application
 * 
 * Dynamic form management using form.io schema format.
 * Provides form definition storage, rendering API, and submission handling.
 */
@SpringBootApplication(scanBasePackages = {
    "com.enterprise.workflow.form",
    "com.enterprise.workflow.common"
})
public class FormServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormServiceApplication.class, args);
    }
}

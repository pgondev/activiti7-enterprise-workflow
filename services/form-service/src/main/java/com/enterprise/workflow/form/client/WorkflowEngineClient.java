package com.enterprise.workflow.form.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Feign client for Workflow Engine service
 */
@FeignClient(name = "workflow-engine", url = "${application.services.workflow-url:http://localhost:8081}")
public interface WorkflowEngineClient {

    /**
     * Deploy a process definition
     */
    @PostMapping(value = "/api/v1/repository/deployments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> createDeployment(
            @RequestParam("deploymentName") String deploymentName,
            @RequestPart("file") MultipartFile file);

    /**
     * Get process definition by key
     */
    @GetMapping("/api/v1/repository/process-definitions/key/{key}")
    Map<String, Object> getProcessDefinitionByKey(@PathVariable("key") String key);

    /**
     * List all process definitions
     */
    @GetMapping("/api/v1/repository/process-definitions")
    Map<String, Object> listProcessDefinitions();
}

package com.enterprise.workflow.form.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Feign client for Decision Engine service
 */
@FeignClient(name = "decision-engine", url = "${application.services.decision-url:http://localhost:8084}")
public interface DecisionEngineClient {

    /**
     * Deploy a DMN decision table
     */
    @PostMapping(value = "/api/v1/repository/deployments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> createDeployment(
            @RequestParam("deploymentName") String deploymentName,
            @RequestPart("file") MultipartFile file);

    /**
     * Get decision definition by key
     */
    @GetMapping("/api/v1/repository/decision-definitions/key/{key}")
    Map<String, Object> getDecisionDefinitionByKey(@PathVariable("key") String key);

    /**
     * List all decision definitions
     */
    @GetMapping("/api/v1/repository/decision-definitions")
    Map<String, Object> listDecisionDefinitions();
}

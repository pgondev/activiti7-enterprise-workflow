package com.enterprise.workflow.form.controller;

import com.enterprise.workflow.form.dto.AppBundleResponse;
import com.enterprise.workflow.form.dto.CreateBundleRequest;
import com.enterprise.workflow.form.entity.AppBundle;
import com.enterprise.workflow.form.service.AppBundleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Application Bundle Management
 */
@RestController
@RequestMapping("/api/v1/app-bundles")
@Tag(name = "Application Bundles", description = "Manage application bundles (BPMN + Forms + DMN)")
@RequiredArgsConstructor
@Slf4j
public class AppBundleController {

    private final AppBundleService bundleService;

    @GetMapping
    @Operation(summary = "List all application bundles")
    public ResponseEntity<List<AppBundleResponse>> listBundles() {
        return ResponseEntity.ok(bundleService.listBundles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bundle details by ID")
    public ResponseEntity<AppBundleResponse> getBundleById(@PathVariable String id) {
        return ResponseEntity.ok(bundleService.getBundleById(id));
    }

    @GetMapping("/by-key/{key}")
    @Operation(summary = "Get bundle by key")
    public ResponseEntity<AppBundleResponse> getBundleByKey(@PathVariable String key) {
        return ResponseEntity.ok(bundleService.getBundleByKey(key));
    }

    @PostMapping("/export")
    @Operation(summary = "Create and export an application bundle")
    public ResponseEntity<byte[]> exportBundle(@RequestBody CreateBundleRequest request) {
        log.info("Exporting bundle: {}", request.getBundleKey());

        AppBundle bundle = bundleService.createBundle(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + bundle.getKey() + "-" + bundle.getVersion() + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bundle.getBundleData());
    }

    @PostMapping("/import")
    @Operation(summary = "Import application bundle from ZIP file")
    public ResponseEntity<AppBundleResponse> importBundle(
            @RequestParam("file") MultipartFile file) {
        log.info("Importing bundle from file: {}", file.getOriginalFilename());

        AppBundle bundle = bundleService.importBundle(file);
        AppBundleResponse response = bundleService.getBundleById(bundle.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deploy")
    @Operation(summary = "Deploy an application bundle")
    public ResponseEntity<Map<String, Object>> deployBundle(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> deploymentRequest) {

        String deploymentName = deploymentRequest != null
                ? deploymentRequest.getOrDefault("deploymentName", "Deployment of " + id)
                : "Deployment of " + id;

        log.info("Deploying bundle: {} with name: {}", id, deploymentName);
        Map<String, Object> result = bundleService.deployBundle(id, deploymentName);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download bundle as ZIP file")
    public ResponseEntity<byte[]> downloadBundle(@PathVariable String id) {
        log.info("Downloading bundle: {}", id);

        AppBundleResponse bundleInfo = bundleService.getBundleById(id);
        byte[] zipData = bundleService.downloadBundle(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + bundleInfo.getKey() + "-" + bundleInfo.getVersion() + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipData);
    }

    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate bundle structure and contents")
    public ResponseEntity<Map<String, Object>> validateBundle(@PathVariable String id) {
        log.info("Validating bundle: {}", id);
        return ResponseEntity.ok(bundleService.validateBundle(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an application bundle")
    public ResponseEntity<Void> deleteBundle(@PathVariable String id) {
        log.info("Deleting bundle: {}", id);
        bundleService.deleteBundle(id);
        return ResponseEntity.noContent().build();
    }
}

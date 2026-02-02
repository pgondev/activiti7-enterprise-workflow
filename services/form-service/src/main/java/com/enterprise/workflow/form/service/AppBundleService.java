package com.enterprise.workflow.form.service;

import com.enterprise.workflow.form.dto.AppBundleResponse;
import com.enterprise.workflow.form.dto.CreateBundleRequest;
import com.enterprise.workflow.form.entity.AppBundle;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Application Bundle management
 */
public interface AppBundleService {

    /**
     * List all application bundles
     */
    List<AppBundleResponse> listBundles();

    /**
     * Get bundle details by ID
     */
    AppBundleResponse getBundleById(String id);

    /**
     * Get bundle by key
     */
    AppBundleResponse getBundleByKey(String key);

    /**
     * Create and export an application bundle
     * Returns the bundle entity with ZIP data
     */
    AppBundle createBundle(CreateBundleRequest request);

    /**
     * Import a bundle from uploaded ZIP file
     * Validates structure and stores in database
     */
    AppBundle importBundle(MultipartFile zipFile);

    /**
     * Deploy an application bundle
     * Deploys all artifacts (BPMN, DMN, forms) to the respective engines
     */
    Map<String, Object> deployBundle(String bundleId, String deploymentName);

    /**
     * Download bundle as ZIP file
     */
    byte[] downloadBundle(String bundleId);

    /**
     * Delete a bundle
     */
    void deleteBundle(String bundleId);

    /**
     * Validate bundle structure and contents
     */
    Map<String, Object> validateBundle(String bundleId);
}

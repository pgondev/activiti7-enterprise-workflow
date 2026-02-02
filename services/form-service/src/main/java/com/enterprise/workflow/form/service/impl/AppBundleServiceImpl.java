package com.enterprise.workflow.form.service.impl;

import com.enterprise.workflow.form.client.DecisionEngineClient;
import com.enterprise.workflow.form.client.WorkflowEngineClient;
import com.enterprise.workflow.form.dto.AppBundleResponse;
import com.enterprise.workflow.form.dto.CreateBundleRequest;
import com.enterprise.workflow.form.entity.AppBundle;
import com.enterprise.workflow.form.repository.AppBundleRepository;
import com.enterprise.workflow.form.service.AppBundleService;
import com.enterprise.workflow.form.service.FormDefinitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Implementation of AppBundleService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppBundleServiceImpl implements AppBundleService {

    private final AppBundleRepository bundleRepository;
    private final FormDefinitionService formDefinitionService;
    private final WorkflowEngineClient workflowEngineClient;
    private final DecisionEngineClient decisionEngineClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AppBundleResponse> listBundles() {
        log.debug("Listing all application bundles");
        List<AppBundle> bundles = bundleRepository.findAll();
        return bundles.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AppBundleResponse getBundleById(String id) {
        log.debug("Getting bundle by ID: {}", id);
        AppBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bundle not found: " + id));
        return mapToResponse(bundle);
    }

    @Override
    @Transactional(readOnly = true)
    public AppBundleResponse getBundleByKey(String key) {
        log.debug("Getting bundle by key: {}", key);
        AppBundle bundle = bundleRepository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("Bundle not found: " + key));
        return mapToResponse(bundle);
    }

    @Override
    @Transactional
    public AppBundle createBundle(CreateBundleRequest request) {
        log.info("Creating bundle: {} v{}", request.getBundleKey(), request.getVersion());

        try {
            // Check if bundle key already exists
            if (bundleRepository.existsByKey(request.getBundleKey())) {
                throw new RuntimeException("Bundle with key '" + request.getBundleKey() + "' already exists");
            }

            // Build manifest
            Map<String, Object> manifest = buildManifest(request);
            String manifestJson = objectMapper.writeValueAsString(manifest);

            // Create ZIP archive
            byte[] zipData = createZipArchive(request, manifest);

            // Create bundle entity
            AppBundle bundle = AppBundle.builder()
                    .key(request.getBundleKey())
                    .name(request.getBundleName())
                    .version(request.getVersion())
                    .description(request.getDescription())
                    .author(request.getAuthor())
                    .category(request.getCategory())
                    .manifestJson(manifestJson)
                    .bundleData(zipData)
                    .bundleSize((long) zipData.length)
                    .status(AppBundle.BundleStatus.CREATED)
                    .build();

            return bundleRepository.save(bundle);

        } catch (Exception e) {
            log.error("Error creating bundle", e);
            throw new RuntimeException("Failed to create bundle: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AppBundle importBundle(MultipartFile zipFile) {
        log.info("Importing bundle from file: {}", zipFile.getOriginalFilename());

        try {
            // Extract and parse manifest
            Map<String, Object> manifest = extractManifest(zipFile.getBytes());
            String manifestJson = objectMapper.writeValueAsString(manifest);

            String bundleKey = (String) manifest.get("id");
            String bundleName = (String) manifest.get("name");
            String version = (String) manifest.get("version");

            // Check for conflicts
            if (bundleRepository.existsByKey(bundleKey)) {
                log.warn("Bundle with key '{}' already exists, creating new version", bundleKey);
                // Could implement versioning logic here
            }

            // Create bundle entity
            AppBundle bundle = AppBundle.builder()
                    .key(bundleKey)
                    .name(bundleName)
                    .version(version)
                    .description((String) manifest.getOrDefault("description", ""))
                    .author((String) manifest.getOrDefault("author", ""))
                    .manifestJson(manifestJson)
                    .bundleData(zipFile.getBytes())
                    .bundleSize(zipFile.getSize())
                    .status(AppBundle.BundleStatus.VALID)
                    .build();

            return bundleRepository.save(bundle);

        } catch (Exception e) {
            log.error("Error importing bundle", e);
            throw new RuntimeException("Failed to import bundle: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> deployBundle(String bundleId, String deploymentName) {
        log.info("Deploying bundle: {}", bundleId);

        AppBundle bundle = bundleRepository.findById(bundleId)
                .orElseThrow(() -> new RuntimeException("Bundle not found: " + bundleId));

        try {
            bundle.setStatus(AppBundle.BundleStatus.DEPLOYING);
            bundleRepository.save(bundle);

            Map<String, Object> result = new HashMap<>();
            Map<String, List<String>> deployedArtifacts = new HashMap<>();
            deployedArtifacts.put("processes", new ArrayList<>());
            deployedArtifacts.put("forms", new ArrayList<>());
            deployedArtifacts.put("decisions", new ArrayList<>());

            // Extract manifest
            Map<String, Object> manifest = objectMapper.readValue(bundle.getManifestJson(), Map.class);
            Map<String, Object> artifacts = (Map<String, Object>) manifest.get("artifacts");

            // Extract ZIP and deploy artifacts
            try (ZipInputStream zis = new ZipInputStream(new java.io.ByteArrayInputStream(bundle.getBundleData()))) {
                ZipEntry entry;
                Map<String, byte[]> artifactFiles = new HashMap<>();

                // First pass: Extract all files
                while ((entry = zis.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        byte[] fileData = zis.readAllBytes();
                        artifactFiles.put(entry.getName(), fileData);
                        log.debug("Extracted: {}", entry.getName());
                    }
                }

                // Deploy BPMN processes
                List<Map<String, Object>> processes = (List<Map<String, Object>>) artifacts.get("processes");
                if (processes != null) {
                    for (Map<String, Object> process : processes) {
                        String filePath = (String) process.get("file");
                        String processKey = (String) process.get("key");

                        if (artifactFiles.containsKey(filePath)) {
                            try {
                                // Create MultipartFile from bytes
                                byte[] bpmnXml = artifactFiles.get(filePath);
                                MockMultipartFile bpmnFile = new MockMultipartFile(
                                        "file",
                                        processKey + ".bpmn20.xml",
                                        "application/xml",
                                        bpmnXml);

                                // Deploy to workflow engine
                                workflowEngineClient.createDeployment(
                                        deploymentName + " - " + processKey,
                                        bpmnFile);

                                deployedArtifacts.get("processes").add(processKey);
                                log.info("Deployed process: {}", processKey);
                            } catch (Exception e) {
                                log.error("Failed to deploy process: {}", processKey, e);
                                throw new RuntimeException("Process deployment failed: " + processKey, e);
                            }
                        }
                    }
                }

                // Deploy DMN decisions
                List<Map<String, Object>> decisions = (List<Map<String, Object>>) artifacts.get("decisions");
                if (decisions != null) {
                    for (Map<String, Object> decision : decisions) {
                        String filePath = (String) decision.get("file");
                        String decisionKey = (String) decision.get("key");

                        if (artifactFiles.containsKey(filePath)) {
                            try {
                                byte[] dmnXml = artifactFiles.get(filePath);
                                MockMultipartFile dmnFile = new MockMultipartFile(
                                        "file",
                                        decisionKey + ".dmn",
                                        "application/xml",
                                        dmnXml);

                                // Deploy to decision engine
                                decisionEngineClient.createDeployment(
                                        deploymentName + " - " + decisionKey,
                                        dmnFile);

                                deployedArtifacts.get("decisions").add(decisionKey);
                                log.info("Deployed decision: {}", decisionKey);
                            } catch (Exception e) {
                                log.error("Failed to deploy decision: {}", decisionKey, e);
                                throw new RuntimeException("Decision deployment failed: " + decisionKey, e);
                            }
                        }
                    }
                }

                // Deploy forms
                List<Map<String, Object>> forms = (List<Map<String, Object>>) artifacts.get("forms");
                if (forms != null) {
                    for (Map<String, Object> form : forms) {
                        String filePath = (String) form.get("file");
                        String formKey = (String) form.get("key");

                        if (artifactFiles.containsKey(filePath)) {
                            try {
                                String formJson = new String(artifactFiles.get(filePath));
                                // Forms are already deployed via FormDefinitionService during export
                                // Just verify they exist
                                formDefinitionService.getLatestFormByKey(formKey);
                                deployedArtifacts.get("forms").add(formKey);
                                log.info("Verified form: {}", formKey);
                            } catch (Exception e) {
                                log.warn("Form not found, will be skipped: {}", formKey);
                            }
                        }
                    }
                }
            }

            // Update bundle status
            bundle.setStatus(AppBundle.BundleStatus.DEPLOYED);
            bundle.setDeployedAt(LocalDateTime.now());
            bundle.setDeploymentId(UUID.randomUUID().toString());
            bundleRepository.save(bundle);

            result.put("success", true);
            result.put("deployedArtifacts", deployedArtifacts);
            result.put("status", "SUCCESS");
            result.put("deploymentId", bundle.getDeploymentId());
            result.put("timestamp", LocalDateTime.now());

            log.info("Bundle deployed successfully: {}", bundleId);
            return result;

        } catch (Exception e) {
            log.error("Error deploying bundle", e);
            bundle.setStatus(AppBundle.BundleStatus.FAILED);
            bundle.setDeploymentError(e.getMessage());
            bundleRepository.save(bundle);
            throw new RuntimeException("Failed to deploy bundle: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadBundle(String bundleId) {
        log.debug("Downloading bundle: {}", bundleId);
        AppBundle bundle = bundleRepository.findById(bundleId)
                .orElseThrow(() -> new RuntimeException("Bundle not found: " + bundleId));
        return bundle.getBundleData();
    }

    @Override
    @Transactional
    public void deleteBundle(String bundleId) {
        log.info("Deleting bundle: {}", bundleId);
        if (!bundleRepository.existsById(bundleId)) {
            throw new RuntimeException("Bundle not found: " + bundleId);
        }
        bundleRepository.deleteById(bundleId);
    }

    @Override
    public Map<String, Object> validateBundle(String bundleId) {
        log.debug("Validating bundle: {}", bundleId);

        AppBundle bundle = bundleRepository.findById(bundleId)
                .orElseThrow(() -> new RuntimeException("Bundle not found: " + bundleId));

        Map<String, Object> validation = new HashMap<>();
        validation.put("valid", true);
        validation.put("warnings", new ArrayList<>());
        validation.put("errors", new ArrayList<>());

        // TODO: Implement validation logic
        // 1. Validate ZIP structure
        // 2. Validate manifest schema
        // 3. Validate BPMN XML
        // 4. Validate form JSON
        // 5. Check for circular dependencies

        return validation;
    }

    // Helper methods

    private Map<String, Object> buildManifest(CreateBundleRequest request) {
        Map<String, Object> manifest = new HashMap<>();
        manifest.put("id", request.getBundleKey());
        manifest.put("name", request.getBundleName());
        manifest.put("version", request.getVersion());
        manifest.put("description", request.getDescription());
        manifest.put("author", request.getAuthor());
        manifest.put("createdDate", LocalDateTime.now().toString());

        Map<String, Object> artifacts = new HashMap<>();
        artifacts.put("processes", buildProcessArtifacts(request.getProcessDefinitionIds()));
        artifacts.put("forms", buildFormArtifacts(request.getFormKeys()));
        artifacts.put("decisions", buildDecisionArtifacts(request.getDecisionDefinitionIds()));

        manifest.put("artifacts", artifacts);
        manifest.put("metadata", request.getMetadata() != null ? request.getMetadata() : new HashMap<>());

        return manifest;
    }

    private List<Map<String, Object>> buildProcessArtifacts(List<String> processIds) {
        if (processIds == null)
            return Collections.emptyList();
        return processIds.stream()
                .map(id -> {
                    Map<String, Object> artifact = new HashMap<>();
                    artifact.put("key", id);
                    artifact.put("file", "processes/" + id + ".bpmn20.xml");
                    return artifact;
                })
                .toList();
    }

    private List<Map<String, Object>> buildFormArtifacts(List<String> formKeys) {
        if (formKeys == null)
            return Collections.emptyList();
        return formKeys.stream()
                .map(key -> {
                    Map<String, Object> artifact = new HashMap<>();
                    artifact.put("key", key);
                    artifact.put("file", "forms/" + key + ".json");
                    return artifact;
                })
                .toList();
    }

    private List<Map<String, Object>> buildDecisionArtifacts(List<String> decisionIds) {
        if (decisionIds == null)
            return Collections.emptyList();
        return decisionIds.stream()
                .map(id -> {
                    Map<String, Object> artifact = new HashMap<>();
                    artifact.put("key", id);
                    artifact.put("file", "decisions/" + id + ".dmn");
                    return artifact;
                })
                .toList();
    }

    private byte[] createZipArchive(CreateBundleRequest request, Map<String, Object> manifest) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            // Add manifest.json
            ZipEntry manifestEntry = new ZipEntry("manifest.json");
            zos.putNextEntry(manifestEntry);
            zos.write(objectMapper.writeValueAsBytes(manifest));
            zos.closeEntry();

            // Add forms
            if (request.getFormKeys() != null) {
                for (String formKey : request.getFormKeys()) {
                    try {
                        var form = formDefinitionService.getLatestFormByKey(formKey);
                        ZipEntry formEntry = new ZipEntry("forms/" + formKey + ".json");
                        zos.putNextEntry(formEntry);
                        zos.write(form.getSchema().getBytes());
                        zos.closeEntry();
                    } catch (Exception e) {
                        log.warn("Could not add form: {}", formKey, e);
                    }
                }
            }

            // TODO: Add BPMN processes, DMN decisions, etc.
        }
        return baos.toByteArray();
    }

    private Map<String, Object> extractManifest(byte[] zipData) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new java.io.ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if ("manifest.json".equals(entry.getName())) {
                    byte[] manifestBytes = zis.readAllBytes();
                    return objectMapper.readValue(manifestBytes, Map.class);
                }
            }
        }
        throw new RuntimeException("manifest.json not found in bundle");
    }

    private AppBundleResponse mapToResponse(AppBundle bundle) {
        try {
            Map<String, Object> manifest = objectMapper.readValue(bundle.getManifestJson(), Map.class);
            Map<String, Object> artifacts = (Map<String, Object>) manifest.get("artifacts");

            Map<String, Integer> artifactCounts = new HashMap<>();
            if (artifacts != null) {
                artifacts.forEach((key, value) -> {
                    if (value instanceof List) {
                        artifactCounts.put(key, ((List<?>) value).size());
                    }
                });
            }

            return AppBundleResponse.builder()
                    .id(bundle.getId())
                    .key(bundle.getKey())
                    .name(bundle.getName())
                    .version(bundle.getVersion())
                    .description(bundle.getDescription())
                    .author(bundle.getAuthor())
                    .category(bundle.getCategory())
                    .status(bundle.getStatus().name())
                    .bundleSize(bundle.getBundleSize())
                    .artifactCount(artifactCounts.values().stream().mapToInt(Integer::intValue).sum())
                    .createdAt(bundle.getCreatedAt())
                    .updatedAt(bundle.getUpdatedAt())
                    .deployedAt(bundle.getDeployedAt())
                    .createdBy(bundle.getCreatedBy())
                    .deploymentId(bundle.getDeploymentId())
                    .manifest(AppBundleResponse.ManifestSummary.builder()
                            .artifactCounts(artifactCounts)
                            .metadata((Map<String, Object>) manifest.get("metadata"))
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping bundle to response", e);
            throw new RuntimeException("Failed to map bundle: " + e.getMessage(), e);
        }
    }
}

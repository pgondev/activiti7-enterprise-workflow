package com.enterprise.workflow.form.controller;

import com.enterprise.workflow.form.dto.*;
import com.enterprise.workflow.form.service.FormDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST API for Form Definition Management.
 * Supports form.io schema format for dynamic form rendering.
 */
@RestController
@RequestMapping("/api/v1/forms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Form Definitions", description = "Dynamic form management API")
public class FormController {

    private final FormDefinitionService formService;

    // ==================== FORM DEFINITIONS ====================

    @PostMapping
    @Operation(summary = "Create a new form definition")
    public ResponseEntity<FormDefinitionResponse> createForm(
            @Valid @RequestBody CreateFormRequest request) {
        log.info("Creating form: {}", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(formService.createForm(request));
    }

    @GetMapping
    @Operation(summary = "Get all form definitions")
    public ResponseEntity<Page<FormDefinitionResponse>> getForms(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return ResponseEntity.ok(formService.getForms(name, category, pageable));
    }

    @GetMapping("/{formId}")
    @Operation(summary = "Get form definition by ID")
    public ResponseEntity<FormDefinitionResponse> getForm(@PathVariable String formId) {
        return ResponseEntity.ok(formService.getForm(formId));
    }

    @GetMapping("/key/{formKey}")
    @Operation(summary = "Get form definition by key")
    public ResponseEntity<FormDefinitionResponse> getFormByKey(@PathVariable String formKey) {
        return ResponseEntity.ok(formService.getFormByKey(formKey));
    }

    @GetMapping("/key/{formKey}/latest")
    @Operation(summary = "Get latest version of a form by key")
    public ResponseEntity<FormDefinitionResponse> getLatestFormByKey(@PathVariable String formKey) {
        return ResponseEntity.ok(formService.getLatestFormByKey(formKey));
    }

    @PutMapping("/{formId}")
    @Operation(summary = "Update form definition")
    public ResponseEntity<FormDefinitionResponse> updateForm(
            @PathVariable String formId,
            @Valid @RequestBody UpdateFormRequest request) {
        log.info("Updating form: {}", formId);
        return ResponseEntity.ok(formService.updateForm(formId, request));
    }

    @DeleteMapping("/{formId}")
    @Operation(summary = "Delete form definition")
    public ResponseEntity<Void> deleteForm(@PathVariable String formId) {
        log.info("Deleting form: {}", formId);
        formService.deleteForm(formId);
        return ResponseEntity.noContent().build();
    }

    // ==================== FORM VERSIONS ====================

    @GetMapping("/{formId}/versions")
    @Operation(summary = "Get all versions of a form")
    public ResponseEntity<List<FormVersionResponse>> getFormVersions(@PathVariable String formId) {
        return ResponseEntity.ok(formService.getFormVersions(formId));
    }

    @PostMapping("/{formId}/publish")
    @Operation(summary = "Publish a new version of the form")
    public ResponseEntity<FormDefinitionResponse> publishForm(@PathVariable String formId) {
        log.info("Publishing form: {}", formId);
        return ResponseEntity.ok(formService.publishForm(formId));
    }

    // ==================== FORM SUBMISSIONS ====================

    @PostMapping("/{formId}/submit")
    @Operation(summary = "Submit form data")
    public ResponseEntity<FormSubmissionResponse> submitForm(
            @PathVariable String formId,
            @Valid @RequestBody FormSubmissionRequest request) {
        log.info("Submitting form: {}", formId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(formService.submitForm(formId, request));
    }

    @GetMapping("/{formId}/submissions")
    @Operation(summary = "Get form submissions")
    public ResponseEntity<Page<FormSubmissionResponse>> getSubmissions(
            @PathVariable String formId,
            Pageable pageable) {
        return ResponseEntity.ok(formService.getSubmissions(formId, pageable));
    }

    @GetMapping("/{formId}/submissions/{submissionId}")
    @Operation(summary = "Get a specific form submission")
    public ResponseEntity<FormSubmissionResponse> getSubmission(
            @PathVariable String formId,
            @PathVariable String submissionId) {
        return ResponseEntity.ok(formService.getSubmission(formId, submissionId));
    }

    // ==================== VALIDATION ====================

    @PostMapping("/{formId}/validate")
    @Operation(summary = "Validate form data without submitting")
    public ResponseEntity<FormValidationResponse> validateForm(
            @PathVariable String formId,
            @RequestBody FormSubmissionRequest request) {
        return ResponseEntity.ok(formService.validateForm(formId, request));
    }

    // ==================== PROCESS INTEGRATION ====================

    @GetMapping("/process/{processDefinitionKey}")
    @Operation(summary = "Get forms linked to a process definition")
    public ResponseEntity<List<FormDefinitionResponse>> getFormsByProcess(
            @PathVariable String processDefinitionKey) {
        return ResponseEntity.ok(formService.getFormsByProcess(processDefinitionKey));
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get form for a specific task")
    public ResponseEntity<FormDefinitionResponse> getFormForTask(@PathVariable String taskId) {
        return ResponseEntity.ok(formService.getFormForTask(taskId));
    }
}

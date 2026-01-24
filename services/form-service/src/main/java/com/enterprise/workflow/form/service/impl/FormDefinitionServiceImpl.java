package com.enterprise.workflow.form.service.impl;

import com.enterprise.workflow.form.dto.*;
import com.enterprise.workflow.form.entity.FormDefinition;
import com.enterprise.workflow.form.entity.FormSubmission;
import com.enterprise.workflow.form.repository.FormDefinitionRepository;
import com.enterprise.workflow.form.repository.FormSubmissionRepository;
import com.enterprise.workflow.form.service.FormDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of FormDefinitionService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FormDefinitionServiceImpl implements FormDefinitionService {

    private final FormDefinitionRepository formRepository;
    private final FormSubmissionRepository submissionRepository;

    @Override
    public FormDefinitionResponse createForm(CreateFormRequest request) {
        log.debug("Creating form with key: {}", request.getKey());
        
        FormDefinition form = FormDefinition.builder()
                .key(request.getKey())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .schema(request.getSchema())
                .version(1)
                .published(false)
                .createdBy(getCurrentUser())
                .build();
        
        form = formRepository.save(form);
        return mapToResponse(form);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormDefinitionResponse> getForms(String name, String category, Pageable pageable) {
        Page<FormDefinition> page;
        
        if (name != null && category != null) {
            page = formRepository.findByNameContainingIgnoreCaseAndCategory(name, category, pageable);
        } else if (name != null) {
            page = formRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (category != null) {
            page = formRepository.findByCategory(category, pageable);
        } else {
            page = formRepository.findAll(pageable);
        }
        
        return page.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public FormDefinitionResponse getForm(String formId) {
        return formRepository.findById(formId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Form not found: " + formId));
    }

    @Override
    @Transactional(readOnly = true)
    public FormDefinitionResponse getFormByKey(String formKey) {
        return formRepository.findByKey(formKey)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Form not found with key: " + formKey));
    }

    @Override
    @Transactional(readOnly = true)
    public FormDefinitionResponse getLatestFormByKey(String formKey) {
        return formRepository.findLatestByKey(formKey)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Form not found with key: " + formKey));
    }

    @Override
    public FormDefinitionResponse updateForm(String formId, UpdateFormRequest request) {
        log.debug("Updating form: {}", formId);
        
        FormDefinition form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found: " + formId));
        
        if (request.getName() != null) form.setName(request.getName());
        if (request.getDescription() != null) form.setDescription(request.getDescription());
        if (request.getCategory() != null) form.setCategory(request.getCategory());
        if (request.getSchema() != null) form.setSchema(request.getSchema());
        
        form = formRepository.save(form);
        return mapToResponse(form);
    }

    @Override
    public void deleteForm(String formId) {
        log.debug("Deleting form: {}", formId);
        formRepository.deleteById(formId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormVersionResponse> getFormVersions(String formId) {
        FormDefinition form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found: " + formId));
        
        return formRepository.findByKeyOrderByVersionDesc(form.getKey()).stream()
                .map(f -> FormVersionResponse.builder()
                        .id(f.getId())
                        .formId(formId)
                        .version(f.getVersion())
                        .schema(f.getSchema())
                        .createdAt(f.getCreatedAt())
                        .createdBy(f.getCreatedBy())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public FormDefinitionResponse publishForm(String formId) {
        log.debug("Publishing form: {}", formId);
        
        FormDefinition form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found: " + formId));
        
        // Create new version
        FormDefinition newVersion = FormDefinition.builder()
                .key(form.getKey())
                .name(form.getName())
                .description(form.getDescription())
                .category(form.getCategory())
                .schema(form.getSchema())
                .version(form.getVersion() + 1)
                .published(true)
                .createdBy(getCurrentUser())
                .build();
        
        newVersion = formRepository.save(newVersion);
        return mapToResponse(newVersion);
    }

    @Override
    public FormSubmissionResponse submitForm(String formId, FormSubmissionRequest request) {
        log.debug("Submitting form: {}", formId);
        
        FormDefinition form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found: " + formId));
        
        FormSubmission submission = FormSubmission.builder()
                .formId(formId)
                .formVersion(form.getVersion())
                .data(request.getData())
                .processInstanceId(request.getProcessInstanceId())
                .taskId(request.getTaskId())
                .submittedBy(getCurrentUser())
                .build();
        
        submission = submissionRepository.save(submission);
        return mapSubmissionToResponse(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormSubmissionResponse> getSubmissions(String formId, Pageable pageable) {
        return submissionRepository.findByFormId(formId, pageable)
                .map(this::mapSubmissionToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public FormSubmissionResponse getSubmission(String formId, String submissionId) {
        return submissionRepository.findById(submissionId)
                .map(this::mapSubmissionToResponse)
                .orElseThrow(() -> new RuntimeException("Submission not found: " + submissionId));
    }

    @Override
    public FormValidationResponse validateForm(String formId, FormSubmissionRequest request) {
        // Basic validation - can be extended with form.io validation logic
        FormDefinition form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found: " + formId));
        
        List<FormValidationResponse.FieldError> errors = new ArrayList<>();
        
        // Validate required fields based on schema
        // This is a placeholder - real implementation would parse form.io schema
        
        return FormValidationResponse.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinitionResponse> getFormsByProcess(String processDefinitionKey) {
        // TODO: Implement process-form linking
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public FormDefinitionResponse getFormForTask(String taskId) {
        // TODO: Integrate with task service to get form key
        throw new UnsupportedOperationException("Task form lookup not yet implemented");
    }

    private FormDefinitionResponse mapToResponse(FormDefinition form) {
        return FormDefinitionResponse.builder()
                .id(form.getId())
                .key(form.getKey())
                .name(form.getName())
                .description(form.getDescription())
                .category(form.getCategory())
                .version(form.getVersion())
                .published(form.isPublished())
                .schema(form.getSchema())
                .createdAt(form.getCreatedAt())
                .updatedAt(form.getUpdatedAt())
                .createdBy(form.getCreatedBy())
                .build();
    }

    private FormSubmissionResponse mapSubmissionToResponse(FormSubmission submission) {
        return FormSubmissionResponse.builder()
                .id(submission.getId())
                .formId(submission.getFormId())
                .formVersion(submission.getFormVersion())
                .data(submission.getData())
                .submittedBy(submission.getSubmittedBy())
                .submittedAt(submission.getSubmittedAt())
                .processInstanceId(submission.getProcessInstanceId())
                .taskId(submission.getTaskId())
                .build();
    }

    private String getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}

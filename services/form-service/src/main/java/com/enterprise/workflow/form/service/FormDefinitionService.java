package com.enterprise.workflow.form.service;

import com.enterprise.workflow.form.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Form Definition management.
 */
public interface FormDefinitionService {

    // Form CRUD
    FormDefinitionResponse createForm(CreateFormRequest request);
    
    Page<FormDefinitionResponse> getForms(String name, String category, Pageable pageable);
    
    FormDefinitionResponse getForm(String formId);
    
    FormDefinitionResponse getFormByKey(String formKey);
    
    FormDefinitionResponse getLatestFormByKey(String formKey);
    
    FormDefinitionResponse updateForm(String formId, UpdateFormRequest request);
    
    void deleteForm(String formId);

    // Versioning
    List<FormVersionResponse> getFormVersions(String formId);
    
    FormDefinitionResponse publishForm(String formId);

    // Submissions
    FormSubmissionResponse submitForm(String formId, FormSubmissionRequest request);
    
    Page<FormSubmissionResponse> getSubmissions(String formId, Pageable pageable);
    
    FormSubmissionResponse getSubmission(String formId, String submissionId);

    // Validation
    FormValidationResponse validateForm(String formId, FormSubmissionRequest request);

    // Process Integration
    List<FormDefinitionResponse> getFormsByProcess(String processDefinitionKey);
    
    FormDefinitionResponse getFormForTask(String taskId);
}

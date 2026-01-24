package com.enterprise.workflow.form.repository;

import com.enterprise.workflow.form.entity.FormSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for FormSubmission entities.
 */
@Repository
public interface FormSubmissionRepository extends JpaRepository<FormSubmission, String> {

    Page<FormSubmission> findByFormId(String formId, Pageable pageable);
    
    List<FormSubmission> findByProcessInstanceId(String processInstanceId);
    
    List<FormSubmission> findByTaskId(String taskId);
}

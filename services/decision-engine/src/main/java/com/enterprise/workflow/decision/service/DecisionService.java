package com.enterprise.workflow.decision.service;

import com.enterprise.workflow.decision.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for DMN Decision Management.
 */
public interface DecisionService {

    // Decision Definitions
    Page<DecisionDefinitionResponse> getDecisions(Pageable pageable);
    
    DecisionDefinitionResponse getDecision(String decisionId);
    
    DecisionDefinitionResponse getDecisionByKey(String decisionKey);
    
    String getDecisionXml(String decisionId);

    // Deployment
    DeploymentResponse deployDecision(DeployDecisionRequest request);
    
    void deleteDeployment(String deploymentId);

    // Execution
    DecisionExecutionResponse executeDecision(String decisionKey, Map<String, Object> variables);
    
    DecisionExecutionResponse executeDecisionById(String decisionId, Map<String, Object> variables);
    
    List<DecisionExecutionResponse> executeBatch(String decisionKey, List<Map<String, Object>> inputsList);

    // History
    Page<DecisionExecutionHistoryResponse> getExecutionHistory(String decisionKey, String processInstanceId, Pageable pageable);
    
    DecisionExecutionHistoryResponse getExecution(String executionId);

    // Validation
    DecisionValidationResponse validateDecision(String dmnXml);
}

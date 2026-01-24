package com.enterprise.workflow.decision.service.impl;

import com.enterprise.workflow.decision.dto.*;
import com.enterprise.workflow.decision.service.DecisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DecisionService.
 * Uses Activiti Cloud runtime for DMN execution.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionServiceImpl implements DecisionService {

        private final RepositoryService repositoryService;

        @Override
        public Page<DecisionDefinitionResponse> getDecisions(Pageable pageable) {
                // Query deployed decision definitions
                List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                                .orderByProcessDefinitionKey().asc()
                                .listPage((int) pageable.getOffset(), pageable.getPageSize());

                long total = repositoryService.createProcessDefinitionQuery().count();

                List<DecisionDefinitionResponse> responses = definitions.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());

                return new PageImpl<>(responses, pageable, total);
        }

        @Override
        public DecisionDefinitionResponse getDecision(String decisionId) {
                ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                                .processDefinitionId(decisionId)
                                .singleResult();

                if (definition == null) {
                        throw new RuntimeException("Decision not found: " + decisionId);
                }

                return mapToResponse(definition);
        }

        @Override
        public DecisionDefinitionResponse getDecisionByKey(String decisionKey) {
                ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                                .processDefinitionKey(decisionKey)
                                .latestVersion()
                                .singleResult();

                if (definition == null) {
                        throw new RuntimeException("Decision not found with key: " + decisionKey);
                }

                return mapToResponse(definition);
        }

        @Override
        public String getDecisionXml(String decisionId) {
                ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                                .processDefinitionId(decisionId)
                                .singleResult();

                if (definition == null) {
                        throw new RuntimeException("Decision not found: " + decisionId);
                }

                try (var stream = repositoryService.getResourceAsStream(
                                definition.getDeploymentId(), definition.getResourceName())) {
                        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (Exception e) {
                        throw new RuntimeException("Failed to read decision XML", e);
                }
        }

        @Override
        public DeploymentResponse deployDecision(DeployDecisionRequest request) {
                log.debug("Deploying decision: {}", request.getName());

                Deployment deployment = repositoryService.createDeployment()
                                .name(request.getName())
                                .addInputStream(request.getName() + ".dmn",
                                                new ByteArrayInputStream(
                                                                request.getDmnXml().getBytes(StandardCharsets.UTF_8)))
                                .tenantId(request.getTenantId())
                                .category(request.getCategory())
                                .deploy();

                return DeploymentResponse.builder()
                                .deploymentId(deployment.getId())
                                .name(deployment.getName())
                                .deployedAt(deployment.getDeploymentTime() != null
                                                ? LocalDateTime.ofInstant(deployment.getDeploymentTime().toInstant(),
                                                                ZoneId.systemDefault())
                                                : LocalDateTime.now())
                                .decisionKeys(List.of(request.getName()))
                                .build();
        }

        @Override
        public void deleteDeployment(String deploymentId) {
                log.debug("Deleting deployment: {}", deploymentId);
                repositoryService.deleteDeployment(deploymentId, true);
        }

        @Override
        public DecisionExecutionResponse executeDecision(String decisionKey, Map<String, Object> variables) {
                log.debug("Executing decision: {} with variables: {}", decisionKey, variables);

                long startTime = System.currentTimeMillis();

                // Decision execution would typically use DMN engine
                // This is a placeholder - actual implementation depends on DMN runtime
                List<Map<String, Object>> outputs = new ArrayList<>();
                outputs.add(variables); // Echo back for now

                long executionTime = System.currentTimeMillis() - startTime;

                return DecisionExecutionResponse.builder()
                                .executionId(UUID.randomUUID().toString())
                                .decisionKey(decisionKey)
                                .inputs(variables)
                                .outputs(outputs)
                                .executedAt(LocalDateTime.now())
                                .executionTimeMs(executionTime)
                                .build();
        }

        @Override
        public DecisionExecutionResponse executeDecisionById(String decisionId, Map<String, Object> variables) {
                DecisionDefinitionResponse decision = getDecision(decisionId);
                return executeDecision(decision.getKey(), variables);
        }

        @Override
        public List<DecisionExecutionResponse> executeBatch(String decisionKey, List<Map<String, Object>> inputsList) {
                return inputsList.stream()
                                .map(inputs -> executeDecision(decisionKey, inputs))
                                .collect(Collectors.toList());
        }

        @Override
        public Page<DecisionExecutionHistoryResponse> getExecutionHistory(
                        String decisionKey, String processInstanceId, Pageable pageable) {
                // History would come from audit logs
                return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        @Override
        public DecisionExecutionHistoryResponse getExecution(String executionId) {
                throw new RuntimeException("Execution not found: " + executionId);
        }

        @Override
        public DecisionValidationResponse validateDecision(String dmnXml) {
                List<DecisionValidationResponse.ValidationError> errors = new ArrayList<>();

                if (dmnXml == null || dmnXml.isEmpty()) {
                        errors.add(DecisionValidationResponse.ValidationError.builder()
                                        .type("EMPTY")
                                        .message("DMN XML is empty")
                                        .build());
                } else if (!dmnXml.contains("<definitions")) {
                        errors.add(DecisionValidationResponse.ValidationError.builder()
                                        .type("INVALID_ROOT")
                                        .message("Missing DMN definitions element")
                                        .build());
                }

                return DecisionValidationResponse.builder()
                                .valid(errors.isEmpty())
                                .errors(errors)
                                .decisionKeys(new ArrayList<>())
                                .build();
        }

        private DecisionDefinitionResponse mapToResponse(ProcessDefinition definition) {
                return DecisionDefinitionResponse.builder()
                                .id(definition.getId())
                                .key(definition.getKey())
                                .name(definition.getName())
                                .description(definition.getDescription())
                                .version(definition.getVersion())
                                .deploymentId(definition.getDeploymentId())
                                .resourceName(definition.getResourceName())
                                .tenantId(definition.getTenantId())
                                .build();
        }
}

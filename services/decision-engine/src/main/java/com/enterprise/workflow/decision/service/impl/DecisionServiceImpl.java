package com.enterprise.workflow.decision.service.impl;

import com.enterprise.workflow.decision.dto.*;
import com.enterprise.workflow.decision.service.DecisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.dmn.api.DmnDecision;
import org.flowable.dmn.api.DmnDeployment;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.dmn.api.DmnDecisionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DecisionService using Flowable DMN.
 * Migrated from Activiti to Flowable for embedded DMN support.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DecisionServiceImpl implements DecisionService {

        private final DmnRepositoryService repositoryService;
        private final DmnDecisionService ruleService;

        @Override
        @Transactional(readOnly = true)
        public Page<DecisionDefinitionResponse> getDecisions(Pageable pageable) {
                // Query deployed decision definitions
                List<DmnDecision> definitions = repositoryService.createDecisionQuery()
                                .orderByDecisionKey().asc()
                                .listPage((int) pageable.getOffset(), pageable.getPageSize());

                long total = repositoryService.createDecisionQuery().count();

                List<DecisionDefinitionResponse> responses = definitions.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());

                return new PageImpl<>(responses, pageable, total);
        }

        @Override
        @Transactional(readOnly = true)
        public DecisionDefinitionResponse getDecision(String decisionId) {
                DmnDecision definition = repositoryService.createDecisionQuery()
                                .decisionId(decisionId)
                                .singleResult();

                if (definition == null) {
                        throw new RuntimeException("Decision not found: " + decisionId);
                }

                return mapToResponse(definition);
        }

        @Override
        @Transactional(readOnly = true)
        public DecisionDefinitionResponse getDecisionByKey(String decisionKey) {
                DmnDecision definition = repositoryService.createDecisionQuery()
                                .decisionKey(decisionKey)
                                .latestVersion()
                                .singleResult();

                if (definition == null) {
                        throw new RuntimeException("Decision not found with key: " + decisionKey);
                }

                return mapToResponse(definition);
        }

        @Override
        @Transactional(readOnly = true)
        public String getDecisionXml(String decisionId) {
                DmnDecision definition = repositoryService.createDecisionQuery()
                                .decisionId(decisionId)
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

                DmnDeployment deployment = repositoryService.createDeployment()
                                .name(request.getName())
                                .addString(request.getName() + ".dmn", request.getDmnXml())
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
                repositoryService.deleteDeployment(deploymentId);
        }

        @Override
        public DecisionExecutionResponse executeDecision(String decisionKey, Map<String, Object> variables) {
                log.debug("Executing decision: {} with variables: {}", decisionKey, variables);

                long startTime = System.currentTimeMillis();

                // Execute decision using Flowable DMN engine
                // Flowable executeDecisionByKey returns Map<String, Object> (single) or
                // List<Map<String, Object>> (multiple)
                Map<String, Object> output = ruleService.createExecuteDecisionBuilder()
                                .decisionKey(decisionKey)
                                .variables(variables)
                                .executeWithSingleResult();

                List<Map<String, Object>> outputs = new ArrayList<>();
                if (output != null) {
                        outputs.add(output);
                }

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
                // Placeholder for history
                return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        @Override
        public DecisionExecutionHistoryResponse getExecution(String executionId) {
                throw new UnsupportedOperationException("History not implemented yet");
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

        private DecisionDefinitionResponse mapToResponse(DmnDecision definition) {
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

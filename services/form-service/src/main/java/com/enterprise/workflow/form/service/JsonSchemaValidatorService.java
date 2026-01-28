package com.enterprise.workflow.form.service;

import com.enterprise.workflow.form.dto.FormValidationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JsonSchemaValidatorService {

    private final ObjectMapper objectMapper;

    /**
     * Validates data against the provided JSON schema.
     *
     * @param schemaMap The form definition schema (map)
     * @param dataMap   The data to validate (map)
     * @return List of validation errors, empty if valid
     */
    public List<FormValidationResponse.FieldError> validate(Map<String, Object> schemaMap,
            Map<String, Object> dataMap) {
        List<FormValidationResponse.FieldError> errors = new ArrayList<>();

        try {
            // Convert Maps to JsonNodes
            JsonNode schemaNode = objectMapper.valueToTree(schemaMap);
            JsonNode dataNode = objectMapper.valueToTree(dataMap);

            // Create Schema
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(schemaNode);

            // Validate
            Set<ValidationMessage> validationMessages = schema.validate(dataNode);

            // Map errors
            for (ValidationMessage message : validationMessages) {
                errors.add(FormValidationResponse.FieldError.builder()
                        .field(message.getPath())
                        .message(message.getMessage())
                        .build());
            }

        } catch (Exception e) {
            log.error("Error validating schema", e);
            errors.add(FormValidationResponse.FieldError.builder()
                    .field("schema")
                    .message("Internal validation error: " + e.getMessage())
                    .build());
        }

        return errors;
    }
}

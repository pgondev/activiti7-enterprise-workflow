package com.enterprise.workflow.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends WorkflowException {

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(
                String.format("%s with ID '%s' not found", resourceType, resourceId),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}

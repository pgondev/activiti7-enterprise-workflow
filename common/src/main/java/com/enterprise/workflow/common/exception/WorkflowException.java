package com.enterprise.workflow.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for workflow platform errors.
 */
public class WorkflowException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public WorkflowException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "WORKFLOW_ERROR";
    }

    public WorkflowException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "WORKFLOW_ERROR";
    }

    public WorkflowException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "WORKFLOW_ERROR";
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

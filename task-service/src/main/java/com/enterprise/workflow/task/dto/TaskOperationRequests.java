package com.enterprise.workflow.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Request DTOs for task operations.
 */
public class TaskOperationRequests {
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DelegateTaskRequest {
    @NotBlank(message = "Delegate user ID is required")
    private String delegateUserId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AssignTaskRequest {
    @NotBlank(message = "Assignee is required")
    private String assignee;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UpdatePriorityRequest {
    @NotNull(message = "Priority is required")
    private Integer priority;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UpdateDueDateRequest {
    private LocalDateTime dueDate;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AddCommentRequest {
    @NotBlank(message = "Message is required")
    private String message;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class BatchCompleteRequest {
    @NotNull(message = "Task IDs are required")
    private List<String> taskIds;
    private Map<String, Object> variables;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class BatchAssignRequest {
    @NotNull(message = "Task IDs are required")
    private List<String> taskIds;
    @NotBlank(message = "Assignee is required")
    private String assignee;
}

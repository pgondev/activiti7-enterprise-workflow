package com.enterprise.workflow.form.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "task-service", url = "${application.services.task-url:http://localhost:8083}")
public interface TaskServiceClient {

    @GetMapping("/api/v1/tasks/{taskId}")
    Map<String, Object> getTask(@PathVariable("taskId") String taskId);
}

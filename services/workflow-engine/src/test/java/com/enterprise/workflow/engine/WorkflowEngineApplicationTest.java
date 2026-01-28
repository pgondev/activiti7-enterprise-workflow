package com.enterprise.workflow.engine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.RepositoryService;

@SpringBootTest
class WorkflowEngineApplicationTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    void testStartProcess() {
        // Verify deployment
        long count = repositoryService.createProcessDefinitionQuery().processDefinitionKey("InvoiceProcess_1").count();
        System.out.println("Process definition count: " + count);

        if (count == 0) {
            System.out.println("Deploying process manually...");
            repositoryService.createDeployment()
                    .addClasspathResource("processes/invoice-process.bpmn20.xml")
                    .deploy();
        }

        // Try to start process
        try {
            System.out.println("Attempting to start process...");
            runtimeService.startProcessInstanceByKey("InvoiceProcess_1");
            System.out.println("Process started successfully.");
        } catch (Exception e) {
            System.err.println("Failed to start process:");
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testGetProcessModel() throws java.io.IOException {
        // Ensure deployed
        if (repositoryService.createProcessDefinitionQuery().processDefinitionKey("InvoiceProcess_1").count() == 0) {
            repositoryService.createDeployment()
                    .addClasspathResource("processes/invoice-process.bpmn20.xml")
                    .deploy();
        }

        org.activiti.engine.repository.ProcessDefinition def = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("InvoiceProcess_1")
                .latestVersion()
                .singleResult();

        if (def == null) {
            throw new RuntimeException("Process definition not found!");
        }

        System.out.println("Found definition: " + def.getId());
        System.out.println("Resource Name: " + def.getResourceName());
        System.out.println("Deployment ID: " + def.getDeploymentId());

        try (java.io.InputStream stream = repositoryService.getResourceAsStream(def.getDeploymentId(),
                def.getResourceName())) {
            if (stream == null) {
                throw new RuntimeException("Resource stream is NULL for " + def.getResourceName());
            }
            String xml = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("Method retrieved " + xml.length() + " bytes of XML.");
            if (!xml.contains("InvoiceProcess_1")) {
                throw new RuntimeException("XML does not contain process ID");
            }
        }
    }
}

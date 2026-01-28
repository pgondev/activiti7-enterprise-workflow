package com.enterprise.workflow.engine.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProcessDefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private org.activiti.engine.RepositoryService repositoryService;

    @Test
    void testGetDefinitionXml_ContentNegotiation() throws Exception {
        // Ensure deployed
        if (repositoryService.createProcessDefinitionQuery().processDefinitionKey("InvoiceProcess_1").count() == 0) {
            repositoryService.createDeployment()
                    .addClasspathResource("processes/invoice-process.bpmn20.xml")
                    .deploy();
        }

        String id = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("InvoiceProcess_1")
                .latestVersion()
                .singleResult()
                .getId();

        // 1. Request with Accept: application/json (Simulate Frontend default)
        // Expecting 406 because controller produces application/xml
        mockMvc.perform(get("/api/v1/process-definitions/" + id + "/xml")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable()); // 406

        // 2. Request with Accept: application/xml (Correct)
        mockMvc.perform(get("/api/v1/process-definitions/" + id + "/xml")
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());
    }
}

package com.harness.backend.controller;

import com.harness.backend.model.RequestEntry;
import com.harness.backend.repository.RequestEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeployControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestEntryRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createEntry_returnsCreatedStatus() throws Exception {
        mockMvc.perform(post("/deploy/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("normal"))
                .andExpect(jsonPath("$.created_at").isNumber());
    }

    @Test
    void executionDetails_returnsDeploymentInfo() throws Exception {
        mockMvc.perform(get("/deploy/execution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service_name").exists())
                .andExpect(jsonPath("$.last_execution_id").exists())
                .andExpect(jsonPath("$.application_version").exists())
                .andExpect(jsonPath("$.deployment_type").value("normal"));
    }

    @Test
    void checkRelease_returnsDeploymentType() throws Exception {
        mockMvc.perform(get("/deploy/check/release"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deployment_type").value("normal"));
    }

    @Test
    void distribution_returnsFilteredEntries() throws Exception {
        // Create some entries first
        repository.save(new RequestEntry("normal"));
        repository.save(new RequestEntry("canary"));

        Instant start = Instant.now().minusSeconds(60);
        Instant end = Instant.now().plusSeconds(60);

        mockMvc.perform(get("/deploy/distribution")
                        .param("start_timestamp", start.toString())
                        .param("end_timestamp", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].created_at").isNumber());
    }

    @Test
    void distributionBar_returnsFilteredEntries() throws Exception {
        repository.save(new RequestEntry("normal"));

        Instant start = Instant.now().minusSeconds(60);
        Instant end = Instant.now().plusSeconds(60);

        mockMvc.perform(get("/deploy/distribution/bar")
                        .param("start_timestamp", start.toString())
                        .param("end_timestamp", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("normal"))
                .andExpect(jsonPath("$[0].created_at").isString());
    }

    @Test
    void distribution_withNoEntries_returnsEmptyList() throws Exception {
        Instant start = Instant.now().minusSeconds(60);
        Instant end = Instant.now().plusSeconds(60);

        mockMvc.perform(get("/deploy/distribution")
                        .param("start_timestamp", start.toString())
                        .param("end_timestamp", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

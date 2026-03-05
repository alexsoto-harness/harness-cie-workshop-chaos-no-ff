package com.harness.backend.controller;

import com.harness.backend.model.RequestEntry;
import com.harness.backend.repository.RequestEntryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/deploy")
public class DeployController {

    private final RequestEntryRepository repository;

    @Value("${SERVICE_NAME:}")
    private String serviceName;

    @Value("${LAST_EXECUTION_ID:}")
    private String lastExecutionId;

    @Value("${APPLICATION_VERSION:}")
    private String applicationVersion;

    @Value("${HOSTNAME:}")
    private String hostname;

    public DeployController(RequestEntryRepository repository) {
        this.repository = repository;
    }

    /**
     * POST /deploy/create
     * Creates a RequestEntry with name="canary" or "normal" based on HOSTNAME.
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createEntry(@RequestBody(required = false) Map<String, Object> body) {
        String deploymentType = resolveDeploymentType();
        RequestEntry entry = new RequestEntry(deploymentType);
        RequestEntry saved = repository.save(entry);

        Map<String, Object> response = new HashMap<>();
        response.put("name", saved.getName());
        response.put("created_at", saved.getCreatedAt().getEpochSecond());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /deploy/execution
     * Returns execution details from environment variables.
     */
    @GetMapping("/execution")
    public ResponseEntity<Map<String, String>> executionDetails() {
        String deploymentType = resolveDeploymentType();

        Map<String, String> payload = new HashMap<>();
        payload.put("service_name", serviceName);
        payload.put("last_execution_id", lastExecutionId);
        payload.put("application_version", applicationVersion);
        payload.put("deployment_type", deploymentType);
        return ResponseEntity.ok(payload);
    }

    /**
     * GET /deploy/distribution?start_timestamp=...&end_timestamp=...
     * Returns list of entries filtered by time range with epoch seconds.
     */
    @GetMapping("/distribution")
    public ResponseEntity<List<Map<String, Object>>> distribution(
            @RequestParam("start_timestamp") String startTimestamp,
            @RequestParam("end_timestamp") String endTimestamp) {

        Instant start = parseTimestamp(startTimestamp);
        Instant end = parseTimestamp(endTimestamp);

        List<RequestEntry> entries = repository.findByCreatedAtBetween(start, end);
        List<Map<String, Object>> result = entries.stream()
                .map(this::toSerializedEntry)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * GET /deploy/distribution/bar?start_timestamp=...&end_timestamp=...
     * Returns list of entries filtered by time range (same format, raw values).
     */
    @GetMapping("/distribution/bar")
    public ResponseEntity<List<Map<String, Object>>> distributionBar(
            @RequestParam("start_timestamp") String startTimestamp,
            @RequestParam("end_timestamp") String endTimestamp) {

        Instant start = parseTimestamp(startTimestamp);
        Instant end = parseTimestamp(endTimestamp);

        List<RequestEntry> entries = repository.findByCreatedAtBetween(start, end);
        List<Map<String, Object>> result = entries.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getName());
                    map.put("created_at", e.getCreatedAt().toString());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * GET /deploy/check/release
     * Returns the deployment type based on HOSTNAME.
     */
    @GetMapping("/check/release")
    public ResponseEntity<Map<String, String>> checkRelease() {
        String deploymentType = resolveDeploymentType();
        Map<String, String> payload = new HashMap<>();
        payload.put("deployment_type", deploymentType);
        return ResponseEntity.ok(payload);
    }

    private String resolveDeploymentType() {
        if (hostname != null && hostname.contains("canary")) {
            return "canary";
        }
        return "normal";
    }

    private Map<String, Object> toSerializedEntry(RequestEntry entry) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", entry.getName());
        map.put("created_at", entry.getCreatedAt().getEpochSecond());
        return map;
    }

    private Instant parseTimestamp(String timestamp) {
        try {
            return Instant.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + timestamp);
        }
    }
}

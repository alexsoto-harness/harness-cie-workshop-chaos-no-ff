package com.harness.frontend.controller;

import com.harness.frontend.service.BackendApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiProxyController {

    private final BackendApiService backendApiService;

    public ApiProxyController(BackendApiService backendApiService) {
        this.backendApiService = backendApiService;
    }

    @GetMapping("/execution")
    public ResponseEntity<Map<String, Object>> execution() {
        return ResponseEntity.ok(backendApiService.getExecutionDetails());
    }

    @GetMapping("/check/release")
    public ResponseEntity<Map<String, Object>> checkRelease() {
        return ResponseEntity.ok(backendApiService.checkRelease());
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createEntry(@RequestBody(required = false) Map<String, String> body) {
        String name = body != null ? body.getOrDefault("name", "normal") : "normal";
        return ResponseEntity.ok(backendApiService.createEntry(name));
    }

    @GetMapping("/distribution")
    public ResponseEntity<Object[]> distribution(
            @RequestParam("start_timestamp") String startTimestamp,
            @RequestParam("end_timestamp") String endTimestamp) {
        return ResponseEntity.ok(backendApiService.getDistribution(startTimestamp, endTimestamp));
    }

    @GetMapping("/distribution/bar")
    public ResponseEntity<Object[]> distributionBar(
            @RequestParam("start_timestamp") String startTimestamp,
            @RequestParam("end_timestamp") String endTimestamp) {
        return ResponseEntity.ok(backendApiService.getBarDistribution(startTimestamp, endTimestamp));
    }
}

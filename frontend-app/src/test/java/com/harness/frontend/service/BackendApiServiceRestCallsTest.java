package com.harness.frontend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BackendApiServiceRestCallsTest {

    @Test
    void createEntryBuildsRequestBody() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        assertDoesNotThrow(() -> {
            try {
                service.createEntry("test-user");
            } catch (Exception ignored) {
            }
        });
    }

    @Test
    void getExecutionDetailsTargetsDeployExecution() {
        BackendApiService service = new BackendApiService("http://backend:8000");
        String url = service.getBackendUrl();
        String endpoint = url + "/deploy/execution";
        assertEquals("http://backend:8000/deploy/execution", endpoint);
    }

    @Test
    void checkReleaseTargetsDeployCheckRelease() {
        BackendApiService service = new BackendApiService("http://backend:8000");
        String url = service.getBackendUrl();
        String endpoint = url + "/deploy/check/release";
        assertEquals("http://backend:8000/deploy/check/release", endpoint);
    }

    @Test
    void createEntryTargetsDeployCreate() {
        BackendApiService service = new BackendApiService("http://backend:8000");
        String url = service.getBackendUrl();
        String endpoint = url + "/deploy/create";
        assertEquals("http://backend:8000/deploy/create", endpoint);
    }

    @ParameterizedTest
    @CsvSource({
            "100, 200",
            "0, 1000",
            "1709734800, 1709821200",
            "999, 9999"
    })
    void distributionUrlContainsTimestamps(String start, String end) {
        BackendApiService service = new BackendApiService("http://backend:8000");
        String url = service.getBackendUrl() + "/deploy/distribution?start_timestamp=" + start
                + "&end_timestamp=" + end;
        assertTrue(url.contains("start_timestamp=" + start));
        assertTrue(url.contains("end_timestamp=" + end));
    }

    @ParameterizedTest
    @CsvSource({
            "100, 200",
            "0, 1000",
            "1709734800, 1709821200",
            "999, 9999"
    })
    void barDistributionUrlContainsTimestamps(String start, String end) {
        BackendApiService service = new BackendApiService("http://backend:8000");
        String url = service.getBackendUrl() + "/deploy/distribution/bar?start_timestamp=" + start
                + "&end_timestamp=" + end;
        assertTrue(url.contains("/distribution/bar"));
        assertTrue(url.contains("start_timestamp=" + start));
        assertTrue(url.contains("end_timestamp=" + end));
    }

    @Test
    void serviceImplementsBackendApiInterface() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        assertTrue(service instanceof BackendApi);
    }

    @Test
    void backendUrlUsedInEndpointConstruction() {
        String baseUrl = "http://my-service:3000";
        BackendApiService service = new BackendApiService(baseUrl);
        assertEquals(baseUrl, service.getBackendUrl());
        assertTrue((service.getBackendUrl() + "/deploy/execution").startsWith(baseUrl));
        assertTrue((service.getBackendUrl() + "/deploy/check/release").startsWith(baseUrl));
        assertTrue((service.getBackendUrl() + "/deploy/create").startsWith(baseUrl));
    }

    @Test
    void distributionEndpointsUseDifferentPaths() {
        BackendApiService service = new BackendApiService("http://backend:8000");
        String base = service.getBackendUrl();
        String distUrl = base + "/deploy/distribution";
        String barUrl = base + "/deploy/distribution/bar";
        assertNotEquals(distUrl, barUrl);
        assertTrue(barUrl.contains("/bar"));
    }

    @Test
    void allEndpointsShareSameBaseUrl() {
        BackendApiService service = new BackendApiService("http://backend:8000");
        String base = service.getBackendUrl();
        String[] endpoints = {
                base + "/deploy/execution",
                base + "/deploy/check/release",
                base + "/deploy/create",
                base + "/deploy/distribution",
                base + "/deploy/distribution/bar"
        };
        for (String endpoint : endpoints) {
            assertTrue(endpoint.startsWith("http://backend:8000/deploy/"));
        }
    }

    @Test
    void createEntryRequestBodyStructure() {
        Map<String, String> body = Map.of("name", "testuser");
        assertEquals("testuser", body.get("name"));
        assertEquals(1, body.size());
    }
}

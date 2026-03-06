package com.harness.frontend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BackendApiServiceTest {

    // --- Constructor and getBackendUrl ---

    @Test
    void constructorSetsBackendUrl() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        assertEquals("http://localhost:8000", service.getBackendUrl());
    }

    @Test
    void backendUrlNotNull() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        assertNotNull(service.getBackendUrl());
    }

    @Test
    void backendUrlNotEmpty() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        assertFalse(service.getBackendUrl().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://localhost:8000",
            "http://backend:8000",
            "http://backend-svc:8000",
            "http://10.0.0.1:8000",
            "http://192.168.1.100:8000",
            "https://backend.example.com",
            "http://backend.default.svc.cluster.local:8000",
            "http://my-backend:9090",
            "http://api-gateway:3000",
            "http://127.0.0.1:8080"
    })
    void backendUrlAcceptsVariousUrls(String url) {
        BackendApiService service = new BackendApiService(url);
        assertEquals(url, service.getBackendUrl());
    }

    @Test
    void backendUrlPreservesTrailingSlash() {
        BackendApiService service = new BackendApiService("http://localhost:8000/");
        assertEquals("http://localhost:8000/", service.getBackendUrl());
    }

    @Test
    void backendUrlPreservesPort() {
        BackendApiService service = new BackendApiService("http://localhost:9999");
        assertTrue(service.getBackendUrl().contains("9999"));
    }

    @Test
    void backendUrlPreservesSchemeHttp() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        assertTrue(service.getBackendUrl().startsWith("http://"));
    }

    @Test
    void backendUrlPreservesSchemeHttps() {
        BackendApiService service = new BackendApiService("https://backend.example.com");
        assertTrue(service.getBackendUrl().startsWith("https://"));
    }

    @Test
    void backendUrlPreservesHostname() {
        BackendApiService service = new BackendApiService("http://my-custom-backend:8000");
        assertTrue(service.getBackendUrl().contains("my-custom-backend"));
    }

    @Test
    void twoInstancesWithSameUrlAreEqual() {
        BackendApiService a = new BackendApiService("http://localhost:8000");
        BackendApiService b = new BackendApiService("http://localhost:8000");
        assertEquals(a.getBackendUrl(), b.getBackendUrl());
    }

    @Test
    void twoInstancesWithDifferentUrlsAreDifferent() {
        BackendApiService a = new BackendApiService("http://localhost:8000");
        BackendApiService b = new BackendApiService("http://localhost:9000");
        assertNotEquals(a.getBackendUrl(), b.getBackendUrl());
    }

    // --- URL construction verification ---

    @Test
    void backendUrlDoesNotContainSpaces() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        assertFalse(service.getBackendUrl().contains(" "));
    }

    @Test
    void backendUrlLengthIsPositive() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        assertTrue(service.getBackendUrl().length() > 0);
    }

    @ParameterizedTest
    @ValueSource(ints = {80, 443, 3000, 4000, 5000, 8000, 8080, 8443, 9000, 9090})
    void backendUrlWithVariousPorts(int port) {
        String url = "http://localhost:" + port;
        BackendApiService service = new BackendApiService(url);
        assertEquals(url, service.getBackendUrl());
    }

    @ParameterizedTest
    @ValueSource(strings = {"backend", "api", "service", "gateway", "proxy", "app", "server", "host", "node", "instance"})
    void backendUrlWithVariousHostnames(String hostname) {
        String url = "http://" + hostname + ":8000";
        BackendApiService service = new BackendApiService(url);
        assertEquals(url, service.getBackendUrl());
    }

    // --- Immutability checks ---

    @Test
    void getBackendUrlReturnsConsistentValue() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        String first = service.getBackendUrl();
        String second = service.getBackendUrl();
        assertEquals(first, second);
    }

    @Test
    void getBackendUrlCalledMultipleTimesReturnsSame() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        for (int i = 0; i < 100; i++) {
            assertEquals("http://localhost:8000", service.getBackendUrl());
        }
    }

    // --- Edge cases ---

    @Test
    void backendUrlWithPath() {
        BackendApiService service = new BackendApiService("http://localhost:8000/api/v1");
        assertEquals("http://localhost:8000/api/v1", service.getBackendUrl());
    }

    @Test
    void backendUrlWithIpAddress() {
        BackendApiService service = new BackendApiService("http://10.244.0.5:8000");
        assertEquals("http://10.244.0.5:8000", service.getBackendUrl());
    }

    @Test
    void backendUrlWithKubernetesServiceDns() {
        BackendApiService service = new BackendApiService("http://backend.workshop.svc.cluster.local:8000");
        assertEquals("http://backend.workshop.svc.cluster.local:8000", service.getBackendUrl());
    }

    @Test
    void backendUrlWithSubdomain() {
        BackendApiService service = new BackendApiService("http://api.backend.internal:8000");
        assertEquals("http://api.backend.internal:8000", service.getBackendUrl());
    }

    @Test
    void backendUrlWithQueryParamStyle() {
        BackendApiService service = new BackendApiService("http://localhost:8000");
        String url = service.getBackendUrl();
        assertFalse(url.contains("?"));
    }

    @Test
    void serviceCanBeInstantiated() {
        assertDoesNotThrow(() -> new BackendApiService("http://localhost:8000"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://a:1", "http://b:2", "http://c:3", "http://d:4", "http://e:5",
            "http://f:6", "http://g:7", "http://h:8", "http://i:9", "http://j:10"
    })
    void serviceCanBeInstantiatedWithMinimalUrls(String url) {
        assertDoesNotThrow(() -> new BackendApiService(url));
    }
}

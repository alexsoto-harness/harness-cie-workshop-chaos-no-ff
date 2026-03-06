package com.harness.frontend.controller;

import com.harness.frontend.service.BackendApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiProxyControllerTest {

    private BackendApi backendApiService;
    private ApiProxyController controller;

    @BeforeEach
    void setUp() {
        backendApiService = mock(BackendApi.class);
        controller = new ApiProxyController(backendApiService);
    }

    // --- /api/execution ---

    @Test
    void executionReturns200() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("service_name", "svc"));
        ResponseEntity<Map<String, Object>> response = controller.execution();
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void executionReturnsServiceName() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("service_name", "TestService"));
        ResponseEntity<Map<String, Object>> response = controller.execution();
        assertEquals("TestService", response.getBody().get("service_name"));
    }

    @Test
    void executionReturnsDeploymentType() {
        Map<String, Object> details = new HashMap<>();
        details.put("deployment_type", "canary");
        when(backendApiService.getExecutionDetails()).thenReturn(details);
        ResponseEntity<Map<String, Object>> response = controller.execution();
        assertEquals("canary", response.getBody().get("deployment_type"));
    }

    @Test
    void executionReturnsVersion() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("application_version", "v2.0"));
        ResponseEntity<Map<String, Object>> response = controller.execution();
        assertEquals("v2.0", response.getBody().get("application_version"));
    }

    @Test
    void executionReturnsLastExecutionId() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("last_execution_id", "exec-99"));
        ResponseEntity<Map<String, Object>> response = controller.execution();
        assertEquals("exec-99", response.getBody().get("last_execution_id"));
    }

    @Test
    void executionCallsBackendService() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of());
        controller.execution();
        verify(backendApiService, times(1)).getExecutionDetails();
    }

    @Test
    void executionBodyNotNull() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("service_name", "svc"));
        assertNotNull(controller.execution().getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ServiceA", "ServiceB", "ServiceC", "ServiceD", "ServiceE", "ServiceF", "ServiceG", "ServiceH", "ServiceI", "ServiceJ"})
    void executionReturnsVariousServiceNames(String name) {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("service_name", name));
        assertEquals(name, controller.execution().getBody().get("service_name"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"v1.0", "v1.1", "v2.0", "v2.1", "v3.0", "v3.1", "v4.0", "v4.1", "v5.0", "v5.1"})
    void executionReturnsVariousVersions(String version) {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("application_version", version));
        assertEquals(version, controller.execution().getBody().get("application_version"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "10", "100", "999", "abc-123", "exec-42", "run-007", "build-99", "id-0"})
    void executionReturnsVariousExecutionIds(String execId) {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("last_execution_id", execId));
        assertEquals(execId, controller.execution().getBody().get("last_execution_id"));
    }

    // --- /api/check/release ---

    @Test
    void checkReleaseReturns200() {
        when(backendApiService.checkRelease()).thenReturn(Map.of("deployment_type", "normal"));
        assertEquals(200, controller.checkRelease().getStatusCode().value());
    }

    @Test
    void checkReleaseReturnsNormal() {
        when(backendApiService.checkRelease()).thenReturn(Map.of("deployment_type", "normal"));
        assertEquals("normal", controller.checkRelease().getBody().get("deployment_type"));
    }

    @Test
    void checkReleaseReturnsCanary() {
        when(backendApiService.checkRelease()).thenReturn(Map.of("deployment_type", "canary"));
        assertEquals("canary", controller.checkRelease().getBody().get("deployment_type"));
    }

    @Test
    void checkReleaseCallsBackendService() {
        when(backendApiService.checkRelease()).thenReturn(Map.of("deployment_type", "normal"));
        controller.checkRelease();
        verify(backendApiService, times(1)).checkRelease();
    }

    @Test
    void checkReleaseDoesNotCallGetExecutionDetails() {
        when(backendApiService.checkRelease()).thenReturn(Map.of("deployment_type", "normal"));
        controller.checkRelease();
        verify(backendApiService, never()).getExecutionDetails();
    }

    @Test
    void checkReleaseBodyNotNull() {
        when(backendApiService.checkRelease()).thenReturn(Map.of("deployment_type", "normal"));
        assertNotNull(controller.checkRelease().getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"normal", "canary", "blue-green", "rolling", "recreate", "shadow", "a-b", "dark", "mirror", "feature"})
    void checkReleaseReturnsVariousDeploymentTypes(String type) {
        when(backendApiService.checkRelease()).thenReturn(Map.of("deployment_type", type));
        assertEquals(type, controller.checkRelease().getBody().get("deployment_type"));
    }

    // --- /api/create ---

    @Test
    void createEntryReturns200() {
        when(backendApiService.createEntry("normal")).thenReturn(Map.of("status", "ok"));
        Map<String, String> body = Map.of("name", "normal");
        assertEquals(200, controller.createEntry(body).getStatusCode().value());
    }

    @Test
    void createEntryWithCanaryName() {
        when(backendApiService.createEntry("canary")).thenReturn(Map.of("status", "ok"));
        Map<String, String> body = Map.of("name", "canary");
        assertEquals(200, controller.createEntry(body).getStatusCode().value());
    }

    @Test
    void createEntryDefaultsToNormalWhenNoBody() {
        when(backendApiService.createEntry("normal")).thenReturn(Map.of("status", "ok"));
        assertEquals(200, controller.createEntry(null).getStatusCode().value());
    }

    @Test
    void createEntryCallsBackendService() {
        when(backendApiService.createEntry("test")).thenReturn(Map.of("status", "ok"));
        controller.createEntry(Map.of("name", "test"));
        verify(backendApiService, times(1)).createEntry("test");
    }

    @Test
    void createEntryReturnsBody() {
        when(backendApiService.createEntry("normal")).thenReturn(Map.of("status", "created"));
        Map<String, String> body = Map.of("name", "normal");
        assertEquals("created", controller.createEntry(body).getBody().get("status"));
    }

    @Test
    void createEntryDefaultsNameWhenKeyMissing() {
        when(backendApiService.createEntry("normal")).thenReturn(Map.of("status", "ok"));
        Map<String, String> body = Map.of("other", "value");
        controller.createEntry(body);
        verify(backendApiService).createEntry("normal");
    }

    @ParameterizedTest
    @ValueSource(strings = {"alpha", "beta", "gamma", "delta", "epsilon", "zeta", "eta", "theta", "iota", "kappa"})
    void createEntryWithVariousNames(String name) {
        when(backendApiService.createEntry(name)).thenReturn(Map.of("status", "ok"));
        controller.createEntry(Map.of("name", name));
        verify(backendApiService).createEntry(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"deploy-1", "deploy-2", "deploy-3", "deploy-4", "deploy-5", "deploy-6", "deploy-7", "deploy-8", "deploy-9", "deploy-10"})
    void createEntryReturnsOkForVariousNames(String name) {
        when(backendApiService.createEntry(name)).thenReturn(Map.of("status", "ok"));
        assertEquals(200, controller.createEntry(Map.of("name", name)).getStatusCode().value());
    }

    // --- /api/distribution ---

    @Test
    void distributionReturns200() {
        when(backendApiService.getDistribution("100", "200")).thenReturn(new Object[]{});
        assertEquals(200, controller.distribution("100", "200").getStatusCode().value());
    }

    @Test
    void distributionReturnsEmptyArray() {
        when(backendApiService.getDistribution("100", "200")).thenReturn(new Object[]{});
        assertEquals(0, controller.distribution("100", "200").getBody().length);
    }

    @Test
    void distributionCallsBackendService() {
        when(backendApiService.getDistribution("100", "200")).thenReturn(new Object[]{});
        controller.distribution("100", "200");
        verify(backendApiService, times(1)).getDistribution("100", "200");
    }

    @Test
    void distributionBodyNotNull() {
        when(backendApiService.getDistribution("100", "200")).thenReturn(new Object[]{});
        assertNotNull(controller.distribution("100", "200").getBody());
    }

    @Test
    void distributionReturnsNonEmptyArray() {
        Object[] data = new Object[]{"item1", "item2"};
        when(backendApiService.getDistribution("100", "200")).thenReturn(data);
        assertEquals(2, controller.distribution("100", "200").getBody().length);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1000", "2000", "3000", "4000", "5000", "6000", "7000", "8000", "9000", "10000"})
    void distributionWithVariousStartTimestamps(String start) {
        when(backendApiService.getDistribution(eq(start), anyString())).thenReturn(new Object[]{});
        assertEquals(200, controller.distribution(start, "99999").getStatusCode().value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1000", "2000", "3000", "4000", "5000", "6000", "7000", "8000", "9000", "10000"})
    void distributionWithVariousEndTimestamps(String end) {
        when(backendApiService.getDistribution(anyString(), eq(end))).thenReturn(new Object[]{});
        assertEquals(200, controller.distribution("1", end).getStatusCode().value());
    }

    // --- /api/distribution/bar ---

    @Test
    void distributionBarReturns200() {
        when(backendApiService.getBarDistribution("100", "200")).thenReturn(new Object[]{});
        assertEquals(200, controller.distributionBar("100", "200").getStatusCode().value());
    }

    @Test
    void distributionBarReturnsEmptyArray() {
        when(backendApiService.getBarDistribution("100", "200")).thenReturn(new Object[]{});
        assertEquals(0, controller.distributionBar("100", "200").getBody().length);
    }

    @Test
    void distributionBarCallsBackendService() {
        when(backendApiService.getBarDistribution("100", "200")).thenReturn(new Object[]{});
        controller.distributionBar("100", "200");
        verify(backendApiService, times(1)).getBarDistribution("100", "200");
    }

    @Test
    void distributionBarBodyNotNull() {
        when(backendApiService.getBarDistribution("100", "200")).thenReturn(new Object[]{});
        assertNotNull(controller.distributionBar("100", "200").getBody());
    }

    @Test
    void distributionBarReturnsNonEmptyArray() {
        Object[] data = new Object[]{"bar1", "bar2", "bar3"};
        when(backendApiService.getBarDistribution("100", "200")).thenReturn(data);
        assertEquals(3, controller.distributionBar("100", "200").getBody().length);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1000", "2000", "3000", "4000", "5000", "6000", "7000", "8000", "9000", "10000"})
    void distributionBarWithVariousStartTimestamps(String start) {
        when(backendApiService.getBarDistribution(eq(start), anyString())).thenReturn(new Object[]{});
        assertEquals(200, controller.distributionBar(start, "99999").getStatusCode().value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1000", "2000", "3000", "4000", "5000", "6000", "7000", "8000", "9000", "10000"})
    void distributionBarWithVariousEndTimestamps(String end) {
        when(backendApiService.getBarDistribution(anyString(), eq(end))).thenReturn(new Object[]{});
        assertEquals(200, controller.distributionBar("1", end).getStatusCode().value());
    }

    // --- Cross-endpoint isolation ---

    @Test
    void executionDoesNotCallCheckRelease() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of());
        controller.execution();
        verify(backendApiService, never()).checkRelease();
    }

    @Test
    void executionDoesNotCallCreateEntry() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of());
        controller.execution();
        verify(backendApiService, never()).createEntry(anyString());
    }

    @Test
    void checkReleaseDoesNotCallCreateEntry() {
        when(backendApiService.checkRelease()).thenReturn(Map.of());
        controller.checkRelease();
        verify(backendApiService, never()).createEntry(anyString());
    }

    @Test
    void distributionDoesNotCallBarDistribution() {
        when(backendApiService.getDistribution("1", "2")).thenReturn(new Object[]{});
        controller.distribution("1", "2");
        verify(backendApiService, never()).getBarDistribution(anyString(), anyString());
    }

    @Test
    void barDistributionDoesNotCallDistribution() {
        when(backendApiService.getBarDistribution("1", "2")).thenReturn(new Object[]{});
        controller.distributionBar("1", "2");
        verify(backendApiService, never()).getDistribution(anyString(), anyString());
    }

    // --- Multiple calls ---

    @Test
    void executionCanBeCalledMultipleTimes() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("service_name", "svc"));
        for (int i = 0; i < 10; i++) {
            controller.execution();
        }
        verify(backendApiService, times(10)).getExecutionDetails();
    }

    @Test
    void checkReleaseCanBeCalledMultipleTimes() {
        when(backendApiService.checkRelease()).thenReturn(Map.of("deployment_type", "normal"));
        for (int i = 0; i < 10; i++) {
            controller.checkRelease();
        }
        verify(backendApiService, times(10)).checkRelease();
    }

    @Test
    void createEntryCanBeCalledMultipleTimes() {
        when(backendApiService.createEntry("normal")).thenReturn(Map.of("status", "ok"));
        for (int i = 0; i < 10; i++) {
            controller.createEntry(Map.of("name", "normal"));
        }
        verify(backendApiService, times(10)).createEntry("normal");
    }

    @Test
    void distributionCanBeCalledMultipleTimes() {
        when(backendApiService.getDistribution("1", "2")).thenReturn(new Object[]{});
        for (int i = 0; i < 10; i++) {
            controller.distribution("1", "2");
        }
        verify(backendApiService, times(10)).getDistribution("1", "2");
    }

    @Test
    void distributionBarCanBeCalledMultipleTimes() {
        when(backendApiService.getBarDistribution("1", "2")).thenReturn(new Object[]{});
        for (int i = 0; i < 10; i++) {
            controller.distributionBar("1", "2");
        }
        verify(backendApiService, times(10)).getBarDistribution("1", "2");
    }
}

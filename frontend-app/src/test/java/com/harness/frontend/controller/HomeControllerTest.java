package com.harness.frontend.controller;

import com.harness.frontend.service.BackendApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    private BackendApi backendApiService;
    private HomeController controller;
    private Model model;

    @BeforeEach
    void setUp() {
        backendApiService = mock(BackendApi.class);
        controller = new HomeController(backendApiService);
        // Inject the ffSdkKey field via reflection since it's @Value injected
        try {
            java.lang.reflect.Field field = HomeController.class.getDeclaredField("ffSdkKey");
            field.setAccessible(true);
            field.set(controller, "test-sdk-key");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        model = new ExtendedModelMap();
    }

    private Map<String, Object> buildDetails(String serviceName, String lastExec, String appVersion, String deployType) {
        Map<String, Object> details = new HashMap<>();
        details.put("service_name", serviceName);
        details.put("last_execution_id", lastExec);
        details.put("application_version", appVersion);
        details.put("deployment_type", deployType);
        return details;
    }

    // --- Home page basic tests ---

    @Test
    void homeReturnsHomeView() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        assertEquals("home", controller.home(model));
    }

    @Test
    void homePathReturnsHomeView() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        String view = controller.home(model);
        assertEquals("home", view);
    }

    // --- Model attribute tests for normal deployment ---

    @Test
    void homeModelContainsServiceName() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("MyService", "1", "v1", "normal"));
        controller.home(model);
        assertEquals("MyService", model.getAttribute("serviceName"));
    }

    @Test
    void homeModelContainsLastExecution() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "42", "v1", "normal"));
        controller.home(model);
        assertEquals("42", model.getAttribute("lastExecution"));
    }

    @Test
    void homeModelContainsApplicationVersion() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v2.5", "normal"));
        controller.home(model);
        assertEquals("v2.5", model.getAttribute("applicationVersion"));
    }

    @Test
    void homeModelContainsDeploymentType() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        controller.home(model);
        assertEquals("normal", model.getAttribute("deploymentType"));
    }

    @Test
    void homeModelIsCanaryFalseForNormal() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        controller.home(model);
        assertEquals(false, model.getAttribute("isCanary"));
    }

    // --- Canary deployment tests ---

    @Test
    void homeModelIsCanaryTrueForCanary() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "canary"));
        controller.home(model);
        assertEquals(true, model.getAttribute("isCanary"));
    }

    @Test
    void homeModelDeploymentTypeCanary() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "canary"));
        controller.home(model);
        assertEquals("canary", model.getAttribute("deploymentType"));
    }

    // --- Feature flag SDK key ---

    @Test
    void homeModelContainsFfSdkKey() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        controller.home(model);
        assertEquals("test-sdk-key", model.getAttribute("ffSdkKey"));
    }

    // --- Error/fallback tests ---

    @Test
    void homeReturnsViewWhenBackendFails() {
        when(backendApiService.getExecutionDetails()).thenThrow(new RuntimeException("Backend down"));
        assertEquals("home", controller.home(model));
    }

    @Test
    void homeReturnsFallbackServiceNameOnError() {
        when(backendApiService.getExecutionDetails()).thenThrow(new RuntimeException("Backend down"));
        controller.home(model);
        assertEquals("Harness Workshop", model.getAttribute("serviceName"));
    }

    @Test
    void homeReturnsFallbackLastExecutionOnError() {
        when(backendApiService.getExecutionDetails()).thenThrow(new RuntimeException("Backend down"));
        controller.home(model);
        assertEquals("12.3", model.getAttribute("lastExecution"));
    }

    @Test
    void homeReturnsFallbackApplicationVersionOnError() {
        when(backendApiService.getExecutionDetails()).thenThrow(new RuntimeException("Backend down"));
        controller.home(model);
        assertEquals("v1.0", model.getAttribute("applicationVersion"));
    }

    @Test
    void homeReturnsFallbackDeploymentTypeOnError() {
        when(backendApiService.getExecutionDetails()).thenThrow(new RuntimeException("Backend down"));
        controller.home(model);
        assertEquals("normal", model.getAttribute("deploymentType"));
    }

    @Test
    void homeReturnsFallbackIsCanaryOnError() {
        when(backendApiService.getExecutionDetails()).thenThrow(new RuntimeException("Backend down"));
        controller.home(model);
        assertEquals(false, model.getAttribute("isCanary"));
    }

    // --- Missing fields fallback ---

    @Test
    void homeDefaultsServiceNameWhenMissing() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("deployment_type", "normal"));
        controller.home(model);
        assertEquals("Harness Workshop", model.getAttribute("serviceName"));
    }

    @Test
    void homeDefaultsLastExecutionWhenMissing() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("deployment_type", "normal"));
        controller.home(model);
        assertEquals("12.3", model.getAttribute("lastExecution"));
    }

    @Test
    void homeDefaultsApplicationVersionWhenMissing() {
        when(backendApiService.getExecutionDetails()).thenReturn(Map.of("deployment_type", "normal"));
        controller.home(model);
        assertEquals("v1.0", model.getAttribute("applicationVersion"));
    }

    // --- Distribution page ---

    @Test
    void distributionReturnsDistributionView() {
        when(backendApiService.getBackendUrl()).thenReturn("http://localhost:8000");
        assertEquals("distribution", controller.distribution(model));
    }

    @Test
    void distributionModelContainsBackendUrl() {
        when(backendApiService.getBackendUrl()).thenReturn("http://localhost:8000");
        controller.distribution(model);
        assertEquals("http://localhost:8000", model.getAttribute("backendUrl"));
    }

    @Test
    void distributionModelBackendUrlCustom() {
        when(backendApiService.getBackendUrl()).thenReturn("http://backend-svc:8000");
        controller.distribution(model);
        assertEquals("http://backend-svc:8000", model.getAttribute("backendUrl"));
    }

    // --- Parameterized service name tests ---

    @ParameterizedTest
    @ValueSource(strings = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota", "Kappa"})
    void homeDisplaysVariousServiceNames(String name) {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails(name, "1", "v1", "normal"));
        controller.home(model);
        assertEquals(name, model.getAttribute("serviceName"));
    }

    // --- Parameterized version tests ---

    @ParameterizedTest
    @ValueSource(strings = {"v1.0", "v1.1", "v2.0", "v2.1", "v3.0", "v3.1", "v4.0", "v4.1", "v5.0", "v5.1"})
    void homeDisplaysVariousVersions(String version) {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", version, "normal"));
        controller.home(model);
        assertEquals(version, model.getAttribute("applicationVersion"));
    }

    // --- Parameterized execution ID tests ---

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "10", "100", "999", "abc-123", "exec-42", "run-007", "build-99", "id-0"})
    void homeDisplaysVariousExecutionIds(String execId) {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", execId, "v1", "normal"));
        controller.home(model);
        assertEquals(execId, model.getAttribute("lastExecution"));
    }

    // --- Verify backend service interactions ---

    @Test
    void homeCallsGetExecutionDetails() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        controller.home(model);
        verify(backendApiService, times(1)).getExecutionDetails();
    }

    @Test
    void distributionCallsGetBackendUrl() {
        when(backendApiService.getBackendUrl()).thenReturn("http://localhost:8000");
        controller.distribution(model);
        verify(backendApiService, times(1)).getBackendUrl();
    }

    @Test
    void homeDoesNotCallGetBackendUrl() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        controller.home(model);
        verify(backendApiService, never()).getBackendUrl();
    }

    @Test
    void distributionDoesNotCallGetExecutionDetails() {
        when(backendApiService.getBackendUrl()).thenReturn("http://localhost:8000");
        controller.distribution(model);
        verify(backendApiService, never()).getExecutionDetails();
    }

    // --- Empty string edge cases ---

    @Test
    void homeHandlesEmptyServiceName() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("", "1", "v1", "normal"));
        controller.home(model);
        assertEquals("", model.getAttribute("serviceName"));
    }

    @Test
    void homeHandlesEmptyVersion() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "", "normal"));
        controller.home(model);
        assertEquals("", model.getAttribute("applicationVersion"));
    }

    @Test
    void homeHandlesEmptyExecutionId() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "", "v1", "normal"));
        controller.home(model);
        assertEquals("", model.getAttribute("lastExecution"));
    }

    // --- Different error types ---

    @Test
    void homeHandlesNullPointerException() {
        when(backendApiService.getExecutionDetails()).thenThrow(new NullPointerException());
        assertDoesNotThrow(() -> controller.home(model));
    }

    @Test
    void homeHandlesIllegalStateException() {
        when(backendApiService.getExecutionDetails()).thenThrow(new IllegalStateException("bad state"));
        assertDoesNotThrow(() -> controller.home(model));
    }

    // --- Multiple sequential calls ---

    @Test
    void homeCanBeCalledMultipleTimes() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        for (int i = 0; i < 5; i++) {
            controller.home(new ExtendedModelMap());
        }
        verify(backendApiService, times(5)).getExecutionDetails();
    }

    @Test
    void distributionCanBeCalledMultipleTimes() {
        when(backendApiService.getBackendUrl()).thenReturn("http://localhost:8000");
        for (int i = 0; i < 5; i++) {
            controller.distribution(new ExtendedModelMap());
        }
        verify(backendApiService, times(5)).getBackendUrl();
    }

    // --- Additional edge cases for more coverage ---

    @ParameterizedTest
    @ValueSource(strings = {"normal", "canary", "blue-green", "rolling", "recreate"})
    void homeHandlesVariousDeploymentTypes(String type) {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", type));
        controller.home(model);
        assertEquals(type, model.getAttribute("deploymentType"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"normal", "canary", "blue-green", "rolling", "recreate"})
    void homeIsCanaryOnlyForCanaryType(String type) {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", type));
        controller.home(model);
        assertEquals("canary".equals(type), model.getAttribute("isCanary"));
    }

    @Test
    void homeModelHasAllExpectedAttributes() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        controller.home(model);
        assertNotNull(model.getAttribute("serviceName"));
        assertNotNull(model.getAttribute("lastExecution"));
        assertNotNull(model.getAttribute("applicationVersion"));
        assertNotNull(model.getAttribute("deploymentType"));
        assertNotNull(model.getAttribute("isCanary"));
        assertNotNull(model.getAttribute("ffSdkKey"));
    }

    @Test
    void homeModelHasSixAttributes() {
        when(backendApiService.getExecutionDetails()).thenReturn(buildDetails("svc", "1", "v1", "normal"));
        controller.home(model);
        assertEquals(6, model.asMap().size());
    }

    @Test
    void distributionModelHasOneAttribute() {
        when(backendApiService.getBackendUrl()).thenReturn("http://localhost:8000");
        controller.distribution(model);
        assertEquals(1, model.asMap().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://a:1", "http://b:2", "http://c:3", "http://d:4", "http://e:5", "http://f:6", "http://g:7", "http://h:8", "http://i:9", "http://j:10"})
    void distributionAcceptsVariousBackendUrls(String url) {
        when(backendApiService.getBackendUrl()).thenReturn(url);
        controller.distribution(model);
        assertEquals(url, model.getAttribute("backendUrl"));
    }
}

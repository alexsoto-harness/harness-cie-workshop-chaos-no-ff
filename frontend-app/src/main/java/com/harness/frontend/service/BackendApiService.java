package com.harness.frontend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BackendApiService implements BackendApi {

    private final RestTemplate restTemplate;
    private final String backendUrl;

    public BackendApiService(@Value("${backend.api.url}") String backendUrl) {
        this.backendUrl = backendUrl;
        this.restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getExecutionDetails() {
        return restTemplate.getForObject(backendUrl + "/deploy/execution", Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> checkRelease() {
        return restTemplate.getForObject(backendUrl + "/deploy/check/release", Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createEntry(String name) {
        Map<String, String> body = Map.of("name", name);
        return restTemplate.postForObject(backendUrl + "/deploy/create", body, Map.class);
    }

    public Object[] getDistribution(String startTimestamp, String endTimestamp) {
        String url = backendUrl + "/deploy/distribution?start_timestamp=" + startTimestamp
                + "&end_timestamp=" + endTimestamp;
        return restTemplate.getForObject(url, Object[].class);
    }

    public Object[] getBarDistribution(String startTimestamp, String endTimestamp) {
        String url = backendUrl + "/deploy/distribution/bar?start_timestamp=" + startTimestamp
                + "&end_timestamp=" + endTimestamp;
        return restTemplate.getForObject(url, Object[].class);
    }

    public String getBackendUrl() {
        return backendUrl;
    }
}

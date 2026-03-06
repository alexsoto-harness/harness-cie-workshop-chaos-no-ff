package com.harness.frontend.service;

import java.util.Map;

public interface BackendApi {
    Map<String, Object> getExecutionDetails();
    Map<String, Object> checkRelease();
    Map<String, Object> createEntry(String name);
    Object[] getDistribution(String startTimestamp, String endTimestamp);
    Object[] getBarDistribution(String startTimestamp, String endTimestamp);
    String getBackendUrl();
}

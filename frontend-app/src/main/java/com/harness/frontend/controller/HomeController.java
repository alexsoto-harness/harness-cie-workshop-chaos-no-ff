package com.harness.frontend.controller;

import com.harness.frontend.service.BackendApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    private final BackendApi backendApiService;

    @Value("${harness.ff.sdk.key}")
    private String ffSdkKey;

    public HomeController(BackendApi backendApiService) {
        this.backendApiService = backendApiService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        try {
            Map<String, Object> details = backendApiService.getExecutionDetails();
            model.addAttribute("serviceName", details.getOrDefault("service_name", "Harness Workshop"));
            model.addAttribute("lastExecution", details.getOrDefault("last_execution_id", "12.3"));
            model.addAttribute("applicationVersion", details.getOrDefault("application_version", "v1.0"));
            model.addAttribute("deploymentType", details.getOrDefault("deployment_type", "normal"));
            model.addAttribute("isCanary", "canary".equals(details.get("deployment_type")));
        } catch (Exception e) {
            model.addAttribute("serviceName", "Harness Workshop");
            model.addAttribute("lastExecution", "12.3");
            model.addAttribute("applicationVersion", "v1.0");
            model.addAttribute("deploymentType", "normal");
            model.addAttribute("isCanary", false);
        }
        model.addAttribute("ffSdkKey", ffSdkKey);
        return "home";
    }

    @GetMapping("/distribution")
    public String distribution(Model model) {
        model.addAttribute("backendUrl", backendApiService.getBackendUrl());
        return "distribution";
    }
}

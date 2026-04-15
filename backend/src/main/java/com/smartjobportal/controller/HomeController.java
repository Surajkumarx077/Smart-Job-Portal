package com.smartjobportal.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Smart Job Portal backend is running");
        response.put("auth", "/api/auth/login");
        response.put("jobs", "/api/jobs/public");
        return response;
    }
}

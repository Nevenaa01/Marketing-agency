package com.bsep2024.MarketingAgency.payload.request;

import jakarta.validation.constraints.NotBlank;

public class RateLimitingRequest {
    private Long id;
    private String role;

    public RateLimitingRequest() {
    }

    public RateLimitingRequest(Long id, String role) {
        this.id = id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

package com.bsep2024.MarketingAgency.payload.request;

public class DenialRequest {
    private Long userId;
    private String email;
    private String report;

    public DenialRequest() {
    }

    public DenialRequest(Long userId, String email, String report) {
        this.userId = userId;
        this.email = email;
        this.report = report;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}

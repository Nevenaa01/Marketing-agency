package com.bsep2024.MarketingAgency.models;

import jakarta.persistence.*;

@Entity
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clientId;
    private Long employeeId;
    private String slogan;
    private String description;
    private Long duration;
    private Long posted;
    private Long requestId;

    public Ad() {
    }

    public Ad(Long id, Long clientId, Long employeeId, String slogan, String description, Long duration, Long posted, Long requestId) {
        this.id = id;
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.slogan = slogan;
        this.description = description;
        this.duration = duration;
        this.posted = posted;
        this.requestId = requestId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getPosted() {
        return posted;
    }

    public void setPosted(Long posted) {
        this.posted = posted;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}

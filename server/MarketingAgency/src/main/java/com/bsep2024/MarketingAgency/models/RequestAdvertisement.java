package com.bsep2024.MarketingAgency.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class RequestAdvertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    @DateTimeFormat
    private LocalDateTime deadlineDate;

    @NotNull
    @DateTimeFormat
    private LocalDateTime activeFrom;

    @NotNull
    @DateTimeFormat
    private LocalDateTime activeTo;

    @NotBlank
    @Size(max = 500)
    private String description;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;*/

    public RequestAdvertisement() {
    }

    public RequestAdvertisement(Long id, Long userId, LocalDateTime deadlineDate, LocalDateTime activeFrom, LocalDateTime activeTo, String description) {
        this.id = id;
        this.userId = userId;
        this.deadlineDate = deadlineDate;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public LocalDateTime getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(LocalDateTime activeFrom) {
        this.activeFrom = activeFrom;
    }

    public LocalDateTime getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(LocalDateTime activeTo) {
        this.activeTo = activeTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean Validate(){
        LocalDateTime deadline = getDeadlineDate();
        LocalDateTime activeFrom = getActiveFrom();
        LocalDateTime activeTo = getActiveTo();

        if (deadline != null && activeFrom != null && activeTo != null) {
            if (deadline.isBefore(activeFrom) && activeFrom.isBefore(activeTo)) {
                return true;
            }
        }
        return false;

    }
}

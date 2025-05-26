package com.bsep2024.MarketingAgency.payload.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;




public class RefreshTokenResponse {
    private Long userId;
    private String token;
    private Long expirationDate; //milliseconds

    public RefreshTokenResponse(Long userId, String token, Long expirationDate) {
        this.userId = userId;
        this.token = token;
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expireDate) {
        this.expirationDate = expireDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

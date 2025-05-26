package com.bsep2024.MarketingAgency.payload.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


public class ResponseWrapper {
    private String accessToken;
    private String refreshToken;

    public ResponseWrapper(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

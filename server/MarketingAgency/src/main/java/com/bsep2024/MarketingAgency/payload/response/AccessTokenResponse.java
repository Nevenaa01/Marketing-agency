package com.bsep2024.MarketingAgency.payload.response;

public class AccessTokenResponse {
    private Long id; //id user-a
    private String accessToken;
    private Long expirationDate;

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public AccessTokenResponse(Long id, String accessToken, Long expirationDate) {
        this.id = id;
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getId() {
        return id;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }
}

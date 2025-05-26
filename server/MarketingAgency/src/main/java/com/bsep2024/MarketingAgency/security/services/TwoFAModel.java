package com.bsep2024.MarketingAgency.security.services;

public class TwoFAModel {
    private String secret;
    private String code;
    private String userEmail;

    public TwoFAModel() {
    }

    public TwoFAModel(String secret, String code, String userEmail) {
        this.secret = secret;
        this.code = code;
        this.userEmail = userEmail;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

package com.bsep2024.MarketingAgency.security.services;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TOTPService {
    private final GoogleAuthenticator googleAuth;

    public TOTPService() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(30000) //30s
                .build();
        this.googleAuth = new GoogleAuthenticator(config);
    }

    public String generateSecretKey() {
        final GoogleAuthenticatorKey key = googleAuth.createCredentials();
        return key.getKey();
    }

    public boolean verifyCode(String secret, int code) {
        return googleAuth.authorize(secret, code);
    }

    public String getQrCodeUrl(String secret, String account) {
        String encodedAccount = URLEncoder.encode(account, StandardCharsets.UTF_8);
        String encodedSecret = URLEncoder.encode(secret, StandardCharsets.UTF_8);
        return String.format("otpauth://totp/%s?secret=%s&issuer=MarketingAgency", encodedAccount, encodedSecret);
    }
}

package com.bsep2024.MarketingAgency.services;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class ActivationLinkService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String SECRET_KEY = "ratherthicc#1919";

    public String generateActivationLink(Long userId) {
        String token = UUID.randomUUID().toString();
        Date expirationDate = calculateExpirationDate();
        String data = userId + "|" + token + "|" + expirationDate.getTime();
        String hmac = generateHmac(data);
        return "https://localhost:8443/api/user/activate?userId=" + userId + "&token=" + token + "&expirationDate=" + expirationDate.getTime() + "&hmac=" + hmac;
    }

    public boolean verifyActivationLink(Long userId, String token, long expirationDate, String hmac) {
        String data = userId + "|" + token + "|" + expirationDate;
        String expectedHmac = generateHmac(data);
        return hmac.equals(expectedHmac) && new Date().getTime() < expirationDate;
    }

    private String generateHmac(String data) {
        try {
            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            byte[] secretKeyBytes = SECRET_KEY.getBytes();
            Key key = new SecretKeySpec(secretKeyBytes, HMAC_ALGORITHM);
            hmac.init(key);
            byte[] hashBytes = hmac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date calculateExpirationDate() {
        // Calculate expiration date, e.g., 24 hours from now
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + (24 * 60 * 60 * 1000); // 24 hours
        return new Date(expirationTime);
    }
}

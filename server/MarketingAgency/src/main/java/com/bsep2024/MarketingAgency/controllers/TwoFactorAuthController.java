package com.bsep2024.MarketingAgency.controllers;

import com.bsep2024.MarketingAgency.security.services.TOTPService;
import com.bsep2024.MarketingAgency.security.services.TwoFAModel;
import com.bsep2024.MarketingAgency.services.UserService;
import com.bsep2024.MarketingAgency.utils.AESUtil;
import com.bsep2024.MarketingAgency.utils.KeyStoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/twoFactorAuth")
public class TwoFactorAuthController {
    @Autowired
    private TOTPService totpService;
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthController.class);

    @GetMapping("/2fa")
    public ResponseEntity<Map<String, String>> get2FAData() {
        logger.info("Getting QR code for 2fa..");
        try {
            String secret = totpService.generateSecretKey();
            String qrUrl = totpService.getQrCodeUrl(secret, "MarketingAgency");

            Map<String, String> response = new HashMap<>();
            response.put("secret", secret);
            response.put("qrUrl", qrUrl);

            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            logger.error("QR code could not be generated, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/verify")
    public boolean verify2fa(@RequestBody TwoFAModel twoFAModel) {
        try {
            if (totpService.verifyCode(twoFAModel.getSecret(), Integer.parseInt(twoFAModel.getCode()))) {
                logger.info("Input code from scanned QR code is valid, user " + twoFAModel.getUserEmail());

                return true;
            } else {
                logger.warn("Input code from scanned QR code is not valid, user " + twoFAModel.getUserEmail());
                return false;
            }
        }
        catch (Exception e){
            logger.error("User " + twoFAModel.getUserEmail() + " couldn't login with input code from scanned QR code , reason: " + e.getMessage());

            return false;
        }
    }
}

package com.bsep2024.MarketingAgency.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES";

    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decryptedData));
    }
}

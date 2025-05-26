package com.bsep2024.MarketingAgency.utils;

import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;


public class KeyStoreUtil {
    private static final String KEYSTORE_DIR = "keystore/";

    static {
        File directory = new File(KEYSTORE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void saveKey(SecretKey key, String fileName) throws Exception {
        FileOutputStream fos = new FileOutputStream(KEYSTORE_DIR + fileName + ".key");
        fos.write(key.getEncoded());
        fos.close();
    }

    public static SecretKey loadKey(String fileName) throws Exception {
        File file = new File(KEYSTORE_DIR + fileName + ".key");
        FileInputStream fis = new FileInputStream(file);
        byte[] encodedKey = new byte[(int) file.length()];
        fis.read(encodedKey);
        fis.close();
        return new SecretKeySpec(encodedKey, "AES");
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }
}

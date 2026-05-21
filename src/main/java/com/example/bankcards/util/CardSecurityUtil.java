package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CardSecurityUtil {

    private static final String ALGORITHM = "AES";
    private static byte[] secretKeyBytes;

    public static void setAesKey(byte[] keyBytes) {
        if (keyBytes == null || keyBytes.length != 32) {
            throw new IllegalArgumentException("Ключ AES-256 должен быть строго 32 байта!");
        }
        secretKeyBytes = keyBytes;
    }

    /**
     * Шифрует чистый номер карты (16 цифр) для БД
     */
    public static String encryptCardNumber(String rawCardNumber) {
        try {
            checkKeyInitialized();
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(rawCardNumber.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка шифрования номера карты", e);
        }
    }

    /**
     * Расшифровывает строку из БД в чистый номер карты
     */
    public static String decryptCardNumber(String encryptedCardNumber) {
        try {
            checkKeyInitialized();
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCardNumber));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка дешифрования номера карты", e);
        }
    }

    /**
     * Маскирует номер карты: **** **** **** 1234
     */
    public static String maskCardNumber(String rawCardNumber) {
        if (rawCardNumber == null || rawCardNumber.length() < 4) {
            return "#### #### #### ####";
        }
        String lastFourDigits = rawCardNumber.substring(rawCardNumber.length() - 4);
        return "**** **** **** " + lastFourDigits;
    }

    private static void checkKeyInitialized() {
        if (secretKeyBytes == null) {
            throw new IllegalStateException("CardSecurityUtil не инициализирован! Ключ шифрования отсутствует.");
        }
    }
}

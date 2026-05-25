package com.example.bankcards.config;

import com.example.bankcards.util.CardSecurityUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import static com.example.bankcards.util.AppConstants.MESSAGE_DIGEST_ALGORITHM;

@Configuration
public class CardSecurityConfig {

    @Value("${application.security.card.secret}")
    private String cardSecretKey;

    @PostConstruct
    public void init() {
        byte[] finalKey;
        try {
            finalKey = Base64.getDecoder().decode(cardSecretKey.trim());
        } catch (IllegalArgumentException e) {
            try {
                MessageDigest digest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
                finalKey = digest.digest(cardSecretKey.getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException("Критическая ошибка: SHA-256 недоступен", ex);
            }
        }
        CardSecurityUtil.setAesKey(finalKey);
    }
}

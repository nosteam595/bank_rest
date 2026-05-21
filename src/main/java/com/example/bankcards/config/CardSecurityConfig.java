package com.example.bankcards.config;

import com.example.bankcards.util.CardSecurityUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.util.Base64;

@Configuration
public class CardSecurityConfig {

    @Value("${application.security.card.secret}")
    private String cardSecretKey;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(cardSecretKey);
        CardSecurityUtil.setAesKey(decodedKey);
    }
}

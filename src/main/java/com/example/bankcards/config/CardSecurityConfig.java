package com.example.bankcards.config;

import com.example.bankcards.util.CardSecurityUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardSecurityConfig {

    @Value("${application.security.card.secret}")
    private String cardSecretKey;

    @PostConstruct
    public void init() {
        CardSecurityUtil.setAesKey(cardSecretKey);
    }
}

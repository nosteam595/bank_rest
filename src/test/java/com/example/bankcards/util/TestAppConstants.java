package com.example.bankcards.util;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatusUpdateRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import java.math.BigDecimal;
import static com.example.bankcards.util.CardStatus.*;

public class TestAppConstants {
    public static final Long ID = 1L;
    public static final Long ID_2 = 2L;
    public static final Long USER_ID = 1L;
    public static final Long FROM_CARD_ID = 1L;
    public static final Long TO_CARD_ID = 2L;
    public static final String USER_NAME = "user123";
    public static final String CARD_NUMBER = "0711150127193831";
    public static final String CARD_NUMBER_ENCRYPT = "encrypted_3831";
    public static final String CARD_NUMBER_MASKED = "**** **** **** 3831";
    public static final String EXPIRATION_DATE = "12/27";
    public static final CardStatus STATUS = ACTIVE;
    public static final BigDecimal AMOUNT = BigDecimal.valueOf(50);
    public static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(5000);
    public static final BigDecimal AMOUNT_BAD = BigDecimal.valueOf(50000);
    public static final BigDecimal BALANCE = BigDecimal.valueOf(5000);
    public static final BigDecimal BALANCE_FROM = BigDecimal.valueOf(4950);
    public static final BigDecimal BALANCE_TO = BigDecimal.valueOf(5050);

    public static final CardCreateRequest CREATE_CARD_REQUEST =
            new CardCreateRequest(USER_ID, CARD_NUMBER, EXPIRATION_DATE, INITIAL_BALANCE);

    public static final CardStatusUpdateRequest UPDATE_CARD_STATUS_REQUEST =
            new CardStatusUpdateRequest(STATUS);

    public static final CardResponse CARD_RESPONSE =
            new CardResponse(ID, USER_ID, CARD_NUMBER_MASKED, EXPIRATION_DATE, STATUS, INITIAL_BALANCE);

    public static final TransferRequest TRANSFER_REQUEST =
            new TransferRequest(FROM_CARD_ID, TO_CARD_ID, AMOUNT);

    public static final TransferRequest TRANSFER_REQUEST_BAD =
            new TransferRequest(FROM_CARD_ID, FROM_CARD_ID, AMOUNT);

    public static final TransferRequest TRANSFER_REQUEST_BAD_AMOUNT =
            new TransferRequest(FROM_CARD_ID, TO_CARD_ID, AMOUNT_BAD);

    public static User TEST_USER = new User() {
        {
            setId(ID);
            setUsername(USER_NAME);
            setStatus(UserStatus.ACTIVE);
        }
    };

    public static final Card TEST_CARD = new Card() {
        {
            setId(ID);
            setUser(TEST_USER);
            setCardNumberEncrypted(CARD_NUMBER_ENCRYPT);
            setExpirationDate(EXPIRATION_DATE);
            setStatus(STATUS);
            setBalance(BALANCE);
        }
    };

    public static final Card TEST_CARD_2 = new Card() {
        {
            setId(ID_2);
            setUser(TEST_USER);
            setStatus(STATUS);
            setBalance(BALANCE);
        }
    };
}

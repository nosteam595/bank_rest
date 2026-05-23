package com.example.bankcards.util;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatusUpdateRequest;
import com.example.bankcards.dto.TransferRequest;
import java.math.BigDecimal;
import static com.example.bankcards.util.CardStatus.*;

public class TestAppConstants {
    public static final Long ID = 1L;
    public static final Long USER_ID = 1L;
    public static final String CARD_NUMBER = "0711150127193831";
    public static final String CARD_NUMBER_MASKED = "**** **** **** 3831";
    public static final String EXPIRATION_DATE = "12/27";
    public static final CardStatus STATUS = ACTIVE;
    public static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(5000);
    public static final Long fromCardId = 1L;
    public static final Long toCardId = 2L;
    public static final BigDecimal amount = BigDecimal.valueOf(50);

    public static final CardCreateRequest CREATE_CARD_REQUEST =
            new CardCreateRequest(USER_ID, CARD_NUMBER, EXPIRATION_DATE, INITIAL_BALANCE);

    public static final CardStatusUpdateRequest UPDATE_CARD_STATUS_REQUEST =
            new CardStatusUpdateRequest(STATUS);

    public static final CardResponse CARD_RESPONSE =
            new CardResponse(ID, USER_ID, CARD_NUMBER_MASKED, EXPIRATION_DATE, STATUS, INITIAL_BALANCE);

    public static final TransferRequest TRANSFER_REQUEST =
            new TransferRequest(fromCardId, toCardId, amount);
}

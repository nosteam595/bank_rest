package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardSecurityUtil;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;
import java.util.Optional;
import static com.example.bankcards.util.TestAppConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardService cardService;

    private MockedStatic<CardSecurityUtil> mockedSecurityUtil;
    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        mockedSecurityUtil = mockStatic(CardSecurityUtil.class);
        mockedSecurityUtil.when(() -> CardSecurityUtil.encryptCardNumber(anyString())).thenReturn(CARD_NUMBER_ENCRYPT);
        mockedSecurityUtil.when(() -> CardSecurityUtil.decryptCardNumber(anyString())).thenReturn(CARD_NUMBER);
        mockedSecurityUtil.when(() -> CardSecurityUtil.maskCardNumber(anyString())).thenReturn(CARD_NUMBER_MASKED);
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
        SecurityContextHolder.clearContext();
    }

    private void mockCurrentUser(User user) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Успешное создание карты администратором")
    void createCardTest() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(TEST_USER));
        when(cardRepository.save(any(Card.class))).thenReturn(TEST_CARD);

        CardResponse response = cardService.createCard(CREATE_CARD_REQUEST);

        assertNotNull(response);
        assertEquals(CARD_NUMBER_MASKED, response.maskedCardNumber());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    @DisplayName("Ошибка при создании карты администратором")
    void createCardTest_ThrowsException() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.createCard(CREATE_CARD_REQUEST));
    }

    @Test
    @DisplayName("Изменение статуса карты администратором")
    void updateCardStatusTest() {
        when(cardRepository.findById(ID)).thenReturn(Optional.of(TEST_CARD));
        when(cardRepository.save(TEST_CARD)).thenReturn(TEST_CARD);

        CardResponse response = cardService.updateCardStatus(ID, UPDATE_CARD_STATUS_REQUEST);

        assertEquals(CardStatus.ACTIVE, TEST_CARD.getStatus());
        assertNotNull(response);
    }

    @Test
    @DisplayName("Просмотр всех карт администратором")
    void getAllCardsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(Collections.singletonList(TEST_CARD));
        when(cardRepository.findAll(pageable)).thenReturn(page);

        Page<CardResponse> result = cardService.getAllCards(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Успешное удаление карты администратором")
    void deleteCardTest() {
        when(cardRepository.existsById(ID)).thenReturn(true);

        cardService.deleteCard(ID);

        verify(cardRepository, times(1)).deleteById(ID);
    }

    @Test
    @DisplayName("Ошибка удаления карты администратором")
    void deleteCardTest_ThrowsException() {
        when(cardRepository.existsById(ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> cardService.deleteCard(ID));
    }

    @Test
    @DisplayName("Просмотр доступных карт пользователем")
    void getMyCardsTest() {
        mockCurrentUser(TEST_USER);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(Collections.singletonList(TEST_CARD));
        when(cardRepository.findByUser(TEST_USER, pageable)).thenReturn(page);

        Page<CardResponse> result = cardService.getMyCards(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Блокировка карты пользователем")
    void requestBlockCardTest() {
        mockCurrentUser(TEST_USER);
        when(cardRepository.findByIdAndUser(ID, TEST_USER)).thenReturn(Optional.of(TEST_CARD));
        when(cardRepository.save(TEST_CARD)).thenReturn(TEST_CARD);

        CardResponse response = cardService.requestBlockCard(ID);

        assertEquals(CardStatus.BLOCKED, TEST_CARD.getStatus());
        assertNotNull(response);
    }

    @Test
    @DisplayName("Перевод средств между картами пользователя")
    void transferBetweenCardsTest() {
        mockCurrentUser(TEST_USER);

        when(cardRepository.findByIdAndUser(ID, TEST_USER)).thenReturn(Optional.of(TEST_CARD));
        when(cardRepository.findByIdAndUser(ID_2, TEST_USER)).thenReturn(Optional.of(TEST_CARD_2));

        cardService.transferBetweenOwnCards(TRANSFER_REQUEST);

        assertEquals(BALANCE_FROM, TEST_CARD.getBalance());
        assertEquals(BALANCE_TO, TEST_CARD_2.getBalance());
        verify(cardRepository, times(1)).save(TEST_CARD);
        verify(cardRepository, times(1)).save(TEST_CARD_2);
    }

    @Test
    @DisplayName("Ошибка перевода средств между картами при блокировке пользователя")
    void transferUserBlockedTest_ThrowsBadRequestException() {
        TEST_USER.setStatus(UserStatus.BLOCKED);
        mockCurrentUser(TEST_USER);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> cardService.transferBetweenOwnCards(TRANSFER_REQUEST));

        assertEquals("Операции приостановлены из-за блокировки аккаунта", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка перевода средств на ту же самую карту пользователя")
    void transferSameCardTest_ThrowsBadRequestException() {
        TEST_USER.setStatus(UserStatus.ACTIVE);
        mockCurrentUser(TEST_USER);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> cardService.transferBetweenOwnCards(TRANSFER_REQUEST_BAD));

        assertEquals("Нельзя совершить перевод на ту же самую карту", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка перевода при нехватке средств на карте пользователя")
    void transferInsufficientFundsTest_ThrowsBadRequestException() {
        mockCurrentUser(TEST_USER);

        when(cardRepository.findByIdAndUser(ID, TEST_USER)).thenReturn(Optional.of(TEST_CARD));
        when(cardRepository.findByIdAndUser(ID_2, TEST_USER)).thenReturn(Optional.of(TEST_CARD_2));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> cardService.transferBetweenOwnCards(TRANSFER_REQUEST_BAD_AMOUNT));

        assertEquals("Недостаточно средств на карте списания", ex.getMessage());
    }
}

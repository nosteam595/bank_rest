package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatusUpdateRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.CardSecurityUtil;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CardResponse createCard(CardCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Card card = new Card();
        card.setUser(user);
        card.setCardNumberEncrypted(CardSecurityUtil.encryptCardNumber(request.cardNumber()));
        card.setExpirationDate(request.expirationDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(request.initialBalance());

        Card savedCard = cardRepository.save(card);
        return mapToCardResponse(savedCard, request.cardNumber());
    }

    @Transactional
    public CardResponse updateCardStatus(Long cardId, CardStatusUpdateRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта не найдена"));

        card.setStatus(request.status());
        Card updatedCard = cardRepository.save(card);
        return mapToCardResponse(updatedCard);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(this::mapToCardResponse);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new ResourceNotFoundException("Карта не найдена");
        }
        cardRepository.deleteById(cardId);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getMyCards(Pageable pageable) {
        User currentUser = getCurrentUser();
        return cardRepository.findByUser(currentUser, pageable).map(this::mapToCardResponse);
    }

    @Transactional
    public CardResponse requestBlockCard(Long cardId) {
        User currentUser = getCurrentUser();
        Card card = cardRepository.findByIdAndUser(cardId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Карта не найдена у данного пользователя"));

        card.setStatus(CardStatus.BLOCKED);
        return mapToCardResponse(cardRepository.save(card));
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferBetweenOwnCards(TransferRequest request) {
        User currentUser = getCurrentUser();

        if (request.fromCardId().equals(request.toCardId())) {
            throw new BadRequestException("Нельзя совершить перевод на ту же самую карту");
        }

        Card fromCard = cardRepository.findByIdAndUser(request.fromCardId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Карта списания не найдена или не принадлежит вам"));

        Card toCard = cardRepository.findByIdAndUser(request.toCardId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Карта зачисления не найдена или не принадлежит вам"));

        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Карта списания заблокирована или её срок истёк");
        }
        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Карта зачисления не активна");
        }

        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new BadRequestException("Недостаточно средств на карте списания");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Текущий пользователь не найден в сессии"));
    }

    private CardResponse mapToCardResponse(Card card) {
        String rawNumber = CardSecurityUtil.decryptCardNumber(card.getCardNumberEncrypted());
        return mapToCardResponse(card, rawNumber);
    }

    private CardResponse mapToCardResponse(Card card, String rawCardNumber) {
        String maskedNumber = CardSecurityUtil.maskCardNumber(rawCardNumber);
        return new CardResponse(
                card.getId(),
                card.getUser().getId(),
                maskedNumber,
                card.getExpirationDate(),
                card.getStatus(),
                card.getBalance()
        );
    }
}

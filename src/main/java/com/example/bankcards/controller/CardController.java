package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatusUpdateRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
        return new ResponseEntity<>(cardService.createCard(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CardResponse> updateCardStatus(
            @PathVariable Long id,
            @Valid @RequestBody CardStatusUpdateRequest request) {
        return ResponseEntity.ok(cardService.updateCardStatus(id, request));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CardResponse>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<Page<CardResponse>> getMyCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getMyCards(pageable));
    }

    @PostMapping("/my/block/{id}")
    public ResponseEntity<CardResponse> userBlockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.requestBlockCard(id));
    }

    @PostMapping("/my/transfer")
    public ResponseEntity<String> transferBetweenOwnCards(@Valid @RequestBody TransferRequest request) {
        cardService.transferBetweenOwnCards(request);
        return ResponseEntity.ok("Перевод успешно выполнен");
    }
}

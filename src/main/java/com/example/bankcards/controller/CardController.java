package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatusUpdateRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Управление картами", description = "Операции над картами")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Создание карты администратором",
            description = "Возвращает информацию о карте")
    @ApiResponse(responseCode = "201", description = "Успешное создание карты")
    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
        return new ResponseEntity<>(cardService.createCard(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Изменение статуса карты администратором",
            description = "Возвращает информацию о статусе карте")
    @ApiResponse(responseCode = "200", description = "Успешное изменение статуса карты")
    @PatchMapping("/{id}/status")
    public ResponseEntity<CardResponse> updateCardStatus(
            @Parameter(description = "Идентификатор карты", schema = @Schema(type = "integer"))
            @PathVariable Long id,
            @Valid @RequestBody CardStatusUpdateRequest request) {
        return ResponseEntity.ok(cardService.updateCardStatus(id, request));
    }

    @Operation(summary = "Просмотр всех карт администратором",
            description = "Возвращает список всех существующих карт с пагинацией")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка")
    @GetMapping("/all")
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @Parameter(hidden = true)
            Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(pageable));
    }

    @Operation(summary = "Удаление карты администратором",
            description = "Возвращает статус 204")
    @ApiResponse(responseCode = "204", description = "Успешное удаление карты")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "Идентификатор карты", schema = @Schema(type = "integer"))
            @PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Просмотр карт пользователем",
            description = "Возвращает список карт текущего авторизованного пользователя с пагинацией")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка")
    @GetMapping("/my")
    public ResponseEntity<Page<CardResponse>> getMyCards(
            @Parameter(hidden = true)
            Pageable pageable) {
        return ResponseEntity.ok(cardService.getMyCards(pageable));
    }

    @Operation(summary = "Блокировка карты пользователем",
            description = "Возвращает информацию о заблокированной карте")
    @ApiResponse(responseCode = "200", description = "Успешная блокировка карты")
    @PostMapping("/my/block/{id}")
    public ResponseEntity<CardResponse> userBlockCard(
            @Parameter(description = "Идентификатор карты", schema = @Schema(type = "integer"))
            @PathVariable Long id) {
        return ResponseEntity.ok(cardService.requestBlockCard(id));
    }

    @Operation(summary = "Перевод средств между картами пользователя",
            description = "Возвращает текстовое сообщение об успешном завершении операции")
    @ApiResponse(responseCode = "200", description = "Операция успешна")
    @PostMapping("/my/transfer")
    public ResponseEntity<String> transferBetweenOwnCards(@Valid @RequestBody TransferRequest request) {
        cardService.transferBetweenOwnCards(request);
        return ResponseEntity.ok("Перевод успешно выполнен");
    }
}

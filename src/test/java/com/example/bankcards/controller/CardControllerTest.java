package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatusUpdateRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import static com.example.bankcards.util.TestAppConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @JsonIgnoreProperties({"pageable", "sort"})
    private interface PageMixin {}

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        objectMapper.addMixIn(Page.class, PageMixin.class);

        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createCardTest() throws Exception {
        when(cardService.createCard(any(CardCreateRequest.class))).thenReturn(CARD_RESPONSE);

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CREATE_CARD_REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateCardStatusTest() throws Exception {
        when(cardService.updateCardStatus(eq(ID), any(CardStatusUpdateRequest.class))).thenReturn(CARD_RESPONSE);

        mockMvc.perform(patch("/api/v1/cards/{id}/status", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UPDATE_CARD_STATUS_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllCardsTest() throws Exception {
        Page<CardResponse> page = new PageImpl<>(Collections.singletonList(CARD_RESPONSE));
        when(cardService.getAllCards(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/cards/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCardTest() throws Exception {
        doNothing().when(cardService).deleteCard(ID);

        mockMvc.perform(delete("/api/v1/cards/{id}", ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMyCardsTest() throws Exception {
        Page<CardResponse> page = new PageImpl<>(Collections.singletonList(CARD_RESPONSE));
        when(cardService.getMyCards(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/cards/my")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void userBlockCardTest() throws Exception {
        when(cardService.requestBlockCard(ID)).thenReturn(CARD_RESPONSE);

        mockMvc.perform(post("/api/v1/cards/my/block/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void transferBetweenOwnCardsTest() throws Exception {
        doNothing().when(cardService).transferBetweenOwnCards(any(TransferRequest.class));

        mockMvc.perform(post("/api/v1/cards/my/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRANSFER_REQUEST)))
                .andDo(result -> result.getResponse().setCharacterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Перевод успешно выполнен\""));
    }
}

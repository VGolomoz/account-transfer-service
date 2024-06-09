package com.account.transfer.api.controller;

import com.account.transfer.api.dto.ExchangeRateResponse;
import com.account.transfer.exception.ExchangeRateNotFoundException;
import com.account.transfer.mapper.ExchangeRateMapper;
import com.account.transfer.service.ExchangeRateService;
import com.account.transfer.service.model.ExchangeRateModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.account.transfer.exception.AppErrorCode.FIELDS_VALIDATION_ERROR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private ExchangeRateMapper exchangeRateMapper;

    @Test
    public void testGetExchangeRate_Success() throws Exception {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal rate = BigDecimal.valueOf(0.85);
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX");

        ExchangeRateModel exchangeRateModel = prepareExchangeRateModel(fromCurrency, toCurrency, rate, dateTime);
        ExchangeRateResponse exchangeRateResponse = prepareExchangeRateResponse(fromCurrency, toCurrency, rate, dateTime);

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenReturn(exchangeRateModel);
        when(exchangeRateMapper.mapToExchangeRateResponse(exchangeRateModel)).thenReturn(exchangeRateResponse);

        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", fromCurrency)
                .param("toCurrency", toCurrency))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value(fromCurrency))
                .andExpect(jsonPath("$.toCurrency").value(toCurrency))
                .andExpect(jsonPath("$.rate").value(rate))
                .andExpect(jsonPath("$.dateTime").value(dateTime.format(formatter)));
    }

    @Test
    public void testGetExchangeRate_InvalidFromCurrency() throws Exception {
        // given
        String invalidFromCurrency = "INVALID";
        String toCurrency = "EUR";

        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", invalidFromCurrency)
                .param("toCurrency", toCurrency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("Parameter 'fromCurrency' must be 3 latin letters"));
    }

    @Test
    public void testGetExchangeRate_InvalidToCurrency() throws Exception {
        // given
        String fromCurrency = "USD";
        String invalidToCurrency = "INVALID";

        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", fromCurrency)
                .param("toCurrency", invalidToCurrency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("Parameter 'toCurrency' must be 3 latin letters"));
    }

    @Test
    public void testGetExchangeRate_NotFound() throws Exception {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new ExchangeRateNotFoundException(fromCurrency, toCurrency));

        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", fromCurrency)
                .param("toCurrency", toCurrency))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetExchangeRate_MissingFromCurrency() throws Exception {
        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", "")
                .param("toCurrency", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("Parameter 'fromCurrency' must be 3 latin letters"));
    }

    @Test
    public void testGetExchangeRate_MissingToCurrency() throws Exception {
        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", "EUR")
                .param("toCurrency", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("Parameter 'toCurrency' must be 3 latin letters"));
    }

    private ExchangeRateModel prepareExchangeRateModel(String fromCurrency, String toCurrency, BigDecimal rate,
                                                       ZonedDateTime dateTime) {
        return ExchangeRateModel.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(rate)
                .dateTime(dateTime)
                .build();
    }

    private ExchangeRateResponse prepareExchangeRateResponse(String fromCurrency, String toCurrency, BigDecimal rate,
                                                             ZonedDateTime dateTime) {
        return ExchangeRateResponse.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(rate)
                .dateTime(dateTime)
                .build();
    }
}
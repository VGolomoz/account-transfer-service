package com.account.transfer.api.controller;

import com.account.transfer.api.dto.ExchangeRateResponse;
import com.account.transfer.exception.ExchangeRateNotFoundException;
import com.account.transfer.exception.ExchangeRateServiceException;
import com.account.transfer.mapper.ExchangeRateMapper;
import com.account.transfer.service.ExchangeRateService;
import com.account.transfer.service.model.ExchangeRateModel;
import com.account.transfer.util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static com.account.transfer.exception.AppErrorCode.*;
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
    public void should_return_200_when_success() throws Exception {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal rate = BigDecimal.valueOf(0.85);
        ZonedDateTime dateTime = ZonedDateTime.now();

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
                .andExpect(jsonPath("$.dateTime").value(DateTimeUtil.formatToString(dateTime)));
    }

    @Test
    public void should_return_400_when_invalid_fromCurrency_param() throws Exception {
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
    public void should_return_400_when_invalid_toCurrency_param() throws Exception {
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
    public void should_return_404_when_throws_ExchangeRateNotFoundException() throws Exception {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        String expectedErrorMsg = String.format("Exchange rate for pairs [%s:%s] is not found", fromCurrency, toCurrency);

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new ExchangeRateNotFoundException(fromCurrency, toCurrency));

        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", fromCurrency)
                .param("toCurrency", toCurrency))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage.key").value(EXCHANGE_RATE_NOT_FOUND_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value(expectedErrorMsg));
    }

    @Test
    public void should_return_500_when_throws_ExchangeRateServiceException() throws Exception {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        String expectedErrorMsg = "test";

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency))
                .thenThrow(new ExchangeRateServiceException(expectedErrorMsg));

        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", fromCurrency)
                .param("toCurrency", toCurrency))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorMessage.key").value(EXCHANGE_RATE_SERVICE_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value(expectedErrorMsg));
    }

    @Test
    public void should_return_400_when_missing_fromCurrency_param() throws Exception {
        // when & then
        mockMvc.perform(get("/exchange-rate")
                .param("fromCurrency", "")
                .param("toCurrency", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("Parameter 'fromCurrency' must be 3 latin letters"));
    }

    @Test
    public void should_return_400_when_missing_toCurrency_param() throws Exception {
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
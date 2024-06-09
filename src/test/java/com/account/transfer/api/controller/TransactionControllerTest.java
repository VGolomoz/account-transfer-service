package com.account.transfer.api.controller;

import com.account.transfer.api.dto.TransactionRequest;
import com.account.transfer.api.dto.TransactionResponse;
import com.account.transfer.entity.TransactionStatus;
import com.account.transfer.exception.*;
import com.account.transfer.mapper.TransactionMapper;
import com.account.transfer.service.TransactionService;
import com.account.transfer.service.model.TransactionModel;
import com.account.transfer.util.DateTimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static com.account.transfer.entity.TransactionStatus.SUCCESS;
import static com.account.transfer.exception.AppErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionMapper transactionMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_return_200_when_success() throws Exception {
        // given
        Long transactionId = 123L;
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(0.42);
        ZonedDateTime dateTime = ZonedDateTime.now();
        BigDecimal availableBalance = BigDecimal.valueOf(1001.42);
        BigDecimal residualBalance = BigDecimal.valueOf(1001.00);
        String baseCurrency = "EUR";
        String targetCurrency = "GBP";
        BigDecimal exchangeRate = BigDecimal.valueOf(0.24);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        TransactionModel transactionModel = prepareTransactionModel(transactionId, accountOwnerId, targetAccountId,
                amount, dateTime, SUCCESS, availableBalance, residualBalance, baseCurrency, targetCurrency, exchangeRate);

        TransactionResponse transactionResponse = prepareTransactionResponse(transactionId, accountOwnerId, targetAccountId,
                amount, dateTime, SUCCESS, residualBalance, baseCurrency, targetCurrency, exchangeRate);

        when(transactionService.performTransfer(request.getAccountOwnerId(), request.getTargetAccountId(), request.getAmount()))
                .thenReturn(transactionModel);
        when(transactionMapper.mapToTransactionResponse(any())).thenReturn(transactionResponse);

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.accountOwnerId").value(accountOwnerId))
                .andExpect(jsonPath("$.targetAccountId").value(targetAccountId))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.dateTime").value(DateTimeUtil.formatToString(dateTime)))
                .andExpect(jsonPath("$.status").value(SUCCESS.toString()))
                .andExpect(jsonPath("$.residualBalance").value(residualBalance))
                .andExpect(jsonPath("$.baseCurrency").value(baseCurrency))
                .andExpect(jsonPath("$.targetCurrency").value(targetCurrency))
                .andExpect(jsonPath("$.exchangeRate").value(exchangeRate));
    }

    @Test
    public void should_return_400_when_invalid_accountOwnerId() throws Exception {
        // given
        Long accountOwnerId = null;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(0.42);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("Account owner id can not be empty"));
    }

    @Test
    public void should_return_400_when_invalid_targetAccountId() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = null;
        BigDecimal amount = BigDecimal.valueOf(0.42);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("Target account id can not be empty"));
    }

    @Test
    public void should_return_400_when_invalid_amount_digits() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(123456789123456789123456789123456789123.42);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text")
                        .value("The amount can have a maximum total of 36 digits and 2 digits after the decimal point."));
    }

    @Test
    public void should_return_400_when_invalid_amount_decimal_points() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(0.423);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text")
                        .value("The amount can have a maximum total of 36 digits and 2 digits after the decimal point."));
    }

    @Test
    public void should_return_400_when_amount_is_null() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = null;

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("Amount can not be empty"));
    }

    @Test
    public void should_return_400_when_amount_is_zero() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.ZERO;

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("The transfer amount must be at least 0.01"));
    }

    @Test
    public void should_return_400_when_amount_is_negative() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(-1);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(FIELDS_VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value("The transfer amount must be at least 0.01"));
    }

    @Test
    public void should_return_400_when_throws_InvalidTransferException() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(4.2);
        String expectedErrorMsg = String.format("Cannot transfer funds to the same account: %s", accountOwnerId);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        when(transactionService.performTransfer(request.getAccountOwnerId(), request.getTargetAccountId(), request.getAmount()))
                .thenThrow(new InvalidTransferException(expectedErrorMsg));

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(INVALID_TRANSFER_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value(expectedErrorMsg));
    }

    @Test
    public void should_return_404_when_throws_AccountNotFoundException() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(4.2);
        String expectedErrorMsg = String.format("Account with owner id: [%s] is not found", accountOwnerId);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        when(transactionService.performTransfer(request.getAccountOwnerId(), request.getTargetAccountId(), request.getAmount()))
                .thenThrow(new AccountNotFoundException(expectedErrorMsg));

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage.key").value(ACCOUNT_NOT_FOUND_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value(expectedErrorMsg));
    }

    @Test
    public void should_return_400_when_throws_InsufficientBalanceException() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(4.2);
        String expectedErrorMsg = String.format("Insufficient balance: 0.00 for amount: %s", amount);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        when(transactionService.performTransfer(request.getAccountOwnerId(), request.getTargetAccountId(), request.getAmount()))
                .thenThrow(new InsufficientBalanceException(expectedErrorMsg));

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.key").value(INSUFFICIENT_BALANCE_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value(expectedErrorMsg));
    }

    @Test
    public void should_return_404_when_throws_ExchangeRateNotFoundException() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(4.2);
        String fromCurrency = "EUR";
        String toCurrency = "GBP";
        String expectedErrorMsg = String.format("Exchange rate for pairs [%s:%s] is not found", fromCurrency, toCurrency);

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        when(transactionService.performTransfer(request.getAccountOwnerId(), request.getTargetAccountId(), request.getAmount()))
                .thenThrow(new ExchangeRateNotFoundException(fromCurrency, toCurrency));

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage.key").value(EXCHANGE_RATE_NOT_FOUND_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value(expectedErrorMsg));
    }

    @Test
    public void should_return_500_when_throws_ExchangeRateServiceException() throws Exception {
        // given
        Long accountOwnerId = 1001L;
        Long targetAccountId = 1002L;
        BigDecimal amount = BigDecimal.valueOf(4.2);
        String expectedErrorMsg = "test";

        TransactionRequest request = new TransactionRequest(accountOwnerId, targetAccountId, amount);

        when(transactionService.performTransfer(request.getAccountOwnerId(), request.getTargetAccountId(), request.getAmount()))
                .thenThrow(new ExchangeRateServiceException(expectedErrorMsg));

        // when & then
        mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorMessage.key").value(EXCHANGE_RATE_SERVICE_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage.text").value(expectedErrorMsg));
    }

    private TransactionModel prepareTransactionModel(Long transactionId, Long accountOwnerId, Long targetAccountId,
                                                     BigDecimal amount, ZonedDateTime dateTime, TransactionStatus status,
                                                     BigDecimal availableBalance, BigDecimal residualBalance,
                                                     String baseCurrency, String targetCurrency, BigDecimal exchangeRate) {
        return TransactionModel.builder()
                .transactionId(transactionId)
                .accountOwnerId(accountOwnerId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .dateTime(dateTime)
                .status(status)
                .availableBalance(availableBalance)
                .residualBalance(residualBalance)
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .exchangeRate(exchangeRate)
                .build();
    }

    private TransactionResponse prepareTransactionResponse(Long transactionId, Long accountOwnerId, Long targetAccountId,
                                                           BigDecimal amount, ZonedDateTime dateTime,
                                                           TransactionStatus status, BigDecimal residualBalance,
                                                           String baseCurrency, String targetCurrency, BigDecimal exchangeRate) {
        return TransactionResponse.builder()
                .transactionId(transactionId)
                .accountOwnerId(accountOwnerId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .dateTime(dateTime)
                .status(status.toString())
                .residualBalance(residualBalance)
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .exchangeRate(exchangeRate)
                .build();

    }
}
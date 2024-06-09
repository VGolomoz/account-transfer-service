package com.account.transfer.mapper;

import com.account.transfer.api.dto.TransactionResponse;
import com.account.transfer.entity.TransactionEntity;
import com.account.transfer.entity.TransactionStatus;
import com.account.transfer.service.model.AccountModel;
import com.account.transfer.service.model.TransactionModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperImplTest {

    private final TransactionMapperImpl transactionMapper = new TransactionMapperImpl();

    @Test
    public void testMapToTransactionModel_ValidEntity() {
        // given
        TransactionEntity entity = new TransactionEntity();
        entity.setId(1L);
        entity.setFromAccountId(123L);
        entity.setToAccountId(456L);
        entity.setAmount(BigDecimal.valueOf(100));
        entity.setDateTime(ZonedDateTime.now());
        entity.setStatus(TransactionStatus.SUCCESS);
        entity.setAvailableBalance(BigDecimal.valueOf(1000));
        entity.setResidualBalance(BigDecimal.valueOf(900));
        entity.setBaseCurrency("USD");
        entity.setTargetCurrency("EUR");
        entity.setExchangeRate(BigDecimal.valueOf(0.85));

        // when
        TransactionModel model = transactionMapper.mapToTransactionModel(entity);

        // then
        assertNotNull(model);

        assertEquals(entity.getId(), model.getTransactionId());
        assertEquals(entity.getFromAccountId(), model.getAccountOwnerId());
        assertEquals(entity.getToAccountId(), model.getTargetAccountId());
        assertEquals(entity.getAmount(), model.getAmount());
        assertEquals(entity.getDateTime(), model.getDateTime());
        assertEquals(entity.getStatus(), model.getStatus());
        assertEquals(entity.getAvailableBalance(), model.getAvailableBalance());
        assertEquals(entity.getResidualBalance(), model.getResidualBalance());
        assertEquals(entity.getBaseCurrency(), model.getBaseCurrency());
        assertEquals(entity.getTargetCurrency(), model.getTargetCurrency());
        assertEquals(entity.getExchangeRate(), model.getExchangeRate());
    }

    @Test
    public void testMapToTransactionModel_NullEntity() {
        // when
        TransactionModel model = transactionMapper.mapToTransactionModel(null);

        // then
        assertNull(model);
    }

    @Test
    public void testMapToTransactionResponse_ValidModel() {
        // given
        TransactionModel model = TransactionModel.builder()
                .transactionId(1L)
                .accountOwnerId(123L)
                .targetAccountId(456L)
                .amount(BigDecimal.valueOf(100))
                .dateTime(ZonedDateTime.now())
                .status(TransactionStatus.SUCCESS)
                .availableBalance(BigDecimal.valueOf(1000))
                .residualBalance(BigDecimal.valueOf(900))
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .exchangeRate(BigDecimal.valueOf(0.85))
                .build();

        // when
        TransactionResponse response = transactionMapper.mapToTransactionResponse(model);

        // then
        assertNotNull(response);

        assertEquals(model.getTransactionId(), response.getTransactionId());
        assertEquals(model.getAccountOwnerId(), response.getAccountOwnerId());
        assertEquals(model.getTargetAccountId(), response.getTargetAccountId());
        assertEquals(model.getAmount(), response.getAmount());
        assertEquals(model.getDateTime(), response.getDateTime());
        assertEquals(model.getStatus().toString(), response.getStatus());
        assertEquals(model.getResidualBalance(), response.getResidualBalance());
        assertEquals(model.getBaseCurrency(), response.getBaseCurrency());
        assertEquals(model.getTargetCurrency(), response.getTargetCurrency());
        assertEquals(model.getExchangeRate(), response.getExchangeRate());
    }

    @Test
    public void testMapToTransactionResponse_NullModel() {
        // when
        TransactionResponse response = transactionMapper.mapToTransactionResponse(null);

        // then
        assertNull(response);
    }

    @Test
    public void testBuildTransactionEntity() {
        // given
        AccountModel accountOwner = AccountModel.builder()
                .ownerId(123L)
                .balance(BigDecimal.valueOf(1000))
                .currency("USD")
                .build();

        AccountModel targetAccount = AccountModel.builder()
                .ownerId(456L)
                .balance(BigDecimal.valueOf(2000))
                .currency("EUR")
                .build();

        BigDecimal amount = BigDecimal.valueOf(100);
        TransactionStatus status = TransactionStatus.SUCCESS;
        BigDecimal residualBalance = BigDecimal.valueOf(900);
        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);

        // when
        TransactionEntity entity = transactionMapper.buildTransactionEntity(accountOwner, targetAccount, amount, status, residualBalance, exchangeRate);

        // then
        assertNotNull(entity);

        assertEquals(accountOwner.getOwnerId(), entity.getFromAccountId());
        assertEquals(targetAccount.getOwnerId(), entity.getToAccountId());
        assertEquals(amount, entity.getAmount());
        assertNotNull(entity.getDateTime());
        assertEquals(status, entity.getStatus());
        assertEquals(accountOwner.getBalance(), entity.getAvailableBalance());
        assertEquals(residualBalance, entity.getResidualBalance());
        assertEquals(accountOwner.getCurrency(), entity.getBaseCurrency());
        assertEquals(targetAccount.getCurrency(), entity.getTargetCurrency());
        assertEquals(exchangeRate, entity.getExchangeRate());
    }

}
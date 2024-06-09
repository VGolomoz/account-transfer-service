package com.account.transfer.mapper;

import com.account.transfer.api.dto.TransactionResponse;
import com.account.transfer.entity.TransactionEntity;
import com.account.transfer.entity.TransactionStatus;
import com.account.transfer.service.model.AccountModel;
import com.account.transfer.service.model.TransactionModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static java.util.Objects.isNull;

@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionModel mapToTransactionModel(TransactionEntity entity) {
        if (isNull(entity)) {
            return null;
        }

        return TransactionModel.builder()
                .transactionId(entity.getId())
                .accountOwnerId(entity.getFromAccountId())
                .targetAccountId(entity.getToAccountId())
                .amount(entity.getAmount())
                .dateTime(entity.getDateTime())
                .status(entity.getStatus())
                .availableBalance(entity.getAvailableBalance())
                .residualBalance(entity.getResidualBalance())
                .baseCurrency(entity.getBaseCurrency())
                .targetCurrency(entity.getTargetCurrency())
                .exchangeRate(entity.getExchangeRate())
                .build();
    }

    @Override
    public TransactionResponse mapToTransactionResponse(TransactionModel model) {
        if (isNull(model)) {
            return null;
        }

        return TransactionResponse.builder()
                .transactionId(model.getTransactionId())
                .accountOwnerId(model.getAccountOwnerId())
                .targetAccountId(model.getTargetAccountId())
                .amount(model.getAmount())
                .dateTime(model.getDateTime())
                .status(model.getStatus().toString())
                .residualBalance(model.getResidualBalance())
                .baseCurrency(model.getBaseCurrency())
                .targetCurrency(model.getTargetCurrency())
                .exchangeRate(model.getExchangeRate())
                .build();
    }

    public TransactionEntity buildTransactionEntity(AccountModel accountOwner, AccountModel targetAccount,
                                                    BigDecimal amount, TransactionStatus status,
                                                    BigDecimal residualBalance, BigDecimal exchangeRate) {
        var transaction = new TransactionEntity();
        transaction.setFromAccountId(accountOwner.getOwnerId());
        transaction.setToAccountId(targetAccount.getOwnerId());
        transaction.setAmount(amount);
        transaction.setDateTime(ZonedDateTime.now());
        transaction.setStatus(status);
        transaction.setAvailableBalance(accountOwner.getBalance());
        transaction.setResidualBalance(residualBalance);
        transaction.setBaseCurrency(accountOwner.getCurrency());
        transaction.setTargetCurrency(targetAccount.getCurrency());
        transaction.setExchangeRate(exchangeRate);

        return transaction;
    }
}

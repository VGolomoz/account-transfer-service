package com.account.transfer.mapper;

import com.account.transfer.api.dto.TransactionResponse;
import com.account.transfer.entity.TransactionEntity;
import com.account.transfer.service.model.TransactionModel;
import org.springframework.stereotype.Component;

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
                .accOwnerId(entity.getFromAccountId())
                .accTargetId(entity.getToAccountId())
                .amount(entity.getAmount())
                .dateTime(entity.getDateTime())
                .status(entity.getStatus())
                .availableBalance(entity.getAvailableBalance())
                .residualBalance(entity.getResidualBalance())
                .baseCurrency(entity.getBaseCurrency())
                .targetCurrency(entity.getTargetCurrency())
                .exchangeRate(entity.getExchangeRate())
                .details(entity.getDetails())
                .build();
    }

    @Override
    public TransactionResponse mapToTransactionResponse(TransactionModel model) {
        if (isNull(model)) {
            return null;
        }

        return TransactionResponse.builder()
                .transactionId(model.getTransactionId())
                .accOwnerId(model.getAccOwnerId())
                .accTargetId(model.getAccTargetId())
                .amount(model.getAmount())
                .dateTime(model.getDateTime())
                .status(model.getStatus().toString())
                .residualBalance(model.getResidualBalance())
                .baseCurrency(model.getBaseCurrency())
                .targetCurrency(model.getTargetCurrency())
                .exchangeRate(model.getExchangeRate())
                .build();
    }
}

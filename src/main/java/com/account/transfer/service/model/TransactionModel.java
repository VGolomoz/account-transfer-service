package com.account.transfer.service.model;

import com.account.transfer.entity.TransactionStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
@Builder
public class TransactionModel {

    Long transactionId;
    Long accOwnerId;
    Long accTargetId;
    BigDecimal amount;
    ZonedDateTime dateTime;
    TransactionStatus status;
    BigDecimal availableBalance;
    BigDecimal residualBalance;
    String baseCurrency;
    String targetCurrency;
    BigDecimal exchangeRate;
    String details;
}

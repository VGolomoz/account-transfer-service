package com.account.transfer.service.model;

import com.account.transfer.entity.TransactionStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
@Builder(toBuilder = true)
public class TransactionModel {

    Long transactionId;
    Long accountOwnerId;
    Long targetAccountId;
    BigDecimal amount;
    ZonedDateTime dateTime;
    TransactionStatus status;
    BigDecimal availableBalance;
    BigDecimal residualBalance;
    String baseCurrency;
    String targetCurrency;
    BigDecimal exchangeRate;
}

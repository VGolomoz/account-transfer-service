package com.account.transfer.api.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
@Builder
public class TransactionResponse {

    Long transactionId;
    Long accountOwnerId;
    Long targetAccountId;
    BigDecimal amount;
    ZonedDateTime dateTime;
    String status;

    BigDecimal residualBalance;
    String baseCurrency;
    String targetCurrency;
    BigDecimal exchangeRate;
}

package com.account.transfer.api.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
@Builder
public class ExchangeRateResponse {

    String fromCurrency;
    String toCurrency;
    BigDecimal rate;
    ZonedDateTime dateTime;
}

package com.account.transfer.service.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
@Builder(toBuilder = true)
public class ExchangeRateModel {

    String fromCurrency;
    String toCurrency;
    BigDecimal rate;
    ZonedDateTime dateTime;
}

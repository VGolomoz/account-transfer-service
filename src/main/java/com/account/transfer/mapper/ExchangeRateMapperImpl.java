package com.account.transfer.mapper;

import com.account.transfer.api.dto.ExchangeRateResponse;
import com.account.transfer.service.model.ExchangeRateModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static java.util.Objects.isNull;

@Component
public class ExchangeRateMapperImpl implements ExchangeRateMapper {

    @Override
    public ExchangeRateResponse mapToExchangeRateResponse(ExchangeRateModel model) {
        if (isNull(model)) {
            return null;
        }

        return ExchangeRateResponse.builder()
                .fromCurrency(model.getFromCurrency())
                .toCurrency(model.getToCurrency())
                .rate(model.getRate())
                .dateTime(model.getDateTime())
                .build();
    }

    @Override
    public ExchangeRateModel buildExchangeRateModel(String fromCurrency, String toCurrency, BigDecimal rate) {
        return ExchangeRateModel.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(rate)
                .dateTime(ZonedDateTime.now())
                .build();
    }
}

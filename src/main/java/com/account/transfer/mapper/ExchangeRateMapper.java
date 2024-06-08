package com.account.transfer.mapper;

import com.account.transfer.api.dto.ExchangeRateResponse;
import com.account.transfer.service.model.ExchangeRateModel;

import java.math.BigDecimal;

/**
 * Service for mapping exchange rate data between different representations.
 */
public interface ExchangeRateMapper {

    /**
     * Maps an ExchangeRateModel to an ExchangeRateResponse.
     *
     * @param model The ExchangeRateModel
     * @return The mapped ExchangeRateResponse
     */
    ExchangeRateResponse mapToExchangeRateResponse(ExchangeRateModel model);

    /**
     * Maps ExchangeRateModel according to input parameters.
     *
     * @param fromCurrency The base currency.
     * @param toCurrency   The target currency
     * @param rate         The actual rate of base currency relative to target currency
     * @return The mapped ExchangeRateModel
     */
    ExchangeRateModel buildExchangeRateModel(String fromCurrency, String toCurrency, BigDecimal rate);
}

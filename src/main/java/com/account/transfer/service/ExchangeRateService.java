package com.account.transfer.service;

import com.account.transfer.exception.ExchangeRateServiceException;
import com.account.transfer.service.model.ExchangeRateModel;
import com.account.transfer.exception.ExchangeRateNotFoundException;

/**
 * Service for working with currency and it exchange rates.
 */
public interface ExchangeRateService {

    /**
     * Retrieves the exchange rate for the specified base and target currency.
     *
     * @param fromCurrency The base currency for which exchange rates are requested.
     * @param toCurrency   The target currency for which exchange rates are requested.
     * @return a set of exchange rate models
     * @throws ExchangeRateNotFoundException if the exchange rates for the specified currency are not found
     * @throws ExchangeRateServiceException  if the external exchange service returns an error
     */
    ExchangeRateModel getExchangeRate(String fromCurrency, String toCurrency);
}

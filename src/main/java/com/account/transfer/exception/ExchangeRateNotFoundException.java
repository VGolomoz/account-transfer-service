package com.account.transfer.exception;

import org.springframework.http.HttpStatus;

import static com.account.transfer.exception.AppErrorCode.EXCHANGE_RATE_NOT_FOUND_ERROR;

public class ExchangeRateNotFoundException extends ServiceException {

    private static final String msg = "Exchange rate for pairs [%s:%s] is not found";

    public ExchangeRateNotFoundException(String fromCurrency, String toCurrency) {
        super(EXCHANGE_RATE_NOT_FOUND_ERROR, String.format(msg, fromCurrency, toCurrency));
    }

    @Override
    public HttpStatus getErrorStatus() {
        return HttpStatus.NOT_FOUND;
    }
}

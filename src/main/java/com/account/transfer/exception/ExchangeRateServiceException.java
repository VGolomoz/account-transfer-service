package com.account.transfer.exception;

import org.springframework.http.HttpStatus;

import static com.account.transfer.exception.AppErrorCode.EXCHANGE_RATE_SERVICE_ERROR;

public class ExchangeRateServiceException extends ServiceException {


    public ExchangeRateServiceException(String message) {
        super(EXCHANGE_RATE_SERVICE_ERROR, message);
    }

    @Override
    public HttpStatus getErrorStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

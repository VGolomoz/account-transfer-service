package com.account.transfer.exception;

import org.springframework.http.HttpStatus;

import static com.account.transfer.exception.AppErrorCode.INSUFFICIENT_BALANCE_ERROR;

public class InsufficientBalanceException extends ServiceException {


    public InsufficientBalanceException(String message) {
        super(INSUFFICIENT_BALANCE_ERROR, message);
    }

    @Override
    public HttpStatus getErrorStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

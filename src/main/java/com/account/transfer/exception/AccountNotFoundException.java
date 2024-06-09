package com.account.transfer.exception;

import org.springframework.http.HttpStatus;

import static com.account.transfer.exception.AppErrorCode.ACCOUNT_NOT_FOUND_ERROR;

public class AccountNotFoundException extends ServiceException {

    public AccountNotFoundException(String msg) {
        super(ACCOUNT_NOT_FOUND_ERROR, msg);
    }

    @Override
    public HttpStatus getErrorStatus() {
        return HttpStatus.NOT_FOUND;
    }
}

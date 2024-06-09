package com.account.transfer.exception;

import org.springframework.http.HttpStatus;

import static com.account.transfer.exception.AppErrorCode.INVALID_TRANSFER_ERROR;

public class InvalidTransferException extends ServiceException {


    public InvalidTransferException(String message) {
        super(INVALID_TRANSFER_ERROR, message);
    }

    @Override
    public HttpStatus getErrorStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

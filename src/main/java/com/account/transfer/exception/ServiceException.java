package com.account.transfer.exception;


import org.springframework.http.HttpStatus;

public abstract class ServiceException extends RuntimeException {


    private static final long serialVersionUID = 6885372276464814099L;
    private static final HttpStatus DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private final ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public HttpStatus getErrorStatus() {
        return DEFAULT_STATUS;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public String toString() {
        ErrorCode errorCode = this.getErrorCode();
        return "AccountTransferException ( errorCode " + errorCode + ")";
    }

}


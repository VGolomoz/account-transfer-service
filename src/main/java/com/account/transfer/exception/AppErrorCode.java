package com.account.transfer.exception;

import lombok.Getter;

@Getter
public enum AppErrorCode implements ErrorCode {


    FIELDS_VALIDATION_ERROR("001"),
    EXCHANGE_RATE_NOT_FOUND_ERROR("002"),
    EXCHANGE_RATE_SERVICE_ERROR("003"),
    ACCOUNT_NOT_FOUND_ERROR("004"),
    INSUFFICIENT_BALANCE_ERROR("005"),
    INVALID_TRANSFER_ERROR("006");

    @Override
    public String getCode() {
        return code;
    }

    private static final String GROUP_CODE = "0";
    public final String code;

    AppErrorCode(String code) {
        this.code = GROUP_CODE + code;
    }
}

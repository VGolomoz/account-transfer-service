package com.account.transfer.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@RequiredArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Account owner id can not be empty")
    Long accountOwnerId;

    @NotNull(message = "Target account id can not be empty")
    Long targetAccountId;

    @Digits(integer = 38, fraction = 2,
            message = "The amount can have a maximum of 38 digits and 2 digits after the decimal point.")
    @DecimalMin(value = "0.01", message = "The transfer amount must be at least 0.01")
    BigDecimal amount;
}

package com.account.transfer.api.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@RequiredArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Account owner id can not be empty")
    Long accOwnerId;

    @NotNull(message = "Account target id can not be empty")
    Long accTargetId;

    @Digits(integer = 38, fraction = 2,
            message = "The amount can have a maximum of 38 digits before the decimal point and 2 digits after the decimal point.")
    BigDecimal amount;
}

package com.account.transfer.service.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AccountModel {

    Long ownerId;
    String currency;
    BigDecimal balance;
}

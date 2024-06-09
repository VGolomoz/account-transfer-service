package com.account.transfer.service.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class AccountModel {

    Long id;
    Long ownerId;
    String currency;
    BigDecimal balance;
}

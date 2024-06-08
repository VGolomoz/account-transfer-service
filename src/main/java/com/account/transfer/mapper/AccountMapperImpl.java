package com.account.transfer.mapper;

import com.account.transfer.entity.AccountEntity;
import com.account.transfer.service.model.AccountModel;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountModel mapToAccountModel(AccountEntity accountEntity) {
        if (isNull(accountEntity)) {
            return null;
        }

        return AccountModel.builder()
                .ownerId(accountEntity.getId())
                .currency(accountEntity.getCurrency())
                .balance(accountEntity.getBalance())
                .build();
    }
}

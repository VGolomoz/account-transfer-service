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
                .id(accountEntity.getId())
                .ownerId(accountEntity.getOwnerId())
                .currency(accountEntity.getCurrency())
                .balance(accountEntity.getBalance())
                .build();
    }

    @Override
    public AccountEntity mapToAccountEntity(AccountModel accountModel) {
        if (isNull(accountModel)) {
            return null;
        }

        var entity = new AccountEntity();
        entity.setId(accountModel.getId());
        entity.setOwnerId(accountModel.getOwnerId());
        entity.setCurrency(accountModel.getCurrency());
        entity.setBalance(accountModel.getBalance());

        return entity;
    }
}

package com.account.transfer.mapper;

import com.account.transfer.entity.AccountEntity;
import com.account.transfer.service.model.AccountModel;

/**
 * Service for mapping account data between different representations.
 */
public interface AccountMapper {


    /**
     * Maps an AccountEntity to an AccountModel.
     *
     * @param accountEntity The AccountEntity
     * @return The mapped AccountModel
     */
    AccountModel mapToAccountModel(AccountEntity accountEntity);
}

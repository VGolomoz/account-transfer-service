package com.account.transfer.service;

import com.account.transfer.exception.AccountNotFoundException;
import com.account.transfer.service.model.AccountModel;

/**
 * Service interface for managing accounts.
 */
public interface AccountService {

    /**
     * Retrieves an account by the owner's ID.
     *
     * @param ownerId the ID of the account owner.
     * @return the {@link AccountModel} representing the account.
     * @throws AccountNotFoundException if the account by the owner's ID is not found
     */
    AccountModel getAccountByOwnerId(Long ownerId);

    /**
     * Saves the provided account model.
     *
     * @param accountModel the {@link AccountModel} to save.
     */
    void save(AccountModel accountModel);
}

package com.account.transfer.service;

import com.account.transfer.service.model.TransactionModel;
import com.account.transfer.exception.InvalidTransferException;
import com.account.transfer.exception.InsufficientBalanceException;

import java.math.BigDecimal;

/**
 * Service interface for managing transactions.
 */
public interface TransactionService {

    /**
     * Performs a transfer of a specified amount from one account to another.
     * The transfer can be either in the same currency or in different currencies,
     * applying the appropriate exchange rate if necessary.
     *
     * @param accountOwnerId the ID of the account from which the money will be transferred.
     * @param targetAccountId the ID of the account to which the money will be transferred.
     * @param amount the amount of money to transfer.
     * @return the {@link TransactionModel} representing the transaction.
     * @throws InvalidTransferException if the transfer is attempted to the same account.
     * @throws InsufficientBalanceException if the balance is insufficient for the transfer.
     */
    TransactionModel performTransfer(Long accountOwnerId, Long targetAccountId, BigDecimal amount);
}

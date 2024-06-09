package com.account.transfer.mapper;

import com.account.transfer.api.dto.TransactionResponse;
import com.account.transfer.entity.TransactionEntity;
import com.account.transfer.entity.TransactionStatus;
import com.account.transfer.service.model.AccountModel;
import com.account.transfer.service.model.TransactionModel;

import java.math.BigDecimal;

/**
 * Service for mapping account transaction data between different representations.
 */
public interface TransactionMapper {

    /**
     * Maps an instance of {@link TransactionEntity} to an instance of {@link TransactionModel}.
     *
     * @param entity The TransactionEntity
     * @return The mapped TransactionModel
     */
    TransactionModel mapToTransactionModel(TransactionEntity entity);

    /**
     * Maps an instance of {@link TransactionModel} to an instance of {@link TransactionResponse}.
     *
     * @param model The TransactionModel
     * @return The mapped TransactionResponse
     */
    TransactionResponse mapToTransactionResponse(TransactionModel model);

    /**
     * Constructs a {@link TransactionEntity} object with the provided details.
     *
     * @param accountOwner    The source account from which the funds are transferred.
     *                        An instance of {@link AccountModel}.
     * @param targetAccount   The target account to which the funds are transferred.
     *                        An instance of {@link AccountModel}.
     * @param amount          The amount of money to transfer. An instance of {@link BigDecimal}.
     * @param status          The status of the transaction. An instance of {@link TransactionStatus}.
     * @param residualBalance The balance remaining in the source account after the transaction.
     *                        An instance of {@link BigDecimal}.
     * @param exchangeRate    The exchange rate used for the transaction if currency conversion is involved.
     *                        An instance of {@link BigDecimal}.
     * @return A {@link TransactionEntity} populated with the provided details.
     */
    TransactionEntity buildTransactionEntity(AccountModel accountOwner, AccountModel targetAccount, BigDecimal amount,
                                             TransactionStatus status, BigDecimal residualBalance, BigDecimal exchangeRate);
}

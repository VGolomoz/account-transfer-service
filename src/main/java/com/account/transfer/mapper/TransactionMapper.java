package com.account.transfer.mapper;

import com.account.transfer.api.dto.TransactionResponse;
import com.account.transfer.entity.TransactionEntity;
import com.account.transfer.service.model.TransactionModel;

/**
 * Service for mapping account transaction data between different representations.
 */
public interface TransactionMapper {

    /**
     * Maps an TransactionEntity to an TransactionModel.
     *
     * @param entity The TransactionEntity
     * @return The mapped TransactionModel
     */
    TransactionModel mapToTransactionModel(TransactionEntity entity);

    /**
     * Maps an TransferModel to an TransferResponse.
     *
     * @param model The TransferModel
     * @return The mapped TransferResponse
     */
    TransactionResponse mapToTransactionResponse(TransactionModel model);
}

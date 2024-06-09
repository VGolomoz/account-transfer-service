package com.account.transfer.api.controller;

import com.account.transfer.api.dto.TransactionRequest;
import com.account.transfer.api.dto.TransactionResponse;
import com.account.transfer.mapper.TransactionMapper;
import com.account.transfer.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing money transfers between accounts.
 * Handles HTTP requests related to transfers.
 *
 * Utilizes {@link TransactionService} to perform transfer logic
 * and {@link TransactionMapper} to transform data.
 */
@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    /**
     * Executes a transfer of funds from one account to another.
     *
     * @param body A {@link TransactionRequest} object containing the transfer details.
     * @return A {@link ResponseEntity} containing the {@link TransactionResponse} with the transfer result.
     * @throws IllegalArgumentException if the request data is invalid.
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> performTransfer(@Valid @RequestBody TransactionRequest body) {
        var transactionModel = transactionService.performTransfer(
                body.getAccountOwnerId(), body.getTargetAccountId(), body.getAmount());

        return ResponseEntity.ok(transactionMapper.mapToTransactionResponse(transactionModel));
    }
}

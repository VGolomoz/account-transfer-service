package com.account.transfer.api.controller;

import com.account.transfer.api.dto.ErrorResponse;
import com.account.transfer.api.dto.TransactionRequest;
import com.account.transfer.api.dto.TransactionResponse;
import com.account.transfer.mapper.TransactionMapper;
import com.account.transfer.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * <p>
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
     * POST /account-transfer-service/transfer
     *
     * @param body A {@link TransactionRequest} object containing the transfer details (required).
     * @return A {@link ResponseEntity} containing the {@link TransactionResponse} with the transfer result.
     * or Bad request (status code 400)
     * or Not found (status code 404)
     * or Internal server error (status code 500)
     */
    @Operation(
            operationId = "performTransfer",
            summary = "Executes a transfer of funds between accounts",
            description = "Transfers funds from one account to another. Throws specific exceptions for invalid or failed transfers.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful transfer", content =
                        @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content =
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Account not found", content =
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content =
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<TransactionResponse> performTransfer(@Valid @RequestBody TransactionRequest body) {
        var transactionModel = transactionService.performTransfer(
                body.getAccountOwnerId(), body.getTargetAccountId(), body.getAmount());

        return ResponseEntity.ok(transactionMapper.mapToTransactionResponse(transactionModel));
    }
}

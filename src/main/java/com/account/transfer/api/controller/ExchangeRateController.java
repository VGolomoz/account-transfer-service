package com.account.transfer.api.controller;

import com.account.transfer.api.dto.ErrorResponse;
import com.account.transfer.api.dto.ExchangeRateResponse;
import com.account.transfer.mapper.ExchangeRateMapper;
import com.account.transfer.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling requests related to currency exchange rates.
 * Manages the retrieval of exchange rates between different currencies.
 * <p>
 * Utilizes {@link ExchangeRateService} to obtain exchange rates and
 * {@link ExchangeRateMapper} to transform data for response.
 */
@RestController
@RequestMapping("/exchange-rate")
@RequiredArgsConstructor
@Validated
public class ExchangeRateController {

    private final ExchangeRateService service;
    private final ExchangeRateMapper mapper;

    /**
     * GET /account-transfer-service/exchange-rate
     *
     * @param fromCurrency The source currency code, e.g., "USD".
     * @param toCurrency   The target currency code, e.g., "EUR".
     * @return A {@link ResponseEntity} containing the {@link ExchangeRateResponse} with the exchange rate details.
     * or Bad request (status code 400)
     * or Not found (status code 404)
     * or Internal server error (status code 500)
     */
    @Operation(
            operationId = "getExchangeRate",
            summary = "Retrieves the exchange rate between two specified currencies",
            description = "Fetches the exchange rate for the given currency pair. Throws specific exceptions for invalid or unsupported currencies.",
            parameters = {
                    @Parameter(
                            name = "fromCurrency",
                            description = "The source currency code (e.g., USD)",
                            required = true,
                            example = "USD",
                            schema = @Schema(type = "string", pattern = "[A-Za-z]{3}", description = "3-letter currency code")
                    ),
                    @Parameter(
                            name = "toCurrency",
                            description = "The target currency code (e.g., EUR)",
                            required = true,
                            example = "EUR",
                            schema = @Schema(type = "string", pattern = "[A-Za-z]{3}", description = "3-letter currency code")
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rate", content =
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ExchangeRateResponse.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Invalid currency codes", content =
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Exchange rate not found for the specified currencies",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content =
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(
            @RequestParam(name = "fromCurrency")
            @Pattern(regexp = "[A-Za-z]{3}", message = "Parameter 'fromCurrency' must be 3 latin letters")
            @Valid String fromCurrency,

            @RequestParam(name = "toCurrency")
            @Pattern(regexp = "[A-Za-z]{3}", message = "Parameter 'toCurrency' must be 3 latin letters")
            @Valid String toCurrency) {

        var exchangeRateModel = service.getExchangeRate(fromCurrency.toUpperCase(), toCurrency.toUpperCase());
        return ResponseEntity.ok(mapper.mapToExchangeRateResponse(exchangeRateModel));
    }
}

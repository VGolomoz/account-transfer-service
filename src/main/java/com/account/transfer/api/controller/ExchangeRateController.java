package com.account.transfer.api.controller;

import com.account.transfer.api.dto.ExchangeRateResponse;
import com.account.transfer.exception.ExchangeRateNotFoundException;
import com.account.transfer.mapper.ExchangeRateMapper;
import com.account.transfer.service.ExchangeRateService;
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
     * Retrieves the exchange rate between two specified currencies.
     *
     * @param fromCurrency The source currency code, e.g., "USD".
     * @param toCurrency   The target currency code, e.g., "EUR".
     * @return A {@link ResponseEntity} containing the {@link ExchangeRateResponse} with the exchange rate details.
     * @throws IllegalArgumentException      if the provided currency codes are invalid or not supported.
     * @throws ExchangeRateNotFoundException if the provided currency codes are not found.
     */
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

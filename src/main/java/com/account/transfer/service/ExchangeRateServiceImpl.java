package com.account.transfer.service;

import com.account.transfer.api.dto.ExchangeRateSourceResponse;
import com.account.transfer.config.ExchangerateApiProperties;
import com.account.transfer.exception.ExchangeRateNotFoundException;
import com.account.transfer.exception.ExchangeRateServiceException;
import com.account.transfer.mapper.ExchangeRateMapper;
import com.account.transfer.service.model.ExchangeRateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final RestTemplate restTemplate;
    private final ExchangerateApiProperties properties;
    private final ExchangeRateMapper mapper;

    @Override
    public ExchangeRateModel getExchangeRate(String fromCurrency, String toCurrency) {
        log.info("Fetch latest exchange rate from host: {}, for currency: {}, to currency: {}",
                properties.getHost(), fromCurrency, toCurrency);
        var apiFullPath = properties.getUrl() + fromCurrency;

        ResponseEntity<ExchangeRateSourceResponse> response =
                restTemplate.getForEntity(apiFullPath, ExchangeRateSourceResponse.class);

        if (HttpStatus.OK == response.getStatusCode()) {
            var rate = Optional.ofNullable(response.getBody())
                    .map(ExchangeRateSourceResponse::getRates)
                    .map(rates -> rates.get(toCurrency))
                    .orElseThrow(() -> new ExchangeRateNotFoundException(fromCurrency, toCurrency));

            log.info("Current exchange rate: {}, for pair: [{}:{}]", rate, fromCurrency, toCurrency);
            return mapper.buildExchangeRateModel(fromCurrency, toCurrency, rate);
        }

        var exceptionMsg = String.format("Fetch latest exchange rate from=%s for currency=%s " +
                "failed with status code=%s", properties.getHost(), fromCurrency, response.getStatusCode());
        throw new ExchangeRateServiceException(exceptionMsg);
    }
}

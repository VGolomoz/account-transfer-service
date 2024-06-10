package com.account.transfer.service;

import com.account.transfer.api.dto.ExchangeRateSourceResponse;
import com.account.transfer.config.ExchangerateApiProperties;
import com.account.transfer.exception.ExchangeRateNotFoundException;
import com.account.transfer.exception.ExchangeRateServiceException;
import com.account.transfer.mapper.ExchangeRateMapper;
import com.account.transfer.service.model.ExchangeRateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
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

        try {
            ResponseEntity<ExchangeRateSourceResponse> response =
                    restTemplate.getForEntity(apiFullPath, ExchangeRateSourceResponse.class);

            var rate = Optional.ofNullable(response.getBody())
                    .filter(it -> fromCurrency.equals(it.getBase()))
                    .map(ExchangeRateSourceResponse::getRates)
                    .map(rates -> rates.get(toCurrency))
                    .orElseThrow(() -> new ExchangeRateNotFoundException(fromCurrency, toCurrency));

            log.info("Current exchange rate: {}, for pair: [{}:{}]", rate, fromCurrency, toCurrency);
            return mapper.buildExchangeRateModel(fromCurrency, toCurrency, rate);

        } catch (RestClientException ex) {
            if (ex instanceof HttpClientErrorException.NotFound) {
                throw new ExchangeRateNotFoundException(fromCurrency, toCurrency);
            }

            var exceptionMsg = String.format("Fetch latest exchange rate from=%s for currency=%s " +
                    "failed by reason=%s", properties.getHost(), fromCurrency, ex.getMessage());
            throw new ExchangeRateServiceException(exceptionMsg);
        }
    }
}

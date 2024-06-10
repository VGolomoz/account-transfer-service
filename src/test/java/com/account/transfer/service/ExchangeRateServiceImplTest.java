package com.account.transfer.service;

import com.account.transfer.api.dto.ExchangeRateSourceResponse;
import com.account.transfer.config.ExchangerateApiProperties;
import com.account.transfer.exception.ExchangeRateNotFoundException;
import com.account.transfer.exception.ExchangeRateServiceException;
import com.account.transfer.mapper.ExchangeRateMapper;
import com.account.transfer.service.model.ExchangeRateModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ExchangeRateServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExchangerateApiProperties properties;

    @Mock
    private ExchangeRateMapper mapper;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    private static final String TEST_API_URI = "https://api.test.com/exchangerate/";
    private static final String TEST_HOST = "api.test.com";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetExchangeRate_Success() {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);

        ExchangeRateSourceResponse sourceResponse = prepareExchangeRateSourceResponse(fromCurrency, toCurrency, expectedRate);
        ResponseEntity<ExchangeRateSourceResponse> responseEntity = new ResponseEntity<>(sourceResponse, HttpStatus.OK);
        ExchangeRateModel expectedExchangeRateModel = prepareExchangeRateModel(fromCurrency, toCurrency, expectedRate);

        when(properties.getUrl()).thenReturn(TEST_API_URI);
        when(properties.getHost()).thenReturn(TEST_HOST);
        when(restTemplate.getForEntity(TEST_API_URI + fromCurrency, ExchangeRateSourceResponse.class))
                .thenReturn(responseEntity);
        when(mapper.buildExchangeRateModel(fromCurrency, toCurrency, expectedRate)).thenReturn(expectedExchangeRateModel);

        // when
        ExchangeRateModel result = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);

        // then
        assertNotNull(result);
        assertEquals(fromCurrency, result.getFromCurrency());
        assertEquals(toCurrency, result.getToCurrency());
        assertEquals(expectedRate, result.getRate());

        verify(properties, times(1)).getUrl();
        verify(properties, times(1)).getHost();
        verify(restTemplate, times(1)).getForEntity(TEST_API_URI + fromCurrency, ExchangeRateSourceResponse.class);
        verify(mapper, times(1)).buildExchangeRateModel(fromCurrency, toCurrency, expectedRate);
    }

    @Test
    public void testGetExchangeRate_RateNotFound_when_ResponseBody_isNull() {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        String expectedMessage = String.format("Exchange rate for pairs [%s:%s] is not found", fromCurrency, toCurrency);

        ResponseEntity<ExchangeRateSourceResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(properties.getUrl()).thenReturn(TEST_API_URI);
        when(properties.getHost()).thenReturn(TEST_HOST);
        when(restTemplate.getForEntity(TEST_API_URI + fromCurrency, ExchangeRateSourceResponse.class))
                .thenReturn(responseEntity);

        // when
        Throwable exception = Assertions.assertThrows(ExchangeRateNotFoundException.class,
                () -> exchangeRateService.getExchangeRate(fromCurrency, toCurrency));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verifyNoInteractions(mapper);
    }

    @Test
    public void testGetExchangeRate_RateNotFound_when_baseCurrency_isInvalid() {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        String invalidBaseCurrency = "GBP";
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);
        String expectedMessage = String.format("Exchange rate for pairs [%s:%s] is not found", fromCurrency, toCurrency);

        ExchangeRateSourceResponse sourceResponse =
                prepareExchangeRateSourceResponse(invalidBaseCurrency, toCurrency, expectedRate);

        ResponseEntity<ExchangeRateSourceResponse> responseEntity = new ResponseEntity<>(sourceResponse, HttpStatus.OK);

        when(properties.getUrl()).thenReturn(TEST_API_URI);
        when(properties.getHost()).thenReturn(TEST_HOST);
        when(restTemplate.getForEntity(TEST_API_URI + fromCurrency, ExchangeRateSourceResponse.class))
                .thenReturn(responseEntity);

        // when
        Throwable exception = Assertions.assertThrows(ExchangeRateNotFoundException.class,
                () -> exchangeRateService.getExchangeRate(fromCurrency, toCurrency));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verifyNoInteractions(mapper);
    }

    @Test
    public void testGetExchangeRate_RateNotFound_when_toCurrency_isAbsent() {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        String responseCurrency = "GBP";
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);
        String expectedMessage = String.format("Exchange rate for pairs [%s:%s] is not found", fromCurrency, toCurrency);

        ExchangeRateSourceResponse sourceResponse =
                prepareExchangeRateSourceResponse(fromCurrency, responseCurrency, expectedRate);

        ResponseEntity<ExchangeRateSourceResponse> responseEntity = new ResponseEntity<>(sourceResponse, HttpStatus.OK);

        when(properties.getUrl()).thenReturn(TEST_API_URI);
        when(properties.getHost()).thenReturn(TEST_HOST);
        when(restTemplate.getForEntity(TEST_API_URI + fromCurrency, ExchangeRateSourceResponse.class))
                .thenReturn(responseEntity);

        // when
        Throwable exception = Assertions.assertThrows(ExchangeRateNotFoundException.class,
                () -> exchangeRateService.getExchangeRate(fromCurrency, toCurrency));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verifyNoInteractions(mapper);
    }

    @Test
    public void testGetExchangeRate_FailedToFetchRate() {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        ResponseEntity<ExchangeRateSourceResponse> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        String expectedMessage = String.format("Fetch latest exchange rate from=%s for currency=%s " +
                "failed with status code=%s", TEST_HOST, fromCurrency, responseEntity.getStatusCode());

        when(properties.getUrl()).thenReturn(TEST_API_URI);
        when(properties.getHost()).thenReturn(TEST_HOST);
        when(restTemplate.getForEntity(TEST_API_URI + fromCurrency, ExchangeRateSourceResponse.class))
                .thenReturn(responseEntity);

        // when
        Throwable exception = Assertions.assertThrows(ExchangeRateServiceException.class,
                () -> exchangeRateService.getExchangeRate(fromCurrency, toCurrency));

        // then
        assertEquals(expectedMessage, exception.getMessage());

        verifyNoInteractions(mapper);
    }

    private ExchangeRateSourceResponse prepareExchangeRateSourceResponse(String fromCurrency, String toCurrency,
                                                                         BigDecimal rate) {
        Map<String, BigDecimal> rates = new HashMap<>() {{
            put(toCurrency, rate);
        }};

        ExchangeRateSourceResponse response = new ExchangeRateSourceResponse();
        response.setBase(fromCurrency);
        response.setRates(rates);

        return response;
    }

    private ExchangeRateModel prepareExchangeRateModel(String fromCurrency, String toCurrency, BigDecimal expectedRate) {
        return ExchangeRateModel.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(expectedRate)
                .dateTime(ZonedDateTime.now())
                .build();
    }
}
package com.account.transfer.mapper;

import com.account.transfer.api.dto.ExchangeRateResponse;
import com.account.transfer.service.model.ExchangeRateModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateMapperImplTest {

    private final ExchangeRateMapperImpl exchangeRateMapper = new ExchangeRateMapperImpl();

    @Test
    public void testMapToExchangeRateResponse_ValidModel() {
        // given
        ExchangeRateModel model = ExchangeRateModel.builder()
                .fromCurrency("USD")
                .toCurrency("EUR")
                .rate(BigDecimal.valueOf(0.85))
                .dateTime(ZonedDateTime.now())
                .build();

        // when
        ExchangeRateResponse response = exchangeRateMapper.mapToExchangeRateResponse(model);

        // then
        assertNotNull(response);

        assertEquals(model.getFromCurrency(), response.getFromCurrency());
        assertEquals(model.getToCurrency(), response.getToCurrency());
        assertEquals(model.getRate(), response.getRate());
        assertEquals(model.getDateTime(), response.getDateTime());
    }

    @Test
    public void testMapToExchangeRateResponse_NullModel() {
        // when
        ExchangeRateResponse response = exchangeRateMapper.mapToExchangeRateResponse(null);

        // then
        assertNull(response);
    }

    @Test
    public void testBuildExchangeRateModel() {
        // given
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal rate = BigDecimal.valueOf(0.85);

        // when
        ExchangeRateModel model = exchangeRateMapper.buildExchangeRateModel(fromCurrency, toCurrency, rate);

        // then
        assertNotNull(model);

        assertEquals(fromCurrency, model.getFromCurrency());
        assertEquals(toCurrency, model.getToCurrency());
        assertEquals(rate, model.getRate());
        assertNotNull(model.getDateTime());
    }

}
package com.account.transfer.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateSourceResponse {

    @JsonProperty("base_code")
    private String base;
    @JsonProperty("conversion_rates")
    private Map<String, BigDecimal> rates = new HashMap<>();
}

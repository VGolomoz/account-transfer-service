package com.account.transfer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "external.service.currency-exchange.exchangerate-api")
public class ExchangerateApiProperties {

    private String host;
    private String url;

}

package com.account.transfer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the external exchangerate-api service.
 * <p>
 * This class holds the configuration properties required to connect to the
 * exchangerate-api, such as the host and URL. The properties are prefixed with
 * `external.service.currency-exchange.exchangerate-api` in the configuration file.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "external.service.currency-exchange.exchangerate-api")
public class ExchangerateApiProperties {

    /**
     * The host address for the exchangerate-api.
     */
    private String host;

    /**
     * The base URL for accessing the exchangerate-api.
     */
    private String url;

}

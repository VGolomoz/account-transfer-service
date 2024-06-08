package com.account.transfer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the Account Transfer Service.
 * <p>
 * Enables configuration properties for the application
 */
@Configuration
@EnableConfigurationProperties({
        ExchangerateApiProperties.class
})
public class AccountTransferAppConfiguration {
    // No additional methods or fields required.
    // This class serves to enable and manage configuration properties.
}

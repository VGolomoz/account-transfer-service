package com.account.transfer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ExchangerateApiProperties.class
})
public class AccountTransferAppConfiguration {
}

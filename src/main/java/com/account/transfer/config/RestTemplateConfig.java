package com.account.transfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for creating and configuring a {@link RestTemplate} bean.
 * <p>
 * Provides a {@link RestTemplate} for use in the application to make HTTP requests.
 * Additional HTTP client configuration, such as SSL settings, can be added here if needed.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a {@link RestTemplate} bean.
     *
     * @return a new {@link RestTemplate} instance.
     */
    @Bean
    public RestTemplate restTemplate() {
        // Add any necessary HTTP client configuration, such as SSL configuration, here.
        return new RestTemplate();
    }
}

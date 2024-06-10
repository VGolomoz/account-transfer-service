package com.account.transfer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {

    @Bean
    public OpenAPI accountTransferOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Account Transfer API")
                        .version("1.0")
                        .description("API documentation for the Account Transfer Service."));
    }
}

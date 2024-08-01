package com.example.rqchallenge.employees.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CloseableHttpClient getCloseableHttpClient() {
        return HttpClients.createDefault();
    }
}

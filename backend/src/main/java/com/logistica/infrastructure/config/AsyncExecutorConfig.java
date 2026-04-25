package com.logistica.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncExecutorConfig {

    @Bean(destroyMethod = "close")
    public ExecutorService packageApiExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

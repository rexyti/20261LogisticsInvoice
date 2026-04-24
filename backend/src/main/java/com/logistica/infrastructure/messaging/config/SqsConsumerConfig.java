package com.logistica.infrastructure.messaging.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.*;
import io.awspring.cloud.sqs.listener.errorhandler.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Configuration
public class SqsConsumerConfig {

    @Bean
    public SqsMessageListenerContainerFactory<Object> sqsListenerContainerFactory(
            SqsAsyncClient sqsAsyncClient) {

        return SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .maxConcurrentMessages(10)
                        .maxMessagesPerPoll(10)
                )
                .build();
    }
}
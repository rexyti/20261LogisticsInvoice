package com.logistica.cierreRuta.infrastructure.messaging.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Configuration
@ConditionalOnProperty(
        name = "spring.cloud.aws.sqs.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class CierreRutaSqsConsumerConfig {

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(
            SqsAsyncClient sqsAsyncClient) {

        return SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .maxConcurrentMessages(10)
                        .maxMessagesPerPoll(10)
                        .acknowledgementMode(AcknowledgementMode.MANUAL)
                )
                .build();
    }
}

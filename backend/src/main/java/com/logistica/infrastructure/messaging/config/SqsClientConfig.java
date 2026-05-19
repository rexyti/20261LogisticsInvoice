package com.logistica.infrastructure.messaging.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder;

import java.net.URI;

@Slf4j
@Configuration
@ConditionalOnProperty(
        name = "spring.cloud.aws.sqs.enabled",
        havingValue = "true"
)
public class SqsClientConfig {

    @Value("${spring.cloud.aws.sqs.endpoint:}")
    private String sqsEndpoint;

    @Value("${spring.cloud.aws.region.static:us-east-1}")
    private String region;

    @Value("${spring.cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key:}")
    private String secretKey;

    /**
     * Bean productivo de SqsAsyncClient.
     *
     * @ConditionalOnMissingBean garantiza que si un @TestConfiguration ya registró
     * su propio SqsAsyncClient (con @Primary), este bean no se crea y el de test
     * gana. En producción/local no existe ningún otro bean, por lo que este se activa.
     *
     * Endpoint override: si la propiedad está vacía (AWS real) se omite el override,
     * dejando al cliente apuntar a los endpoints públicos de AWS.
     */
    @Bean
    @ConditionalOnMissingBean(SqsAsyncClient.class)
    public SqsAsyncClient sqsAsyncClient() {
        boolean usaEndpointCustom = StringUtils.hasText(sqsEndpoint);

        log.info("Iniciando SqsAsyncClient — región: '{}' | endpoint: {}",
                region,
                usaEndpointCustom ? sqsEndpoint : "AWS (producción)");

        SqsAsyncClientBuilder builder = SqsAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)));

        if (usaEndpointCustom) {
            builder.endpointOverride(URI.create(sqsEndpoint));
        }

        return builder.build();
    }
}

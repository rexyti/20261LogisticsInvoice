package com.logistica.cierreRuta.infrastructure.messaging.consumers;

import com.logistica.cierreRuta.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.cierreRuta.application.usecases.ruta.ProcesarRutaCerradaUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
class RutaCerradaConsumerIT {

    private static final String QUEUE_NAME = "ruta-cerrada-queue";

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.3.0")
    ).withServices(LocalStackContainer.Service.SQS)
     .withStartupTimeout(Duration.ofMinutes(2));

    @TestConfiguration
    static class SqsTestConfig {

        // @Primary garantiza que este bean gana sobre cualquier otro SqsAsyncClient
        // que la auto-configuración pudiera intentar crear.
        @Bean
        @Primary
        public SqsAsyncClient sqsAsyncClient() {
            return SqsAsyncClient.builder()
                    .endpointOverride(localStack.getEndpoint())
                    .region(Region.of(localStack.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    localStack.getAccessKey(),
                                    localStack.getSecretKey())))
                    .build();
        }
    }

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @MockBean
    private ProcesarRutaCerradaUseCase procesarRutaCerradaUseCase;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // La cola se crea con la CLI interna de LocalStack: no se abre ni cierra
        // ningún SqsAsyncClient Java aquí, evitando que el shutdown de Netty rompa
        // el event loop del cliente que creará el @TestConfiguration.
        try {
            localStack.execInContainer(
                    "awslocal", "sqs", "create-queue", "--queue-name", QUEUE_NAME
            );
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear la cola SQS en LocalStack", e);
        }

        registry.add("spring.cloud.aws.sqs.endpoint",
                () -> localStack.getEndpoint().toString());
        registry.add("spring.cloud.aws.region.static",
                localStack::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key",
                localStack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key",
                localStack::getSecretKey);
        registry.add("spring.cloud.aws.sqs.enabled", () -> "true");
        registry.add("app.sqs.queue.ruta-cerrada", () -> QUEUE_NAME);
    }

    @Test
    void debeConsumirMensajeDeSqsYEjecutarUseCase() throws Exception {

        String rutaId = UUID.randomUUID().toString();
        String vehiculoId = UUID.randomUUID().toString();
        String conductorId = UUID.randomUUID().toString();
        String paradaId = UUID.randomUUID().toString();
        String paqueteId = UUID.randomUUID().toString();

        String mensajeJson = """
        {
          "ruta_id": "%s",
          "tipo_evento": "RUTA_CERRADA",
          "fecha_hora_inicio_transito": "2026-04-28T10:00:00",
          "fecha_hora_cierre": "2026-04-28T12:00:00",
          "vehiculo": {
            "vehiculo_id": "%s",
            "tipo": "CAMION_PESADO"
          },
          "conductor": {
            "conductor_id": "%s",
            "nombre": "Carlos Perez",
            "modelo_contrato": "TERCERIZADO"
          },
          "paradas": [
            {
              "parada_id": "%s",
              "paquete_id": "%s",
              "estado": "EXITOSA"
            }
          ]
        }
        """.formatted(rutaId, vehiculoId, conductorId, paradaId, paqueteId);

        // Envío vía SDK directo (sin SqsTemplate): simula un productor externo que
        // envía JSON puro sin atributos contentType de Spring.
        String queueUrl = sqsAsyncClient
                .getQueueUrl(r -> r.queueName(QUEUE_NAME))
                .get()
                .queueUrl();

        sqsAsyncClient.sendMessage(r -> r
                .queueUrl(queueUrl)
                .messageBody(mensajeJson)
        ).get();

        verify(procesarRutaCerradaUseCase, timeout(Duration.ofSeconds(10).toMillis()))
                .ejecutar(any(RutaCerradaEventDTO.class));
    }
}

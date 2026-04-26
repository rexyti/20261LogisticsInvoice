# Implementation Plan: Registro/Sincronización del estado de paquete para facturación

**Date**: 2026-04-25  
**Branch objetivo**: `feature/mod3-Registrar-Estado-Pago`  
**Repositorio**: `InsoftUnimag/20261LogisticsInvoice`  
**Backend base**: `backend/`  
**Spec relacionada**: `Registrar estado del pago.md`  

## Summary

Esta versión del plan queda alineada con la estructura real del backend existente en la rama `feature/mod3-Registrar-Estado-Pago`. El backend actual no implementa entidades llamadas `Pago`, `EstadoPago` o `EventoTransaccion`; la implementación existente modela el flujo a partir del estado del paquete, su historial y los logs de sincronización. Por tanto, este plan adapta el objetivo funcional del registro/actualización de estado para que coincida con los archivos reales del proyecto:

- sincronizar el estado de un paquete asociado a una ruta;
- consultar el historial de estados del paquete;
- consultar los logs técnicos/funcionales de sincronización;
- proteger la consistencia del estado mediante reglas de dominio;
- mantener separación por capas bajo una arquitectura limpia ya presente en el backend: `application`, `domain`, `infrastructure` y `shared`.

El flujo actual queda entendido como una integración operativa necesaria para el módulo de facturación/liquidación: el estado del paquete es la fuente para determinar avances, novedades y condiciones que luego impactan la liquidación o el estado financiero asociado.

## Technical Context

**Language/Version**: Java 21  
**Framework**: Spring Boot 3.3.5  
**Build Tool**: Gradle  
**Primary Dependencies**: Spring Web, Spring Data JPA, Spring Cloud OpenFeign, PostgreSQL Driver, Validation, Resilience4j, Lombok  
**Storage**: PostgreSQL 15  
**Testing**: JUnit 5, Mockito, Spring Boot Test, H2 para perfil de pruebas  
**Target Platform**: Local development / AWS-ready  
**Project Type**: Backend REST API  

## Performance Goals

- Responder las consultas REST sin acoplar el controlador a lógica de infraestructura.
- Registrar intentos de sincronización con trazabilidad suficiente para soporte.
- Evitar duplicación innecesaria de historial cuando el estado no cambia.
- Mantener el backend preparado para concurrencia mediante control de versión en la entidad persistente de paquete.

## Constraints

- La estructura del plan debe coincidir con el backend real del repositorio.
- No se deben introducir paquetes ni archivos inexistentes como `PagoEntity`, `EstadoPagoEntity`, `EventoEntity`, `WebhookPagoController` o `PagoEventProcessor` mientras no existan en el código real.
- La lógica de dominio debe mantenerse fuera de controladores y adaptadores HTTP.
- Las dependencias externas deben permanecer encapsuladas en `infrastructure/http`.
- La persistencia debe permanecer encapsulada en `infrastructure/persistence` y adaptadores de repositorio.

## Project Structure

### Repository root

```text
20261LogisticsInvoice/
├── .ai/
│   └── mcp/
├── .claude/
├── .idea/
├── Prototipo/
├── Specs/
├── backend/
├── README.md
├── Sistema financiero modulo 3.drawio (1).drawio.png
├── StakeHolders.md
└── gitflow-modulo3-pagosLiquidación.png
```

### Backend root

```text
backend/
├── .gradle/
├── .idea/
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── logistica/
│   │   │           ├── LogisticaApplication.java
│   │   │           ├── application/
│   │   │           ├── domain/
│   │   │           ├── infrastructure/
│   │   │           └── shared/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── logistica/
│       │           ├── infrastructure/
│       │           ├── integration/
│       │           └── unit/
│       └── resources/
│           └── application-test.yml
├── build.gradle
├── gradlew
├── gradlew.bat
└── settings.gradle
```

### Backend source structure

```text
backend/src/main/java/com/logistica/
├── LogisticaApplication.java
│
├── application/
│   ├── dtos/
│   │   ├── request/
│   │   │   ├── EventoEstadoPagoRequestDTO.java
│   │   │   └── ActualizarEstadoPagoRequestDTO.java
│   │   │
│   │   └── response/
│   │       ├── EstadoPagoResponseDTO.java
│   │       ├── EventoTransaccionResponseDTO.java
│   │       ├── PagoResponseDTO.java
│   │       └── RecepcionEventoPagoResponseDTO.java
│   │
│   └── usecases/
│       └── pago/
│           ├── RecibirEventoPagoUseCase.java
│           ├── ProcesarEventoPagoUseCase.java
│           ├── RegistrarEstadoPagoUseCase.java
│           ├── ActualizarEstadoPagoUseCase.java
│           ├── ObtenerEstadoPagoUseCase.java
│           ├── ObtenerEventosTransaccionUseCase.java
│           └── PagoService.java
│
├── domain/
│   ├── enums/
│   │   ├── EstadoPagoEnum.java
│   │   ├── EstadoEventoTransaccion.java
│   │   └── TipoEventoPago.java
│   │
│   ├── models/
│   │   ├── Pago.java
│   │   ├── EstadoPago.java
│   │   ├── EventoTransaccion.java
│   │   └── LiquidacionReferencia.java
│   │
│   ├── repositories/
│   │   ├── PagoRepository.java
│   │   ├── EstadoPagoRepository.java
│   │   ├── EventoTransaccionRepository.java
│   │   └── LiquidacionRepository.java
│   │
│   └── services/
│       ├── EstadoPagoDomainService.java
│       ├── IdempotenciaEventoPagoService.java
│       └── TransicionEstadoPagoService.java
│
├── infrastructure/
│   ├── adapters/
│   │   ├── PagoRepositoryAdapter.java
│   │   ├── EstadoPagoRepositoryAdapter.java
│   │   ├── EventoTransaccionRepositoryAdapter.java
│   │   ├── LiquidacionRepositoryAdapter.java
│   │   └── PagoMapper.java
│   │
│   ├── async/
│   │   ├── config/
│   │   │   └── AsyncPagoConfig.java
│   │   │
│   │   ├── listeners/
│   │   │   └── EventoPagoListener.java
│   │   │
│   │   └── processors/
│   │       └── EventoPagoProcessor.java
│   │
│   ├── config/
│   │   ├── JpaConfig.java
│   │   └── WebhookConfig.java
│   │
│   ├── persistence/
│   │   ├── entities/
│   │   │   ├── PagoEntity.java
│   │   │   ├── EstadoPagoEntity.java
│   │   │   ├── EventoTransaccionEntity.java
│   │   │   └── LiquidacionReferenciaEntity.java
│   │   │
│   │   └── repositories/
│   │       ├── PagoJpaRepository.java
│   │       ├── EstadoPagoJpaRepository.java
│   │       ├── EventoTransaccionJpaRepository.java
│   │       └── LiquidacionJpaRepository.java
│   │
│   └── web/
│       ├── controllers/
│       │   ├── WebhookPagoController.java
│       │   └── PagoController.java
│       │
│       └── handlers/
│           └── GlobalExceptionHandler.java
│
└── shared/
    ├── constants/
    │   └── AppConstants.java
    │
    └── exceptions/
        ├── LiquidacionNoEncontradaException.java
        ├── PagoNoEncontradoException.java
        ├── EstadoPagoInvalidoException.java
        ├── TransicionEstadoPagoInvalidaException.java
        ├── EventoDuplicadoException.java
        └── EventoPagoNoProcesableException.java
        
        

### Backend test structure

```text
backend/src/test/java/com/logistica/
├── infrastructure/
│   └── web/
│       └── controllers/
│           ├── WebhookPagoControllerTest.java
│           └── PagoControllerTest.java
│
├── integration/
│   ├── RegistroEstadoPagoIntegrationTest.java
│   ├── ActualizacionEstadoPagoIntegrationTest.java
│   ├── IdempotenciaEventoPagoIntegrationTest.java
│   └── EventoPagoAsincronoIntegrationTest.java
│
└── unit/
    ├── EstadoPagoDomainServiceTest.java
    ├── TransicionEstadoPagoServiceTest.java
    ├── IdempotenciaEventoPagoServiceTest.java
    ├── PagoServiceTest.java
    ├── EventoPagoProcessorTest.java
    └── PagoMapperTest.java
### Backend resources

```text
backend/src/main/resources/
└── application.yml

backend/src/test/resources/
└── application-test.yml
```

## Structure Decision

La estructura del backend debe orientarse al flujo financiero definido por el spec de registro asíncrono del estado del pago, no al flujo operativo de paquetes.

El spec exige que el sistema reciba eventos provenientes de la entidad financiera, responda de inmediato la recepción del evento y procese la creación o actualización del estado de pago en segundo plano. Por tanto, la estructura debe incluir explícitamente:

1. `Pago`: agregado principal asociado a una liquidación previamente calculada.
2. `EstadoPago`: modelo que representa la condición actual del pago.
3. `EventoTransaccion`: registro técnico y funcional de cada evento recibido desde el banco.
4. `LiquidacionReferencia`: referencia necesaria para validar que la liquidación exista antes de modificar el pago.
5. Servicios de dominio para validar estados, controlar transiciones e impedir modificaciones inválidas sobre estados finales.
6. Infraestructura asíncrona para desacoplar la recepción del webhook del procesamiento real del evento.
7. Repositorios separados para pago, estado de pago, eventos de transacción y liquidación.


---
## Phase 1: Setup & Infrastructure Alignment

**Purpose**: Consolidar la infraestructura necesaria para recibir eventos asíncronos de estado de pago, procesarlos en segundo plano y persistir la trazabilidad del evento sin acoplar la lógica de negocio a controladores, entidades JPA o mecanismos externos de mensajería.

- [ ] T001 Verificar `backend/build.gradle` con Java 21, Spring Boot, Spring Web, Spring Data JPA, PostgreSQL Driver, Validation, Lombok y dependencias de test.
- [ ] T002 Verificar que el proyecto mantenga Spring Boot y Gradle correctamente configurados para compilar y ejecutar el backend desde IntelliJ o terminal.
- [ ] T003 Verificar `backend/src/main/resources/application.yml` con conexión PostgreSQL, configuración JPA y propiedades necesarias para el procesamiento asíncrono de eventos de pago.
- [ ] T004 Verificar `backend/src/test/resources/application-test.yml` con H2 o una configuración aislada para pruebas de persistencia, idempotencia y procesamiento de eventos.
- [ ] T005 Mantener `LogisticaApplication.java` como punto único de arranque del backend.
- [ ] T006 Incorporar o verificar configuración asíncrona mediante `AsyncPagoConfig.java` o configuración equivalente con `@EnableAsync`, `TaskExecutor` y manejo controlado de errores.
- [ ] T007 Verificar que la recepción del evento pueda responder inmediatamente con `202 Accepted`, sin esperar a que finalice la actualización real del estado de pago.
- [ ] T008 Mantener la infraestructura externa aislada en `infrastructure`, evitando que `application` o `domain` dependan de Spring Web, JPA, controladores o detalles técnicos de mensajería.

**Checkpoint**: El backend compila, arranca y tiene la infraestructura mínima para recibir eventos de pago, responder inmediatamente y delegar su procesamiento en segundo plano.

---

## Phase 2: Domain & Data Integrity

**Purpose**: Definir el núcleo de dominio del registro de estado de pago, garantizando consistencia, validación de estados, idempotencia, control de transiciones y protección frente a eventos duplicados, inválidos o desordenados.

- [ ] T009 Crear o revisar `EstadoPagoEnum.java` para representar únicamente los estados válidos del pago:
    - `PENDIENTE`
    - `EN_PROCESO`
    - `PAGADO`
    - `RECHAZADO`

- [ ] T010 Crear o revisar `EstadoEventoTransaccion.java` para representar el resultado técnico del evento recibido:
    - `RECIBIDO`
    - `PROCESADO`
    - `DUPLICADO`
    - `RECHAZADO`
    - `ERROR`

- [ ] T011 Crear o revisar `TipoEventoPago.java` para clasificar el evento recibido:
    - `REGISTRO_INICIAL`
    - `ACTUALIZACION_ESTADO`

- [ ] T012 Crear o revisar `Pago.java` como agregado principal de la transacción económica, incluyendo como mínimo:
    - `idPago`
    - `idUsuario`
    - `montoBase`
    - `fecha`
    - `idPenalidad`
    - `montoNeto`
    - `idLiquidacion`
    - `estadoActual`
    - `fechaUltimaActualizacion`
    - `ultimaSecuenciaProcesada`

- [ ] T013 Crear o revisar `EstadoPago.java` para representar el estado actual o histórico del pago, incluyendo:
    - `idEstadoPago`
    - `idPago`
    - `estado`
    - `fechaRegistro`
    - `fechaEventoBanco`
    - `secuenciaEvento`
    - `idEventoTransaccion`

- [ ] T014 Crear o revisar `EventoTransaccion.java` para registrar cada comunicación recibida desde la entidad financiera, incluyendo:
    - `idEvento`
    - `idTransaccionBanco`
    - `idPago`
    - `idLiquidacion`
    - `payloadRecibido`
    - `fechaRecepcion`
    - `fechaEventoBanco`
    - `secuencia`
    - `estadoProcesamiento`
    - `mensajeError`
    - `procesado`

- [ ] T015 Crear o revisar `LiquidacionReferencia.java` como modelo mínimo para validar que la liquidación previamente calculada existe antes de registrar o actualizar el estado del pago.
- [ ] T016 Crear o revisar `PagoRepository.java` como puerto de dominio para consultar y persistir pagos.
- [ ] T017 Crear o revisar `EstadoPagoRepository.java` como puerto de dominio para registrar y consultar estados de pago.
- [ ] T018 Crear o revisar `EventoTransaccionRepository.java` como puerto de dominio para persistir eventos, consultar duplicados por `idTransaccionBanco` y mantener trazabilidad.
- [ ] T019 Crear o revisar `LiquidacionRepository.java` como puerto de dominio para validar existencia de la liquidación asociada al evento.
- [ ] T020 Crear o revisar `EstadoPagoDomainService.java` para validar estados conocidos y reglas generales del estado de pago.
- [ ] T021 Crear o revisar `IdempotenciaEventoPagoService.java` para detectar eventos duplicados con el mismo `idTransaccionBanco`.
- [ ] T022 Crear o revisar `TransicionEstadoPagoService.java` para impedir transiciones inválidas, especialmente cuando el pago ya esté en estado final `PAGADO` o `RECHAZADO`.
- [ ] T023 Asegurar que un evento con estado desconocido sea rechazado con error funcional controlado y registrado como evento fallido.
- [ ] T024 Asegurar que un evento sobre una liquidación inexistente no modifique datos de pago y quede registrado como evento rechazado o con error.
- [ ] T025 Asegurar que un evento con el mismo estado actual no genere cambios innecesarios en base de datos, pero sí confirme la recepción y preserve la idempotencia.
- [ ] T026 Asegurar que eventos desordenados sean evaluados mediante `fechaEventoBanco` o `secuencia`, evitando que un estado transitorio atrasado sobrescriba un estado final o más reciente.

**Checkpoint**: El dominio controla los estados válidos de pago, la idempotencia, las transiciones permitidas, los eventos duplicados, los eventos inválidos y la trazabilidad funcional.

---

## Phase 3: Application Use Cases

**Purpose**: Implementar casos de uso orientados al flujo real del spec: recibir eventos de pago, responder inmediatamente, procesar en segundo plano, registrar el evento, validar liquidación, crear o actualizar el estado del pago y consultar la trazabilidad.

- [ ] T027 Crear o revisar `RecibirEventoPagoUseCase.java` como contrato para recibir el evento enviado por la entidad financiera.
- [ ] T028 Crear o revisar `ProcesarEventoPagoUseCase.java` como contrato para procesar en segundo plano el evento recibido.
- [ ] T029 Crear o revisar `RegistrarEstadoPagoUseCase.java` como contrato para registrar el estado inicial del pago cuando la entidad financiera informe el inicio del proceso.
- [ ] T030 Crear o revisar `ActualizarEstadoPagoUseCase.java` como contrato para actualizar el estado del pago a `EN_PROCESO`, `PAGADO` o `RECHAZADO`.
- [ ] T031 Crear o revisar `ObtenerEstadoPagoUseCase.java` como contrato para consultar el estado actual de un pago.
- [ ] T032 Crear o revisar `ObtenerEventosTransaccionUseCase.java` como contrato para consultar los eventos recibidos y procesados para un pago.
- [ ] T033 Crear o revisar `PagoService.java` como implementación de los casos de uso anteriores.
- [ ] T034 Asegurar que `PagoService.java` use únicamente puertos de dominio y no manipule directamente `JpaRepository`, entidades JPA ni clases de controladores.
- [ ] T035 Asegurar que `PagoService.java` registre primero el `EventoTransaccion` recibido antes de intentar modificar el estado del pago.
- [ ] T036 Asegurar que `PagoService.java` valide la existencia de la liquidación antes de crear o actualizar un estado de pago.
- [ ] T037 Asegurar que `PagoService.java` consulte duplicados por `idTransaccionBanco` antes de procesar el evento.
- [ ] T038 Asegurar que `PagoService.java` delegue reglas de transición en `TransicionEstadoPagoService.java`.
- [ ] T039 Asegurar que `PagoService.java` delegue validaciones de estado en `EstadoPagoDomainService.java`.
- [ ] T040 Asegurar que `PagoService.java` delegue la detección de duplicados en `IdempotenciaEventoPagoService.java`.
- [ ] T041 Asegurar que el procesamiento de eventos sea transaccional en la operación crítica de actualización de pago y registro de estado.
- [ ] T042 Asegurar que, cuando el evento sea duplicado, el sistema no cree un nuevo `EstadoPago` ni modifique el pago existente.
- [ ] T043 Asegurar que, cuando el pago ya esté en estado final, el sistema ignore o rechace transiciones inválidas y registre el intento en `EventoTransaccion`.
- [ ] T044 Asegurar que los DTOs de respuesta sean inmutables o controlados mediante `@Builder`, constructores o `record`, sin exponer entidades internas.

**Checkpoint**: La capa de aplicación orquesta el flujo completo de registro y actualización de estado de pago sin mezclar lógica de negocio con detalles HTTP, JPA o asincronía.

---

## Phase 4: Infrastructure Adapters

**Purpose**: Implementar los detalles técnicos de persistencia, mapeo, recepción asíncrona y procesamiento desacoplado sin contaminar el dominio ni los casos de uso.

- [ ] T045 Crear o revisar `PagoRepositoryAdapter.java` para adaptar `PagoRepository` usando `PagoJpaRepository`.
- [ ] T046 Crear o revisar `EstadoPagoRepositoryAdapter.java` para adaptar `EstadoPagoRepository` usando `EstadoPagoJpaRepository`.
- [ ] T047 Crear o revisar `EventoTransaccionRepositoryAdapter.java` para adaptar `EventoTransaccionRepository` usando `EventoTransaccionJpaRepository`.
- [ ] T048 Crear o revisar `LiquidacionRepositoryAdapter.java` para adaptar `LiquidacionRepository` usando `LiquidacionJpaRepository`.
- [ ] T049 Crear o revisar `PagoMapper.java` para convertir entre modelos de dominio y entidades JPA sin perder campos críticos como `idPago`, `idLiquidacion`, `estadoActual`, `fechaUltimaActualizacion`, `ultimaSecuenciaProcesada` y versión.
- [ ] T050 Crear o revisar `PagoEntity.java` como entidad persistente de la transacción económica.
- [ ] T051 Crear o revisar `EstadoPagoEntity.java` como entidad persistente del estado del pago.
- [ ] T052 Crear o revisar `EventoTransaccionEntity.java` como entidad persistente del evento recibido desde la entidad financiera.
- [ ] T053 Crear o revisar `LiquidacionReferenciaEntity.java` o adaptar la entidad de liquidación existente para validar la existencia de la liquidación previamente calculada.
- [ ] T054 Asegurar que `PagoEntity.java` tenga control de concurrencia con `@Version` si el pago puede recibir múltiples eventos cercanos en el tiempo.
- [ ] T055 Asegurar que `EventoTransaccionEntity.java` tenga una restricción única sobre `idTransaccionBanco` para reforzar la idempotencia a nivel de base de datos.
- [ ] T056 Asegurar que `EstadoPagoEntity.java` tenga relación correcta con `PagoEntity`.
- [ ] T057 Asegurar que `PagoJpaRepository.java` permita consultar pagos por `idPago` y, si aplica, por `idLiquidacion`.
- [ ] T058 Asegurar que `EstadoPagoJpaRepository.java` permita consultar el último estado de un pago ordenado por fecha o secuencia.
- [ ] T059 Asegurar que `EventoTransaccionJpaRepository.java` permita consultar eventos por `idTransaccionBanco`, `idPago`, `estadoProcesamiento` y fecha de recepción.
- [ ] T060 Crear o revisar `AsyncPagoConfig.java` para declarar el executor usado por el procesamiento en segundo plano.
- [ ] T061 Crear o revisar `EventoPagoProcessor.java` para ejecutar el caso de uso `ProcesarEventoPagoUseCase` de forma asíncrona.
- [ ] T062 Crear o revisar `EventoPagoListener.java` sólo si el proyecto decide consumir una cola de mensajería en vez de webhook directo.
- [ ] T063 Mantener cualquier configuración de webhook o seguridad de recepción en `WebhookConfig.java`, sin acoplarla al dominio.

**Checkpoint**: La infraestructura resuelve persistencia, mapeo, asincronía y recepción técnica del evento sin contaminar `domain` ni `application`.

---

## Phase 5: Web Layer & API Contract

**Purpose**: Exponer una API coherente con el registro asíncrono del estado de pago, permitiendo que la entidad financiera notifique eventos de pago sin bloquear la comunicación con el sistema principal. La capa web sólo debe recibir el evento, validarlo a nivel de contrato, responder inmediatamente y delegar el procesamiento real a la capa de aplicación.

- [ ] T064 Crear o revisar `WebhookPagoController.java` como controlador responsable de recibir eventos asíncronos provenientes de la entidad financiera.
- [ ] T065 Exponer el endpoint principal de recepción de eventos de estado de pago:

```http
POST /api/v1/pagos/webhook/estado
```

- [ ] T066 Asegurar que el endpoint anterior reciba un `EventoEstadoPagoRequestDTO` con los campos mínimos exigidos por el flujo:

```json
{
  "idEvento": "evt-20260426-001",
  "idTransaccionBanco": "txn-bank-0001",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "EN_PROCESO",
  "fechaEvento": "2026-04-26T10:30:00",
  "secuencia": 1,
  "payloadOriginal": {
    "banco": "Entidad Financiera",
    "canal": "webhook"
  }
}
```

- [ ] T067 Validar en el DTO de entrada que los siguientes campos sean obligatorios:
    - `idEvento`
    - `idTransaccionBanco`
    - `idPago`
    - `idLiquidacion`
    - `estado`
    - `fechaEvento`

- [ ] T068 Validar que `estado` sólo acepte valores soportados por el dominio:
    - `PENDIENTE`
    - `EN_PROCESO`
    - `PAGADO`
    - `RECHAZADO`

- [ ] T069 Asegurar que `WebhookPagoController.java` responda inmediatamente con `202 Accepted` cuando el evento tenga un contrato válido y haya sido recibido para procesamiento asíncrono.

Respuesta esperada:

```http
202 Accepted
```

```json
{
  "mensaje": "Evento de pago recibido correctamente",
  "idEvento": "evt-20260426-001",
  "idTransaccionBanco": "txn-bank-0001",
  "procesamiento": "ASINCRONO"
}
```

- [ ] T070 Asegurar que el controlador no procese directamente reglas de negocio, no consulte repositorios JPA y no modifique entidades persistentes.
- [ ] T071 Asegurar que `WebhookPagoController.java` invoque únicamente el caso de uso `RecibirEventoPagoUseCase`.
- [ ] T072 Asegurar que `RecibirEventoPagoUseCase` registre la recepción inicial del evento o delegue inmediatamente al componente asíncrono `EventoPagoProcessor`.
- [ ] T073 Crear o revisar `PagoController.java` para exponer endpoints de consulta relacionados con el estado de pago y su trazabilidad.
- [ ] T074 Exponer endpoint para consultar el estado actual de un pago:

```http
GET /api/v1/pagos/{idPago}/estado
```

Respuesta esperada:

```json
{
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "PAGADO",
  "fechaUltimaActualizacion": "2026-04-26T10:35:00",
  "ultimaSecuenciaProcesada": 2
}
```

- [ ] T075 Exponer endpoint para consultar los eventos de transacción recibidos para un pago:

```http
GET /api/v1/pagos/{idPago}/eventos
```

Respuesta esperada:

```json
[
  {
    "idEvento": "evt-20260426-001",
    "idTransaccionBanco": "txn-bank-0001",
    "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
    "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
    "estadoSolicitado": "EN_PROCESO",
    "estadoProcesamiento": "PROCESADO",
    "fechaRecepcion": "2026-04-26T10:30:01",
    "fechaEventoBanco": "2026-04-26T10:30:00",
    "secuencia": 1,
    "mensajeError": null
  }
]
```

- [ ] T076 Si el backend ya cuenta con una entidad o consulta de liquidación disponible, exponer endpoint complementario para consultar el estado de pago desde una liquidación:

```http
GET /api/v1/liquidaciones/{idLiquidacion}/pago/estado
```

Este endpoint sólo debe existir si el modelo de liquidación ya está disponible en el backend y si el flujo necesita consultar el estado de pago desde la liquidación.

- [ ] T077 Revisar `GlobalExceptionHandler.java` para manejar respuestas claras ante:
    - `LiquidacionNoEncontradaException`
    - `PagoNoEncontradoException`
    - `EstadoPagoInvalidoException`
    - `TransicionEstadoPagoInvalidaException`
    - `EventoDuplicadoException`
    - `EventoPagoNoProcesableException`
    - errores de validación de DTOs
    - errores generales no controlados

- [ ] T078 Mantener códigos HTTP coherentes con el spec:

```text
202 Accepted: evento recibido correctamente para procesamiento asíncrono.
200 OK: consulta exitosa de estado o eventos.
400 Bad Request: payload inválido, estado desconocido o campos obligatorios ausentes.
404 Not Found: pago o liquidación inexistente en consultas directas.
409 Conflict: transición inválida o evento duplicado cuando se decida rechazar explícitamente.
500 Internal Server Error: error no controlado.
```

- [ ] T079 Asegurar que los eventos duplicados puedan responder `202 Accepted` si se decide tratarlos como idempotentes, siempre que no creen nuevos registros ni modifiquen el estado del pago.
- [ ] T080 Asegurar que los errores funcionales ocurridos durante el procesamiento asíncrono queden registrados en `EventoTransaccion`, porque el webhook ya pudo haber respondido `202 Accepted`.

**Checkpoint**: La API pública queda alineada con el spec de pago: recibe eventos asíncronos del banco, responde de inmediato, permite consultar el estado del pago y permite auditar los eventos recibidos. No conserva endpoints de paquete ni rutas de sincronización operativa.

---

## Phase 6: Testing Strategy

**Purpose**: Validar que el backend cumple el flujo definido por el spec: recepción asíncrona del evento, respuesta inmediata, procesamiento en segundo plano, creación o actualización del estado de pago, idempotencia por ID de transacción, validación de liquidación existente, rechazo de estados inválidos y protección de estados finales.

### Unit tests

- [ ] T081 Crear o revisar `EstadoPagoDomainServiceTest.java` para validar:
    - aceptación de estados válidos;
    - rechazo de estados desconocidos;
    - normalización de estados si el sistema recibe valores equivalentes desde el banco;
    - identificación de estados finales `PAGADO` y `RECHAZADO`.

- [ ] T082 Crear o revisar `TransicionEstadoPagoServiceTest.java` para validar transiciones permitidas:

```text
PENDIENTE -> EN_PROCESO
PENDIENTE -> PAGADO
PENDIENTE -> RECHAZADO
EN_PROCESO -> PAGADO
EN_PROCESO -> RECHAZADO
```

- [ ] T083 Crear o revisar `TransicionEstadoPagoServiceTest.java` para validar transiciones inválidas:

```text
PAGADO -> EN_PROCESO
PAGADO -> PENDIENTE
PAGADO -> RECHAZADO
RECHAZADO -> EN_PROCESO
RECHAZADO -> PAGADO
RECHAZADO -> PENDIENTE
```

- [ ] T084 Crear o revisar `IdempotenciaEventoPagoServiceTest.java` para validar que un evento con el mismo `idTransaccionBanco` sea detectado como duplicado.
- [ ] T085 Crear o revisar `PagoServiceTest.java` para validar registro inicial de estado de pago cuando llega un evento válido con estado `EN_PROCESO`.
- [ ] T086 Crear o revisar `PagoServiceTest.java` para validar actualización exitosa de estado a `PAGADO`.
- [ ] T087 Crear o revisar `PagoServiceTest.java` para validar actualización exitosa de estado a `RECHAZADO`.
- [ ] T088 Crear o revisar `PagoServiceTest.java` para validar que un evento con liquidación inexistente:
    - no cree estado de pago;
    - no actualice el pago;
    - registre el error en `EventoTransaccion`.

- [ ] T089 Crear o revisar `PagoServiceTest.java` para validar que un evento duplicado:
    - no cree un nuevo `EstadoPago`;
    - no actualice nuevamente el pago;
    - preserve la trazabilidad del evento.

- [ ] T090 Crear o revisar `PagoServiceTest.java` para validar que un evento con el mismo estado actual:
    - sea tratado como idempotente;
    - responda funcionalmente como recibido;
    - no genere cambios innecesarios en base de datos.

- [ ] T091 Crear o revisar `PagoServiceTest.java` para validar que un evento desordenado por `fechaEvento` o `secuencia` no sobrescriba un estado más reciente.
- [ ] T092 Crear o revisar `PagoServiceTest.java` para validar que un pago en estado final `PAGADO` no pueda ser sobrescrito por un evento atrasado `EN_PROCESO`.
- [ ] T093 Crear o revisar `EventoPagoProcessorTest.java` para validar que el procesador asíncrono:
    - reciba el evento;
    - invoque `ProcesarEventoPagoUseCase`;
    - capture errores controlados;
    - marque el evento como `PROCESADO`, `DUPLICADO`, `RECHAZADO` o `ERROR` según corresponda.

- [ ] T094 Crear o revisar `PagoMapperTest.java` para validar mapeos entre:
    - `PagoEntity` y `Pago`;
    - `EstadoPagoEntity` y `EstadoPago`;
    - `EventoTransaccionEntity` y `EventoTransaccion`.

### Controller tests

- [ ] T095 Crear o revisar `WebhookPagoControllerTest.java` para validar recepción exitosa de evento:

```http
POST /api/v1/pagos/webhook/estado
```

Resultado esperado:

```http
202 Accepted
```

- [ ] T096 Validar en `WebhookPagoControllerTest.java` que el endpoint rechace payloads sin campos obligatorios con:

```http
400 Bad Request
```

- [ ] T097 Validar en `WebhookPagoControllerTest.java` que un estado desconocido sea rechazado con:

```http
400 Bad Request
```

- [ ] T098 Validar en `WebhookPagoControllerTest.java` que el controlador no exponga entidades internas en la respuesta.
- [ ] T099 Crear o revisar `PagoControllerTest.java` para validar consulta exitosa del estado actual:

```http
GET /api/v1/pagos/{idPago}/estado
```

Resultado esperado:

```http
200 OK
```

- [ ] T100 Crear o revisar `PagoControllerTest.java` para validar consulta exitosa de eventos:

```http
GET /api/v1/pagos/{idPago}/eventos
```

Resultado esperado:

```http
200 OK
```

- [ ] T101 Validar en `PagoControllerTest.java` que la consulta de un pago inexistente responda:

```http
404 Not Found
```

### Integration tests

- [ ] T102 Crear o revisar `RegistroEstadoPagoIntegrationTest.java` para validar el flujo completo:
    - recepción del evento;
    - persistencia de `EventoTransaccion`;
    - creación de `EstadoPago`;
    - asociación correcta con `Pago` y `Liquidacion`;
    - respuesta `202 Accepted`.

- [ ] T103 Crear o revisar `ActualizacionEstadoPagoIntegrationTest.java` para validar actualización de `EN_PROCESO` a `PAGADO`.
- [ ] T104 Crear o revisar `ActualizacionEstadoPagoIntegrationTest.java` para validar actualización de `EN_PROCESO` a `RECHAZADO`.
- [ ] T105 Crear o revisar `IdempotenciaEventoPagoIntegrationTest.java` para validar que dos eventos con el mismo `idTransaccionBanco` no generen registros duplicados.
- [ ] T106 Crear o revisar `EventoPagoAsincronoIntegrationTest.java` para validar que el webhook responda `202 Accepted` antes de finalizar la actualización real del estado de pago.
- [ ] T107 Crear o revisar `LiquidacionInexistenteEventoPagoIntegrationTest.java` para validar que un evento con `idLiquidacion` inexistente:
    - sea registrado como evento con error o rechazado;
    - no cree `EstadoPago`;
    - no modifique `Pago`.

- [ ] T108 Crear o revisar `EventoPagoDesordenadoIntegrationTest.java` para validar que un evento atrasado no sobrescriba un estado más reciente o final.
- [ ] T109 Crear o revisar `EstadoPagoFinalIntegrationTest.java` para validar que un pago en estado `PAGADO` o `RECHAZADO` no acepte transiciones inválidas.
- [ ] T110 Crear o revisar `EventoEstadoPagoInvalidoIntegrationTest.java` para validar que un estado desconocido sea rechazado y quede trazabilidad del fallo.

**Checkpoint**: Los tests cubren el comportamiento pedido por el spec y eliminan cualquier referencia a paquete, historial de paquete, logs de sincronización de paquete o endpoints `/api/v1/paquetes`.

---

## Phase 7: Operational Validation with Postman

**Purpose**: Validar manualmente el flujo real de registro y actualización del estado de pago mediante eventos asíncronos enviados por la entidad financiera.

### Precondiciones

Antes de ejecutar las pruebas en Postman:

- [ ] El backend debe estar corriendo en:

```http
http://localhost:8080
```

- [ ] PostgreSQL debe estar activo.
- [ ] Debe existir una liquidación previamente calculada.
- [ ] Debe existir o poder crearse un pago asociado a esa liquidación.
- [ ] Los UUID usados en los ejemplos deben reemplazarse por datos reales existentes en la base de datos.
- [ ] El header debe incluir:

```http
Content-Type: application/json
```

---

### Request 1: Registrar estado inicial de pago

Este request simula el evento inicial enviado por la entidad financiera indicando que el pago inició su proceso.

```http
POST http://localhost:8080/api/v1/pagos/webhook/estado
Content-Type: application/json
```

Body:

```json
{
  "idEvento": "evt-20260426-001",
  "idTransaccionBanco": "txn-bank-0001",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "EN_PROCESO",
  "fechaEvento": "2026-04-26T10:30:00",
  "secuencia": 1,
  "payloadOriginal": {
    "banco": "Entidad Financiera",
    "canal": "webhook",
    "descripcion": "Inicio del proceso de pago"
  }
}
```

Respuesta esperada:

```http
202 Accepted
```

```json
{
  "mensaje": "Evento de pago recibido correctamente",
  "idEvento": "evt-20260426-001",
  "idTransaccionBanco": "txn-bank-0001",
  "procesamiento": "ASINCRONO"
}
```

Validación esperada en base de datos:

```text
Debe existir un EventoTransaccion asociado al idPago.
Debe existir un EstadoPago con estado EN_PROCESO.
El Pago debe quedar asociado a la liquidación indicada.
```

---

### Request 2: Actualizar estado de pago a PAGADO

Este request simula que la entidad financiera confirma que el pago fue completado exitosamente.

```http
POST http://localhost:8080/api/v1/pagos/webhook/estado
Content-Type: application/json
```

Body:

```json
{
  "idEvento": "evt-20260426-002",
  "idTransaccionBanco": "txn-bank-0002",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "PAGADO",
  "fechaEvento": "2026-04-26T10:35:00",
  "secuencia": 2,
  "payloadOriginal": {
    "banco": "Entidad Financiera",
    "canal": "webhook",
    "descripcion": "Pago finalizado exitosamente"
  }
}
```

Respuesta esperada:

```http
202 Accepted
```

Validación esperada:

```text
El Pago debe quedar en estado PAGADO.
Debe crearse un nuevo EstadoPago con estado PAGADO.
Debe registrarse el EventoTransaccion como PROCESADO.
La ultimaSecuenciaProcesada debe quedar en 2.
```

---

### Request 3: Actualizar estado de pago a RECHAZADO

Este request aplica para un pago que esté en estado `PENDIENTE` o `EN_PROCESO`, no para uno que ya esté `PAGADO`.

```http
POST http://localhost:8080/api/v1/pagos/webhook/estado
Content-Type: application/json
```

Body:

```json
{
  "idEvento": "evt-20260426-003",
  "idTransaccionBanco": "txn-bank-0003",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "RECHAZADO",
  "fechaEvento": "2026-04-26T10:40:00",
  "secuencia": 3,
  "payloadOriginal": {
    "banco": "Entidad Financiera",
    "canal": "webhook",
    "descripcion": "Pago rechazado por la entidad financiera"
  }
}
```

Respuesta esperada:

```http
202 Accepted
```

Validación esperada:

```text
El Pago debe quedar en estado RECHAZADO si la transición es válida.
Debe crearse un EstadoPago con estado RECHAZADO.
Debe registrarse el EventoTransaccion como PROCESADO.
```

---

### Request 4: Probar idempotencia con evento duplicado

Enviar exactamente el mismo evento del Request 2, conservando el mismo `idTransaccionBanco`.

```http
POST http://localhost:8080/api/v1/pagos/webhook/estado
Content-Type: application/json
```

Body:

```json
{
  "idEvento": "evt-20260426-002",
  "idTransaccionBanco": "txn-bank-0002",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "PAGADO",
  "fechaEvento": "2026-04-26T10:35:00",
  "secuencia": 2,
  "payloadOriginal": {
    "banco": "Entidad Financiera",
    "canal": "webhook",
    "descripcion": "Reintento del mismo evento"
  }
}
```

Respuesta esperada recomendada:

```http
202 Accepted
```

Validación esperada:

```text
No debe crearse un segundo EstadoPago PAGADO.
No debe modificarse nuevamente el Pago.
El sistema debe reconocer el idTransaccionBanco como duplicado.
Debe mantenerse trazabilidad del intento duplicado como DUPLICADO o evento ya recibido.
```

---

### Request 5: Probar liquidación inexistente

```http
POST http://localhost:8080/api/v1/pagos/webhook/estado
Content-Type: application/json
```

Body:

```json
{
  "idEvento": "evt-20260426-004",
  "idTransaccionBanco": "txn-bank-0004",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "00000000-0000-0000-0000-000000000000",
  "estado": "EN_PROCESO",
  "fechaEvento": "2026-04-26T10:45:00",
  "secuencia": 4,
  "payloadOriginal": {
    "banco": "Entidad Financiera",
    "canal": "webhook",
    "descripcion": "Evento asociado a liquidación inexistente"
  }
}
```

Respuesta esperada del webhook:

```http
202 Accepted
```

Validación esperada:

```text
El evento debe registrarse.
El procesamiento debe marcarse como ERROR o RECHAZADO.
No debe crearse ni actualizarse EstadoPago.
Debe quedar mensaje de error asociado a liquidación inexistente.
```

Nota: si la validación de existencia de liquidación se hace antes de aceptar el evento, también puede usarse `404 Not Found`; sin embargo, para respetar el comportamiento asíncrono del spec, se recomienda aceptar la recepción con `202 Accepted` y registrar el fallo durante el procesamiento.

---

### Request 6: Probar estado desconocido

```http
POST http://localhost:8080/api/v1/pagos/webhook/estado
Content-Type: application/json
```

Body:

```json
{
  "idEvento": "evt-20260426-005",
  "idTransaccionBanco": "txn-bank-0005",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "APROBADO_PARCIALMENTE",
  "fechaEvento": "2026-04-26T10:50:00",
  "secuencia": 5,
  "payloadOriginal": {
    "banco": "Entidad Financiera",
    "canal": "webhook",
    "descripcion": "Estado no soportado"
  }
}
```

Respuesta esperada:

```http
400 Bad Request
```

Respuesta sugerida:

```json
{
  "error": "estado invalido",
  "mensaje": "El estado de pago recibido no es soportado",
  "estadoRecibido": "APROBADO_PARCIALMENTE"
}
```

Validación esperada:

```text
No debe modificarse el Pago.
No debe crearse EstadoPago.
Debe registrarse el fallo en logs o EventoTransaccion si el diseño registra eventos inválidos.
```

---

### Request 7: Probar evento desordenado

Este request simula que llega tarde un evento `EN_PROCESO` después de que el pago ya quedó `PAGADO`.

```http
POST http://localhost:8080/api/v1/pagos/webhook/estado
Content-Type: application/json
```

Body:

```json
{
  "idEvento": "evt-20260426-006",
  "idTransaccionBanco": "txn-bank-0006",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "EN_PROCESO",
  "fechaEvento": "2026-04-26T10:20:00",
  "secuencia": 1,
  "payloadOriginal": {
    "banco": "Entidad Financiera",
    "canal": "webhook",
    "descripcion": "Evento atrasado recibido después del pago final"
  }
}
```

Respuesta esperada:

```http
202 Accepted
```

Validación esperada:

```text
El Pago debe permanecer en estado PAGADO.
No debe sobrescribirse el estado final con EN_PROCESO.
El EventoTransaccion debe quedar como RECHAZADO, IGNORADO o ERROR_CONTROLADO por evento desordenado.
```

---

### Request 8: Consultar estado actual del pago

```http
GET http://localhost:8080/api/v1/pagos/8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812/estado
```

Respuesta esperada:

```http
200 OK
```

```json
{
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "PAGADO",
  "fechaUltimaActualizacion": "2026-04-26T10:35:00",
  "ultimaSecuenciaProcesada": 2
}
```

---

### Request 9: Consultar eventos recibidos para el pago

```http
GET http://localhost:8080/api/v1/pagos/8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812/eventos
```

Respuesta esperada:

```http
200 OK
```

```json
[
  {
    "idEvento": "evt-20260426-001",
    "idTransaccionBanco": "txn-bank-0001",
    "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
    "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
    "estadoSolicitado": "EN_PROCESO",
    "estadoProcesamiento": "PROCESADO",
    "fechaRecepcion": "2026-04-26T10:30:01",
    "fechaEventoBanco": "2026-04-26T10:30:00",
    "secuencia": 1,
    "mensajeError": null
  },
  {
    "idEvento": "evt-20260426-002",
    "idTransaccionBanco": "txn-bank-0002",
    "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
    "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
    "estadoSolicitado": "PAGADO",
    "estadoProcesamiento": "PROCESADO",
    "fechaRecepcion": "2026-04-26T10:35:01",
    "fechaEventoBanco": "2026-04-26T10:35:00",
    "secuencia": 2,
    "mensajeError": null
  },
  {
    "idEvento": "evt-20260426-006",
    "idTransaccionBanco": "txn-bank-0006",
    "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
    "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
    "estadoSolicitado": "EN_PROCESO",
    "estadoProcesamiento": "RECHAZADO",
    "fechaRecepcion": "2026-04-26T10:55:01",
    "fechaEventoBanco": "2026-04-26T10:20:00",
    "secuencia": 1,
    "mensajeError": "Evento desordenado: no se permite sobrescribir un estado final o más reciente"
  }
]
```

**Checkpoint**: Postman valida los escenarios principales y edge cases del spec: registro inicial, actualización a pagado o rechazado, evento duplicado, liquidación inexistente, estado desconocido, evento desordenado, consulta del estado actual y consulta de trazabilidad.

## Dependencies & Execution Order

1. Verificar infraestructura Gradle y configuración de base de datos.
2. Levantar PostgreSQL local o Docker.
3. Ejecutar backend desde IntelliJ o `./gradlew bootRun`.
4. Ejecutar tests unitarios y de integración.
5. Validar endpoints con Postman.
6. Revisar logs de sincronización para confirmar trazabilidad.
7. Sólo después de estabilizar este flujo, planear entidades financieras explícitas como `Pago`, `EstadoPago` o `EventoTransaccion`, si el equipo decide evolucionar el módulo desde sincronización de paquete hacia registro bancario real.

## Files that must NOT be added in this plan version

La siguiente estructura pertenecía al plan anterior, pero no coincide con el backend actual y por tanto no debe aparecer como obligatoria en esta versión:

```text
application/usecases/pago/
domain/models/Pago.java
domain/models/EstadoPago.java
domain/models/EventoTransaccion.java
domain/enums/EstadoPagoEnum.java
domain/repositories/PagoRepository.java
domain/repositories/EstadoPagoRepository.java
domain/repositories/EventoRepository.java
infrastructure/web/controllers/WebhookPagoController.java
infrastructure/async/
infrastructure/security/WebhookSecurityConfig.java
frontend/src/modules/pagos/
```

Estos archivos sólo deberían incorporarse en una fase posterior si se crea realmente el submódulo bancario de pagos asíncronos. En el estado actual del repositorio, la implementación real se concentra en `paquete`, `historial_estados` y `logs_sincronizacion`.

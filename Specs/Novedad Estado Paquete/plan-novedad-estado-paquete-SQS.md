# Implementation Plan: Novedad estado del paquete — Comunicación por SQS

**Date**: 2026-04-08  
**Updated**: 2026-05-16  
**Spec**: [Novedad estado del paquete - SQS.md]

## Summary

Esta funcionalidad establece la integración asíncrona entre el Módulo Financiero y el Módulo de Gestión de Paquetes usando AWS SQS. El Módulo Financiero ya no debe ejecutar consultas HTTP GET hacia Gestión de Paquetes ni depender de clientes Feign/WebClient para sincronizar estados. En su lugar, consume mensajes publicados por Gestión en una cola SQS, valida el evento, aplica las reglas financieras de pago, actualiza el paquete, registra el historial y conserva auditoría completa del procesamiento.

El sistema incluye idempotencia por `eventId`, control de eventos desordenados mediante `fechaEvento` o `secuenciaEvento`, manejo de errores funcionales no reintentables, reintentos técnicos mediante SQS y DLQ para mensajes que no puedan procesarse después de los intentos configurados.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+  
**Primary Dependencies**: Spring Boot Web, Spring Data JPA, PostgreSQL Driver, Spring Cloud AWS SQS, Jackson, Validation, Axios  
**Dependencies removed/replaced**: OpenFeign/WebClient para Gestión de Paquetes, WireMock como simulador HTTP, Resilience4j para timeout/retry HTTP  
**Storage**: PostgreSQL 15  
**Messaging**: AWS SQS + DLQ  
**Testing**: JUnit 5, Mockito, Testcontainers LocalStack, Awaitility / Jest  
**Target Platform**: AWS  
**Project Type**: Web application + event-driven integration

**Performance Goals**: Consumir y persistir eventos válidos en menos de 120 segundos desde su publicación, evitando bloqueo del proceso de liquidación.

**Constraints**:

- No debe existir comunicación sincrónica HTTP con Gestión de Paquetes para obtener estados.
- La cola SQS es la fuente de verdad operativa para novedades de estado.
- La eliminación del mensaje de SQS solo ocurre cuando el procesamiento termina correctamente o cuando el error funcional queda auditado.
- Los errores técnicos deben permitir reintento por `visibilityTimeout` y redrive hacia DLQ.
- La idempotencia por `eventId` debe ser obligatoria a nivel de base de datos.

---

## Project Structure

### Documentation

```text
specs/novedad-estado-paquete/
├── Novedad estado del paquete - SQS.md
├── plan-novedad-estado-paquete-SQS.md
├── spec-frontend-novedad-estado-paquete-SQS.md
└── plan-frontend-novedad-estado-paquete-SQS.md
```

### Source Code (backend)

```text
backend/
├── src/main/java/com/logistica/
│   ├── application/
│   │   ├── dtos/
│   │   │   ├── request/
│   │   │   │   └── EventoEstadoPaqueteMessageDTO.java
│   │   │   └── response/
│   │   │       ├── HistorialEstadoResponseDTO.java
│   │   │       └── LogSincronizacionResponseDTO.java
│   │   └── usecases/
│   │       └── paquete/
│   │           ├── ProcesarEventoEstadoPaqueteUseCase.java
│   │           ├── ObtenerHistorialUseCase.java
│   │           └── ObtenerLogsSincronizacionUseCase.java
│   │
│   ├── domain/
│   │   ├── enums/
│   │   │   ├── EstadoPaquete.java
│   │   │   └── EstadoProcesamientoEvento.java
│   │   ├── exceptions/
│   │   │   ├── EventoDuplicadoException.java
│   │   │   ├── EventoAtrasadoException.java
│   │   │   ├── EstadoPaqueteNoMapeadoException.java
│   │   │   └── PaqueteNoEncontradoException.java
│   │   ├── models/
│   │   │   ├── Paquete.java
│   │   │   ├── HistorialEstado.java
│   │   │   ├── LogSincronizacion.java
│   │   │   └── EventoEstadoPaquete.java
│   │   ├── repositories/
│   │   │   ├── PaqueteRepository.java
│   │   │   ├── HistorialRepository.java
│   │   │   └── LogSincronizacionRepository.java
│   │   └── services/
│   │       └── EstadoPaqueteService.java
│   │
│   ├── infrastructure/
│   │   ├── adapters/
│   │   │   └── PaqueteMapper.java
│   │   ├── messaging/
│   │   │   ├── consumers/
│   │   │   │   └── EventoEstadoPaqueteSqsListener.java
│   │   │   ├── config/
│   │   │   │   └── SqsConfig.java
│   │   │   └── mappers/
│   │   │       └── EventoEstadoPaqueteMessageMapper.java
│   │   ├── persistence/
│   │   │   ├── entities/
│   │   │   │   ├── PaqueteEntity.java
│   │   │   │   ├── HistorialEstadoEntity.java
│   │   │   │   └── LogSincronizacionEntity.java
│   │   │   └── repositories/
│   │   │       ├── JpaPaqueteRepository.java
│   │   │       ├── JpaHistorialEstadoRepository.java
│   │   │       └── JpaLogSincronizacionRepository.java
│   │   └── web/
│   │       ├── controllers/
│   │       │   ├── PaqueteController.java
│   │       │   └── SincronizacionController.java
│   │       └── handlers/
│   │           └── GlobalExceptionHandler.java
│   │
│   └── shared/
│       ├── constants/
│       └── utils/
│
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
│       └── Vx__novedad_estado_paquete_sqs.sql
│
└── src/test/java/
    ├── integration/
    │   └── EventoEstadoPaqueteSqsIntegrationTest.java
    └── unit/
        ├── EstadoPaqueteServiceTest.java
        └── ProcesarEventoEstadoPaqueteUseCaseTest.java
```

**Structure Decision**: Se reemplaza `infrastructure/http/clients` por `infrastructure/messaging/consumers`. La integración externa deja de ser llamada saliente y pasa a ser entrada asíncrona desde SQS. La capa de aplicación procesa eventos, la capa de dominio aplica reglas financieras e idempotencia, y la infraestructura solo se encarga de escuchar la cola y persistir.

---

## Phase 1: Setup SQS & Messaging Infrastructure

**Purpose**: Configurar AWS SQS, DLQ, variables de entorno y dependencias necesarias para consumir eventos.

- [ ] T001 Reemplazar dependencias HTTP por mensajería en `build.gradle`:
  - Remover `spring-cloud-starter-openfeign` si ya no se usa en otra feature.
  - Remover `resilience4j-spring-boot3` si estaba dedicado únicamente al cliente HTTP de Gestión.
  - Remover WireMock de los tests de esta feature.
  - Agregar `io.awspring.cloud:spring-cloud-aws-starter-sqs`.
  - Agregar Testcontainers LocalStack para pruebas de integración.
- [ ] T002 Configurar variables en `application.yml`:
  - `aws.region`
  - `aws.sqs.novedad-estado-paquete.queue-name`
  - `aws.sqs.novedad-estado-paquete.dlq-name`
  - `aws.sqs.novedad-estado-paquete.visibility-timeout`
  - `aws.sqs.novedad-estado-paquete.max-messages`
- [ ] T003 Crear `SqsConfig.java` para centralizar configuración de cliente SQS, región, endpoint local para desarrollo y credenciales por ambiente.
- [ ] T004 Definir política de DLQ: número máximo de recepciones recomendado entre 3 y 5 antes de mover el mensaje a DLQ.
- [ ] T005 Documentar el contrato del mensaje SQS en el spec y compartirlo con el equipo del Módulo de Gestión.

**Checkpoint**: El backend compila con Spring Cloud AWS SQS y puede conectarse a una cola local de LocalStack o a una cola real según ambiente.

---

## Phase 2: Data Integrity & Domain Foundations

**Purpose**: Ajustar el esquema de datos para auditoría asíncrona, idempotencia y control de orden.

- [ ] T006 Mantener entidades base:
  - `Paquete`: `idPaquete`, `idRuta`, `estadoActual`.
  - `HistorialEstado`: `idPaquete`, `estado`, `fecha`.
  - `LogSincronizacion`: auditoría de eventos SQS.
- [ ] T007 Extender `PaqueteEntity` con:
  - `fechaUltimoEvento`
  - `secuenciaUltimoEvento`
  - `@Version` para bloqueo optimista ante eventos concurrentes.
- [ ] T008 Extender `HistorialEstadoEntity` con:
  - `eventId`
  - `fechaEvento`
  - `secuenciaEvento`
- [ ] T009 Reestructurar `LogSincronizacionEntity`, eliminando dependencia de `codigoRespuestaHTTP` como campo principal y agregando:
  - `eventId`
  - `sqsMessageId`
  - `idRuta`
  - `idPaquete`
  - `estadoRecibido`
  - `payloadRecibido`
  - `estadoProcesamiento`
  - `motivoError`
  - `fechaRecepcion`
  - `fechaProcesamiento`
  - `numeroIntentos`
- [ ] T010 Crear índice único sobre `eventId` en `LogSincronizacion` para garantizar idempotencia.
- [ ] T011 Mantener `EstadoPaquete` con reglas de pago:

  | Estado | % de Pago |
  |---|---:|
  | `ENTREGADO` | 100 |
  | `DEVUELTO` | 50 |
  | `DAÑADO` | 0 |
  | `EXTRAVIADO` | 0 |

- [ ] T012 Crear `EstadoProcesamientoEvento` con valores:
  - `PROCESADO`
  - `DUPLICADO`
  - `DESCARTADO_ATRASADO`
  - `ERROR_PAQUETE_NO_ENCONTRADO`
  - `ERROR_ESTADO_NO_MAPEADO`
  - `ERROR_PAYLOAD_INVALIDO`
  - `ERROR_TECNICO`

**Checkpoint**: La base de datos soporta idempotencia, auditoría de eventos SQS, control de concurrencia y control de orden.

---

## Phase 3: User Story 1 — Consumo asíncrono de estado desde SQS

**Goal**: Consumir eventos de estado de paquete desde SQS, validarlos, aplicar reglas financieras y persistir historial/auditoría sin comunicación HTTP saliente.

**Independent Test**: Publicar un mensaje en LocalStack SQS con estado `ENTREGADO`; esperar con Awaitility hasta que el backend lo consuma; verificar `estadoActual`, `HistorialEstado` y `LogSincronizacion`.

### Tests para User Story 1

- [ ] T013 [P] [US1] Test unitario de `EstadoPaqueteService`: validar `ENTREGADO`→100, `DEVUELTO`→50, `DAÑADO`→0, `EXTRAVIADO`→0.
- [ ] T014 [P] [US1] Test unitario de mapper `EventoEstadoPaqueteMessageMapper`: JSON SQS válido → modelo de dominio.
- [ ] T015 [P] [US1] Test unitario para payload inválido: campos obligatorios nulos o JSON malformado → `ERROR_PAYLOAD_INVALIDO`.
- [ ] T016 [P] [US1] Test unitario de idempotencia: evento con `eventId` ya registrado no crea historial duplicado.
- [ ] T017 [P] [US1] Test unitario de evento atrasado: `fechaEvento` o `secuenciaEvento` anterior no sobrescribe `estadoActual`.
- [ ] T018 [P] [US1] Test unitario de paquete inexistente: registra `ERROR_PAQUETE_NO_ENCONTRADO` sin historial.
- [ ] T019 [P] [US1] Test unitario de estado no mapeado: registra `ERROR_ESTADO_NO_MAPEADO` sin cálculo de pago.
- [ ] T020 [P] [US1] Test de integración con LocalStack SQS: mensaje válido termina como `PROCESADO`.
- [ ] T021 [P] [US1] Test de integración con LocalStack SQS: error técnico simulado no elimina el mensaje y permite reintento/DLQ.
- [ ] T022 [P] [US1] Test de consulta REST: `GET /api/sincronizacion/logs` retorna logs de eventos SQS con paginación.
- [ ] T023 [P] [US1] Test de consulta REST: `GET /api/paquetes/{idPaquete}/historial` retorna historial ordenado por fecha descendente.

### Implementation para User Story 1

- [ ] T024 [P] [US1] Crear `EventoEstadoPaqueteMessageDTO` con campos `eventId`, `idRuta`, `idPaquete`, `estado`, `fechaEvento`, `secuenciaEvento`, `origen`.
- [ ] T025 [P] [US1] Crear `EventoEstadoPaqueteSqsListener.java` con `@SqsListener` para la cola configurada.
- [ ] T026 [P] [US1] El listener debe delegar inmediatamente en `ProcesarEventoEstadoPaqueteUseCase`; no debe contener lógica de negocio.
- [ ] T027 [P] [US1] Implementar `ProcesarEventoEstadoPaqueteUseCase` con `@Transactional`:
  1. Registrar recepción del evento en auditoría.
  2. Validar payload obligatorio.
  3. Verificar idempotencia por `eventId`.
  4. Validar existencia de paquete.
  5. Validar estado contra `EstadoPaquete`.
  6. Validar orden por `fechaEvento` o `secuenciaEvento`.
  7. Actualizar `Paquete.estadoActual`.
  8. Registrar `HistorialEstado` solo si hay cambio válido o evento nuevo aplicable.
  9. Marcar log como `PROCESADO`, `DUPLICADO`, `DESCARTADO_ATRASADO` o error funcional según corresponda.
- [ ] T028 [P] [US1] Implementar idempotencia en repositorio mediante `existsByEventId` y restricción única en base de datos.
- [ ] T029 [P] [US1] Implementar control de orden en dominio: un evento con fecha/secuencia menor a la última aplicada no modifica el paquete.
- [ ] T030 [P] [US1] Implementar errores funcionales como eventos auditados no reintentables.
- [ ] T031 [P] [US1] Permitir reintentos SQS únicamente para errores técnicos no controlados, evitando capturarlos como éxito.
- [ ] T032 [US1] Mantener endpoints de lectura para frontend:
  - `GET /api/paquetes/{idPaquete}/historial?page={page}&size={size}`
  - `GET /api/sincronizacion/logs?page={page}&size={size}`
  - `GET /api/sincronizacion/logs/paquetes/{idPaquete}?page={page}&size={size}`
- [ ] T033 [US1] Actualizar DTO `LogSincronizacionResponseDTO` para exponer campos de evento SQS, no campos HTTP:
  - `id`
  - `eventId`
  - `sqsMessageId`
  - `idRuta`
  - `idPaquete`
  - `estadoRecibido`
  - `estadoProcesamiento`
  - `motivoError`
  - `payloadRecibido`
  - `fechaRecepcion`
  - `fechaProcesamiento`

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T034 Añadir métricas de consumo SQS: mensajes recibidos, procesados, duplicados, descartados, enviados a DLQ y tiempo promedio de procesamiento.
- [ ] T035 Añadir índices en `HistorialEstado(idPaquete, fecha DESC)` y `LogSincronizacion(idPaquete, fechaRecepcion DESC)`.
- [ ] T036 Añadir endpoint administrativo de solo lectura para consultar logs por `estadoProcesamiento`.
- [ ] T037 Documentar en Swagger/OpenAPI únicamente endpoints REST de consulta; la integración de escritura queda documentada como contrato SQS.
- [ ] T038 Añadir guía de ejecución local con LocalStack y comandos para crear cola + DLQ.

---

## Dependencies & Execution Order

**SQS antes de lógica de negocio**: La configuración de cola, DLQ y listener debe existir antes de implementar el caso de uso para poder probar el flujo completo con LocalStack.

**Idempotencia antes del listener productivo**: El índice único por `eventId` debe existir antes de conectar el listener a la cola real. SQS puede entregar mensajes más de una vez.

**Control de orden desde la primera versión**: Los eventos pueden llegar desordenados. `fechaEvento` o `secuenciaEvento` deben validarse antes de actualizar `estadoActual`.

**No WireMock para esta feature**: Las pruebas ya no simulan endpoints HTTP. Deben usar LocalStack/Testcontainers para SQS y Mockito para casos unitarios.

**No Resilience4j HTTP para esta feature**: Los reintentos se gobiernan con SQS `visibilityTimeout`, redrive policy y DLQ.

**Frontend al final**: React solo consume endpoints de lectura del backend financiero. No publica mensajes SQS ni dispara sincronización manual.

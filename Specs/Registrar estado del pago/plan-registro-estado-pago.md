# Implementation Plan: Registro asíncrono del estado del pago

**Date**: 2026-04-10
**Spec**: [Registrar estado del pago.md]

## Summary

El objetivo de esta funcionalidad es registrar y actualizar automáticamente el estado del pago asociado a una liquidación previamente calculada, mediante la recepción asíncrona de eventos provenientes de la entidad financiera. El sistema debe recibir el evento, confirmar su recepción sin bloquear al emisor, procesarlo en segundo plano, crear o actualizar el estado del pago correspondiente, garantizar idempotencia frente a eventos duplicados, validar que la liquidación exista, bloquear transiciones inválidas cuando el pago ya se encuentre en estado final, controlar el orden de eventos mediante marcas de tiempo o secuencia, y rechazar con mensaje explícito cualquier estado de pago desconocido.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+

**Primary Dependencies**: Spring Boot (Web, Data JPA, Validation, Security), PostgreSQL Driver, Axios

**Storage**: PostgreSQL 15

**Testing**: JUnit 5, Mockito, Testcontainers / Jest

**Target Platform**: AWS

**Project Type**: Web application

**Performance Goals**: Responder la recepción del evento con HTTP 202 en menos de 200ms y completar el procesamiento de eventos válidos en menos de 120 segundos (SC-001).

**Constraints**: Idempotencia estricta por identificador de transacción bancaria (SC-002), consistencia transaccional en la actualización del estado del pago, rechazo explícito de estados desconocidos con mensaje "Error: estado inválido" (edge case del spec), protección de estados finales (FR-006), procesamiento desacoplado del hilo de recepción (FR-002).

**Scale/Scope**: Preparado para recibir eventos bancarios concurrentes de múltiples pagos sin generar duplicados ni inconsistencias de estado (SC-004).

## Project Structure

### Documentation (this feature)

```text
specs/registrar-estado-del-pago/
├── plan.md              # Este archivo
└── spec.md              # Especificación: Registrar estado del pago.md
```

### Source Code (repository root)

```text
project/
├── backend/
│   ├── src/main/java/com/logistica/
│   │
│   │   ├── application/                             # Casos de uso
│   │   │   ├── usecases/
│   │   │   │   ├── pago/
│   │   │   │   │   ├── ProcesarEventoPagoUseCase.java
│   │   │   │   │   ├── ConsultarEstadoPagoUseCase.java
│   │   │   │   │   └── RegistrarEventoUseCase.java
│   │   │   │
│   │   │   └── dtos/
│   │   │       ├── request/                         # Entrada webhook
│   │   │       │   └── EventoPagoRequestDTO.java
│   │   │       │
│   │   │       └── response/                        # Salida API
│   │   │           └── EstadoPagoResponseDTO.java
│   │
│   │   ├── domain/                                  # Núcleo del negocio
│   │   │   ├── models/
│   │   │   │   ├── Pago.java
│   │   │   │   ├── EstadoPago.java
│   │   │   │   ├── EventoTransaccion.java
│   │   │   │   └── Penalidad.java
│   │   │   │
│   │   │   ├── enums/
│   │   │   │   ├── EstadoPagoEnum.java
│   │   │   │   └── TipoEventoPago.java
│   │   │   │
│   │   │   ├── repositories/                        # Puertos
│   │   │   │   ├── PagoRepository.java
│   │   │   │   ├── EstadoPagoRepository.java
│   │   │   │   └── EventoRepository.java
│   │   │   │
│   │   │   ├── services/                            # Lógica de dominio compleja
│   │   │   │   ├── ProcesadorEstadoPagoService.java
│   │   │   │   └── IdempotenciaService.java
│   │   │   │
│   │   │   ├── validators/                          # Reglas de transición
│   │   │   │   └── TransicionEstadoValidator.java
│   │   │   │
│   │   │   └── exceptions/
│   │   │       ├── EventoDuplicadoException.java
│   │   │       ├── TransicionInvalidaException.java
│   │   │       └── PagoNoEncontradoException.java
│   │
│   │   ├── infrastructure/                          # Implementación técnica
│   │   │   ├── persistence/
│   │   │   │   ├── entities/                        # JPA
│   │   │   │   │   ├── PagoEntity.java
│   │   │   │   │   ├── EstadoPagoEntity.java
│   │   │   │   │   └── EventoEntity.java
│   │   │   │   │
│   │   │   │   └── repositories/                    # Spring Data
│   │   │   │
│   │   │   ├── web/
│   │   │   │   ├── controllers/
│   │   │   │   │   └── WebhookPagoController.java  # Recepción eventos
│   │   │   │   │
│   │   │   │   └── handlers/
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── async/                              # Ejecución asíncrona
│   │   │   │   ├── AsyncConfig.java
│   │   │   │   └── TaskExecutorConfig.java
│   │   │   │
│   │   │   ├── security/                           # Seguridad webhook
│   │   │   │   └── WebhookSecurityConfig.java
│   │   │   │
│   │   │   ├── adapters/                           # Mappers
│   │   │   │   └── PagoMapper.java
│   │   │   │
│   │   │   └── config/                             # Config global (CORS, etc)
│   │
│   │   └── shared/
│   │       ├── utils/
│   │       └── constants/
│
│   ├── src/main/resources/
│   │   ├── db/migration/
│   │   │   └── Vx__registro_estado_pago.sql
│   │   └── application.yml
│   │
│   └── pom.xml / build.gradle
│
│
├── frontend/
│   ├── src/
│   │
│   │   ├── app/                                  # Config global (router, estado)
│   │
│   │   ├── modules/                              # Por features
│   │   │   ├── pagos/
│   │   │   │   ├── components/                  # Estados, mensajes de procesamiento
│   │   │   │   ├── pages/                       # Seguimiento de pago
│   │   │   │   ├── services/                    # Axios calls
│   │   │   │   └── hooks/                       # Manejo de polling o estado async
│   │   │
│   │   ├── shared/
│   │   │   ├── components/                      # UI reutilizable
│   │   │   ├── services/                        # Axios base
│   │   │   └── utils/
│   │
│   │   ├── assets/
│   │   └── styles/
│
│   └── package.json
```

**Structure Decision**: Se separa claramente la recepción del evento, su persistencia inicial y su procesamiento de negocio. Esto permite responder rápido a la entidad financiera y procesar de forma segura la creación o actualización del estado del pago sin bloquear la comunicación externa.

---

## Phase 1: Setup & DevOps Foundation (Shared Infrastructure)

**Purpose**: Configurar la infraestructura de recepción asíncrona, seguridad y procesamiento desacoplado.

- [ ] T001 Configurar la recepción segura del evento bancario en Spring Boot, incluyendo autenticación del emisor y validación básica del payload.
- [ ] T002 Habilitar procesamiento asíncrono en backend y definir un `ThreadPoolTaskExecutor` dedicado para el flujo de pagos.
- [ ] T003 Configurar variables de entorno y secretos para validar la autenticidad de los eventos provenientes de la entidad financiera.
- [ ] T004 Definir el contrato estándar de respuesta inmediata del webhook: HTTP 202 para eventos recibidos correctamente, y el mensaje `"Error: estado inválido"` para eventos con estado desconocido (edge case del spec).

---

## Phase 2: Foundational & Data Integrity (Blocking Prerequisites)

**Purpose**: Definir el modelo de datos, las restricciones de idempotencia, el bloqueo optimista ante concurrencia y las reglas de transición antes de implementar la historia de usuario.

- [ ] T005 Crear las entidades JPA y DTOs correspondientes según las Key Entities del spec:
    - `Pago` (IdPago, idUsuario, MontoBase, fecha, IdPenalidad, MontoNeto, idLiquidación) → `PagoDTO`
    - `EstadoPago` (IdEstadoPago, idPago, estado) → `EstadoPagoDTO`
    - `EventoTransaccion` (IdEvento, idPago, payloadRecibido, fechaRecepcion, procesado) → `EventoTransaccionDTO`
    - `Ajustes/Penalidad` (IdAjustes, TipoAjustes) → `AjustesDTO`
    - `WebhookPagoRequestDTO`
    - `WebhookAcceptedResponseDTO`
    - `ErrorResponseDTO`
- [ ] T006 Definir el enum `EstadoPagoEnum` con los estados válidos del sistema según el spec: `PENDIENTE`, `EN_PROCESO`, `PAGADO`, `RECHAZADO`. Cualquier valor recibido fuera de este enum debe disparar el mensaje `"Error: estado inválido"` y registrarse en logs.
- [ ] T007 Añadir `@Version` en la entidad `Pago` o `EstadoPago` para implementar bloqueo optimista desde el inicio, garantizando consistencia ante eventos concurrentes de múltiples pagos (SC-004). Esta restricción debe existir desde que se crean las entidades para evitar un período de vulnerabilidad ante concurrencia.
- [ ] T008 Crear la migración de base de datos para las tablas `pagos`, `estado_pago` y `evento_transaccion`, con claves foráneas hacia la liquidación correspondiente.
- [ ] T009 Configurar una restricción `UNIQUE(id_transaccion_banco)` en `evento_transaccion` para impedir el registro duplicado de un mismo evento bancario (FR-004, SC-002).
- [ ] T010 Implementar en la capa de dominio la matriz de transiciones válidas del estado del pago para impedir cambios inválidos una vez que el pago esté en estado final (FR-006):
    - `PENDIENTE` → `EN_PROCESO` ✓
    - `EN_PROCESO` → `PAGADO` ✓
    - `EN_PROCESO` → `RECHAZADO` ✓
    - Cualquier transición sobre `PAGADO` o `RECHAZADO` → rechazada.
- [ ] T011 Implementar los `JpaRepository` necesarios, incluyendo búsquedas por `idTransaccionBanco`, `idLiquidacion` e historial del último `EstadoPago`.
- [ ] T012 Implementar un `@RestControllerAdvice` global que capture estados inválidos, liquidaciones inexistentes, eventos duplicados y errores de persistencia, retornando mensajes estructurados según el contrato definido en T004.

**Checkpoint**: El backend ya tiene un esquema de datos auditable con idempotencia garantizada a nivel de base de datos, bloqueo optimista ante concurrencia, y reglas explícitas para los estados válidos del pago y sus transiciones permitidas.

---

## Phase 3: User Story 1 — Registrar y actualizar estado del pago vía eventos asíncronos (Prioridad: P1)

**Goal**: Permitir que la entidad financiera envíe eventos asíncronos para registrar o actualizar el estado del pago de una liquidación específica, procesándolos en segundo plano sin bloquear procesos del usuario, preservando consistencia, idempotencia y orden lógico de estados (FR-001 al FR-006).

**Independent Test**: Simular el envío de un payload JSON hacia el webhook y verificar que la recepción retorna HTTP 202 inmediatamente. Verificar que el evento se procesa en segundo plano y que la liquidación correcta queda asociada a un pago cuyo estado se crea o actualiza según corresponda. Repetir la misma transacción para validar idempotencia. Enviar un evento para liquidación inexistente, enviar un estado final sobre un pago ya finalizado, enviar eventos desordenados en el tiempo y enviar un estado inválido para confirmar que el sistema retorna el mensaje `"Error: estado inválido"` y lo registra en logs.

### Tests para User Story 1

- [ ] T013 [P] [US1] Test de integración llamando al webhook con un evento válido de inicio de pago y verificando respuesta HTTP 202 inmediata (FR-002).
- [ ] T014 [P] [US1] Test de integración para verificar que un evento válido crea el registro inicial de `Pago`, crea `EstadoPago` con estado `PENDIENTE` y lo asocia a la liquidación correcta (escenario 1 del spec).
- [ ] T015 [P] [US1] Test de integración para validar que un segundo evento con el mismo `idTransaccionBanco` no duplica registros y preserva idempotencia (escenario 2 del spec, FR-004, SC-002).
- [ ] T016 [P] [US1] Test de integración para validar que un evento de actualización cambia el estado de `EN_PROCESO` a `PAGADO` correctamente (escenario 3 del spec, SC-003).
- [ ] T017 [P] [US1] Test de integración para validar que un evento de actualización cambia el estado de `EN_PROCESO` a `RECHAZADO` correctamente (escenario 3 del spec, SC-003).
- [ ] T018 [P] [US1] Test de integración para validar que si la liquidación no existe, el sistema registra el error en logs, rechaza la actualización y no crea ni actualiza el pago (edge case del spec, FR-005).
- [ ] T019 [P] [US1] Test de integración para validar que si el pago ya está en estado final (`PAGADO` o `RECHAZADO`), una nueva actualización no altera el estado almacenado y queda registrado en auditoría (edge case del spec, FR-006).
- [ ] T020 [P] [US1] Test de integración para validar que si llega el mismo estado actual, el sistema responde con éxito al banco pero no genera cambios persistentes adicionales (edge case del spec, idempotencia garantizada).
- [ ] T021 [P] [US1] Test de concurrencia para validar que eventos desordenados por timestamp o secuencia no sobrescriben un estado más reciente con uno atrasado (edge case del spec, SC-004).
- [ ] T022 [P] [US1] Test unitario para validar que un estado de pago desconocido dispara el mensaje `"Error: estado inválido"` hacia el banco y registra el fallo en logs (edge case del spec).
- [ ] T023 [P] [US1] Test unitario para validar que las transiciones inválidas definidas en la matriz de T010 son rechazadas antes de persistirse (FR-006).
- [ ] T024 [P] [US1] Test de componente en React para verificar la consulta del estado actual del pago y la visualización del resultado procesado para una liquidación.

### Implementation para User Story 1

- [ ] T025 [P] [US1] Implementar el endpoint `POST /api/webhooks/pagos` para recibir el payload bancario, validar su estructura mínima y el estado recibido contra `EstadoPagoEnum`, persistir el evento recibido y responder HTTP 202 sin ejecutar el procesamiento pesado en el hilo de entrada. Si el estado es desconocido, responder con el mensaje `"Error: estado inválido"` y registrar en logs.
- [ ] T026 [P] [US1] Implementar `EventoTransaccionService.java` para registrar el evento recibido con su payload crudo, fecha de recepción, identificador bancario y estado inicial de procesamiento.
- [ ] T027 [P] [US1] Implementar `PagoEventProcessor.java` con procesamiento asíncrono (`@Async`) del evento recibido, ejecutado por el `ThreadPoolTaskExecutor` configurado en Phase 1.
- [ ] T028 [P] [US1] Implementar en el procesador la validación de existencia de la liquidación asociada antes de crear o actualizar cualquier pago. Si no existe, registrar el error en logs y rechazar la actualización (FR-005).
- [ ] T029 [P] [US1] Implementar la lógica de creación del registro `Pago` cuando el evento representa el inicio del proceso de pago de una liquidación válida.
- [ ] T030 [P] [US1] Implementar la lógica de creación del estado inicial de pago en `EstadoPago` cuando el pago aún no existe.
- [ ] T031 [P] [US1] Implementar la lógica de actualización de `EstadoPago` cuando el evento representa una transición válida sobre un pago ya registrado, validando la matriz de transiciones antes de persistir.
- [ ] T032 [P] [US1] Implementar la verificación de idempotencia por `idTransaccionBanco` antes de crear nuevos registros o reprocesar eventos repetidos (FR-004).
- [ ] T033 [P] [US1] Implementar la verificación del timestamp o secuencial del evento entrante contra el último estado persistido para descartar eventos atrasados (SC-004).
- [ ] T034 [P] [US1] Implementar la validación de estado final para impedir modificaciones sobre pagos ya marcados como `PAGADO` o `RECHAZADO`, registrando en auditoría el intento (FR-006, edge case del spec).
- [ ] T035 [P] [US1] Implementar el registro de auditoría funcional del procesamiento del evento, incluyendo resultado, motivo de descarte o causa de error.
- [ ] T036 [US1] Exponer el endpoint `GET /api/pagos/liquidaciones/{idLiquidacion}/estado` para consultar el estado actual del pago asociado a una liquidación.
- [ ] T037 [US1] Desarrollar en React la vista de consulta del estado del pago de una liquidación, incluyendo visualización del estado actual y mensajes funcionales de procesamiento, rechazo o error.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T038 Incorporar métricas de procesamiento para medir tiempos de recepción, cola, éxito, rechazo e idempotencia de eventos.
- [ ] T039 Estandarizar los logs funcionales y técnicos del flujo asíncrono para facilitar soporte y trazabilidad operativa.
- [ ] T040 Verificar la integración entre el módulo de liquidaciones calculadas y el módulo de registro del estado del pago para asegurar consistencia de claves y relaciones.

---

## Dependencies & Execution Order

**Modelo de datos y bloqueo optimista desde Phase 2**: Las tablas, restricciones únicas, estados válidos, matriz de transiciones, repositorios y el campo `@Version` deben existir antes de exponer el endpoint externo. El bloqueo optimista no puede agregarse después porque existe un período de vulnerabilidad ante concurrencia desde el primer evento procesado.

**Contrato de respuesta definido antes del endpoint**: El mensaje `"Error: estado inválido"` y el HTTP 202 deben estar documentados en el contrato de T004 antes de implementar el webhook, para que el equipo de la entidad financiera sepa qué esperar ante cada escenario.

**Procesador antes de la UI**: La lógica de recepción, persistencia inicial y actualización del estado del pago debe quedar estabilizada y probada antes de construir la consulta visual del estado en frontend.

**Frontend al final de la historia**: React debe consumir únicamente el estado consolidado que el backend ya procesó y validó, sin participar en el flujo de registro asíncrono del banco.

**Dependencia de liquidaciones previas**: Esta funcionalidad depende de que la liquidación ya haya sido calculada y almacenada. Sin una liquidación válida, no puede registrarse ni actualizarse el pago asociado.
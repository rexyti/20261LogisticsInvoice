# Implementation Plan: Registro asíncrono del estado del pago

**Date**: 2026-04-08
**Spec**: [Registrar estado del pago.md]

## Summary

Este plan detalla la construcción del módulo de conciliación bancaria asíncrona. El sistema expondrá un Webhook (o consumirá una cola de mensajería) para recibir actualizaciones de estado de pagos desde una entidad financiera. Para garantizar la consistencia, el sistema implementará un motor de máquina de estados (State Machine) que validará las transiciones de pago, gestionará la idempotencia para evitar duplicados, y utilizará marcas de tiempo (timestamps) para descartar eventos desordenados, todo esto procesado en segundo plano sin bloquear al emisor.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+
**Primary Dependencies**: Spring Boot (Web, Data JPA, Spring State Machine - opcional pero recomendado), PostgreSQL Driver, Axios
**Storage**: PostgreSQL 15
**Testing**: JUnit 5, Mockito, Testcontainers / Jest
**Target Platform**: AWS (API Gateway -> SQS/Webhook -> Spring Boot)
**Project Type**: Web application (Backend Webhook/Consumer + Frontend Dashboard)
**Performance Goals**: Responder al banco con 202 Accepted en < 200ms; procesar el evento final en < 120s.
**Constraints**: Garantizar idempotencia estricta mediante el ID de transacción bancaria. Bloquear transiciones inválidas (ej. de "Pagado" a "En proceso").

## Project Structure

### Documentation (this feature)

```text
specs/registro-estado-pago/
├── plan.md              # Este archivo 
└── spec.md             # Especificación: Registrar estado del pago.md
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/logistica/
│   ├── config/          # Configuración de asincronía (@EnableAsync) o SQS
│   ├── controllers/     # Webhook endpoint para recibir el JSON del banco
│   ├── models/          # Entidades JPA (Pago, EstadoPago, EventoTransaccion)
│   ├── statemachine/    # Lógica de validación de estados y transiciones
│   ├── repositories/    # Interfaces Spring Data JPA
│   └── services/        # Procesamiento en segundo plano de los eventos
└── src/test/java/       # Pruebas de concurrencia e idempotencia

frontend/
├── src/
│   ├── components/      # UI: Dashboard de conciliación bancaria
│   ├── services/        # Peticiones para consultar estados de pago
│   └── pages/           # Vistas para el equipo de tesorería/finanzas
└── package.json
```

**Structure Decision**: Se añade un paquete statemachine/ o lógica equivalente para separar las complejas reglas de transición de estados de los servicios CRUD tradicionales.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Configurar la infraestructura para procesamiento en segundo plano y Webhooks.

- [ ] T001 Configurar la anotación @EnableAsync en Spring Boot y definir un ThreadPoolTaskExecutor personalizado para manejar los hilos de procesamiento en segundo plano sin saturar el servidor.
- [ ] T002 (Si se usa mensajería AWS) Configurar el listener de AWS SQS para desencolar los eventos financieros.
- [ ] T003 Configurar variables de entorno para tokens de seguridad del Webhook (autenticación de la entidad financiera).

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Diseñar el esquema de base de datos auditable y la máquina de estados.

- [ ] T004 Crear entidades JPA: Pago, EstadoPago, EventoTransaccion y Ajustes.
- [ ] T005 Configurar restricción UNIQUE en la tabla EventoTransaccion usando el ID de transacción bancaria para garantizar la idempotencia a nivel de base de datos (FR-004).
- [ ] T006 Implementar la lógica de la Máquina de Estados: Definir transiciones válidas (ej. PENDIENTE -> EN_PROCESO -> PAGADO/RECHAZADO) y rechazar retrocesos.
- [ ] T007 Implementar los JpaRepository necesarios.

**Checkpoint**: Base de datos preparada para soportar alta concurrencia y rechazar eventos duplicados a nivel de motor SQL.

---

## Phase 3: User Story 1 - Recepción Asíncrona e Idempotencia (Priority: P1)

**Goal**: Recibir el evento del banco, responder inmediatamente y encolar para proceso.

**Independent Test**: Enviar un POST al webhook con un payload del banco. Verificar que responde HTTP 202 al instante. Luego, verificar en la DB que el EventoTransaccion se guardó y el EstadoPago inicial se registró.

### Tests for User Story 1

- [ ] T008 [P] [US1] Test de integración llamando al Webhook dos veces con el mismo ID de transacción; verificar que el segundo retorna éxito pero no duplica registros en la DB.
- [ ] T009 [P] [US1] Test unitario para validar que si la liquidación no existe, el sistema registra el error en logs y aborta (Edge Case).

### Implementation for User Story 1

- [ ] T010 [P] [US1] Crear el controlador REST (Webhook) que reciba el JSON, guarde un registro crudo en EventoTransaccion (estado = PENDIENTE_DE_PROCESO) y devuelva 202 Accepted (FR-002).
- [ ] T011 [US1] Crear el servicio asíncrono (@Async) que lee el evento crudo, busca la liquidación y crea el registro en Pago y EstadoPago.
- [ ] T012 [US1] Implementar validación: Si la liquidación asociada no existe, marcar el EventoTransaccion como ERROR_LIQUIDACION_NO_ENCONTRADA.

---

## Phase 4: Edge Cases & Transiciones de Estado (Priority: P1)

**Goal**: Procesar actualizaciones, evitar eventos desordenados y proteger estados finales.

**Independent Test**: Simular la llegada de un evento "En proceso" con fecha 10:05, seguido de un evento "Pagado" con fecha 10:10. Luego enviar un "En proceso" retrasado con fecha 10:02. Validar que el pago quede como "Pagado".

### Tests for User Story 2

- [ ] T013 [P] [US2] Test de concurrencia para validar el ordenamiento de eventos por timestamp (FR-006 / Edge Cases).
- [ ] T014 [P] [US2] Test unitario de la máquina de estados: Intentar pasar de "Rechazado" a "Pagado" y validar que lance una excepción de transición inválida.

### Implementation for User Story 2

- [ ] T015 [P] [US2] Modificar el procesador asíncrono para evaluar el timestamp del payload recibido vs. el timestamp del último EstadoPago registrado. Si el entrante es más antiguo, descartarlo.
- [ ] T016 [US2] Implementar validación de máquina de estados: Si el pago ya está en estado final (Pagado/Rechazado), ignorar actualizaciones e insertar log de auditoría.
- [ ] T017 [US2] Desarrollar un dashboard en React para el equipo de tesorería donde puedan ver en tiempo real el estado de los pagos y una tabla específica para "Eventos fallidos/Huérfanos".

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T018 Implementar bloqueo optimista (@Version en JPA) en la entidad Pago para prevenir problemas de concurrencia si llegan dos eventos del mismo pago en el mismo milisegundo.
- [ ] T019 Añadir métricas de salud (Actuator/Micrometer) para monitorear el tamaño de la cola de eventos asíncronos y tiempos de procesamiento.
- [ ] T020 Refinar la UI de React añadiendo websockets o polling para actualizar el estado del pago en pantalla sin que el usuario recargue la página.

---

## Dependencies & Execution Order

**Persistencia e Idempotencia**: La configuración de la base de datos con restricciones únicas (UNIQUE) es el primer paso crítico. Sin esto, la concurrencia romperá los datos.

**Máquina de Estados**: Definir las reglas lógicas en código puro (Java) probadas exhaustivamente con JUnit antes de conectarlas a la base de datos o la web.

**Controlador Asíncrono**: Exponer el Webhook asegurando el retorno rápido (202 Accepted) y delegando el procesamiento.

**Dashboard Frontend**: Por último, construir las vistas en React para consumir la información que el backend va consolidando silenciosamente en segundo plano.

# Implementation Plan: Visualizar estado del pago

**Date**: 2026-04-10
**Spec**: [Visualizar estado del pago.md]

## Summary

El objetivo de esta funcionalidad es permitir la consulta segura del estado de los pagos asociados a liquidaciones previamente calculadas. El sistema debe exponer al usuario autorizado una vista clara del estado actual del pago, y en una segunda historia, el detalle completo incluyendo ajustes y penalizaciones aplicadas junto con la posibilidad de descargar un comprobante en PDF.

La funcionalidad está orientada exclusivamente a consulta. No modifica el estado del pago ni ejecuta procesos financieros. Cualquier intento de acceso a un pago ajeno debe bloquearse y registrarse como un evento de seguridad (edge case del spec).

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+

**Primary Dependencies**: Spring Boot (Web, Data JPA, Validation, Security), PostgreSQL Driver, Axios

**Storage**: PostgreSQL 15

**Testing**: JUnit 5, Mockito, Spring Security Test / Jest, React Testing Library

**Target Platform**: AWS

**Project Type**: Web application

**Data Integrity**: La información de pago visualizada debe obtenerse desde registros persistidos y validados; no se permite construir estados calculados en frontend.

**Security**: Spring Security + JWT para garantizar que cada usuario consulte únicamente la información de pago que le corresponde. Los intentos de acceso no autorizado deben registrarse como eventos de seguridad (FR-005, edge case del spec).

**API Pattern**: DTOs de lectura para desacoplar entidades internas de la respuesta expuesta al cliente.

**Performance Goals**: Consulta de lista y detalle del estado del pago en el servidor en menos de 300ms.

**Constraints**: Control de acceso estricto, consistencia con la liquidación asociada, manejo explícito de ausencia de datos, protección frente a accesos no autorizados con registro del intento, respuesta controlada ante fallas del sistema de almacenamiento.

## Project Structure

### Documentation (this feature)

```text
specs/visualizar-estado-pago/
├── plan.md              # Este archivo
└── spec.md              # Especificación: Visualizar estado del pago.md
```

### Source Code (repository root)

```text
```text
project/
├── backend/
│   ├── src/main/java/com/logistica/
│   │
│   │   ├── application/                             # Casos de uso (orquestación)
│   │   │   ├── usecases/
│   │   │   │   ├── pago/
│   │   │   │   │   ├── ProcesarWebhookPagoUseCase.java
│   │   │   │   │   ├── RegistrarEventoPagoUseCase.java
│   │   │   │   │   └── ConsultarEstadoPagoUseCase.java
│   │   │   │
│   │   │   └── dtos/
│   │   │       ├── request/
│   │   │       │   └── WebhookPagoRequestDTO.java
│   │   │       │
│   │   │       └── response/
│   │   │           ├── EstadoPagoResponseDTO.java
│   │   │           └── EventoProcesadoResponseDTO.java
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
│   │   │   ├── services/                            # Lógica de dominio crítica
│   │   │   │   ├── ProcesadorEstadoPagoService.java
│   │   │   │   ├── IdempotenciaService.java
│   │   │   │   └── AuditoriaPagoService.java
│   │   │   │
│   │   │   ├── validators/                          # Reglas de negocio
│   │   │   │   └── TransicionEstadoValidator.java
│   │   │   │
│   │   │   ├── events/                              # Eventos de dominio (🔥 pro)
│   │   │   │   └── PagoProcesadoEvent.java
│   │   │   │
│   │   │   └── exceptions/
│   │   │       ├── EventoDuplicadoException.java
│   │   │       ├── TransicionInvalidaException.java
│   │   │       └── PagoNoEncontradoException.java
│   │
│   │   ├── infrastructure/                          # Implementación técnica
│   │   │   ├── persistence/
│   │   │   │   ├── entities/
│   │   │   │   │   ├── PagoEntity.java
│   │   │   │   │   ├── EstadoPagoEntity.java
│   │   │   │   │   └── EventoEntity.java
│   │   │   │   │
│   │   │   │   └── repositories/
│   │   │   │
│   │   │   ├── web/
│   │   │   │   ├── controllers/
│   │   │   │   │   └── WebhookPagoController.java
│   │   │   │   │
│   │   │   │   └── handlers/
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── async/                              # Procesamiento asíncrono
│   │   │   │   ├── AsyncConfig.java
│   │   │   │   └── TaskExecutorConfig.java
│   │   │   │
│   │   │   ├── messaging/                          # (opcional) eventos externos
│   │   │   │   └── EventPublisher.java
│   │   │   │
│   │   │   ├── security/
│   │   │   │   └── WebhookSecurityConfig.java
│   │   │   │
│   │   │   ├── adapters/
│   │   │   │   └── PagoMapper.java
│   │   │   │
│   │   │   └── config/
│   │
│   │   └── shared/
│   │       ├── utils/
│   │       ├── constants/
│   │       └── logging/
│
│   ├── src/main/resources/
│   │   ├── db/migration/
│   │   │   └── Vx__registro_estado_pago.sql
│   │   │
│   │   └── application.yml
│   │
│   └── pom.xml / build.gradle
│
│
├── frontend/
│   ├── src/
│   │
│   │   ├── app/
│   │
│   │   ├── modules/
│   │   │   ├── pagos/
│   │   │   │   ├── components/                  # Estado, timeline, mensajes async
│   │   │   │   ├── pages/                       # Seguimiento de pago
│   │   │   │   ├── services/                    # Axios calls
│   │   │   │   └── hooks/                       # Polling, refresh automático
│   │   │
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   ├── services/
│   │   │   └── utils/
│   │
│   │   ├── assets/
│   │   └── styles/
│
│   └── package.json
```



**Structure Decision**: Se utiliza una arquitectura desacoplada con separación entre repositorios, servicios, controladores y DTOs de lectura. La validación de permisos se centraliza en la capa de servicio. El registro de intentos de acceso no autorizado se implementa en la misma capa de seguridad para garantizar trazabilidad. La generación del comprobante se implementa en una utilidad de backend para mantener el documento consistente con la información persistida.

---

## Phase 1: Setup & DevOps Foundation (Shared Infrastructure)

**Purpose**: Preparar la base de seguridad, conectividad y estructura mínima para exponer consultas de estado del pago.

- [ ] T001 Configurar Spring Boot con dependencias: Web, Data JPA, Validation, Security y el driver de PostgreSQL.
- [ ] T002 Configurar React con Axios e interceptores globales para manejo uniforme de errores HTTP.
- [ ] T003 Definir la configuración base de seguridad con JWT y reglas de autorización para endpoints de consulta de pagos.
- [ ] T004 Preparar la integración con Flyway para versionar los cambios de esquema requeridos por esta funcionalidad.
- [ ] T005 Configurar CORS para permitir el consumo seguro de la API desde el frontend autorizado.

---

## Phase 2: Foundational & Data Integrity (Blocking Prerequisites)

**Purpose**: Definir las entidades, consultas y reglas de acceso necesarias antes de implementar las historias de usuario.

- [ ] T006 Revisar y completar las entidades JPA involucradas en la consulta según las Key Entities del spec: `Pago` (IdPago, idUsuario, MontoBase, fecha, IdPenalidad, MontoNeto, idLiquidación), `EstadoPago` (IdEstadoPago, idPago, estado), `Liquidacion`, `Usuario` (idUser, nombre, TotalPagado, PagosPendientes), `Ajustes/Penalidad` (IdAjustes, TipoAjustes).
- [ ] T007 Crear los DTOs de lectura:
    - `PagoListDTO`: identificador del pago, identificador de la liquidación asociada, fecha, monto y estado del pago.
    - `PagoDetailDTO`: MontoBase, MontoNeto, fecha, Ajustes/penalidades, estado del pago, idRuta e idLiquidación (campos requeridos por la User Story 2 del spec).
- [ ] T008 Implementar los repositorios necesarios para consulta:
    - `PagoRepository` con método de búsqueda por usuario autenticado y por identificador específico.
    - `EstadoPagoRepository`
    - `LiquidacionRepository`
- [ ] T009 Implementar las excepciones de negocio requeridas:
    - `PagoNotFoundException`
    - `AccessDeniedPaymentException`
    - `StorageUnavailableException`
- [ ] T010 Implementar un `@RestControllerAdvice` global para capturar errores de negocio, errores de acceso y fallas del sistema de almacenamiento, retornando respuestas JSON estructuradas con código HTTP apropiado.
- [ ] T011 Definir y aplicar índices de base de datos sobre `id_usuario`, `id_liquidacion`, `fecha` y `estado` para optimizar las consultas frecuentes.
- [ ] T012 Implementar el mecanismo de registro de eventos de seguridad para intentos de acceso no autorizado a pagos ajenos, requerido explícitamente por el edge case del spec. Este registro debe ocurrir en la capa de servicio antes de retornar el error al cliente.

**Checkpoint**: El backend puede autenticar al usuario, identificar su contexto de acceso, recuperar correctamente los pagos persistidos con su relación a la liquidación, y registrar automáticamente cualquier intento de acceso no autorizado.

---

## Phase 3: User Story 1 — Consultar estado del pago (Prioridad: P1)

**Goal**: Permitir al usuario autorizado consultar el estado actual de su pago (Pagado, Pendiente, Rechazado, En proceso) asociado a una liquidación, mostrando la liquidación asociada cuando el pago está aprobado y el motivo del rechazo cuando fue rechazado (escenarios 1, 2 y 3 del spec).

**Independent Test**: Autenticarse con un usuario válido y consultar el listado de pagos. Verificar que el sistema muestra el estado correcto para cada pago (Pagado con liquidación asociada, Pendiente, Rechazado con motivo). Verificar que el acceso a pagos ajenos es bloqueado y que el intento queda registrado como evento de seguridad.

### Tests para User Story 1

- [ ] T013 [P] [US1] Test de integración con `@WithMockUser` para validar que el endpoint de listado retorna únicamente los pagos que el usuario autenticado tiene permitido consultar (FR-004, FR-005).
- [ ] T014 [P] [US1] Test de integración para validar que un pago con estado "Pagado" muestra también la liquidación asociada (escenario 1 del spec).
- [ ] T015 [P] [US1] Test de integración para validar que un pago con estado "Pendiente" muestra el estado correctamente (escenario 2 del spec).
- [ ] T016 [P] [US1] Test de integración para validar que un pago con estado "Rechazado" muestra el motivo del rechazo (escenario 3 del spec).
- [ ] T017 [P] [US1] Test de integración para validar que la búsqueda de un pago inexistente retorna HTTP 404 con mensaje funcional comprensible (edge case del spec).
- [ ] T018 [P] [US1] Test de seguridad para validar que un usuario sin permisos sobre un pago específico recibe HTTP 403 y que el intento queda registrado como evento de seguridad (edge case del spec, FR-005).
- [ ] T019 [P] [US1] Test de controlador para validar que una falla del sistema de almacenamiento retorna HTTP 503.
- [ ] T020 [P] [US1] Test de componente en React para verificar que la lista de pagos muestra estado, fecha, identificadores y acceso al detalle.
- [ ] T021 [P] [US1] Test de componente en React para verificar que se muestran mensajes adecuados para pago inexistente y sistema no disponible.

### Implementation para User Story 1

- [ ] T022 [P] [US1] Implementar en `PagoService.java` la lógica `listarPagosDelUsuarioAutenticado(...)`, que obtiene el identificador del usuario desde el contexto de seguridad y consulta únicamente los pagos autorizados para ese usuario (FR-004, FR-005).
- [ ] T023 [P] [US1] Implementar en `PagoService.java` la lógica `obtenerEstadoPago(...)`, que recupera el estado actual del pago, valida permisos, y en caso de acceso no autorizado registra el intento como evento de seguridad antes de retornar HTTP 403.
- [ ] T024 [P] [US1] Crear el endpoint `GET /api/pagos` para listar los pagos visibles por el usuario autenticado, con soporte de paginación y filtros básicos.
- [ ] T025 [P] [US1] Crear el endpoint `GET /api/pagos/{id}` para consultar el estado actual de un pago específico autorizado, incluyendo la liquidación asociada cuando el estado es "Pagado" y el motivo cuando el estado es "Rechazado".
- [ ] T026 [US1] Implementar en React la vista de listado de pagos mostrando identificador del pago, identificador de la liquidación, fecha, monto y estado del pago.
- [ ] T027 [US1] Implementar en React la búsqueda de un pago específico dentro del listado disponible para el usuario.
- [ ] T028 [US1] Implementar mensajes funcionales explícitos para los casos: pago inexistente, acceso no autorizado y sistema de almacenamiento no disponible.

---

## Phase 4: User Story 2 — Consultar detalle del pago y descargar comprobante (Prioridad: P2)

**Goal**: Permitir al usuario autorizado visualizar la información detallada del pago incluyendo MontoBase, MontoNeto, fecha, ajustes y penalizaciones aplicadas, y descargar el comprobante en PDF con los campos requeridos por el spec: IDRuta, Fecha de Emisión, MontoBase, MontoNeto y ajustes de penalidad (FR-003, FR-006).

**Independent Test**: Autenticarse con un usuario válido, acceder al detalle de un pago autorizado y verificar que el sistema muestra MontoBase, MontoNeto, fecha, ajustes/penalidades y estado. Descargar el comprobante y verificar que el PDF contiene IDRuta, Fecha de Emisión, MontoBase, MontoNeto y ajustes de penalidad. Verificar que un usuario sin permisos no puede acceder al detalle ni descargar el comprobante de un pago ajeno, y que el intento queda registrado.

### Tests para User Story 2

- [ ] T029 [P] [US2] Test de integración para validar que el endpoint de detalle retorna MontoBase, MontoNeto, fecha, ajustes/penalidades y estado del pago cuando el registro existe y el usuario tiene permisos (escenario 1 de la User Story 2 del spec).
- [ ] T030 [P] [US2] Test de integración para validar que el endpoint de descarga retorna `application/pdf` cuando el pago existe y el usuario tiene permisos (escenario 2 de la User Story 2 del spec).
- [ ] T031 [P] [US2] Test de seguridad para validar que un usuario que intenta acceder al detalle o descargar el comprobante de un pago ajeno recibe HTTP 403 y el intento queda registrado como evento de seguridad.
- [ ] T032 [P] [US2] Test unitario para validar que el generador del comprobante construye correctamente el PDF con los campos requeridos por el spec: IDRuta, Fecha de Emisión, MontoBase, MontoNeto y ajustes de penalidad.
- [ ] T033 [P] [US2] Test de componente en React para verificar que el botón de descarga solicita el archivo y maneja correctamente la respuesta binaria.
- [ ] T034 [P] [US2] Test de componente en React para verificar que se muestra un mensaje de error cuando el comprobante no pueda generarse o el pago no exista.

### Implementation para User Story 2

- [ ] T035 [P] [US2] Añadir la dependencia de generación de PDF al proyecto (ej. iText o Apache PDFBox) en esta fase, ya que es exclusivamente necesaria para esta historia de usuario.
- [ ] T036 [P] [US2] Implementar en `PagoService.java` la lógica `obtenerDetallePago(...)`, que recupera el pago solicitado, valida permisos de acceso y construye el `PagoDetailDTO` con MontoBase, MontoNeto, fecha, ajustes/penalidades, estado del pago, idRuta e idLiquidación.
- [ ] T037 [P] [US2] Implementar una utilidad de backend para construir el comprobante en formato PDF con los campos requeridos por el spec: IDRuta, Fecha de Emisión, MontoBase, MontoNeto y ajustes de penalidad.
- [ ] T038 [P] [US2] Implementar en `PagoService.java` la lógica `generarComprobantePago(...)`, reutilizando la validación de acceso y la recuperación del detalle autorizado del pago.
- [ ] T039 [P] [US2] Crear el endpoint `GET /api/pagos/{id}/detalle` para consultar el detalle completo del pago autorizado.
- [ ] T040 [P] [US2] Crear el endpoint `GET /api/pagos/{id}/comprobante` para descargar el comprobante del pago autorizado.
- [ ] T041 [US2] Implementar en React la vista de detalle del pago mostrando MontoBase, MontoNeto, fecha, ajustes/penalidades y estado.
- [ ] T042 [US2] Implementar en React el botón de descarga del comprobante dentro de la vista de detalle del pago.
- [ ] T043 [US2] Implementar en React la gestión de la respuesta binaria para descargar el archivo PDF generado por el backend.
- [ ] T044 [US2] Mostrar mensajes funcionales claros cuando la descarga falle por inexistencia del pago, falta de permisos o indisponibilidad temporal del sistema.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T045 Configurar perfiles de Spring Boot (`application-dev.yml`, `application-prod.yml`) con variables de entorno para credenciales y configuración de seguridad.
- [ ] T046 Añadir documentación OpenAPI para los endpoints de consulta del estado del pago, detalle y descarga de comprobante.
- [ ] T047 Implementar estados de carga en React para listado, detalle y descarga del comprobante.
- [ ] T048 Refinar la serialización de respuestas para garantizar uniformidad entre errores funcionales, errores de autorización y errores de infraestructura.

---

## Dependencies & Execution Order

**Dependencia funcional**: Esta funcionalidad depende de que los módulos de cálculo de liquidación y de registro del estado del pago ya hayan persistido información válida. Sin esos datos, no existe contenido real que visualizar.

**Seguridad antes que consulta**: La autenticación, autorización y el registro de eventos de seguridad deben implementarse antes de exponer cualquier endpoint de lectura. El filtrado de pagos no puede depender del frontend.

**Repositorios antes de servicios**: Las consultas sobre `Pago`, `EstadoPago` y `Liquidacion` deben estar definidas y probadas antes de construir la capa de servicio.

**User Story 1 antes de User Story 2**: El detalle y el comprobante (US2) dependen de que la consulta de estado (US1) ya esté implementada y validada, ya que reutilizan la misma lógica de validación de acceso.

**Dependencia de generación de PDF en Phase 4**: La librería de PDF se incorpora únicamente cuando se implementa la User Story 2, evitando añadir dependencias innecesarias antes de que sean requeridas.

**Frontend al final**: La interfaz React debe integrarse únicamente cuando los endpoints de listado, detalle y descarga ya devuelvan respuestas estables y seguras.
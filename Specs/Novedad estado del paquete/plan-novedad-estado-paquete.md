# Implementation Plan: Novedad estado del paquete

**Date**: 2026-04-08
**Spec**: [Novedad estado del paquete.md]

## Summary

Esta funcionalidad establece la integración sincrónica entre el Módulo Financiero y el Módulo de Gestión de Paquetes. El sistema realizará peticiones HTTP GET automáticamente al iniciar un proceso de liquidación, para obtener el estado oficial de cada paquete de una ruta cerrada. Ningún usuario dispara esta consulta manualmente.

El sistema incluye un motor de mapeo que transforma los estados operativos (Entregado, Devuelto, Dañado, Extraviado) en reglas porcentuales de pago, e implementa políticas de resiliencia (reintentos y timeouts) para tolerar fallos de red. Toda interacción queda registrada en auditoría de forma consistente, priorizando la integridad del log sobre el rendimiento.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+

**Primary Dependencies**: Spring Boot (Web, Data JPA), Spring Cloud OpenFeign o WebClient (para peticiones HTTP), Resilience4j (para Timeouts y Retries), Axios (Frontend)

**Storage**: PostgreSQL 15

**Testing**: JUnit 5, WireMock (para simular el API externa), Mockito / Jest

**Target Platform**: AWS

**Project Type**: Web application (Integración de Microservicios)

**Performance Goals**: 95% de las peticiones completadas en menos de 500ms (SC-003).

**Constraints**: Timeout estricto de 2 segundos con política de reintentos antes de marcar como "Pendiente por Sincronización". Registro obligatorio de todas las respuestas HTTP, incluyendo las distintas a 200 OK (SC-002). El guardado del log de sincronización debe ser consistente con la operación principal — no puede perderse ante un fallo.

## Project Structure

### Documentation (this feature)

```text
specs/novedad-estado-paquete/
├── plan.md              # Este archivo
└── spec.md              # Especificación: Novedad estado del paquete.md
```

### Source Code (repository root)

```text
project/
├── backend/
│   ├── src/main/java/com/logistica/
│   │
│   │   ├── application/                         # Casos de uso
│   │   │   ├── usecases/
│   │   │   │   ├── paquete/
│   │   │   │   │   ├── SincronizarPaqueteUseCase.java
│   │   │   │   │   ├── ObtenerHistorialUseCase.java
│   │   │   │   │   └── ObtenerLogsSincronizacionUseCase.java
│   │   │   │
│   │   │   └── dtos/
│   │   │       ├── request/
│   │   │       └── response/
│   │   │           └── PaqueteResponseDTO.java
│   │
│   │   ├── domain/                              # Núcleo del negocio
│   │   │   ├── models/
│   │   │   │   ├── Paquete.java
│   │   │   │   ├── HistorialEstado.java
│   │   │   │   └── LogSincronizacion.java
│   │   │   │
│   │   │   ├── enums/
│   │   │   │   └── EstadoPaquete.java           # Incluye lógica de porcentaje de pago
│   │   │   │
│   │   │   ├── repositories/                    # Interfaces (puertos)
│   │   │   │   ├── PaqueteRepository.java
│   │   │   │   ├── HistorialRepository.java
│   │   │   │   └── LogSincronizacionRepository.java
│   │   │   │
│   │   │   └── services/                        # Lógica pura (si aplica)
│   │   │       └── EstadoPaqueteService.java
│   │
│   │   ├── infrastructure/                      # Implementaciones técnicas
│   │   │   ├── persistence/
│   │   │   │   ├── entities/                    # Entidades JPA
│   │   │   │   └── repositories/                # Spring Data JPA
│   │   │   │
│   │   │   ├── http/
│   │   │   │   ├── clients/                     # Feign / WebClient
│   │   │   │   │   └── GestionClient.java
│   │   │   │   │
│   │   │   │   ├── dto/                         # DTOs externos (otros servicios)
│   │   │   │   │   └── GestionPaqueteDTO.java
│   │   │   │   │
│   │   │   │   └── mappers/                     # Map externo → dominio
│   │   │   │
│   │   │   ├── resilience/                      # Configuración resiliente
│   │   │   │   ├── RetryConfig.java
│   │   │   │   ├── CircuitBreakerConfig.java
│   │   │   │   └── TimeoutConfig.java
│   │   │   │
│   │   │   ├── web/
│   │   │   │   ├── controllers/                 # REST API
│   │   │   │   │   └── PaqueteController.java
│   │   │   │   │
│   │   │   │   └── handlers/                    # Manejo global de errores
│   │   │   │
│   │   │   ├── adapters/                        # Mappers dominio ↔ DTO interno
│   │   │   │   └── PaqueteMapper.java
│   │   │   │
│   │   │   └── config/                          # Seguridad, CORS, etc
│   │
│   │   └── shared/
│   │       ├── utils/
│   │       └── constants/
│
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │
│   └── src/test/java/
│       ├── integration/                         # Tests con WireMock
│       └── unit/
│
│
├── frontend/
│   ├── src/
│   │
│   │   ├── app/                                # Configuración global (router, store)
│   │
│   │   ├── modules/                            # Estructura por features
│   │   │   ├── paquetes/
│   │   │   │   ├── components/                 # Tablas de historial, estados
│   │   │   │   ├── pages/                      # Vista principal de paquetes
│   │   │   │   ├── services/                   # Axios calls
│   │   │   │   └── hooks/                      # Lógica reutilizable (React)
│   │   │   │
│   │   │   └── auditoria/
│   │   │       ├── components/                 # Logs de sincronización
│   │   │       ├── pages/                      # Vista auditoría financiera
│   │   │       └── services/
│   │
│   │   ├── shared/                            # Reutilizable global
│   │   │   ├── components/                    # UI genérica (tabla, modal, etc)
│   │   │   ├── services/                      # Axios base config
│   │   │   └── utils/
│   │
│   │   ├── assets/
│   │   └── styles/
│
│   └── package.json
```

**Structure Decision**: La carpeta `clients/` aísla la integración HTTP externa del resto del código, respetando el principio de responsabilidad única. El Enum `EstadoPaquete` centraliza todas las reglas de pago para evitar condicionales dispersos en el servicio.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar las herramientas de integración HTTP, resiliencia y simulación para pruebas.

- [ ] T001 Añadir dependencias en `pom.xml` / `build.gradle`: `spring-cloud-starter-openfeign` (o WebFlux), `resilience4j-spring-boot3`, y `wiremock` (scope de test únicamente).
- [ ] T002 Configurar la URL base del Módulo de Gestión en `application.properties` usando variable de entorno (`PACKAGE_API_URL`). Nunca hardcodear URLs en el código.
- [ ] T003 Configurar los parámetros de Resilience4j en `application.properties`:
    - `max-attempts: 3` (reintentos antes de marcar como "Pendiente por Sincronización")
    - `wait-duration: 500ms` (espera entre reintentos)
    - `timeout-duration: 2s` (timeout estricto por petición, según edge case del spec)
    - Orden de decoración obligatorio: `@CircuitBreaker` envuelve a `@Retry`, que envuelve a `@TimeLimiter`. Este orden es crítico en Resilience4j — invertirlo produce comportamiento impredecible.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Crear el esquema de base de datos para historial, auditoría y el motor de reglas de pago. Todo debe existir antes de implementar la lógica de consulta.

- [ ] T004 Crear las entidades JPA con sus campos exactos según el spec:
    - `Paquete`: `idPaquete`, `idRuta`, `estadoActual`
    - `HistorialEstado`: `idPaquete`, `estado`, `fecha`
    - `LogSincronizacion`: `idPaquete`, `codigoRespuestaHTTP`, `jsonRecibido`
- [ ] T005 Implementar los `JpaRepository` para las tres entidades.
- [ ] T006 Crear el `Enum` `EstadoPaquete` con los cuatro estados definidos en el spec y su porcentaje de pago correspondiente:

  | Estado | % de Pago |
    |:---|:---|
  | `ENTREGADO` | 100% |
  | `DEVUELTO` | 50% |
  | `DAÑADO` | 0% |
  | `EXTRAVIADO` | 0% |

  Cualquier estado recibido que no esté en este Enum se considera no mapeado y activa el edge case correspondiente.

**Checkpoint**: El esquema de base de datos está creado, el Enum cubre los cuatro estados del spec, y el proyecto puede compilar y conectarse a PostgreSQL.

---

## Phase 3: User Story 1 — Consulta Sincrónica de Estado (Prioridad: P1)

**Goal**: Implementar la consulta HTTP automática al Módulo de Gestión, el mapeo financiero del estado recibido, la actualización del paquete y el registro consistente en historial y auditoría. Esta fase cubre también los edge cases de timeout, paquete inexistente y estado no mapeado, ya que todos son requerimientos funcionales de la misma historia de usuario.

**Independent Test**: Levantar un servidor WireMock local que simule el endpoint `GET /route/{idRoute}/package/{idPaquete}`. Invocar el servicio desde el backend e verificar que el estado queda guardado en `HistorialEstado` y en `estadoActual` del paquete, y que el `LogSincronizacion` registra el código HTTP 200 y el JSON recibido.

### Tests para User Story 1

- [ ] T007 [P] [US1] Test con WireMock simulando respuesta exitosa (HTTP 200) para validar que el JSON `{ "idPaquete": "...", "estado": "ENTREGADO" }` se deserializa correctamente en `PaqueteResponseDTO`.
- [ ] T008 [P] [US1] Test unitario para el motor de reglas (FR-002): verificar que cada estado del Enum mapea al porcentaje correcto — `ENTREGADO` → 100%, `DEVUELTO` → 50%, `DAÑADO` → 0%, `EXTRAVIADO` → 0%.
- [ ] T009 [P] [US1] Test para verificar el SC-001: que el `estadoActual` guardado en la entidad `Paquete` y la entrada en `HistorialEstado` coinciden exactamente con el estado recibido en la respuesta del Módulo de Gestión.
- [ ] T010 [P] [US1] Test para verificar el FR-004: que si un paquete consulta dos veces y el estado cambia (ej. de `DEVUELTO` a `ENTREGADO`), el sistema actualiza `estadoActual` en `Paquete` y agrega una nueva entrada en `HistorialEstado`, sin sobrescribir el registro anterior.
- [ ] T011 [P] [US1] Test con WireMock simulando HTTP 404 (paquete inexistente): verificar que el sistema registra el error en `LogSincronizacion`, detiene el cálculo de ese paquete específico y continúa con los demás paquetes de la ruta.
- [ ] T012 [P] [US1] Test con WireMock introduciendo un delay de 3 segundos: verificar que Resilience4j aborta a los 2 segundos, reintenta hasta 3 veces y finalmente marca el paquete como "Pendiente por Sincronización", registrando el fallo en `LogSincronizacion`.
- [ ] T013 [P] [US1] Test para el edge case de estado no mapeado: WireMock devuelve un estado desconocido (ej. `EN_INSPECCION`) y el sistema omite el cálculo de pago pero registra la consulta completa en `LogSincronizacion` (SC-002).

### Implementation para User Story 1

- [ ] T014 [P] [US1] Crear el DTO `PaqueteResponseDTO` para mapear la respuesta JSON del Módulo de Gestión: `{ "idPaquete": "...", "estado": "ENTREGADO" }`.
- [ ] T015 [P] [US1] Implementar el cliente HTTP `PackageApiClient` configurado para consumir `GET /route/{idRoute}/package/{idPaquete}`, decorado con las anotaciones de Resilience4j en el orden correcto: `@CircuitBreaker` → `@Retry` → `@TimeLimiter`.
- [ ] T016 [P] [US1] Implementar el método fallback en `PackageApiClient` que se ejecuta cuando fallan todos los reintentos o se supera el timeout. Este método debe marcar el paquete como "Pendiente por Sincronización" y registrar el fallo en `LogSincronizacion`.
- [ ] T017 [P] [US1] Implementar `PaqueteService.sincronizarEstado(UUID idRuta, UUID idPaquete)` con la siguiente secuencia, marcada con `@Transactional` para garantizar que la actualización del paquete y el registro en historial y auditoría ocurran de forma atómica:
    1. Invocar `PackageApiClient` para consultar el estado del paquete.
    2. Registrar la respuesta (código HTTP y JSON) en `LogSincronizacion` independientemente del resultado.
    3. Si la respuesta es HTTP 200 y el estado está en el Enum: actualizar `estadoActual` en `Paquete`, agregar entrada en `HistorialEstado` con timestamp, y retornar el porcentaje de pago correspondiente.
    4. Si la respuesta es HTTP 404: registrar "Paquete no encontrado" en `LogSincronizacion` y detener el cálculo de ese paquete sin afectar los demás.
    5. Si el estado recibido no está en el Enum: omitir el cálculo de pago y registrar la consulta completa en `LogSincronizacion`.
- [ ] T018 [US1] Desarrollar en React la vista de auditoría que consuma los registros de `LogSincronizacion`, mostrando los paquetes con estado "Pendiente por Sincronización" o con errores HTTP distintos a 200 para revisión del equipo financiero.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T019 Optimizar el cliente HTTP utilizando Connection Pooling para contribuir a que el 95% de las peticiones estén por debajo de 500ms (SC-003).
- [ ] T020 Implementar paginación e índices en la tabla `HistorialEstado` sobre las columnas `idPaquete` y `fecha` en PostgreSQL, ya que esta tabla crecerá con cada consulta realizada.
- [ ] T021 Añadir alertas visuales en React cuando los logs de sincronización muestren fallos recurrentes de HTTP 500, para que el equipo financiero pueda actuar antes de que afecten la liquidación.
- [ ] T022 Añadir Swagger / OpenAPI para documentar la integración y facilitar la coordinación con el equipo del Módulo de Gestión de Paquetes.

---

## Dependencies & Execution Order

**WireMock desde el inicio (Phase 1)**: Es fundamental configurar WireMock en la fase de setup para no depender del equipo del Módulo de Gestión de Paquetes al momento de desarrollar y probar. Todas las pruebas de integración HTTP se hacen contra WireMock, no contra el servicio real.

**Enum `EstadoPaquete` con los cuatro estados (Phase 2)**: Debe existir antes del servicio. Es el vocabulario financiero del módulo — si falta `EXTRAVIADO`, el sistema lo tratará como estado no mapeado incorrectamente.

**`@Transactional` en el servicio (Phase 3)**: La actualización de `Paquete`, el registro en `HistorialEstado` y el guardado en `LogSincronizacion` deben ocurrir de forma atómica. Si cualquiera falla, los tres se revierten para evitar estados inconsistentes que violarían el SC-001 y el FR-003.

**Orden de Resilience4j obligatorio**: `@CircuitBreaker` → `@Retry` → `@TimeLimiter`. Este orden debe respetarse desde la primera implementación del cliente HTTP.

**El sistema dispara la consulta, no el usuario**: La consulta sincrónica es invocada automáticamente por el proceso de liquidación. No existe ningún endpoint manual para que React dispare esta operación.

**Frontend al final**: La vista de auditoría en React se construye una vez que el backend tiene validado el flujo completo, incluyendo todos los edge cases.
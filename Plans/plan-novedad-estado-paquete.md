# Implementation Plan: Novedad estado del paquete

**Date**: 2026-04-08
**Spec**: [Novedad estado del paquete.md]

## Summary

Esta funcionalidad establece la integración sincrónica entre el Módulo Financiero y el Módulo de Gestión de Paquetes. El sistema realizará peticiones HTTP GET para obtener el estado oficial de cada paquete de una ruta cerrada. Incluye un motor de mapeo para transformar estados operativos ("Entregado", "Devuelto") en reglas porcentuales de pago, e implementa políticas de resiliencia (reintentos y timeouts) para asegurar la tolerancia a fallos en la red. Toda interacción será debidamente auditada.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+

**Primary Dependencies**: Spring Boot (Web, Data JPA), Spring Cloud OpenFeign o WebClient (para peticiones HTTP), Resilience4j (para Timeouts y Retries), Axios (Frontend)

**Storage**: PostgreSQL 15

**Testing**: JUnit 5, WireMock (para simular el API externa), Mockito / Jest

**Target Platform**: AWS

**Project Type**: Web application (Integración de Microservicios)

**Performance Goals**: 95% de las peticiones completadas en < 500ms

**Constraints**: Timeout estricto de 2 segundos con política de reintentos antes de marcar como "Pendiente por Sincronización". Registro obligatorio de respuestas HTTP distintas a 200 OK.

## Project Structure

### Documentation (this feature)

```text
Specs/
└── Novedad estado del paquete.md      # Especificación funcional de la feature

Plans/
└── plan-novedad-estado-paquete.md     # Plan técnico de implementación
```

### Source Code (repository root)

```text
backend/
├── build.gradle                       # Dependencias Spring Boot, OpenFeign, Resilience4j, Flyway, Lombok
├── settings.gradle
├── gradlew / gradlew.bat
└── src/
    ├── main/
    │   ├── java/com/logistica/
    │   │   ├── LogisticaApplication.java
    │   │   ├── application/
    │   │   │   ├── dtos/response/
    │   │   │   │   ├── HistorialEstadoDTO.java
    │   │   │   │   ├── LogSincronizacionDTO.java
    │   │   │   │   └── SincronizacionResultadoDTO.java
    │   │   │   ├── ports/
    │   │   │   │   ├── PackageStatusGateway.java
    │   │   │   │   └── PackageStatusResult.java
    │   │   │   └── usecases/paquete/
    │   │   │       ├── ObtenerHistorialUseCase.java
    │   │   │       ├── ObtenerLogsSincronizacionUseCase.java
    │   │   │       └── SincronizarPaqueteUseCase.java
    │   │   ├── domain/
    │   │   │   ├── enums/
    │   │   │   │   └── EstadoPaquete.java
    │   │   │   ├── exceptions/
    │   │   │   │   ├── EstadoNoMapeadoException.java
    │   │   │   │   ├── PaqueteNotFoundException.java
    │   │   │   │   └── SincronizacionException.java
    │   │   │   ├── models/
    │   │   │   │   ├── HistorialEstado.java
    │   │   │   │   ├── LogSincronizacion.java
    │   │   │   │   └── Paquete.java
    │   │   │   ├── repositories/
    │   │   │   │   ├── HistorialRepository.java
    │   │   │   │   ├── LogSincronizacionRepository.java
    │   │   │   │   └── PaqueteRepository.java
    │   │   │   └── services/
    │   │   │       └── EstadoPaqueteService.java
    │   │   └── infrastructure/
    │   │       ├── config/
    │   │       │   ├── AsyncExecutionConfig.java
    │   │       │   └── FeignConfig.java
    │   │       ├── http/
    │   │       │   ├── clients/
    │   │       │   │   └── GestionClient.java
    │   │       │   ├── dto/
    │   │       │   │   └── GestionPaqueteDTO.java
    │   │       │   └── service/
    │   │       │       ├── ApiCallResult.java
    │   │       │       └── PackageApiClientService.java
    │   │       ├── persistence/
    │   │       │   ├── entities/
    │   │       │   │   ├── HistorialEstadoEntity.java
    │   │       │   │   ├── LogSincronizacionEntity.java
    │   │       │   │   └── PaqueteEntity.java
    │   │       │   ├── mapper/
    │   │       │   │   └── PaqueteEntityMapper.java
    │   │       │   └── repositories/
    │   │       │       ├── HistorialJpaRepository.java
    │   │       │       ├── HistorialRepositoryImpl.java
    │   │       │       ├── LogSincronizacionJpaRepository.java
    │   │       │       ├── LogSincronizacionRepositoryImpl.java
    │   │       │       ├── PaqueteJpaRepository.java
    │   │       │       └── PaqueteRepositoryImpl.java
    │   │       └── web/
    │   │           ├── controllers/
    │   │           │   └── PaqueteController.java
    │   │           └── handlers/
    │   │               ├── ErrorResponse.java
    │   │               └── GlobalExceptionHandler.java
    │   └── resources/
    │       ├── application.yml         # Configuración de API externa, Feign, Resilience4j y datasource
    │       └── db/migration/
    │           └── V1__create_paquete_schema.sql
    └── test/java/                      # Pruebas con JUnit 5 y WireMock

Prototipo/
├── Acción Pago.png
├── Detalle liquidación.png
├── Registrar contrato.png
├── Vaucher Registro de estado de pago.png
├── Visualizar estado de pago.png
├── Visualizar liquidación.png
└── Link.md
```

**Structure Decision**: La estructura actual separa el dominio, los casos de uso, los puertos de aplicación y los adaptadores de infraestructura. La integración HTTP con el Módulo de Gestión de Paquetes queda aislada en `infrastructure/http`, mientras que la persistencia JPA queda en `infrastructure/persistence`. La capa `application` orquesta la sincronización mediante puertos, evitando que la lógica de negocio dependa directamente de Feign, Spring Data JPA o detalles de infraestructura.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar las herramientas de integración HTTP y resiliencia.

- [ ] T001 Añadir dependencias en pom.xml/build.gradle: spring-cloud-starter-openfeign (o WebFlux), resilience4j-spring-boot3, y wiremock (para scope de test).
- [ ] T002 Configurar las URLs base del Módulo de Gestión en application.properties usando variables de entorno (PACKAGE_API_URL).
- [ ] T003 Configurar los parámetros de Resilience4j en application.properties (max-attempts: 3, wait-duration: 500ms, timeout-duration: 2s).

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Crear el esquema de base de datos para historial y auditoría.

- [ ] T004 Crear las entidades JPA Paquete, HistorialEstado y LogSincronizacion con sus respectivas anotaciones y relaciones.
- [ ] T005 Implementar los JpaRepository para las tres entidades.
- [ ] T006 Crear un Enum en Java que contenga las reglas de negocio de pagos (ej. ENTREGADO(1.0), DEVUELTO(0.5), DAÑADO(0.0)).
- [ ] T007 Implementar servicio base de auditoría para guardar el LogSincronizacion de forma asíncrona (@Async) para no penalizar el tiempo de respuesta del hilo principal.

**Checkpoint**: Base de datos lista para registrar estados y fallos de comunicación.

---

## Phase 3: User Story 1 - Consulta Sincrónica de Estado (Priority: P1)

**Goal**: Conectar con el API externa, recuperar el estado y mapearlo financieramente.

**Independent Test**: Levantar un servidor local WireMock que simule el endpoint /route/123/package/456. Invocar el servicio de Spring Boot y verificar que se guarda en PostgreSQL el estado y el LogSincronizacion con un código HTTP 200.

### Tests for User Story 1

- [ ] T008 [P] [US1] JUnit 5 test usando WireMock para simular una respuesta exitosa y validar la extracción del JSON.
- [ ] T009 [P] [US1] Test unitario para validar el motor de reglas (FR-002): Asegurar que un estado "Devuelto" asigne el 50% de pago.

### Implementation for User Story 1

- [ ] T010 [P] [US1] Crear el DTO PaqueteResponseDTO para mapear el JSON { "idPaquete": "...", "estado": "..." }.
- [ ] T011 [US1] Implementar el cliente HTTP (PackageApiClient.java) configurado para consumir GET /route/{idRoute}/package/{idPaquete}.
- [ ] T012 [US1] Implementar en PaqueteService.java la lógica que orquesta la llamada, actualiza el Paquete y guarda en HistorialEstado.
- [ ] T013 [US1] Crear endpoint en el Módulo Financiero GET /api/finanzas/paquetes/{id}/sincronizar para disparar el proceso manualmente desde React (si es necesario).

---

## Phase 4: Edge Cases & Resiliencia (Priority: P2)

**Goal**: Manejar caídas de red, timeouts y respuestas no mapeadas de forma elegante.

**Independent Test**: Configurar WireMock para que introduzca un retraso (delay) de 3 segundos en la respuesta. Verificar que Spring Boot aborta al los 2 segundos, reintenta, y finalmente guarda el paquete como "Pendiente por Sincronización".

### Tests for User Story 2

- [ ] T014 [P] [US2] Test de integración con WireMock para simular errores 404 (Paquete inexistente) y errores 500. Validar la creación del LogSincronizacion respectivo.
- [ ] T015 [P] [US2] Test para verificar la política de Timeout (FR-EdgeCase) utilizando @SpringBootTest y configuración de Resilience4j.

### Implementation for User Story 2

- [ ] T016 [P] [US2] Decorar el método del cliente HTTP con anotaciones de resiliencia (@Retry, @TimeLimiter, @CircuitBreaker).
- [ ] T017 [US2] Implementar el método fallback que se ejecutará si fallan los reintentos, el cual marcará el paquete como "Pendiente por Sincronización".
- [ ] T018 [US2] Implementar lógica para identificar estados no mapeados (ej. "EN_INSPECCION") y omitir el cálculo, pero registrar la auditoría.
- [ ] T019 [US2] Desarrollar un componente en React que liste los paquetes con estado "Pendiente por Sincronización" o errores 404 para la revisión del equipo financiero.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T020 Optimizar el cliente HTTP utilizando Connection Pooling para asegurar que el 95% de las peticiones estén por debajo de 500ms (SC-003).
- [ ] T021 Implementar paginación e índices en la tabla HistorialEstado (idPaquete, fecha) en PostgreSQL, ya que crecerá muy rápido.
- [ ] T022 Añadir alertas visuales en el Frontend (React) cuando los logs de sincronización muestren fallos recurrentes de HTTP 500.

---

## Dependencies & Execution Order

**Infraestructura de Pruebas (WireMock)**: Es fundamental implementarlo en la Fase 1/2. No debes depender del equipo de Gestión de Paquetes para empezar a programar y probar tu código.

**Cliente HTTP y Entidades (Fase 3)**: Programar el "camino feliz" donde el servidor responde rápido y con estado 200 OK.

**Resiliencia (Fase 4)**: Una vez funciona la comunicación, envuelves la llamada en las lógicas de reintento y captura de timeouts.

**UI (React)**: Finalmente, creas las vistas para que el analista financiero pueda ver el estado de estas sincronizaciones y actuar sobre los casos pendientes.

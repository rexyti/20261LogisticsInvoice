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
│   │   └── response/
│   │       ├── HistorialEstadoResponseDTO.java
│   │       ├── LogSincronizacionResponseDTO.java
│   │       ├── PaqueteResponseDTO.java
│   │       └── SincronizacionResultadoDTO.java
│   │
│   └── usecases/
│       └── paquete/
│           ├── ObtenerHistorialUseCase.java
│           ├── ObtenerLogsSincronizacionUseCase.java
│           ├── PaqueteService.java
│           └── SincronizarPaqueteUseCase.java
│
├── domain/
│   ├── enums/
│   │   └── EstadoPaquete.java
│   │
│   ├── models/
│   │   ├── HistorialEstado.java
│   │   ├── LogSincronizacion.java
│   │   └── Paquete.java
│   │
│   ├── repositories/
│   │   ├── HistorialRepository.java
│   │   ├── LogSincronizacionRepository.java
│   │   └── PaqueteRepository.java
│   │
│   └── services/
│       └── EstadoPaqueteService.java
│
├── infrastructure/
│   ├── adapters/
│   │   ├── HistorialRepositoryAdapter.java
│   │   ├── LogSincronizacionRepositoryAdapter.java
│   │   ├── PaqueteMapper.java
│   │   └── PaqueteRepositoryAdapter.java
│   │
│   ├── config/
│   │   └── FeignConfig.java
│   │
│   ├── http/
│   │   ├── clients/
│   │   │   ├── GestionClient.java
│   │   │   └── PackageApiClient.java
│   │   │
│   │   ├── dto/
│   │   │   └── GestionPaqueteDTO.java
│   │   │
│   │   └── mappers/
│   │       └── GestionPaqueteMapper.java
│   │
│   ├── persistence/
│   │   ├── entities/
│   │   │   ├── HistorialEstadoEntity.java
│   │   │   ├── LogSincronizacionEntity.java
│   │   │   └── PaqueteEntity.java
│   │   │
│   │   └── repositories/
│   │       ├── HistorialEstadoJpaRepository.java
│   │       ├── LogSincronizacionJpaRepository.java
│   │       └── PaqueteJpaRepository.java
│   │
│   ├── resilience/
│   │   └── ResilienceConfig.java
│   │
│   └── web/
│       ├── controllers/
│       │   └── PaqueteController.java
│       │
│       └── handlers/
│           └── GlobalExceptionHandler.java
│
└── shared/
    ├── constants/
    │   └── AppConstants.java
    │
    └── exceptions/
        ├── PaqueteNoEncontradoException.java
        └── PendienteSincronizacionException.java
```

### Backend test structure

```text
backend/src/test/java/com/logistica/
├── infrastructure/
│   └── web/
│       └── controllers/
│           └── PaqueteControllerTest.java
│
├── integration/
│   └── PaqueteSincronizacionIntegrationTest.java
│
└── unit/
    ├── EstadoPaqueteServiceTest.java
    ├── EstadoPaqueteTest.java
    ├── GestionPaqueteMapperTest.java
    └── PaqueteServiceTest.java
```

### Backend resources

```text
backend/src/main/resources/
└── application.yml

backend/src/test/resources/
└── application-test.yml
```

## Structure Decision

La estructura real ya expresa una arquitectura limpia en cuatro zonas principales:

1. `domain`: núcleo del negocio, modelos, enum de estados y reglas de transición.
2. `application`: casos de uso del módulo de paquete y DTOs de salida.
3. `infrastructure`: adaptadores JPA, clientes Feign, configuración, resiliencia y controladores REST.
4. `shared`: constantes y excepciones comunes.

Por esa razón, este plan elimina la estructura anterior basada en `Pago`, `EstadoPago`, `EventoTransaccion`, `WebhookPagoController` y frontend React, porque esos archivos no existen en el backend actual. La implementación debe evolucionar sobre `Paquete`, `HistorialEstado` y `LogSincronizacion`.

---

## Phase 1: Setup & Infrastructure Alignment

**Purpose**: Consolidar la infraestructura actual del backend sin introducir paquetes inexistentes.

- [ ] T001 Verificar `backend/build.gradle` con Java 21, Spring Boot, JPA, PostgreSQL, OpenFeign, Validation, Lombok, Resilience4j y dependencias de test.
- [ ] T002 Verificar `backend/settings.gradle` para que el módulo Gradle mantenga el nombre real del backend.
- [ ] T003 Verificar `backend/src/main/resources/application.yml` con conexión PostgreSQL, URL base del servicio externo de gestión de paquetes y configuración JPA.
- [ ] T004 Verificar `backend/src/test/resources/application-test.yml` con H2 o configuración aislada de pruebas.
- [ ] T005 Mantener `LogisticaApplication.java` como punto único de arranque del backend.
- [ ] T006 Mantener `FeignConfig.java` y `ResilienceConfig.java` como infraestructura transversal de comunicación externa y tolerancia a fallos.

**Checkpoint**: El proyecto compila y arranca desde IntelliJ o Gradle sin requerir cambios de estructura.

---

## Phase 2: Domain & Data Integrity

**Purpose**: Garantizar que el dominio del estado de paquete tenga reglas claras, trazabilidad e integridad persistente.

- [ ] T007 Revisar `EstadoPaquete.java` para asegurar que contenga únicamente los estados válidos usados por el backend.
- [ ] T008 Revisar `EstadoPaqueteService.java` para centralizar las reglas de transición y cálculo funcional derivado del estado.
- [ ] T009 Revisar `Paquete.java` para que represente el agregado principal del flujo de sincronización.
- [ ] T010 Revisar `HistorialEstado.java` para que represente cada cambio real de estado del paquete.
- [ ] T011 Revisar `LogSincronizacion.java` para registrar cada intento de sincronización, exitoso o fallido.
- [ ] T012 Revisar `PaqueteEntity.java` para confirmar que tenga `@Version` y así proteger actualizaciones concurrentes.
- [ ] T013 Revisar `HistorialEstadoEntity.java` para asegurar relación correcta con el paquete y fecha del cambio.
- [ ] T014 Revisar `LogSincronizacionEntity.java` para asegurar relación correcta con el paquete, código de respuesta, mensaje y fecha.
- [ ] T015 Revisar `PaqueteRepository.java`, `HistorialRepository.java` y `LogSincronizacionRepository.java` como puertos de dominio.
- [ ] T016 Revisar los repositorios JPA en `infrastructure/persistence/repositories` para que mantengan métodos de consulta por `idPaquete`, orden por fecha y persistencia sin lógica de negocio.

**Checkpoint**: El dominio controla los estados y la persistencia queda aislada detrás de puertos y adaptadores.

---

## Phase 3: Application Use Cases

**Purpose**: Mantener los casos de uso como orquestadores del flujo, sin depender directamente de controladores ni entidades JPA.

- [ ] T017 Revisar `SincronizarPaqueteUseCase.java` como contrato principal para sincronizar un paquete por `idRuta` e `idPaquete`.
- [ ] T018 Revisar `ObtenerHistorialUseCase.java` como contrato para consultar historial por paquete.
- [ ] T019 Revisar `ObtenerLogsSincronizacionUseCase.java` como contrato para consultar logs por paquete.
- [ ] T020 Revisar `PaqueteService.java` para que implemente los tres casos de uso anteriores.
- [ ] T021 Asegurar que `PaqueteService.java` use puertos de dominio y no manipule directamente `JpaRepository` ni entidades JPA.
- [ ] T022 Asegurar que `PaqueteService.java` delegue reglas de estado en `EstadoPaqueteService.java`.
- [ ] T023 Asegurar que el servicio no cree historial duplicado cuando el estado sincronizado sea igual al estado actual.
- [ ] T024 Asegurar que el servicio registre `LogSincronizacion` tanto en éxito como en fallos controlados.
- [ ] T025 Asegurar que los DTOs de respuesta sean inmutables o controlados mediante `@Builder`/constructores, sin exponer entidades internas.

**Checkpoint**: La capa de aplicación orquesta sincronización, historial y logs sin mezclar detalles HTTP, Feign o JPA.

---

## Phase 4: Infrastructure Adapters

**Purpose**: Mantener los detalles técnicos aislados de la lógica de negocio.

- [ ] T026 Revisar `PaqueteRepositoryAdapter.java` para mapear entre `PaqueteEntity` y `Paquete` mediante `PaqueteMapper`.
- [ ] T027 Revisar `HistorialRepositoryAdapter.java` para mapear entidades de historial hacia modelos de dominio.
- [ ] T028 Revisar `LogSincronizacionRepositoryAdapter.java` para mapear logs persistentes hacia modelos de dominio.
- [ ] T029 Revisar `PaqueteMapper.java` para que no pierda campos críticos como `idPaquete`, estado, fecha y versión.
- [ ] T030 Revisar `GestionClient.java` y `PackageApiClient.java` para confirmar que las llamadas externas queden aisladas en `infrastructure/http/clients`.
- [ ] T031 Revisar `GestionPaqueteDTO.java` como DTO exclusivo de integración externa, sin usarlo como modelo de dominio.
- [ ] T032 Revisar `GestionPaqueteMapper.java` para convertir respuestas externas a modelos/datos aceptables para la aplicación.
- [ ] T033 Revisar `ResilienceConfig.java` para timeouts, retry/circuit breaker y degradación controlada.

**Checkpoint**: Los adaptadores resuelven persistencia e integración externa sin contaminar dominio ni aplicación.

---

## Phase 5: Web Layer & API Contract

**Purpose**: Exponer los endpoints reales existentes en el backend sin inventar rutas nuevas.

- [ ] T034 Revisar `PaqueteController.java` para mantener el endpoint de sincronización:

```http
POST /api/v1/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar
```

- [ ] T035 Revisar `PaqueteController.java` para mantener el endpoint de historial:

```http
GET /api/v1/paquetes/{idPaquete}/historial
```

- [ ] T036 Revisar `PaqueteController.java` para mantener el endpoint de logs:

```http
GET /api/v1/paquetes/{idPaquete}/logs
```

- [ ] T037 Verificar que el controlador sólo dependa de casos de uso y no de repositorios JPA, clientes Feign o entidades.
- [ ] T038 Revisar `GlobalExceptionHandler.java` para respuestas claras ante `PaqueteNoEncontradoException`, `PendienteSincronizacionException`, errores de validación y errores generales.
- [ ] T039 Mantener los códigos HTTP coherentes: `200 OK` para consultas exitosas, `404 Not Found` para paquete no encontrado, `503 Service Unavailable` o equivalente para sincronización pendiente/fallo externo controlado, y `500 Internal Server Error` sólo para errores no controlados.

**Checkpoint**: La API pública coincide con el backend real y puede probarse desde Postman.

---

## Phase 6: Testing Strategy

**Purpose**: Mantener los tests alineados con los archivos reales del backend.

### Unit tests

- [ ] T040 Revisar `EstadoPaqueteTest.java` para validar estados soportados por `EstadoPaquete`.
- [ ] T041 Revisar `EstadoPaqueteServiceTest.java` para validar reglas de negocio y transiciones/cálculos derivados del estado.
- [ ] T042 Revisar `GestionPaqueteMapperTest.java` para validar conversión del DTO externo hacia el dominio/aplicación.
- [ ] T043 Revisar `PaqueteServiceTest.java` para validar sincronización exitosa, paquete no encontrado, error externo, creación de historial, registro de logs y no duplicación de historial cuando no cambia el estado.

### Controller tests

- [ ] T044 Revisar `PaqueteControllerTest.java` para validar:
  - `POST /api/v1/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar`;
  - `GET /api/v1/paquetes/{idPaquete}/historial`;
  - `GET /api/v1/paquetes/{idPaquete}/logs`;
  - manejo de excepciones del controlador.

### Integration tests

- [ ] T045 Revisar `PaqueteSincronizacionIntegrationTest.java` para validar el flujo completo con contexto Spring:
  - persistencia de paquete;
  - sincronización;
  - consulta de historial;
  - consulta de logs;
  - integración con perfil `application-test.yml`.

**Checkpoint**: Los tests cubren las capas reales del proyecto y no hacen referencia a archivos inexistentes.

---

## Phase 7: Operational Validation with Postman

**Purpose**: Validar manualmente los endpoints reales una vez el backend esté levantado.

### Request 1: Sincronizar paquete

```http
POST http://localhost:8080/api/v1/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar
```

Ejemplo:

```http
POST http://localhost:8080/api/v1/rutas/550e8400-e29b-41d4-a716-446655440000/paquetes/123e4567-e89b-12d3-a456-426614174000/sincronizar
```

### Request 2: Consultar historial

```http
GET http://localhost:8080/api/v1/paquetes/{idPaquete}/historial
```

Ejemplo:

```http
GET http://localhost:8080/api/v1/paquetes/123e4567-e89b-12d3-a456-426614174000/historial
```

### Request 3: Consultar logs

```http
GET http://localhost:8080/api/v1/paquetes/{idPaquete}/logs
```

Ejemplo:

```http
GET http://localhost:8080/api/v1/paquetes/123e4567-e89b-12d3-a456-426614174000/logs
```

**Checkpoint**: Postman consume las rutas reales del backend sin agregar parámetros duplicados ni incluir el método HTTP dentro de la URL.

---

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

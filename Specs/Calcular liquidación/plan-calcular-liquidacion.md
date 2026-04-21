# Implementation Plan: Calcular liquidación

**Date**: 2026-04-07
**Spec**: [Calcular liquidación.md]

## Summary

El objetivo de esta funcionalidad es automatizar completamente el cálculo de las liquidaciones de los transportistas. El sistema actúa de forma autónoma:
en cuanto recibe el evento de cierre de ruta desde el Módulo de Rutas y Flotas y consulta el estado final de los paquetes, calcula la liquidación sin intervención de ningún usuario, aplicando las reglas del modelo de contratación, las tarifas definidas y las penalizaciones correspondientes.

Existe un flujo secundario de recálculo, que ocurre únicamente cuando un conductor solicita formalmente una revisión de su liquidación y esa solicitud es aceptada por un administrador.
En ese caso, el administrador ingresa los nuevos ajustes manualmente y el sistema recalcula el valor final de forma automática, dejando siempre trazabilidad completa mediante auditoría.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+

**Primary Dependencies**: Spring Boot (Web, Data JPA, Flyway, Validation, Security), PostgreSQL Driver, Axios

**Storage**: PostgreSQL 15


**Testing**: JUnit 5, Mockito / Jest, React Testing Library

**Target Platform**: AWS

**Project Type**: Web application

**Data Integrity**: Uso de`BigDecimal` para todos los cálculos monetarios.

**Scheme Management**: Flyway para migraciones de PostgreSQL.

**Security**: Spring Security + JWT para protección de endpoints financieros.

**API Pattern**: Implementacion de DTOs para desacoplar la base de datos de la capa de presentación.

**Performance Goals**: Procesamiento del cálculo en el servidor < 300ms

**Constraints**: Consistencia transaccional ACID, restricción UNIQUE en base de datos para prevenir liquidaciones duplicadas por ruta, gestión segura de variables de entorno para la nube.

**Scale/Scope**: Preparado para escalar horizontalmente en la nube gestionando miles de cierres de ruta.

## Project Structure

### Documentation (this feature)

```text
Specs/Calcular-liquidación/
├── plan.md              # Este archivo 
└── spec.md             # Especificación: Calcular liquidación.md
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/logistica/
│
│   ├── application/                 # Casos de uso (lo que hace el sistema)
│   │   ├── usecases/
│   │   │   ├── liquidacion/
│   │   │   │   ├── CalcularLiquidacionUseCase.java
│   │   │   │   ├── RecalcularLiquidacionUseCase.java
│   │   │   │   └── LiquidacionStrategyFactoty.java
│   │   │
│   │   └── dtos/                    # DTOs de entrada/salida
│   │       ├── request/
│   │       │   ├── AjusteDto
│   │       │   ├── CierreRutaEventDTO
│   │       │   ├── PaqueteDto
│   │       │   └── RecalcularLiquidacionRequestDTO
│   │       └── response/
│   │           ├── AjusteResponseDto
│   │           └── RecalcularLiquidacionRequestDTO
│   ├── domain/                      # Núcleo del negocio (LO MÁS IMPORTANTE)
│   │   ├── models/                  # Entidades de negocio (sin JPA si quieres pureza)
│   │   │   ├── Ajuste.java
│   │   │   ├── Contrato.java
│   │   │   ├── Liquidacion.java
│   │   │   ├── Paquete.java
│   │   │   ├── Ruta.java
│   │   │   └── AuditoriaLiquidacion.java
│   │   │
│   │   ├── enums/ 
│   │   │   ├── EstadoLiquidacion.java
│   │   │   ├── EstadoPaquete.java
│   │   │   ├── TipoAjuste.java
│   │   │   ├── TipoContratacion.java
│   │   │   ├── TipoOperacion.java
│   │   │   └── TipoResponsable.java
│   │   │
│   │   ├── exceptions/              # Excepciones de negocio
│   │   │   ├── ContratoNotFoundException.java
│   │   │   ├── DomainException.java
│   │   │   ├── LiquidacionNotFoundException.java
│   │   │   └── LiquidacionDuplicadaException.java
│   │   │
│   │   ├── repositories/            # Interfaces (puertos)
│   │   │   ├── LiquidacionRepository.java
│   │   │   ├── AjusteRepository.java
│   │   │   ├── AuditoriaLiquidacionRepository.java
│   │   │   └── AjusteRepository.java
│   │   │
│   │   └── strategies/              # Reglas de negocio (core)
│   │       ├── LiquidacionStrategy.java
│   │       ├── PorParadaStrategy.java
│   │       └── RecorridoCompletoStrategy.java
│   │      
│   ├── infrastructure/              # Implementaciones técnicas
│   │   ├── persistence/
│   │   │   ├── entities/            # Entidades JPA (separadas del dominio)
│   │   │   │    ├── AjusteEntity.java
│   │   │   │    ├── ContratoEntity.java
│   │   │   │    ├── LiquidacionEntity.java
│   │   │   │    ├── InmutableBaseEntity.java
│   │   │   │    ├── BaseEntity.java
│   │   │   │    └── AuditoriaLiquidacionEntity.java  
│   │   │   │
│   │   │   ├── repositories/          # Spring Data JPA
│   │   │   │    ├── AjusteJpaRepository.java
│   │   │   │    ├── AjusteRepositoryImpl.java
│   │   │   │    ├── AuditoriaLiquidacionJpaRepository.java
│   │   │   │    ├── AuditoriaLiquidacionRepositoryImpl.java
│   │   │   │    ├── ContratoJpaRepository.java
│   │   │   │    ├── ContratoRepositoryImpl.java
│   │   │   │    ├── LiquidacionJpaRepository.java
│   │   │   │    └── LiquidacionRepositoryImpl.java
│   │   │   │
│   │   │   └── mapper/ 
│   │   │       ├── AjusteMapper.java
│   │   │       ├── ContratoMapper.java
│   │   │       ├── LiquidacionMapper.java
│   │   │       ├── RutaMapper.java
│   │   │       └── AuditoriaLiquidacionMapper.java
│   │   │
│   │   ├── web/
│   │   │   ├── controllers/         # REST controllers
│   │   │   │     ├── EventoController.java
│   │   │   │     └── LiquidacionController.java
│   │   │   │ 
│   │   │   └── handlers/            # Manejo global de errores
│   │   │       └── GlobalExceptionHandler.java
│   │   │
│   │   └── config/                  # Seguridad, CORS, etc
│   │       ├── JwtAuthenticationFilter.java
│   │       ├── JwtService.java
│   │       └── SecurityConfig.java
│   
│
│
├── src/main/resources/
│   ├── db/migration/
│   │   └── V1__init_schema.sql
│   └── application.yml
│
├── Dockerfile
└── pom.xml / build.gradle

frontend/
├── src/
│
│   ├── app/                        # Configuración global (router, store)
│
│   ├── modules/                    # Feature-based structure
│   │   ├── liquidacion/
│   │   │   ├── components/
│   │   │   ├── pages/
│   │   │   ├── services/
│   │   │   └── hooks/
│   │
│   │   └── ajustes/
│   │       ├── components/
│   │       ├── pages/
│   │       └── services/
│
│   ├── shared/                     # Reutilizable
│   │   ├── components/             # Botones, inputs, modales genéricos
│   │   ├── services/               # Axios config
│   │   └── utils/
│
│   ├── assets/
│   └── styles/
│
├── Dockerfile
└── package.json
```

**Structure Decision**: Se utiliza una arquitectura desacoplada con el patrón Strategy para el motor de cálculo, separando la lógica de cada tipo de contrato en clases independientes.
Esto facilita agregar nuevos modelos de contratación en el futuro sin modificar el servicio principal.

---

## Phase 1: Setup & DevOps Foundation (Shared Infrastructure)

**Purpose**: Configuración inicial y preparación del entorno de desarrollo y despliegue.

- [ ] T001 Inicializar Spring Boot con dependencias: Web, Data JPA, Flyway, Validation, Security y el driver de PostgreSQL.
- [ ] T002 Inicializar React con Vite y configurara Axios Interceptors para manejo de errores global de errores HTTP.
- [ ] T003 Crear Docker Compose para entorno local (App + DB) y configurar Dockerfiles para AWS.
- [ ] T004  Definir el esquema inicial en el script de Flyway `V1__init_schema.sql`, incluyendo todas las tablas del módulo (`liquidaciones`, `ajustes`, `auditoria_liquidacion`) y sus restricciones. En particular, agregar una restricción `UNIQUE(id_ruta)` en la tabla `liquidaciones` para garantizar a nivel de base de datos que no existan liquidaciones duplicadas por ruta.

---

## Phase 2: Foundational & Data Integrity (Blocking Prerequisites)

**Purpose**: Definir el esquema de datos, las entidades, la auditoría y la seguridad de comunicación. Esta fase debe completarse antes de implementar cualquier lógica de negocio.

- [ ] T005 Configurar CORS y un `SecurityConfig` básico con roles definidos (`ROLE_ADMIN`, `ROLE_TRANSPORTISTA`) para proteger los endpoints financieros mediante JWT.
- [ ] T006 Crear las entidades JPA y sus DTOs correspondientes:
    - `Contrato` → `ContratoDTO`
    - `Ruta` → `RutaDTO`
    - `Liquidacion` → `LiquidacionResponseDTO`
    - `Ajuste` → `AjusteDTO`
    - `AuditoriaLiquidacion` → `AuditoriaDTO` *(debe crearse aquí, ya que el primer cálculo también genera un registro de auditoría, no solo el recálculo)*
- [ ] T007 Implementar los `JpaRepository` para cada entidad, incluyendo el método `existsByIdRuta(UUID idRuta)` en `LiquidacionRepository` para la validación de duplicados en la capa de servicio.
- [ ] T008 Implementar un `@RestControllerAdvice` global que capture excepciones de negocio (`ContratoNotFoundException`, `LiquidacionDuplicadaException`, `SolicitudRevisionNoAceptadaException`) y errores de base de datos, retornando respuestas JSON estructuradas con código HTTP apropiado.


**Checkpoint**:  El backend se conecta a PostgreSQL mediante variables de entorno, el esquema está creado con todas sus restricciones, y el frontend puede hacer llamadas básicas sin errores de CORS.

---

## Phase 3: User Story 1 - Calcular liquidación automáticamente (Priority: P1)

**Goal**: Implementar el motor de cálculo automático que se activa al recibir el evento de cierre de ruta, sin intervención de ningún usuario. React no dispara este cálculo; solo consume el resultado para mostrarlo.

**Independent Test**: Simular el envío del evento de cierre de ruta mediante Postman al endpoint `POST /api/eventos/cierre-ruta` con un payload válido.
Verificar que se genera correctamente el registro de liquidación en PostgreSQL con el valor esperado y que se crea el registro de auditoría correspondiente, sin ninguna acción adicional del usuario.

### Tests for User Story 1

- [ ] T009 [P] [US1]  Test unitario en JUnit 5 para `PorParadaStrategy`: verificar que el cálculo aplica correctamente los porcentajes según el estado de cada parada (exitosa al 100%, fallida por cliente al porcentaje configurado, fallida por transportista al 0% + penalización).
- [ ] T010 [P] [US1] Test unitario en JUnit 5 para `RecorridoCompletoStrategy`: verificar que se asigna el valor fijo del contrato cuando la ruta cumple los criterios de completitud.
- [ ] T011 [P] [US1] Test de integración con `@DataJpaTest` para confirmar que la restricción de duplicados lanza `LiquidacionDuplicadaException` cuando se intenta calcular una segunda liquidación para la misma ruta.
- [ ] T012 [P] [US1] Test para el edge case: el contrato referenciado en el evento no existe → el sistema lanza `ContratoNotFoundException` y no genera ningún registro de liquidación.
- [ ] T013 [P] [US1] Test para el edge case: la fecha de cierre de la ruta es anterior a la fecha de inicio → el sistema rechaza el evento y registra el error en los logs sin crear liquidación.
- [ ] T014 [P] [US1] Test para el edge case: un paquete no tiene regla de pago aplicable → el sistema omite ese paquete, continúa con los demás y registra el paquete como "sin regla aplicable".

### Implementation for User Story 1

- [ ] T015 [P] [US1] Implementar la interfaz `ContratoStrategy` con el método `calcular(Ruta ruta, Contrato contrato): BigDecimal` y sus dos implementaciones:
    - `PorParadaStrategy`: itera las paradas, aplica el porcentaje de pago según el responsable de la falla y multiplica por la tarifa por parada.
    - `RecorridoCompletoStrategy`: verifica el cumplimiento de la ruta y retorna el valor fijo pactado en el contrato.
- [ ] T016 [P] [US1] Implementar `CalculationService.java` con el método principal `calcularLiquidacion(CierreRutaEventDTO evento)`, que: valida que no exista liquidación duplicada para esa ruta, selecciona la estrategia correcta según el tipo de contrato, aplica ajustes y penalizaciones al subtotal, persiste la liquidación y registra la operación en `AuditoriaLiquidacion`.
- [ ] T017 [P] [US1] Marcar el método de creación de liquidación con `@Transactional` para garantizar que el guardado de la liquidación y el registro de auditoría ocurran de forma atómica. Si cualquiera de los dos falla, ambos se revierten.
- [ ] T018 [US1] Crear el endpoint `POST /api/eventos/cierre-ruta` en `EventoController.java`, que recibe el evento del Módulo de Rutas y Flotas e invoca `CalculationService`. Este endpoint **no es llamado por React**; es invocado por el sistema externo de rutas.
- [ ] T019 [US1] Crear en React la vista de resultado de liquidación, que consulta mediante `GET /api/liquidaciones/{id}` el registro ya calculado y lo muestra al usuario. React únicamente lee el resultado; no dispara el cálculo.

---

## Phase 4: User Story 2 - Recalcular liquidación (Priority: P2)

**Goal**:  Permitir al administrador ingresar nuevos ajustes sobre una liquidación existente y ordenar el recálculo, pero únicamente cuando exista una solicitud de revisión aceptada para esa liquidación. El sistema debe validar ese estado antes de permitir cualquier acción.

**Independent Test**: Desde la UI de React, como administrador, intentar acceder al panel de recálculo de una liquidación sin solicitud aceptada y verificar que el sistema lo bloquea.
Luego, con una solicitud aceptada, ingresar un ajuste con motivo obligatorio, presionar "Recalcular" y confirmar en PostgreSQL que el valor de la liquidación fue actualizado y que se creó un nuevo registro en `auditoria_liquidacion` con el valor anterior, el valor nuevo y el responsable.

### Tests for User Story 2

- [ ] T020 [P] [US2] Test unitario para verificar que `CalculationService.recalcular()` lanza `SolicitudRevisionNoAceptadaException` cuando no existe una solicitud de revisión aceptada para la liquidación, impidiendo el recálculo.
- [ ] T021 [P] [US2] Test unitario para verificar que al ejecutar el recálculo exitoso, el registro previo de la liquidación (valor anterior) queda íntegro en `auditoria_liquidacion` junto con el valor nuevo, la fecha y el responsable.
- [ ] T022 [US2] Test de componente en React para confirmar que el formulario de ajustes no se muestra si no hay solicitud de revisión aceptada, y que el campo "Motivo del ajuste" es obligatorio antes de habilitar el botón "Recalcular".

### Implementation for User Story 2

- [ ] T023 [P] [US2] Implementar en `CalculationService.java` el método `recalcularLiquidacion(UUID idLiquidacion, List<AjusteDTO> nuevosAjustes, UUID idAdmin)`, que: verifica que existe una solicitud de revisión aceptada para esa liquidación, aplica los nuevos ajustes al cálculo base, actualiza el valor final y registra en `AuditoriaLiquidacion` el valor anterior, el valor nuevo, la fecha y el administrador responsable.
- [ ] T024 [P] [US2] Crear el endpoint `PUT /api/liquidaciones/{id}/recalcular` en el controlador, protegido para `ROLE_ADMIN` únicamente.
- [ ] T025 [US2] Desarrollar en React el panel de recálculo para administradores, que: primero valida si existe una solicitud de revisión aceptada (consultando el backend) antes de mostrar el formulario, incluye campos para ingresar nuevos ajustes con tipo, monto y motivo (obligatorio), y muestra un modal de confirmación antes de ejecutar el recálculo.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T026 Configurar perfiles de Spring Boot (`application-dev.yml`, `application-prod.yml`) con variables de entorno para credenciales de AWS RDS.
- [ ] T027 Añadir Swagger / OpenAPI para documentar los endpoints y facilitar la integración con el equipo de frontend y con el Módulo de Rutas y Flotas.
- [ ] T028  Implementar estados de carga en React (skeleton loaders) mientras se espera la respuesta del backend tras el recálculo.
- [ ] T029 Agregar índices en PostgreSQL sobre las columnas `id_ruta` y `fecha_calculo` en la tabla `liquidaciones` para optimizar las consultas de búsqueda.

---

## Dependencies & Execution Order

**Schema y restricciones (Fase 1 y 2)**: El script de Flyway con la restricción `UNIQUE(id_ruta)` y la entidad `AuditoriaLiquidacion` deben existir desde el inicio, ya que el primer cálculo ya genera auditoría. No pueden agregarse después.

**Estrategias antes del servicio**: Las clases `PorParadaStrategy` y `RecorridoCompletoStrategy` deben implementarse y probarse con JUnit antes de integrarlas en `CalculationService`.

**Servicios antes de controladores**: El motor de cálculo se programa y valida con tests unitarios antes de exponerse vía REST.

**El frontend no dispara el cálculo**: React únicamente visualiza el resultado. El cálculo lo dispara el Módulo de Rutas y Flotas mediante el evento de cierre de ruta. Esta separación debe respetarse durante toda la implementación.

**Integración UI**: React entra en juego al final de cada historia de usuario, consumiendo endpoints que ya están validados por el backend.
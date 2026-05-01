# Implementation Plan: Visualizar liquidación

**Date**: 2026-04-10
**Spec**: [Visualizar liquidación.md]

## Summary

El objetivo de esta funcionalidad es permitir la consulta segura, trazable y ordenada de las liquidaciones previamente calculadas y almacenadas en el sistema. La solución debe mostrar las liquidaciones en forma de lista organizada, permitir la búsqueda de una liquidación específica, mostrar mensajes claros cuando la liquidación no exista o aún no haya sido generada, y aplicar control de acceso para que cada usuario visualice únicamente la información que tiene permitido consultar según su rol.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+

**Primary Dependencies**: Spring Boot (Web, Data JPA, Validation, Security), PostgreSQL Driver, Axios

**Storage**: PostgreSQL 15

**Testing**: JUnit 5, Mockito, Spring Security Test / Jest, React Testing Library

**Target Platform**: AWS

**Project Type**: Web application

**Performance Goals**: Respuesta de consultas de lista y detalle en el servidor en menos de 300ms para filtros por identificador y consultas paginadas.

**Constraints**: Control de acceso basado en roles y alcance de datos (FR-004), paginación obligatoria para listados, manejo resiliente frente a indisponibilidad del almacenamiento, desacoplamiento mediante DTOs para no exponer entidades completas al frontend.

**Scale/Scope**: Preparado para consultar historiales de liquidaciones de múltiples usuarios sin degradación sensible del tiempo de respuesta.

## Project Structure

### Documentation (this feature)

```text
specs/visualizar-liquidacion/
├── plan.md              # Este archivo
└── spec.md              # Especificación: Visualizar liquidación.md
```

### Source Code (repository root)

```text
project/
├── backend/
│   ├── src/main/java/com/logistica/
│   │
│   │   ├── application/                             # Casos de uso (queries)
│   │   │   ├── usecases/
│   │   │   │   ├── liquidacion/
│   │   │   │   │   ├── ListarLiquidacionesUseCase.java
│   │   │   │   │   ├── ObtenerDetalleLiquidacionUseCase.java
│   │   │   │   │   └── BuscarLiquidacionesUseCase.java
│   │   │   │
│   │   │   └── dtos/
│   │   │       ├── request/
│   │   │       │   └── FiltroLiquidacionDTO.java
│   │   │       │
│   │   │       └── response/
│   │   │           ├── LiquidacionListItemDTO.java
│   │   │           ├── LiquidacionDetalleDTO.java
│   │   │           └── AjusteLiquidacionDTO.java
│   │   │           └── LiquidacionListResponseDTO.java
│   │
│   │   ├── domain/                                  # Núcleo del negocio
│   │   │   ├── models/
│   │   │   │   ├── Liquidacion.java                 # Modelo existente
│   │   │   │   ├── Ajuste.java
│   │   │   │   ├── ResultadoBusquedaPorRuta.java
│   │   │   │   └── Ruta.java
│   │   │   ├── repositories/                        # Puertos
│   │   │   │   └── LiquidacionRepository.java
│   │   │   ├── enums/  
│   │   │   │   └── EstadoLiquidacion.java
│   │   │   └── exceptions/
│   │   │       ├── LiquidacionNoEncontradaException.java
│   │   │       ├── LiquidacionAunNoCalculadaException.java
│   │   │       └── AccesoDenegadoException.java
│   │
│   │   ├── infrastructure/                          # Implementación técnica
│   │   │   ├── persistence/
│   │   │   │   ├── entities/                        # JPA (reutilizadas)
│   │   │   │       └── AjusteEntity.java
│   │   │   │       └── LiquidacionEntity.java
│   │   │   │       └── RutaEntity.java
│   │   │   │   └── repositories/                    # Spring Data + queries
│   │   │   │       └── LiquidacionJpaRepository.java
│   │   │   │       └── LiquidacionRepositoryImpl.java
│   │   │   │       └── RutaJpaRepository.java
│   │   │   │
│   │   │   ├── web/
│   │   │   │   ├── controllers/
│   │   │   │   │   └── LiquidacionController.java
│   │   │   │   │
│   │   │   │   └── handlers/
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   ├── security/  
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   │   └── JwtService.java   
│   │   │   ├── adapters/                            # Mappers
│   │   │   │   └── LiquidacionMapper.java
│   │   │   │
│   │   │   └── config/
│   │   │       ├── WebConfig.java                   # CORS
│   │   │       ├── SecurityConfig.java              # Seguridad
│   │   │       └── PaginationConfig.java            # Default page/size
│   │
│   │   └── shared/
│   │       ├── utils/
│   │       └── constants/
│
│   ├── src/main/resources/
│   │   ├── db/migration/
│   │   │   └── Vx__indexes_visualizacion_liquidacion.sql
│   │   │   └── Vx__init_schema.sql
│   │   │
│   │   └── application.properties
│   │   └── application-dev.properties
│   │   └── application-prod.properties
│   │
│   └── pom.xml / build.gradle / settings.gradle
│
│
├── frontend/
│   ├── src/
│   │
│   │   ├── app/                                  # Router, config global
│   │
│   │   ├── modules/                              # Feature-based
│   │   │   ├── liquidaciones/
│   │   │   │   ├── components/                  # Tabla, buscador, alerts
│   │   │   │   ├── pages/                       # Listado y detalle
│   │   │   │   ├── services/                    # Axios calls
│   │   │   │   └── hooks/                       # Manejo de filtros/paginación
│   │   │
│   │   ├── shared/
│   │   │   ├── components/                      # Tabla genérica, loaders, empty states
│   │   │   ├── services/                        # Axios base config
│   │   │   └── utils/
│   │
│   │   ├── assets/
│   │   └── styles/
│
│   └── package.json
```


**Structure Decision**: Se utiliza una arquitectura desacoplada con consultas paginadas en backend y DTOs específicos para lista y detalle. Esto permite cumplir el requerimiento de visualización organizada sin exponer relaciones innecesarias y facilita aplicar filtros por permisos del usuario autenticado.

---

## Phase 1: Setup & DevOps Foundation (Shared Infrastructure)

**Purpose**: Configuración base para exponer consultas seguras, paginadas y tolerantes a fallos de infraestructura.

- [ ] T001 Configurar Spring Security para proteger los endpoints de consulta de liquidaciones y definir los roles de acceso requeridos por el módulo.
- [ ] T002 Configurar CORS y serialización JSON para respuestas paginadas y DTOs de detalle financiero.
- [ ] T003 Preparar la configuración de Axios en frontend para enviar token JWT en todas las consultas al módulo financiero.
- [ ] T004 Definir el contrato estándar de errores HTTP para búsquedas fallidas, recursos inexistentes, acceso denegado e indisponibilidad del sistema de almacenamiento.

---

## Phase 2: Foundational & Data Integrity (Blocking Prerequisites)

**Purpose**: Definir las consultas, DTOs y reglas de acceso necesarias antes de implementar la historia de usuario.

- [ ] T005 Crear los DTOs de lectura:
    - `LiquidacionListItemDTO`
    - `LiquidacionListResponseDTO`
    - `LiquidacionDetailDTO`
    - `AjusteLiquidacionDTO`
    - `ErrorResponseDTO`
- [ ] T006 Implementar en `LiquidacionRepository` consultas paginadas para listar liquidaciones ordenadas por fecha de cálculo descendente.
- [ ] T007 Implementar en `LiquidacionRepository` la consulta por identificadores relevantes de negocio para búsqueda específica, incluyendo `idLiquidacion` e `idRuta`.
- [ ] T008 Implementar en `LiquidacionRepository` la consulta filtrada por usuario propietario o alcance autorizado, para cumplir el FR-004 de verificar si el usuario tiene permisos para ver liquidaciones de otros usuarios o únicamente las propias.
- [ ] T009 Crear índices en PostgreSQL sobre `id_ruta`, `fecha_calculo`, `estado_liquidacion` e identificador del usuario relacionado para acelerar búsquedas y listados.
- [ ] T010 Implementar un `@RestControllerAdvice` global que capture `EntityNotFoundException`, `AccessDeniedException`, `DataAccessResourceFailureException` y retorne respuestas JSON consistentes.

**Checkpoint**: El backend ya puede resolver consultas paginadas y filtradas por permisos, con respuestas consistentes para recursos inexistentes y para caídas de la base de datos.

---

## Phase 3: User Story 1 — Visualizar liquidación (Prioridad: P1)

**Goal**: Permitir que un miembro autorizado consulte desde una lista organizada las liquidaciones previamente calculadas, busque una liquidación específica y visualice sus detalles, aplicando control de acceso según el rol del usuario (FR-004): usuarios con permisos globales ven todas las liquidaciones autorizadas, usuarios sin permisos globales ven únicamente las propias.

**Independent Test**: Consultar desde la interfaz una lista de liquidaciones previamente calculadas y verificar que se visualicen correctamente los campos requeridos por el spec: `IdRuta`, `FechaInicio`, `FechaCierre`, `IdLiquidación`, `Ajustes (tipo, monto, razón)`, `tipo de vehículo`, `PrecioParada`, `Número de paradas`, `Monto Bruto`, `Monto Neto`, `estadoLiquidación` y `FechaCalculo`. Luego buscar una liquidación específica, verificar su visualización, probar la búsqueda de una liquidación inexistente y comprobar que el acceso queda restringido según los permisos del usuario (SC-002).

### Tests para User Story 1

- [ ] T011 [P] [US1] Test de integración con `@WithMockUser` para validar que un usuario autorizado puede consultar el listado paginado de liquidaciones.
- [ ] T012 [P] [US1] Test de integración para validar que la respuesta del listado incluye todos los campos de negocio exigidos por el spec y no omite datos clave de trazabilidad.
- [ ] T013 [P] [US1] Test de integración para búsqueda por `idLiquidacion` retornando el detalle correcto cuando la liquidación existe.
- [ ] T014 [P] [US1] Test de integración para búsqueda por `idRuta` retornando la liquidación correcta cuando existe correspondencia.
- [ ] T015 [P] [US1] Test de integración para validar respuesta controlada cuando se busca una liquidación inexistente en el registro (escenario 3 del spec).
- [ ] T016 [P] [US1] Test de integración para validar respuesta controlada cuando el contrato o ruta consultada aún no posee liquidación calculada (escenario 4 del spec), diferenciando este caso del anterior.
- [ ] T017 [P] [US1] Test de seguridad para verificar que un usuario sin permisos globales no puede visualizar liquidaciones ajenas (FR-004, SC-002).
- [ ] T018 [P] [US1] Test de seguridad para verificar que un usuario con permisos globales sí puede visualizar liquidaciones autorizadas de diferentes usuarios (FR-004).
- [ ] T019 [P] [US1] Test de controlador para validar que una falla del sistema de almacenamiento retorna HTTP 503 con mensaje funcionalmente útil para el frontend (edge case del spec).
- [ ] T020 [P] [US1] Test de componente en React para validar que la tabla muestra las liquidaciones, que el buscador filtra correctamente y que los estados vacíos muestran los mensajes definidos en el spec.

### Implementation para User Story 1

- [ ] T021 [P] [US1] Implementar `LiquidacionQueryService.java` con el método `listarLiquidaciones(Pageable pageable, UsuarioAutenticado usuario)` que aplique las reglas de visibilidad según permisos del FR-004: si el usuario tiene permisos globales ejecuta `findAll` paginado; si no, filtra por su propio identificador.
- [ ] T022 [P] [US1] Implementar en `LiquidacionQueryService.java` el método `buscarLiquidacion(LiquidacionSearchCriteria criteria, UsuarioAutenticado usuario)` para resolver búsquedas específicas por `idLiquidacion` e `idRuta`.
- [ ] T023 [P] [US1] Implementar en la capa de servicio la validación de acceso sobre cada registro antes de devolver el detalle de una liquidación, retornando error controlado si el usuario no tiene permisos sobre ese registro específico.
- [ ] T024 [P] [US1] Implementar el mapeo a `LiquidacionListItemDTO` incluyendo todos los campos visibles requeridos por el spec para el listado organizado.
- [ ] T025 [P] [US1] Implementar el mapeo a `LiquidacionDetailDTO` incluyendo desglose de ajustes con `tipo`, `monto` y `razón`.
- [ ] T026 [US1] Crear el endpoint `GET /api/liquidaciones` con parámetros de paginación, orden y criterios de búsqueda para lista organizada.
- [ ] T027 [US1] Crear el endpoint `GET /api/liquidaciones/{id}` para obtener el detalle completo de una liquidación específica autorizada.
- [ ] T028 [US1] Implementar la respuesta funcional diferenciada para liquidación inexistente (escenario 3) y liquidación aún no calculada (escenario 4), con mensajes distintos para cada caso.
- [ ] T029 [US1] Desarrollar en React la vista de listado de liquidaciones con tabla paginada, barra de búsqueda y navegación hacia el detalle.
- [ ] T030 [US1] Desarrollar en React la vista de detalle de liquidación mostrando la información registrada para garantizar la trazabilidad financiera.
- [ ] T031 [US1] Implementar en React los mensajes de estado para: liquidación inexistente, liquidación aún no calculada, acceso no autorizado e indisponibilidad temporal del sistema.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T032 Incorporar validaciones de parámetros de entrada para evitar búsquedas con identificadores mal formados o tamaños de página inválidos.
- [ ] T033 Estandarizar mensajes funcionales del frontend para que la experiencia de búsqueda sea consistente en lista, detalle y estados de error.

---

## Dependencies & Execution Order

**Dependencia de datos previos**: Esta funcionalidad depende de que el módulo de cálculo de liquidación ya genere y almacene liquidaciones válidas. Sin esos registros, no existirán datos que consultar.

**Consultas antes de controladores**: Las consultas paginadas, los filtros por permisos y los DTOs de lectura deben implementarse primero para asegurar que el contrato de datos esté estabilizado antes de exponer la API.

**Seguridad antes de UI**: Las reglas de acceso por usuario deben resolverse en backend antes de conectar el frontend para evitar fugas de información financiera sensible.

**Frontend al final de la historia**: La vista React debe consumir únicamente endpoints ya validados funcional y técnicamente, incluyendo sus respuestas de error y estados vacíos.
# Implementation Plan: Registrar modelo de contratación

**Date**: 2026-04-08
**Spec**: [Registrar modelo de contratación.md]

## Summary

Este módulo es el núcleo administrativo del sistema de tarifas. Permite la creación formal y la consulta de los modelos de contratación de los transportistas. Su objetivo principal es garantizar la absoluta integridad de los datos de entrada: previene la duplicidad de identificadores, asegura la coherencia temporal de las fechas de vigencia y aplica reglas de negocio dinámicas como la validación del tipo de precio según el modelo de contrato (precio por parada vs precio fijo por ruta).

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+

**Primary Dependencies**: Spring Boot (Web, Data JPA, Validation), PostgreSQL Driver, Axios

**Storage**: PostgreSQL 15

**Testing**: JUnit 5, Mockito, Spring Boot Test / Jest, React Testing Library

**Target Platform**: AWS

**Project Type**: Web application (Backend API + Frontend Dashboard)

**Performance Goals**: Consultas de contratos en menos de 200ms; inserciones con validación en menos de 300ms.

**Constraints**: Restricción UNIQUE en base de datos para el identificador externo del contrato (FR-004). Validación estricta a nivel de API para campos obligatorios, coherencia de fechas (Fecha Final estrictamente mayor a Fecha Inicio) y precio condicional según tipo de contrato.

## Project Structure

### Documentation (this feature)

```text
specs/registrar-contrato/
├── plan.md              # Este archivo
└── spec.md              # Especificación: Registrar modelo de contratación.md
```

### Source Code (repository root)

```text
project/
├── backend/
│   ├── src/main/java/com/logistica/
│   │
│   │   ├── application/                         # Casos de uso
│   │   │   ├── usecases/
│   │   │   │   ├── contrato/
│   │   │   │   │   ├── CrearContratoUseCase.java
│   │   │   │   │   ├── BuscarContratoUseCase.java
│   │   │   │   │   └── ListarContratosUseCase.java
│   │   │   │
│   │   │   └── dtos/
│   │   │       ├── request/
│   │   │       │   └── ContratoRequestDTO.java
│   │   │       └── response/
│   │   │           └── ContratoResponseDTO.java
│   │
│   │   ├── domain/                              # Núcleo del negocio
│   │   │   ├── models/
│   │   │   │   ├── Contrato.java
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Vehiculo.java
│   │   │   │   └── Seguro.java
│   │   │   │
│   │   │   ├── repositories/                    # Interfaces (puertos)
│   │   │   │   ├── ContratoRepository.java
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   └── VehiculoRepository.java
│   │   │   │
│   │   │   ├── validators/                      # Reglas de negocio (validaciones)
│   │   │   │   ├── ContratoValidator.java
│   │   │   │   ├── FechasContratoValidator.java
│   │   │   │   └── PrecioCondicionalValidator.java
│   │   │   │
│   │   │   └── exceptions/                      # Excepciones de negocio
│   │   │       ├── ContratoInvalidoException.java
│   │   │       └── RecursoNoEncontradoException.java
│   │
│   │   ├── infrastructure/                      # Implementación técnica
│   │   │   ├── persistence/
│   │   │   │   ├── entities/                    # Entidades JPA
│   │   │   │   └── repositories/                # Spring Data JPA
│   │   │   │
│   │   │   ├── web/
│   │   │   │   ├── controllers/                 # REST Controllers
│   │   │   │   │   └── ContratoController.java
│   │   │   │   │
│   │   │   │   └── handlers/                    # @RestControllerAdvice
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── adapters/                        # Mappers dominio ↔ DTO
│   │   │   │   └── ContratoMapper.java
│   │   │   │
│   │   │   └── config/                          # Configuración global (CORS, seguridad)
│   │
│   │   └── shared/
│   │       ├── utils/
│   │       └── constants/
│
│   └── src/test/java/
│       ├── unit/                                # Validadores y lógica
│       └── integration/                         # Persistencia (opcional)
│
│
├── frontend/
│   ├── src/
│   │
│   │   ├── app/                                # Router, config global
│   │
│   │   ├── modules/                            # Feature-based
│   │   │   ├── contratos/
│   │   │   │   ├── components/                 # Formularios, modales de error
│   │   │   │   ├── pages/                      # CrearContrato, BuscarContrato
│   │   │   │   ├── services/                   # Axios calls
│   │   │   │   └── hooks/                      # Manejo del formulario
│   │   │
│   │   ├── shared/                            # Reutilizable
│   │   │   ├── components/                    # Inputs, botones, modales genéricos
│   │   │   ├── services/                      # Axios base config
│   │   │   └── utils/
│   │
│   │   ├── assets/
│   │   └── styles/
│
│   └── package.json
```

**Structure Decision**: Se introduce la carpeta `exceptions/` para centralizar el manejo de errores y garantizar que el frontend reciba mensajes limpios y estandarizados. La carpeta `validators/` dentro de `services/` agrupa los Custom Validators de Bean Validation que no pueden implementarse con anotaciones estándar como `@NotNull`.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar las dependencias de validación y la estructura base del proyecto.

- [ ] T001 Añadir `spring-boot-starter-validation` al proyecto Spring Boot para habilitar anotaciones estándar como `@NotNull`, `@NotBlank` y `@FutureOrPresent`, y para permitir la creación de Custom Validators.
- [ ] T002 Configurar la librería de manejo de formularios en React (ej. React Hook Form o Formik) junto con Yup/Zod para validaciones en el cliente antes de enviar al backend.
- [ ] T003 Configurar variables de entorno y conexión a PostgreSQL.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Definir el esquema relacional completo, las entidades, los DTOs de entrada y salida, y el manejo global de errores. Todo debe existir antes de implementar cualquier endpoint.

- [ ] T004 Crear las entidades JPA según las Key Entities del spec con sus relaciones explícitas:
    - `Usuario`: `idUsuario`, `nombre`
    - `Vehiculo`: `idVehiculo`, `tipo`, relación `@ManyToOne` con `Usuario`
    - `Seguro`: `idSeguro`, `estado`, relación `@ManyToOne` con `Usuario`
    - `Contrato`: `idContrato`, `tipoContrato`, `nombreConductor`, `precioParadas` (nullable), `precio` (nullable), `tipoVehiculo`, `fechaInicio`, `fechaFinal`, relación `@ManyToOne` con `Usuario` y `@ManyToOne` con `Vehiculo`. La restricción `@Column(unique = true)` se aplica sobre `idContrato`.

  La relación entre `Contrato` y las entidades satelitales es: un contrato pertenece a un `Usuario` (el conductor) y está asociado a un `Vehiculo`. El `Seguro` está asociado al `Usuario`, no directamente al `Contrato`.

- [ ] T005 Crear los DTOs de entrada y salida desde esta fase, ya que son necesarios para todos los endpoints:
    - `ContratoRequestDTO`: campos obligatorios con anotaciones `@NotNull` y `@NotBlank` para los campos estándar. Los campos `precioParadas` y `precio` se validan condicionalmente mediante Custom Validator.
    - `ContratoResponseDTO`: proyección de los datos del contrato que se expone al frontend, sin exponer campos internos de la entidad JPA.

- [ ] T006 Implementar el `GlobalExceptionHandler` con `@RestControllerAdvice` para capturar y transformar en respuestas HTTP estructuradas:
    - `MethodArgumentNotValidException` → HTTP 400 con lista de campos faltantes o inválidos (FR-002, edge case campos incompletos).
    - `DataIntegrityViolationException` → HTTP 409 con mensaje "El contrato con este identificador ya existe" (FR-004).
    - `EntityNotFoundException` → HTTP 404 con mensaje "Contrato no encontrado".

- [ ] T007 Implementar los `JpaRepository` para `Contrato`, `Usuario`, `Vehiculo` y `Seguro`, incluyendo el método `existsByIdContrato(String idContrato)` en `ContratoRepository` para validación de duplicados en la capa de servicio.

**Checkpoint**: El esquema está creado con todas sus relaciones y restricciones, los DTOs de entrada y salida existen, y el sistema rechaza automáticamente payloads malformados o duplicados con mensajes de error estructurados.

---

## Phase 3: User Story 1 — Registrar el contrato (Prioridad: P1)

**Goal**: Permitir la persistencia segura de un nuevo contrato cumpliendo todas las reglas de negocio: campos obligatorios, coherencia de fechas, precio condicional según tipo de contrato y prevención de duplicados.

**Independent Test**: Enviar un `POST /api/contratos` con fechas invertidas (fin antes de inicio) y verificar HTTP 400 con el mensaje de error de validación. Enviar un payload válido completo y verificar HTTP 201 con el contrato creado en `ContratoResponseDTO`. Enviar el mismo payload dos veces y verificar HTTP 409 con el mensaje "El contrato con este identificador ya existe".

### Tests para User Story 1

- [ ] T008 [P] [US1] Test unitario para `FechasContratoValidator`: verificar que lanza error de validación cuando la fecha de fin es igual a la fecha de inicio (fechas iguales no son válidas, ya que el spec exige Fecha Final estrictamente mayor a Fecha Inicio) y cuando la fecha de fin es anterior a la de inicio.
- [ ] T009 [P] [US1] Test unitario para `PrecioCondicionalValidator`: verificar que cuando `tipoContrato` es "Por Parada" el campo `precioParadas` es obligatorio y `precio` se ignora, y que cuando `tipoContrato` es "Recorrido Completo" el campo `precio` es obligatorio y `precioParadas` se ignora.
- [ ] T010 [P] [US1] Test de integración verificando que al enviar campos obligatorios faltantes, la respuesta HTTP 400 contiene la lista de los campos específicos que faltan, no solo un mensaje genérico de error (edge case campos incompletos del spec).
- [ ] T011 [P] [US1] Test de integración verificando que al intentar registrar un contrato duplicado, la respuesta HTTP 409 contiene el mensaje "El contrato con este identificador ya existe" (escenario 3 de la User Story 1).
- [ ] T012 [P] [US1] Test de integración con payload válido completo: verificar HTTP 201 y que el contrato queda persistido correctamente en base de datos con todas sus relaciones.

### Implementation para User Story 1

- [ ] T013 [P] [US1] Implementar `FechasContratoValidator` como Custom Validator de Bean Validation a nivel de clase en `ContratoRequestDTO`. Valida que `fechaFinal` sea estrictamente mayor a `fechaInicio`.
- [ ] T014 [P] [US1] Implementar `PrecioCondicionalValidator` como Custom Validator de Bean Validation a nivel de clase en `ContratoRequestDTO`. Valida que el campo de precio correcto esté presente según el `tipoContrato` recibido.
- [ ] T015 [P] [US1] Implementar `ContratoService.registrarContrato(ContratoRequestDTO dto)` marcado con `@Transactional` para garantizar que la creación del contrato y todas sus asociaciones con `Usuario` y `Vehiculo` ocurran de forma atómica. Si cualquier asociación falla, la operación completa se revierte.
- [ ] T016 [US1] Crear el endpoint `POST /api/contratos` en `ContratoController`, protegido para el rol `ROLE_GESTOR_TARIFAS`.
- [ ] T017 [US1] Construir en React el formulario interactivo de "Nuevo Contrato" que: muestra los errores de validación en tiempo real debajo de cada campo, oculta o muestra el campo de precio correcto según el tipo de contrato seleccionado, y despliega el mensaje de error del backend cuando el contrato ya existe.

---

## Phase 4: User Story 2 — Consultar contrato (Prioridad: P3)

**Goal**: Facilitar la búsqueda y visualización de la información contractual registrada, incluyendo sus relaciones con vehículo y seguro, sin problema de N+1 consultas.

**Independent Test**: Consultar un contrato existente vía `GET /api/contratos/{idContrato}` y validar que el JSON de respuesta en `ContratoResponseDTO` contiene todos los campos del contrato. Consultar un identificador inexistente y verificar HTTP 404 con mensaje "Contrato no encontrado".

### Tests para User Story 2

- [ ] T018 [P] [US2] Test de integración para validar la correcta recuperación de un contrato y que el `ContratoResponseDTO` retornado contiene todos los campos esperados según el spec.
- [ ] T019 [P] [US2] Test unitario verificando que el controlador retorna HTTP 404 con el mensaje "Contrato no encontrado" cuando el identificador no existe en base de datos.

### Implementation para User Story 2

- [ ] T020 [P] [US2] Implementar el método de búsqueda en `ContratoRepository` usando `@EntityGraph` o `JOIN FETCH` para cargar el contrato junto con su `Vehiculo` y `Usuario` en una sola consulta, evitando el problema de N+1 desde el primer día.
- [ ] T021 [US2] Crear el endpoint `GET /api/contratos/{idContrato}` en `ContratoController`, que retorna el contrato en `ContratoResponseDTO`.
- [ ] T022 [US2] Desarrollar la vista en React con un campo de búsqueda que consulte la API y muestre la información completa del contrato encontrado, o el mensaje "No se encontraron resultados" cuando el identificador no exista.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T023 Añadir alertas globales (Toast notifications) en React para confirmar "Contrato guardado exitosamente" o mostrar los mensajes de error del backend de forma visible al usuario.
- [ ] T024 Añadir Swagger / OpenAPI para documentar los endpoints `POST /api/contratos` y `GET /api/contratos/{idContrato}`, facilitando la integración con otros módulos que consuman información de contratos.

---

## Dependencies & Execution Order

**DTOs de entrada y salida desde la Phase 2**: Los `ContratoRequestDTO` y `ContratoResponseDTO` deben existir antes de construir cualquier endpoint. Implementarlos al final como "polish" expone las entidades JPA directamente al frontend durante todo el desarrollo.

**Custom Validators antes del servicio**: `FechasContratoValidator` y `PrecioCondicionalValidator` deben implementarse y probarse con tests unitarios antes de integrarlos al endpoint. Las anotaciones estándar de Bean Validation no pueden implementar estas reglas condicionales.

**`@Transactional` en el servicio desde el inicio**: La creación del contrato y sus asociaciones deben ser atómicas desde la primera implementación. Agregarlo después puede dejar registros parciales en base de datos durante el período de desarrollo.

**Relaciones claras entre entidades**: `Contrato` se asocia a `Usuario` y `Vehiculo`. El `Seguro` está asociado al `Usuario`, no directamente al `Contrato`. Esta distinción debe respetarse al construir las entidades JPA y los DTOs.

**`@EntityGraph` desde el primer endpoint de consulta**: La optimización de consultas con `JOIN FETCH` debe implementarse junto con el endpoint `GET`, no como mejora posterior. En una tabla con miles de contratos, el problema de N+1 aparece desde el primer día en producción.

**Frontend al final de cada historia**: React se construye una vez que el backend tiene validado y probado el flujo completo de cada historia de usuario.
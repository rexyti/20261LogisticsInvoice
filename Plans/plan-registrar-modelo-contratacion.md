# Implementation Plan: Registrar modelo de contratación

**Date**: 2026-04-08
**Spec**: [Registrar modelo de contratación.md]

## Summary

Este módulo es el núcleo administrativo del sistema de tarifas. Permite la creación formal y la consulta de los modelos de contratación de los transportistas. Su objetivo principal es garantizar la absoluta integridad de los datos de entrada: previene la duplicidad de identificadores, asegura la coherencia temporal de las fechas de vigencia y aplica reglas de negocio dinámicas (como la validación del tipo de precio según el tipo de contrato).

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+
**Primary Dependencies**: Spring Boot (Web, Data JPA, Validation), PostgreSQL Driver, Axios
**Storage**: PostgreSQL 15
**Testing**: JUnit 5, Mockito, Spring Boot Test / Jest, React Testing Library
**Target Platform**: AWS
**Project Type**: Web application (Backend API + Frontend Dashboard)
**Performance Goals**: Consultas de contratos en < 200ms; inserciones con validación en < 300ms.
**Constraints**: Restricciones UNIQUE en la base de datos para identificadores externos (FR-004). Validación estricta a nivel de API para campos obligatorios y coherencia de fechas (Fecha Final > Fecha Inicio).

## Project Structure

### Documentation (this feature)

```text
specs/registrar-contrato/
├── plan.md              # Este archivo 
└── spec.md             # Especificación: Registrar modelo de contratación.md
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/logistica/
│   ├── config/          # Configuraciones globales
│   ├── controllers/     # Endpoints REST para contratos (GET, POST)
│   ├── dtos/            # Objetos de transferencia con anotaciones @Valid
│   ├── exceptions/      # Manejadores de errores personalizados (@RestControllerAdvice)
│   ├── models/          # Entidades JPA (Contrato, Usuario, Vehiculo, Seguro)
│   ├── repositories/    # Interfaces Spring Data JPA
│   └── services/        # Lógica de registro y validaciones complejas
└── src/test/java/       # Pruebas unitarias de validación y persistencia

frontend/
├── src/
│   ├── components/      # UI: Formularios de registro, modales de error
│   ├── hooks/           # Custom hooks para manejar estados del formulario
│   ├── services/        # Peticiones Axios
│   └── pages/           # Vistas: Crear Contrato y Buscar Contrato
└── package.json
```

**Structure Decision**: Se introduce una carpeta de exceptions/ en el backend para manejar de forma centralizada las violaciones de integridad y los campos faltantes, garantizando que el frontend reciba mensajes de error limpios y estandarizados.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar las dependencias de validación y la estructura base.

- [ ] T001 Añadir spring-boot-starter-validation al proyecto Spring Boot para habilitar validaciones como @NotNull, @NotBlank y @FutureOrPresent.
- [ ] T002 Configurar la librería de manejo de formularios en React (ej. React Hook Form o Formik) junto con Yup/Zod para validaciones en el cliente.
- [ ] T003 Configurar variables de entorno y conexión a PostgreSQL.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Esquema relacional, entidades maestras y manejo global de errores.

- [ ] T004 Crear entidades JPA satélites: Usuario, Vehiculo y Seguro. Estas deben existir para mantener la integridad referencial.
- [ ] T005 Crear la entidad Contrato con restricción @Column(unique = true) en el campo idContrato (identificador externo).
- [ ] T006 Implementar el @RestControllerAdvice (GlobalExceptionHandler.java) para capturar: MethodArgumentNotValidException (para campos incompletos - FR-002) y DataIntegrityViolationException (para intentos de duplicidad - FR-004).
- [ ] T007 Implementar repositorios JPA básicos para las entidades.

**Checkpoint**: El sistema es capaz de rechazar automáticamente payloads malformados o registros duplicados a nivel de base de datos y controlador.

---

## Phase 3: User Story 1 - Registrar el contrato (Priority: P1)

**Goal**: Permitir la persistencia segura de un nuevo contrato cumpliendo todas las reglas de negocio.

**Independent Test**: Enviar un POST a /api/contratos con fechas invertidas (Fin antes de Inicio). El sistema debe retornar HTTP 400 Bad Request con el detalle del error. Enviar un payload correcto y verificar el HTTP 201 Created.

### Tests for User Story 1

- [ ] T008 [P] [US1] JUnit test para verificar que ContratoService lance una excepción si la fecha de fin es anterior a la fecha de inicio.
- [ ] T009 [P] [US1] Test unitario para validar la regla condicional: Si el tipo es "Por Parada", exigir precioParadas; si es otro, exigir precio.
- [ ] T010 [P] [US1] Test de integración confirmando que se evita el registro duplicado (FR-004).

### Implementation for User Story 1

- [ ] T011 [P] [US1] Crear el ContratoRequestDTO aplicando anotaciones de validación. Implementar una validación de clase personalizada (Custom Validator) para verificar la coherencia de fechas y precios condicionales.
- [ ] T012 [US1] Desarrollar la lógica en ContratoService.java para asociar el Contrato con su Vehículo, Seguro y Usuario, y guardarlo.
- [ ] T013 [US1] Crear el endpoint POST /api/contratos.
- [ ] T014 [US1] En React, construir el formulario interactivo de "Nuevo Contrato" mostrando los errores de validación en tiempo real debajo de cada campo.

---

## Phase 4: User Story 2 - Consultar contrato (Priority: P3)

**Goal**: Facilitar la búsqueda y visualización de la información contractual guardada.

**Independent Test**: Consultar un contrato existente vía GET /api/contratos/{idContrato} y validar que el JSON de respuesta contenga todos los detalles, incluyendo vehículo y seguro.

### Tests for User Story 2

- [ ] T015 [P] [US2] Test de integración para validar la correcta recuperación de un contrato y sus entidades anidadas.
- [ ] T016 [US2] Test unitario verificando que el controlador retorne HTTP 404 Not Found si el identificador no existe.

### Implementation for User Story 2

- [ ] T017 [P] [US2] Implementar método de búsqueda en ContratoRepository por el identificador externo.
- [ ] T018 [US2] Crear el endpoint GET /api/contratos/{idContrato}.
- [ ] T019 [US2] Desarrollar la vista en React con un campo de búsqueda (Search Bar) que consulte la API y una tarjeta/tabla que despliegue la información completa del contrato encontrado.
- [ ] T020 [US2] Manejar el estado de "No se encontraron resultados" en la UI de React.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T021 Implementar DTOs de respuesta (ContratoResponseDTO) para no exponer las entidades completas de base de datos hacia el frontend (buena práctica de seguridad).
- [ ] T022 Añadir alertas globales (Toast notifications) en React para confirmar "Contrato guardado exitosamente" o mostrar los mensajes de error del backend.
- [ ] T023 Optimizar las consultas con @EntityGraph o JOIN FETCH en JPA para evitar el problema de N+1 consultas al recuperar contratos y sus relaciones (Vehículos/Seguros).

---

## Dependencies & Execution Order

**Entidades Base**: Primero deben crearse los repositorios de Vehiculo, Seguro y Usuario. Un contrato no puede existir en el aire sin estar atado a un conductor y su vehículo.

**Capa de Validación (Backend)**: Definir los DTOs y el ExceptionHandler antes de construir los endpoints asegura que ninguna data basura entre al sistema.

**Formulario React**: El desarrollo del frontend debe ir emparejado con la API, mapeando exactamente los mismos nombres de campos para facilitar la serialización del JSON.

# Implementation Plan: Calcular liquidación

**Date**: 2026-04-07
**Spec**: [Calcular liquidación.md]

## Summary

El objetivo de esta funcionalidad es automatizar el cálculo de las liquidaciones de los transportistas basándose en el cierre de rutas, el estado final de los paquetes y los modelos de contratación definidos. El sistema debe procesar datos de rutas finalizadas, aplicar tarifas, gestionar penalizaciones y permitir recálculos si existen ajustes posteriores, garantizando siempre la trazabilidad mediante auditorías.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+
**Primary Dependencies**: Spring Boot, PostgreSQL Driver, Axios
**Storage**: PostgreSQL 15
**Testing**: JUnit 5, Mockito / Jest
**Target Platform**: AWS
**Project Type**: Web application
**Performance Goals**: Procesamiento del cálculo en el servidor < 300ms
**Constraints**: Consistencia transaccional ACID, gestión segura de variables de entorno para la nube, y prevención de duplicados.
**Scale/Scope**: Preparado para escalar horizontalmente en la nube gestionando miles de cierres de ruta.

## Project Structure

### Documentation (this feature)

```text
specs/calcular-liquidacion/
├── plan.md              # Este archivo 
└── spec.md             # Especificación: Calcular liquidación.md
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/logistica/
│   ├── config/          # Configuraciones 
│   ├── controllers/     # Endpoints REST API
│   ├── models/          # Entidades JPA 
│   ├── repositories/    # Interfaces Spring Data JPA
│   └── services/        # Lógica de cálculo y reglas de negocio
├── Dockerfile           # Instrucciones para empaquetar en AWS
└── pom.xml / build.gradle

frontend/
├── src/
│   ├── components/      # UI: Tablas, modales y botones en React
│   ├── services/        # Peticiones Axios hacia la API de Spring Boot
│   └── pages/           # Vistas principales
├── Dockerfile           
└── package.json
```

**Structure Decision**: Se utiliza una estructura completamente desacoplada con archivos de configuración para contenedores, lo cual es el estándar de la industria para despliegues en AWS.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Configuración inicial y preparación para la nube.

- [ ] T001 Inicializar el proyecto Spring Boot con Web, JPA y el driver de PostgreSQL.
- [ ] T002 Inicializar el proyecto React usando Vite.
- [ ] T003 Crear los archivos Dockerfile básicos para el backend y frontend para simular el entorno de AWS en local.
- [ ] T004 Configurar el archivo application.properties en Spring Boot usando variables de entorno para la conexión a la base de datos.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Esquema de datos, conectividad y seguridad de comunicación.

- [ ] T005 Configurar CORS Global en Spring Boot para permitir que el frontend de React se comunique con la API.
- [ ] T006 Crear las entidades JPA para Contrato, Ruta, Liquidacion y Penalizacion mapeando a las tablas de PostgreSQL.
- [ ] T007 Implementar los JpaRepository para cada entidad.
- [ ] T008 Implementar un @RestControllerAdvice para capturar errores de base de datos y validaciones, retornando JSONs limpios a React.

**Checkpoint**: Backend expone rutas y se conecta a PostgreSQL mediante variables de entorno; el frontend puede hacer llamadas básicas sin errores de CORS.

---

## Phase 3: User Story 1 - Calcular liquidación automáticamente (Priority: P1)

**Goal**: Generar el valor final de la liquidación basado en la información de la ruta.

**Independent Test**: Lanzar el backend y enviar un POST con Postman o cURL simulando a React. Verificar el cálculo y la inserción en PostgreSQL.

### Tests for User Story 1

- [ ] T009 [P] [US1] JUnit 5 test para validar que CalculationService aplique la tarifa correcta según las paradas.
- [ ] T010 [P] [US1] Test de integración con @DataJpaTest para confirmar la restricción de liquidaciones duplicadas.

### Implementation for User Story 1

- [ ] T011 [P] [US1] Implementar la lógica matemática del cálculo y penalizaciones base en CalculationService.java.
- [ ] T012 [US1] Crear el controlador REST POST /api/liquidaciones/calcular.
- [ ] T013 [US1] Desarrollar el servicio en React con Axios para invocar el endpoint de cálculo.
- [ ] T014 [US1] Crear el componente visual en React que muestre el resumen del cálculo exitoso al usuario.

---

## Phase 4: User Story 2 - Recalcular liquidación (Priority: P2)

**Goal**: Permitir agregar nuevos ajustes sobre un cálculo existente manteniendo la trazabilidad.

**Independent Test**: Modificar penalizaciones desde la UI de React, pulsar recalcular, y confirmar en la base de datos que el registro de auditoría fue creado en PostgreSQL.

### Tests for User Story 2

- [ ] T015 [P] [US2] Test unitario para verificar que el servicio de auditoría guarde el valor anterior y el nuevo.
- [ ] T016 [US2] Test de componente en React para confirmar que la vista de liquidación se actualiza sin recargar la página completa.

### Implementation for User Story 2

- [ ] T017 [P] [US2] Crear la entidad JPA AuditoriaLiquidacion para cumplir con el FR-003.
- [ ] T018 [US2] Implementar el método de actualización en CalculationService y registrar la auditoría.
- [ ] T019 [US2] Crear el controlador PUT /api/liquidaciones/{id}/recalcular.
- [ ] T020 [US2] Desarrollar un formulario Modal en React para que el gestor ingrese el ajuste y confirme el recálculo.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T021 Configurar perfiles de Spring Boot específicos para despliegue en AWS RDS.
- [ ] T022 Añadir Swagger/OpenAPI para documentar la API y facilitar la integración con el equipo de frontend.
- [ ] T023 Implementar manejo de estados de carga en React mientras se espera la respuesta de Spring Boot.

---

## Dependencies & Execution Order

**Variables y Configuración (Fase 1 y 2)**: Es crítico resolver el CORS y la inyección de credenciales de DB antes de empezar a programar la lógica de negocio.

**Modelos JPA**: Se deben definir las relaciones en Java antes de codificar los repositorios.

**Servicios antes de Controladores**: El motor de cálculo matemático se programa y prueba con JUnit antes de exponerlo vía REST.

**Integración UI**: React entra en juego al final de cada historia de usuario, consumiendo lo que Spring Boot ya tiene validado.

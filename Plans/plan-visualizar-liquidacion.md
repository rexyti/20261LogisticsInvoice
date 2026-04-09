# Implementation Plan: Visualizar liquidación

**Date**: 2026-04-08
**Spec**: [Visualizar liquidación.md]

## Summary

Este módulo proporciona la interfaz de consulta y trazabilidad para las liquidaciones ya procesadas. Permite a los analistas financieros buscar, filtrar y visualizar en detalle los registros, asegurando al mismo tiempo que los transportistas individuales solo puedan acceder a sus propias liquidaciones. La implementación técnica prioriza la eficiencia en la recuperación de datos mediante paginación indexada y el manejo resiliente frente a caídas de la base de datos.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+
**Primary Dependencies**: Spring Boot (Web, Data JPA, Security), Axios, React Table (o similar para DataGrids)
**Storage**: PostgreSQL 15
**Testing**: JUnit 5, Mockito, Spring Security Test / Jest, React Testing Library
**Target Platform**: AWS
**Project Type**: Web application (Backend API + Frontend Dashboard)
**Performance Goals**: Búsquedas y filtrados de liquidaciones en < 300ms.
**Constraints**: Control de acceso estricto basado en roles (ROLE_FINANCE vs ROLE_DRIVER). Paginación obligatoria en el servidor para evitar sobrecarga de memoria al consultar el historial.

## Project Structure

### Documentation (this feature)

```text
specs/visualizar-liquidacion/
├── plan.md              # Este archivo 
└── spec.md             # Especificación: Visualizar liquidación.md
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/logistica/
│   ├── config/          # Configuración de Roles y Seguridad
│   ├── controllers/     # Endpoints REST (GET /liquidaciones)
│   ├── dtos/            # DTOs de respuesta (LiquidacionListDTO, LiquidacionDetailDTO)
│   ├── exceptions/      # Manejo de caídas de BD (DataAccessResourceFailureException)
│   ├── repositories/    # Repositorios con soporte de Paginación (PagingAndSortingRepository)
│   └── services/        # Lógica de filtrado y control de acceso
└── src/test/java/       # Pruebas de acceso por roles y filtros de búsqueda

frontend/
├── src/
│   ├── components/      # UI: DataGrids, Barras de búsqueda, Filtros
│   ├── services/        # Peticiones Axios con parámetros de paginación
│   └── pages/           # Vistas: Historial de Liquidaciones
└── package.json
```

**Structure Decision**: Se mantiene la arquitectura base, pero se incorpora fuertemente el concepto de paginación (Pageable en Spring) en la capa de repositorios y controladores.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar la infraestructura de seguridad por roles y manejo de excepciones globales.

- [ ] T001 Definir y configurar los roles en Spring Security (ej. ROLE_FINANCIERO, ROLE_TRANSPORTISTA).
- [ ] T002 Implementar un @ExceptionHandler en el GlobalExceptionHandler.java para capturar CannotCreateTransactionException o DataAccessResourceFailureException, devolviendo un HTTP 503 (Service Unavailable) para cubrir el Edge Case de caída de base de datos.
- [ ] T003 En React, configurar el componente visual de DataGrid (Tabla interactiva) que soporte paginación desde el servidor y estados de carga (Loading/Error).

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Estructurar las consultas eficientes y el control de acceso en la base de datos.

- [ ] T004 Implementar consultas paginadas en LiquidacionRepository usando Page<Liquidacion> findAll(Pageable pageable).
- [ ] T005 Implementar consulta de búsqueda específica: Optional<Liquidacion> findById(Long id).
- [ ] T006 Implementar consulta filtrada por usuario para garantizar el FR-004: Page<Liquidacion> findByContrato_Usuario_Id(Long usuarioId, Pageable pageable).
- [ ] T007 Crear los DTOs de lectura plana (LiquidacionListDTO) para evitar enviar el árbol completo de relaciones a la vista de lista.

**Checkpoint**: El backend es capaz de devolver cientos de registros segmentados en páginas de 10 o 20 elementos sin saturar la memoria.

---

## Phase 3: User Story 1 - Visualizar y Buscar liquidaciones (Priority: P1)

**Goal**: Exponer los datos al frontend asegurando que cada rol vea exactamente lo que le corresponde.

**Independent Test**: Autenticarse como ROLE_FINANCIERO y hacer una petición GET, confirmando que retorna liquidaciones de múltiples usuarios. Autenticarse como ROLE_TRANSPORTISTA y confirmar que el mismo endpoint solo retorna sus liquidaciones personales.

### Tests for User Story 1

- [ ] T008 [P] [US1] Test de integración con @WithMockUser(roles="FINANCIERO") verificando el acceso total a la lista.
- [ ] T009 [P] [US1] Test de integración con @WithMockUser(roles="TRANSPORTISTA", username="user123") verificando que se filtra la información correctamente.
- [ ] T010 [P] [US1] Test de validación de respuesta HTTP 404 Not Found cuando se busca un ID de liquidación inexistente.

### Implementation for User Story 1

- [ ] T011 [P] [US1] Implementar la lógica en LiquidacionService.java que evalúe el rol del usuario actual: Si es Financiero → Ejecuta findAll. Si es Transportista → Extrae su ID del token y ejecuta findByUsuario.
- [ ] T012 [US1] Crear el controlador GET /api/liquidaciones que acepte parámetros ?page=0&size=10&search=ID_Ruta.
- [ ] T013 [US1] Crear el controlador GET /api/liquidaciones/{id} para obtener el detalle completo (incluyendo array de Ajustes y paradas).
- [ ] T014 [US1] Desarrollar la vista en React que renderice la tabla, la barra de búsqueda y controle los estados vacíos ("Liquidación inexistente").

---

## Phase 4: Edge Cases & UI/UX (Priority: P2)

**Goal**: Manejar errores de sistema y mejorar la experiencia del usuario.

**Independent Test**: Bajar el servicio de PostgreSQL localmente e intentar buscar una liquidación. El frontend debe mostrar un banner amigable de "Sistema de almacenamiento no disponible temporalmente".

### Tests for Edge Cases

- [ ] T015 [P] [Edge] Mockear un fallo de conexión a BD en el test de controlador y afirmar que retorna HTTP 503.

### Implementation for Edge Cases

- [ ] T016 [P] [Edge] Implementar interceptor en Axios (React) para capturar errores HTTP 503 y mostrar una alerta global de "Sistema no disponible".
- [ ] T017 [UI] Desarrollar la vista de "Detalle de Liquidación" en React que despliegue el desglose del Monto Bruto, Ajustes y Monto Neto en un formato de factura/recibo.
- [ ] T018 [UI] Añadir esqueletos de carga (Skeleton Loaders) en React mientras se espera la resolución de la búsqueda.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T019 Agregar índices en PostgreSQL a las columnas idRuta y fechaCalculo en la tabla liquidaciones para acelerar drásticamente los filtros de búsqueda.
- [ ] T020 Implementar filtros avanzados en el API (por rango de fechas o por estado de liquidación) usando Specification de Spring Data JPA.
- [ ] T021 Auditar las consultas realizadas por los usuarios financieros registrando quién buscó qué liquidación (para cumplimiento de normativas).

---

## Dependencies & Execution Order

**Datos Requeridos**: Esta funcionalidad depende completamente de que las historias de Calcular liquidación ya estén finalizadas, de lo contrario no habrá datos reales que mostrar ni paginar.

**Capa de Repositorio (Paginación)**: La paginación en base de datos debe implementarse desde el día uno. Intentar implementarla después cuando la tabla tenga millones de registros causará problemas de refactorización masivos.

**Control de Acceso (Servicio)**: Las reglas lógicas de quién puede ver qué registro deben centralizarse en la capa de servicio y probarse antes de conectar el controlador REST.

**Frontend**: Se integra al final, alimentándose del objeto Page devuelto por Spring Boot (que incluye los metadatos de cuántas páginas totales existen).

# Implementation Plan: Cierre de ruta

**Date**: 2026-04-08
**Spec**: [Cierre de ruta.md]

## Summary

El objetivo de esta funcionalidad es que el Módulo Financiero procese de forma asíncrona los eventos de RUTA_CERRADA emitidos por el Módulo de Flotas. El sistema debe recibir el payload en formato JSON, persistir la información del transportista, vehículo y desglosar las paradas (exitosas y fallidas con sus motivos). Esto servirá como base de datos limpia y estructurada para el posterior cálculo de liquidaciones, garantizando el manejo de casos borde como modelos de contrato nulos o tarifas inexistentes.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+
**Primary Dependencies**: Spring Boot, Spring Cloud AWS (SQS) / Spring Kafka (para eventos asíncronos), PostgreSQL Driver, Axios
**Storage**: PostgreSQL 15
**Testing**: JUnit 5, Mockito, Testcontainers (para simular colas de mensajes) / Jest
**Target Platform**: AWS (EC2/ECS, y AWS SQS/EventBridge para la mensajería)
**Project Type**: Web application (Backend Consumer + Frontend Dashboard)
**Performance Goals**: Procesar y guardar el evento en base de datos en < 5 segundos.
**Constraints**: Garantizar idempotencia (ignorar eventos duplicados usando ruta_id) y clasificar correctamente los motivos de falla financiera.

## Project Structure

### Documentation (this feature)

```text
specs/cierre-ruta/
├── plan.md              # Este archivo 
└── spec.md             # Especificación: Cierre de ruta.md
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/logistica/
│   ├── config/          # Configuración de Colas (SQS/Kafka) y Base de Datos
│   ├── consumers/       # Listeners para consumir el evento asíncrono
│   ├── dtos/            # Objetos para mapear el JSON de entrada (RutaCerradaEvent)
│   ├── models/          # Entidades JPA (Ruta, Parada, Transportista)
│   ├── repositories/    # Interfaces Spring Data JPA
│   └── services/        # Lógica de validación, clasificación y persistencia
└── src/test/            # Pruebas unitarias e integración

frontend/
├── src/
│   ├── components/      # UI: Tablas para monitoreo de rutas cerradas y alertas
│   ├── services/        # Peticiones Axios para consultar el estado de las rutas
│   └── pages/           # Vista del equipo financiero
└── package.json
```

**Structure Decision**: Se mantiene la arquitectura desacoplada, pero en el backend se introduce el patrón Consumer/Listener en lugar de un Controller REST tradicional, ya que la comunicación es asíncrona y sin respuesta directa esperada.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Configurar la infraestructura de mensajería para recibir eventos asíncronos.

- [ ] T001 Añadir las dependencias de mensajería (ej. spring-cloud-starter-aws-messaging o spring-kafka) en el pom.xml/build.gradle.
- [ ] T002 Configurar las credenciales y la URL de la cola (Queue/Topic) en el application.properties usando variables de entorno.
- [ ] T003 Configurar una Dead Letter Queue (DLQ) en AWS para enviar los mensajes JSON que no se puedan procesar por errores fatales.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Crear el esquema de base de datos para almacenar el detalle del evento.

- [ ] T004 Crear las entidades JPA Transportista, Ruta y Parada con sus relaciones (Una ruta tiene muchas paradas).
- [ ] T005 Implementar la lógica de Idempotencia (FR-004): Configurar la columna ruta_id como UNIQUE en PostgreSQL para evitar registros duplicados a nivel de base de datos.
- [ ] T006 Crear los JpaRepository para guardar la ruta y las paradas en cascada.
- [ ] T007 Definir un Enum en Java para los MotivoFalla y reglas de quién es el responsable (Cliente vs Transportador).

**Checkpoint**: La base de datos está lista para recibir el mapeo estructurado del JSON del Módulo de Flotas.

---

## Phase 3: User Story 1 - Procesar Informe de Cierre (Priority: P1)

**Goal**: Escuchar el evento, mapear el JSON y guardarlo en la base de datos aplicando reglas de negocio.

**Independent Test**: Publicar un mensaje JSON ficticio en la cola local. Verificar que los registros de Ruta y Paradas aparezcan en PostgreSQL con la clasificación correcta en menos de 5 segundos.

### Tests for User Story 1

- [ ] T008 [P] [US1] JUnit 5 test usando @JsonTest para validar que el RutaCerradaEventDTO mapea perfectamente el JSON del contrato.
- [ ] T009 [P] [US1] Test de integración con Mockito para asegurar que la inserción se aborta/ignora si el ruta_id ya existe en la base de datos.

### Implementation for User Story 1

- [ ] T010 [P] [US1] Crear la clase DTO RutaCerradaEvent que represente la estructura del JSON enviado por el Módulo de Flotas.
- [ ] T011 [US1] Implementar el Listener (ej. @SqsListener("ruta-cerrada-queue")) en la capa consumers/.
- [ ] T012 [US1] Implementar la lógica en RutaService.java para procesar el DTO: determinar responsable de la falla y persistir en la base de datos (FR-001, FR-002).
- [ ] T013 [US1] Crear un endpoint REST GET /api/rutas/{id} (opcional por ahora) para que el frontend pueda consultar los datos procesados.

---

## Phase 4: Edge Cases & Manejo de Alertas (Priority: P2)

**Goal**: Manejar las inconsistencias en los datos maestros (vehículos desconocidos o contratos nulos) informando al equipo financiero.

**Independent Test**: Enviar un evento con un tipo de vehículo "INVENTADO". Verificar que la ruta se guarde pero con estado "Alerta: Tarifa no encontrada", y que se visualice en React.

### Tests for User Story 2

- [ ] T014 [P] [US2] Test unitario para verificar la validación de idVehiculo contra el catálogo vigente (FR-003).

### Implementation for User Story 2

- [ ] T015 [P] [US2] Modificar la entidad Ruta para añadir un campo estado_procesamiento (ej. OK, REQUIERE_REVISION).
- [ ] T016 [US2] Implementar la lógica para marcar la ruta con alerta si el modelo_contrato es nulo o el tipo de vehículo no existe.
- [ ] T017 [US2] Desarrollar un dashboard en React que consuma las rutas con estado REQUIERE_REVISION para que el equipo financiero pueda aplicar acciones manuales.

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T018 Implementar manejo de transacciones con @Transactional en Spring Boot para asegurar que si falla el guardado de una parada, no se guarde la ruta incompleta (ACID).
- [ ] T019 Añadir logs detallados (Slf4j) al iniciar y terminar el procesamiento del evento para auditoría en CloudWatch (AWS).
- [ ] T020 Refinar la UI en React implementando filtros por fecha y estado de procesamiento de ruta.

---

## Dependencies & Execution Order

**Configuración de Mensajería**: Primero se debe establecer el canal de comunicación (La cola asíncrona) para poder inyectar mensajes de prueba.

**DTOs y Modelos**: Mapear correctamente el JSON entrante a objetos Java es el bloque fundamental.

**Persistencia (Fase 3)**: Programar el guardado y probar exhaustivamente la idempotencia (evitar duplicados).

**Casos Borde (Fase 4)**: Una vez funciona el camino feliz, se añaden las validaciones para notificar al equipo financiero sobre contratos o vehículos inválidos.

**Frontend**: El dashboard de monitoreo en React se construye al final para visualizar los datos ya procesados por el backend.

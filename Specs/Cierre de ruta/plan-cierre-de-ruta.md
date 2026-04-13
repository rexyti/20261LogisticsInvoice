# Implementation Plan: Cierre de ruta

**Date**: 2026-04-08
**Spec**: [Cierre de ruta.md]

## Summary

El objetivo de esta funcionalidad es que el Módulo Financiero procese de forma asíncrona los eventos `RUTA_CERRADA` emitidos por el Módulo de Flotas y Rutas. El sistema debe recibir el payload en formato JSON, validar su contenido, clasificar cada parada según el responsable de la falla (cliente, transportista o daño en ruta) y persistir la información estructurada en base de datos. Esta información es el insumo directo para el cálculo automático de liquidaciones, por lo que la integridad y completitud de los datos almacenados es crítica.

El sistema garantiza idempotencia ignorando eventos duplicados por `ruta_id`, maneja casos de contratos nulos o vehículos sin tarifa mediante logs de advertencia y notificaciones al equipo financiero para revisión manual, y asegura que el total de paradas guardadas siempre coincida con el total enviado en el evento.

## Technical Context

**Language/Version**: Java 21 / JavaScript / React 18+

**Primary Dependencies**: Spring Boot (Web, Data JPA, Validation, Security), Spring Cloud AWS (SQS) / Spring Kafka (para eventos asíncronos), PostgreSQL Driver, Axios

**Storage**: PostgreSQL 15

**Testing**: JUnit 5, Mockito, Testcontainers (para simular colas de mensajes) / Jest, React Testing Library

**Target Platform**: AWS (EC2/ECS, AWS SQS/EventBridge para mensajería)

**Project Type**: Web application (Backend Consumer + Frontend Dashboard)

**Performance Goals**: Procesar y persistir el evento completo en base de datos en menos de 5 segundos tras su recepción.

**Constraints**: Garantizar idempotencia mediante restricción `UNIQUE` sobre `ruta_id` en base de datos. Consistencia transaccional ACID: si falla el guardado de cualquier parada, la ruta completa se revierte. Clasificación correcta de motivos de falla según las reglas financieras del negocio.

## Project Structure

### Documentation (this feature)

```text
specs/cierre-ruta/
├── plan.md              # Este archivo
└── spec.md              # Especificación: Cierre de ruta.md
```

### Source Code (repository root)

```text
project/
├── backend/
│   ├── src/main/java/com/logistica/
│   │
│   │   ├── application/                             # Casos de uso (orquestación)
│   │   │   ├── usecases/
│   │   │   │   ├── ruta/
│   │   │   │   │   ├── ProcesarRutaCerradaUseCase.java
│   │   │   │   │   ├── RegistrarRutaUseCase.java
│   │   │   │   │   └── ClasificarResultadoRutaUseCase.java
│   │   │   │
│   │   │   └── dtos/
│   │   │       ├── request/
│   │   │       │   └── RutaCerradaEventDTO.java
│   │   │       │
│   │   │       └── response/
│   │   │           └── RutaProcesadaResponseDTO.java
│   │
│   │   ├── domain/                                  # Núcleo del negocio
│   │   │   ├── models/
│   │   │   │   ├── Ruta.java
│   │   │   │   ├── Parada.java
│   │   │   │   └── Transportista.java
│   │   │   │
│   │   │   ├── enums/
│   │   │   │   └── MotivoFalla.java                # Responsable + % pago
│   │   │   │
│   │   │   ├── repositories/                        # Puertos
│   │   │   │   ├── RutaRepository.java
│   │   │   │   ├── ParadaRepository.java
│   │   │   │   └── TransportistaRepository.java
│   │   │   │
│   │   │   ├── services/                            # Lógica de negocio
│   │   │   │   ├── ClasificacionRutaService.java
│   │   │   │   └── IdempotenciaRutaService.java
│   │   │   │
│   │   │   ├── validators/                          # Reglas de negocio
│   │   │   │   └── RutaValidator.java
│   │   │   │
│   │   │   ├── events/                              # Eventos de dominio
│   │   │   │   └── RutaCerradaProcesadaEvent.java
│   │   │   │
│   │   │   └── exceptions/
│   │   │       └── EventoDuplicadoException.java
│   │
│   │   ├── infrastructure/                          # Implementación técnica
│   │   │   ├── messaging/                          # Integración con colas
│   │   │   │   ├── consumers/
│   │   │   │   │   └── RutaCerradaConsumer.java
│   │   │   │   │
│   │   │   │   └── config/
│   │   │   │       ├── KafkaConfig.java            # o SQSConfig.java
│   │   │   │       └── ConsumerConfig.java
│   │   │   │
│   │   │   ├── persistence/
│   │   │   │   ├── entities/
│   │   │   │   │   ├── RutaEntity.java
│   │   │   │   │   ├── ParadaEntity.java
│   │   │   │   │   └── TransportistaEntity.java
│   │   │   │   │
│   │   │   │   └── repositories/
│   │   │   │
│   │   │   ├── web/                                # (solo lectura si aplica)
│   │   │   │   ├── controllers/
│   │   │   │   │   └── RutaController.java
│   │   │   │   │
│   │   │   │   └── handlers/
│   │   │   │
│   │   │   ├── adapters/                           # Mappers
│   │   │   │   └── RutaMapper.java
│   │   │   │
│   │   │   └── config/
│   │
│   │   └── shared/
│   │       ├── utils/
│   │       ├── constants/
│   │       └── logging/
│
│   └── src/test/
│       ├── unit/
│       └── integration/
│
│
├── frontend/
│   ├── src/
│   │
│   │   ├── app/
│   │
│   │   ├── modules/
│   │   │   ├── rutas/
│   │   │   │   ├── components/                  # Tablas, alertas
│   │   │   │   ├── pages/                       # Dashboard financiero
│   │   │   │   ├── services/                    # Axios calls
│   │   │   │   └── hooks/                       # polling / estado dinámico
│   │   │
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   ├── services/
│   │   │   └── utils/
│   │
│   │   ├── assets/
│   │   └── styles/
│
│   └── package.json
```

**Structure Decision**: Se introduce el patrón Consumer/Listener en lugar de un Controller REST tradicional, ya que la comunicación es asíncrona y sin respuesta directa. El Enum `MotivoFalla` centraliza la lógica de clasificación financiera para evitar condicionales dispersos en el servicio.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Configurar la infraestructura de mensajería asíncrona para recibir y procesar eventos de cierre de ruta.

- [ ] T001 Añadir las dependencias de mensajería (`spring-cloud-starter-aws-messaging` o `spring-kafka`) en el `pom.xml` / `build.gradle`.
- [ ] T002 Configurar las credenciales y la URL de la cola (Queue/Topic) en el `application.properties` usando variables de entorno. Nunca hardcodear credenciales en el código.
- [ ] T003 Registrar el bean del Consumer (`RutaCerradaConsumer`) en el contexto de Spring dentro de `config/`, asegurando que el listener quede activo al iniciar la aplicación y apuntando a la cola correcta según el perfil de entorno (dev/prod).
- [ ] T004 Configurar una Dead Letter Queue (DLQ) en AWS para capturar los mensajes que fallen de forma no recuperable (ej. JSON malformado o error de esquema). Definir la política de reintentos: errores recuperables (fallo temporal de base de datos) deben reintentarse un máximo de 3 veces antes de enviarse a la DLQ. Errores fatales (JSON inválido) van directamente a la DLQ sin reintentos.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Definir el esquema de base de datos, las entidades, el mapeo de motivos de falla y la lógica de idempotencia. Todo esto debe existir antes de implementar el procesamiento del evento.

- [ ] T005 Crear las entidades JPA `Transportista`, `Ruta` y `Parada` con sus relaciones (`@OneToMany`: una ruta tiene muchas paradas), mapeando exactamente los campos definidos en el spec: `idRuta`, `idTransportista`, `tipoVehiculo` y `modeloContrato`.
- [ ] T006 Implementar la restricción de idempotencia (FR-004): configurar la columna `ruta_id` como `UNIQUE` en PostgreSQL mediante migración de Flyway (`V1__init_schema.sql`). Esta restricción actúa como segunda línea de defensa a nivel de base de datos, complementando la validación en la capa de servicio.
- [ ] T007 Crear el `Enum` `MotivoFalla` en Java con todos los motivos definidos en el spec, mapeando cada uno a su responsable financiero y porcentaje de pago correspondiente:

  | Motivo | Responsable | % de Pago |
    |:---|:---|:---|
  | `DIRECCIÓN_ERRONEA` | Cliente | 30% - 50% |
  | `CLIENTE_AUSENTE` | Cliente | 30% - 50% |
  | `RECHAZADO` | Cliente | 30% - 50% |
  | `ZONA_DIFÍCIL_ACCESO` | Fuerza Mayor / Empresa | Por definir con negocio |
  | `PAQUETE_DAÑADO` | Transportista | 0% + penalidad |
  | `PÉRDIDA_PAQUETE` | Transportista | 0% + penalidad |

- [ ] T008 Crear los DTOs de entrada: `RutaCerradaEventDTO` con un campo `List<ParadaDTO> paradas` (array, no objeto único) para mapear correctamente la estructura del JSON del evento. Cada `ParadaDTO` debe incluir `paradaId`, `estado` y `motivoNoEntrega`.
- [ ] T009 Implementar los `JpaRepository` para `Ruta`, `Parada` y `Transportista`, incluyendo el método `existsByRutaId(UUID rutaId)` en `RutaRepository` para la validación de duplicados en la capa de servicio.

**Checkpoint**: El esquema de base de datos está creado con todas sus restricciones, los DTOs mapean correctamente el JSON del evento incluyendo el array de paradas, y el Enum `MotivoFalla` centraliza toda la lógica de clasificación financiera.

---

## Phase 3: User Story 1 — Procesar Informe de Cierre (Prioridad: P1)

**Goal**: Escuchar el evento asíncrono, validar su contenido, clasificar las paradas según responsable financiero y persistir toda la información de forma atómica en base de datos. Esta fase cubre tanto el camino exitoso como los edge cases de contrato nulo y vehículo sin tarifa, ya que ambos son requerimientos funcionales de la misma historia de usuario.

**Independent Test**: Publicar un mensaje JSON válido en la cola local usando Testcontainers. Verificar que los registros de `Ruta` y `Parada` aparezcan en PostgreSQL con la clasificación correcta en menos de 5 segundos, y que el total de paradas guardadas coincida exactamente con el total enviado en el evento.

### Tests para User Story 1

- [ ] T010 [P] [US1] Test con `@JsonTest` para validar que `RutaCerradaEventDTO` mapea correctamente el JSON del contrato, incluyendo que el campo `paradas` se deserializa como `List<ParadaDTO>` y no como objeto único.
- [ ] T011 [P] [US1] Test unitario para verificar que `RutaService` clasifica correctamente el responsable de cada parada según su `motivoNoEntrega`. Por ejemplo: `CLIENTE_AUSENTE` → responsable Cliente, `PAQUETE_DAÑADO` → responsable Transportista. Este es el test más crítico del feature ya que valida el FR-002.
- [ ] T012 [P] [US1] Test de integración para confirmar que la restricción de idempotencia funciona: enviar el mismo `ruta_id` dos veces y verificar que el segundo evento es ignorado sin crear un registro duplicado ni lanzar un error al sistema de mensajería.
- [ ] T013 [P] [US1] Test para verificar el SC-003: que el total de paradas persistidas en base de datos coincide exactamente con el total de paradas recibidas en el evento.
- [ ] T014 [P] [US1] Test para el edge case de contrato nulo: enviar un evento con `modelo_contrato` nulo y verificar que el sistema registra un log de advertencia `WARN` con el `ruta_id` afectado, envía una notificación al equipo financiero y persiste la ruta sin intentar calcular liquidación.
- [ ] T015 [P] [US1] Test para el edge case de vehículo sin tarifa: enviar un evento con un `tipo` de vehículo que no existe en el catálogo financiero y verificar que el sistema registra un log de advertencia `WARN` con el `ruta_id` y el tipo de vehículo desconocido, y envía una notificación al equipo financiero para revisión manual.

### Implementation para User Story 1

- [ ] T016 [P] [US1] Implementar el listener `RutaCerradaConsumer` en `consumers/` con la anotación correspondiente (`@SqsListener` o `@KafkaListener`) apuntando a la cola configurada. El listener debe deserializar el JSON entrante al DTO y delegar el procesamiento a `RutaService`.
- [ ] T017 [P] [US1] Implementar `RutaService.procesarCierreRuta(RutaCerradaEventDTO evento)` con la siguiente secuencia:
    1. Verificar si `ruta_id` ya existe en base de datos. Si existe, ignorar el evento y registrar log `INFO: Evento duplicado ignorado para ruta_id: {id}`.
    2. Validar que `modelo_contrato` no sea nulo. Si es nulo, registrar log `WARN: Contrato nulo para ruta_id: {id}`, enviar notificación al equipo financiero para revisión manual y persistir la ruta sin disparar el cálculo de liquidación.
    3. Validar que el `tipo` de vehículo exista en el catálogo de tarifas. Si no existe, registrar log `WARN: Tarifa no encontrada para vehículo tipo {tipo} en ruta_id: {id}` y enviar notificación al equipo financiero para revisión manual.
    4. Clasificar cada parada según su `motivoNoEntrega` usando el Enum `MotivoFalla`.
    5. Persistir la ruta y todas sus paradas.
- [ ] T018 [P] [US1] Marcar el método `procesarCierreRuta` con `@Transactional` para garantizar atomicidad: si falla el guardado de cualquier parada, la ruta completa se revierte y no queda ningún registro parcial en base de datos.
- [ ] T019 [US1] Crear el endpoint REST `GET /api/rutas/{id}` en un `RutaController` para que el módulo de liquidación y el frontend puedan consultar los datos procesados. Este endpoint **no es opcional**: es el mecanismo de consulta que otros módulos necesitan para verificar que el cierre fue procesado correctamente.
- [ ] T020 [US1] Desarrollar el dashboard en React que consuma `GET /api/rutas` mostrando el listado de rutas procesadas. El equipo financiero podrá identificar rutas con problemas directamente desde las notificaciones recibidas, que incluirán el `ruta_id` afectado y el motivo del problema (contrato nulo o tarifa no encontrada).

---

## Phase N: Polish & Cross-Cutting Concerns

- [ ] T021 Añadir logs detallados con `Slf4j` al inicio y fin del procesamiento de cada evento, incluyendo el `ruta_id`, el resultado del procesamiento y el tiempo transcurrido. Estos logs quedan disponibles en CloudWatch (AWS) para auditoría.
- [ ] T022 Configurar perfiles de Spring Boot (`application-dev.yml`, `application-prod.yml`) para apuntar a la cola correcta y a las credenciales de AWS RDS según el entorno.
- [ ] T023 Refinar la UI en React implementando filtros por fecha en el dashboard de rutas.
- [ ] T024 Añadir Swagger / OpenAPI para documentar el endpoint `GET /api/rutas/{id}` y facilitar la integración con otros módulos.

---

## Dependencies & Execution Order

**Infraestructura de mensajería y registro del Consumer (Phase 1)**: La cola asíncrona y el registro del bean del listener deben estar configurados antes de cualquier otra tarea. Sin esto, no es posible inyectar mensajes de prueba ni verificar el procesamiento.

**Enum `MotivoFalla` y DTOs (Phase 2)**: El mapeo de motivos a responsables financieros y el DTO con `List<ParadaDTO>` deben existir antes de implementar el servicio. Son el vocabulario sobre el que se construye toda la lógica de clasificación.

**`@Transactional` desde el inicio (Phase 3)**: La atomicidad del guardado de ruta y paradas no es un detalle de pulido — es un requisito de integridad que debe estar presente desde la primera implementación del servicio para evitar registros parciales en base de datos.

**Edge cases en la misma fase que el camino feliz**: Los casos de contrato nulo y vehículo sin tarifa son requerimientos funcionales (FR-001, FR-003), no mejoras opcionales. Se implementan en la Phase 3 junto con el procesamiento principal.

**Endpoint `GET /api/rutas/{id}` obligatorio**: El módulo de liquidación depende de este endpoint para verificar los datos del cierre antes de calcular. No puede tratarse como opcional.

**Frontend al final**: El dashboard de React se construye una vez que el backend tiene validado el procesamiento completo, incluyendo los edge cases.
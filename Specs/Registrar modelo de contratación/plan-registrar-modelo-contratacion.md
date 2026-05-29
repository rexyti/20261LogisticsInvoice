# Implementation Plan: Gestión de contratos en Finanzas (Event-Driven)

**Fecha**: 2026-05-16
**Versión**: 3.0 — Migración a arquitectura asíncrona SQS
**Spec**: [Registrar modelo de contratación.md]

---

## Resumen del cambio arquitectónico

| Versión | Modelo | Descripción |
|---|---|---|
| v1.0 | REST síncrono owner | Finanzas creaba contratos localmente vía `POST /api/contratos` |
| v2.0 | REST síncrono consumer | Finanzas consumía contratos del Gestor de Tarifas vía Feign |
| **v3.0** | **Eventos asíncronos** | **Finanzas recibe `CONTRATO_CREADO` por SQS y persiste localmente** |

**Responsabilidad actual de Finanzas**:
- Consumir eventos de contratos desde SQS.
- Mantener copia local de contratos para liquidación.
- Exponer endpoints `GET` de sólo lectura sobre esa copia local.

---

## Technical Context

**Language/Version**: Java 21
**Framework**: Spring Boot 3.2.4
**Mensajería**: AWS SQS (`io.awspring.cloud:spring-cloud-aws-starter-sqs`)
**Storage**: PostgreSQL 15
**Testing**: JUnit 5, Mockito, Spring Boot Test, Testcontainers + LocalStack

---

## Estructura final del módulo

```text
backend/src/main/java/com/logistica/
│
├── application/contratos/
│   ├── dtos/
│   │   ├── event/
│   │   │   └── ContratoCreadoEvent.java          ← DTO del mensaje SQS
│   │   └── response/
│   │       ├── ContratoResponseDTO.java           ← Respuesta GET
│   │       ├── TransportistaResponseDTO.java
│   │       └── SeguroResponseDTO.java
│   │
│   ├── mappers/
│   │   ├── ContratoEventMapper.java               ← Event → dominio
│   │   └── ContratoResponseMapper.java            ← Dominio → response DTO
│   │
│   └── usecases/contrato/
│       ├── ProcesarContratoCreadoUseCase.java     ← Orquesta procesamiento del evento
│       └── ConsultarContratoUseCase.java          ← Lectura desde DB local
│
├── domain/contratos/
│   ├── models/
│   │   ├── Contrato.java
│   │   ├── Transportista.java
│   │   ├── Seguro.java
│   │   └── Vehiculo.java
│   │
│   ├── repositories/                              ← Puertos de dominio
│   │   ├── ContratoRepository.java
│   │   └── TransportistaContratoRepository.java
│   │
│   └── exceptions/
│       └── RecursoNoEncontradoException.java
│
└── infrastructure/contratos/
    ├── adapters/
    │   └── ContratoMapper.java                   ← Entidad ↔ dominio
    │
    ├── messaging/
    │   └── consumer/
    │       └── ContratoCreadoConsumer.java        ← @SqsListener
    │
    ├── persistence/
    │   ├── entities/
    │   │   ├── ContratoEntity.java
    │   │   ├── TransportistaEntity.java
    │   │   ├── SeguroEntity.java
    │   │   └── VehiculoEntity.java
    │   │
    │   └── repositories/
    │       ├── ContratoJpaRepository.java
    │       ├── ContratoRepositoryImpl.java
    │       ├── ContratoTransportistaJpaRepository.java   ← Compartido con cierreRuta
    │       └── TransportistaContratoRepositoryImpl.java
    │
    └── web/
        └── controllers/
            └── ContratoController.java            ← GET /api/contratos/**
```

---

## Flujo de procesamiento

```
1. Gestor de Tarifas publica ContratoCreadoEvent en SQS
   └─ cola: ${app.sqs.queue.contrato-creado}

2. ContratoCreadoConsumer (@SqsListener)
   └─ recibe el mensaje deserializado como ContratoCreadoEvent

3. ProcesarContratoCreadoUseCase
   ├─ [idempotencia] existePorIdContrato → si existe, ack y retorna
   ├─ ContratoEventMapper.toDomain(evento) → Contrato
   ├─ TransportistaContratoRepository.buscarPorId / guardar (upsert)
   └─ ContratoRepository.guardar(contrato) → DB local

4. Ack manual a SQS si todo OK
   └─ Si excepción → relanza → SQS reintenta → DLQ tras N intentos

5. GET /api/contratos/** (ContratoController)
   └─ ConsultarContratoUseCase → ContratoRepository → DB local → ContratoResponseDTO
```

---

## Phase 1: Infraestructura SQS

**Propósito**: Habilitar el consumer de contratos reutilizando la configuración SQS existente.

- [x] T001 Verificar que `SqsConsumerConfig` (en `cierreRuta`) define el bean
      `defaultSqsListenerContainerFactory` con acknowledgement MANUAL. El consumer de
      contratos lo usará automáticamente.
- [x] T002 Agregar propiedad `app.sqs.queue.contrato-creado` en `application-local.yml`
      (y análogos: `application-docker.yml`, `application-prod.properties`).
- [x] T003 Verificar que `spring.cloud.aws.sqs.enabled=true` está configurado para
      los perfiles donde SQS debe estar activo.

**Configuración mínima requerida**:
```yaml
app:
  sqs:
    queue:
      ruta-cerrada: ruta-cerrada-queue
      contrato-creado: contrato-creado-queue

spring:
  cloud:
    aws:
      sqs:
        enabled: true
```

---

## Phase 2: Event DTO y Mapper

**Propósito**: Definir el contrato de mensajería y la traducción al modelo de dominio.

- [x] T004 Implementar `ContratoCreadoEvent` con todos los campos que publica Gestor de Tarifas:
  - `tipoEvento`, `idContrato`, `tipoContrato`
  - `transportistaId` (UUID), `nombreTransportista`
  - `tipoVehiculo` (String → se convierte a `TipoVehiculo` enum en el mapper)
  - `esPorParada` (Boolean), `precioParadas`, `precio`
  - `fechaInicio`, `fechaFinal` (LocalDateTime)
  - `seguro` (inner class `SeguroEventDTO` con `numeroPoliza` y `estado`)

- [x] T005 Implementar `ContratoEventMapper.toDomain(ContratoCreadoEvent)` que:
  - Convierte `tipoVehiculo` String → `TipoVehiculo` enum (falla con `IllegalArgumentException` si inválido).
  - Genera `UUID.randomUUID()` para `id` del contrato y `idSeguro`.
  - Construye objetos `Transportista` y `Seguro` del dominio.

**Checkpoint**: Con un `ContratoCreadoEvent` de prueba, el mapper produce un `Contrato`
de dominio con todos los campos correctamente traducidos.

---

## Phase 3: Persistencia del evento

**Propósito**: Guardar el contrato recibido en la base de datos local de Finanzas.

- [x] T006 Implementar `TransportistaContratoRepository` (puerto de dominio) con:
  - `buscarPorId(UUID): Optional<Transportista>`
  - `guardar(Transportista): Transportista`

- [x] T007 Implementar `TransportistaContratoRepositoryImpl` usando
      `ContratoTransportistaJpaRepository` (JPA repo compartido con `cierreRuta`).

- [x] T008 Actualizar `ContratoRepository` con métodos de escritura:
  - `guardar(Contrato): Contrato`
  - `existePorIdContrato(String): boolean`
  - `buscarPorIdContrato(String): Optional<Contrato>`
  - `listar(Pageable): Page<Contrato>`
  - `listarPorTransportista(UUID, Pageable): Page<Contrato>`

- [x] T009 Actualizar `ContratoRepositoryImpl`:
  - `guardar()` obtiene `TransportistaEntity` por FK antes de guardar (`orElseThrow`).
  - Todos los métodos de lectura usan `@Transactional(readOnly = true)`.

- [x] T010 Restaurar `ContratoMapper.toEntity(Contrato, TransportistaEntity)` en
      `infrastructure/contratos/adapters/`.

**Checkpoint**: Dado un `Contrato` de dominio con transportista ya existente en DB,
`ContratoRepository.guardar()` persiste el contrato con FK correcta.

---

## Phase 4: Consumer SQS

**Propósito**: Conectar la cola SQS con el use case de procesamiento.

- [x] T011 Implementar `ProcesarContratoCreadoUseCase.ejecutar(ContratoCreadoEvent)`:
  1. Guarda de no ser nulo el evento.
  2. `existePorIdContrato` → si existe, log de idempotencia y `return`.
  3. `ContratoEventMapper.toDomain(evento)`.
  4. Upsert del transportista: `buscarPorId` → si no existe, `guardar`.
  5. Reconstruye `Contrato` con el transportista resuelto.
  6. `ContratoRepository.guardar(contrato)`.
  Todo bajo `@Transactional`.

- [x] T012 Implementar `ContratoCreadoConsumer`:
  - `@SqsListener("${app.sqs.queue.contrato-creado}")`.
  - Llama al use case, ack manual si OK.
  - `IllegalArgumentException` → relanza (no recuperable).
  - Cualquier otra excepción → relanza (SQS reintenta).

**Checkpoint**: Publicar un evento de prueba en LocalStack. Verificar en DB local
que el contrato aparece con todos los campos. Publicar el mismo evento dos veces —
verificar que la segunda vez no crea duplicado.

---

## Phase 5: Endpoints de consulta

**Propósito**: Exponer los contratos locales para el gestor financiero.

- [x] T013 Implementar `ConsultarContratoUseCase` con:
  - `buscarPorIdContrato(String)` → `ContratoResponseDTO` o `RecursoNoEncontradoException` (404).
  - `listar(Pageable)` → `Page<ContratoResponseDTO>`.
  - `listarPorTransportista(UUID, Pageable)` → `Page<ContratoResponseDTO>`.
  Todo bajo `@Transactional(readOnly = true)`.

- [x] T014 Implementar `ContratoResponseMapper.toResponseDTO(Contrato)` que traduce
      dominio → DTOs de respuesta (`ContratoResponseDTO`, `TransportistaResponseDTO`, `SeguroResponseDTO`).

- [x] T015 Implementar `ContratoController` con tres endpoints `GET`:
  - `GET /api/contratos/{idContrato}` → contrato por ID de negocio.
  - `GET /api/contratos` → listado paginado (`sort=fechaInicio,DESC`).
  - `GET /api/contratos/transportista/{idTransportista}` → contratos de un transportista.
  Todos con `@PreAuthorize("hasRole('GESTOR_FINANCIERO')")`.

**Checkpoint**: Con un contrato en DB, los tres endpoints retornan los datos
correctamente. Sin contrato, `GET /{idContrato}` retorna 404.

---

## Phase 6: Tests

**Propósito**: Verificar el flujo completo de consumo y consulta.

- [ ] T016 [Unit] Test de `ContratoEventMapper`:
  - Verifica mapeo correcto de todos los campos.
  - Verifica que `tipoVehiculo` inválido lanza `IllegalArgumentException`.

- [ ] T017 [Unit] Test de `ProcesarContratoCreadoUseCase`:
  - Evento válido → llama a `guardar`.
  - Evento con `idContrato` existente → no llama a `guardar` (idempotencia).
  - Evento null → lanza `IllegalArgumentException`.
  - Transportista nuevo → llama a `transportistaRepository.guardar`.
  - Transportista existente → no llama a `guardar` del transportista.

- [ ] T018 [Integration / LocalStack] Test de `ContratoCreadoConsumer`:
  - Publica evento en cola de LocalStack.
  - Verifica que el contrato queda en DB.
  - Publica el mismo evento → verifica que no hay duplicado.

- [ ] T019 [Integration] Test de `ContratoController`:
  - `GET /api/contratos/{id}` con contrato existente → 200 con todos los campos.
  - `GET /api/contratos/{id}` sin contrato → 404 con mensaje.
  - `GET /api/contratos` → 200 con página.
  - `GET /api/contratos/transportista/{id}` → 200 con contratos del transportista.
  - Sin autenticación → 401.

---

## Qué se eliminó y por qué

| Artefacto eliminado | Razón |
|---|---|
| `POST /api/contratos` | Finanzas no crea contratos |
| `CrearContratoUseCase` | Reemplazado por el consumer de eventos |
| `ContratoRequestDTO`, `SeguroRequestDTO` | DTOs de entrada REST ya no existen |
| `FechasContratoValidator`, `PrecioCondicionalValidator` | Validación es responsabilidad del Gestor de Tarifas |
| `Contrato.crear()` (factory method) | Validaciones de negocio pertenecen al owner |
| `GestorTarifasContratoClient` (Feign) | No hay consumo REST; sólo eventos SQS |
| `ContratoExternoPort` | El puerto REST no tiene sentido en arquitectura asíncrona |
| `BuscarContratoUseCase`, `ListarContratosUseCase`, `ListarContratosPorTransportistaUseCase` | Consolidados en `ConsultarContratoUseCase` |
| `SeguroRepository`, `VehiculoRepository`, `TransportistaRepository` (v1) | Repositorios de escritura que ya no tiene Finanzas |
| `ContratoYaExisteException`, `ContratoInvalidoException`, `TransportistaNotFoundException` | Excepciones de creación eliminadas |
| Tests de creación y validación | Tests asociados a funcionalidad eliminada |

---

## Dependencias y orden de ejecución

1. **Phase 1 primero**: sin cola configurada, el consumer no levanta.
2. **Phase 2 antes de Phase 4**: el mapper debe existir antes del use case.
3. **Phase 3 antes de Phase 4**: el repositorio debe estar listo para que el use case pueda guardar.
4. **Phase 4 y Phase 5 son independientes** entre sí (escribir vs leer), pero ambas
   dependen de que las entidades JPA y el repositorio estén correctos (Phase 3).
5. **Phase 6 al final**: los tests de integración necesitan el flujo completo.

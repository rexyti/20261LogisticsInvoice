# Feature Specification: Gestión de contratos en Finanzas

**Última actualización**: 2026-05-16
**Versión**: 3.0 — Arquitectura orientada a eventos (SQS)

> **Cambio arquitectónico**: Finanzas ya no es owner del contrato.
> El módulo **Gestor de Tarifas** crea y gestiona contratos.
> Finanzas únicamente **consume eventos** publicados por ese módulo,
> persiste una copia local para procesamiento financiero y expone
> endpoints de **sólo lectura** sobre esa copia.

---

## Contexto del sistema

```
Gestor de Tarifas  ──publica──▶  CONTRATO_CREADO (SQS)
                                          │
                               ContratoCreadoConsumer
                                          │
                            ProcesarContratoCreadoUseCase
                                   (persistencia local)
                                          │
                               ContratoController (GET)
                              ◀──────────────────────────
                              GESTOR_FINANCIERO consulta
```

---

## User Scenarios & Testing *(mandatory)*

Dado que el Gestor de Tarifas publica un evento `CONTRATO_CREADO` en la cola SQS,
Finanzas debe consumir ese evento, persistir la información del contrato localmente
y dejarla disponible para consulta por el gestor financiero.

---

### User Story 1 — Consumir y persistir contrato desde evento (Priority: P1)

Como módulo de Finanzas, quiero consumir el evento `CONTRATO_CREADO` publicado por
el Gestor de Tarifas para mantener una copia local del contrato y poder usarla en
procesos de liquidación.

**Why this priority**: Sin contratos locales no es posible calcular liquidaciones.
El evento es el único punto de entrada de contratos en este módulo.

**Independent Test**: Publicar un mensaje `CONTRATO_CREADO` válido en la cola SQS
y verificar que el contrato queda persistido en la tabla `contratos` de la base de
datos de Finanzas con todos sus campos.

**Acceptance Scenarios**:

1. **Scenario**: Persistencia exitosa del evento
   - **Given** El Gestor de Tarifas publica un `ContratoCreadoEvent` válido en SQS.
   - **When** El consumer de Finanzas recibe el mensaje.
   - **Then** El contrato queda guardado localmente con todos los campos del evento.
   - **And** El mensaje es acknowledged en SQS.

2. **Scenario**: Idempotencia — evento duplicado
   - **Given** Ya existe localmente un contrato con el mismo `idContrato`.
   - **When** Llega un segundo evento con el mismo `idContrato`.
   - **Then** El sistema ignora el evento sin error.
   - **And** El mensaje es acknowledged sin reprocesar.

3. **Scenario**: Transportista nuevo en el evento
   - **Given** El `transportistaId` del evento no existe aún en la tabla local.
   - **When** Se procesa el evento.
   - **Then** El transportista es creado automáticamente (upsert) y el contrato queda asociado.

4. **Scenario**: Evento malformado o con datos inválidos
   - **Given** El evento llega con campos nulos o un `tipoVehiculo` inválido.
   - **When** El consumer intenta procesar el mensaje.
   - **Then** Se lanza una excepción no recuperable.
   - **And** SQS reintenta automáticamente hasta agotar los reintentos (DLQ).

---

### User Story 2 — Consultar contratos locales (Priority: P2)

Como gestor financiero, quiero consultar los contratos disponibles localmente para
verificar la información antes de procesar liquidaciones.

**Why this priority**: El gestor necesita visualizar contratos para validar
datos de liquidación. La consulta lee de la copia local — no llama al Gestor de Tarifas.

**Independent Test**: Dado un contrato persistido en DB local, hacer
`GET /api/contratos/{idContrato}` y verificar que retorna todos sus campos correctamente.

**Acceptance Scenarios**:

1. **Scenario**: Consultar contrato existente por ID
   - **Given** Un contrato previamente persistido por el consumer.
   - **When** El gestor financiero llama a `GET /api/contratos/{idContrato}`.
   - **Then** El sistema retorna `200 OK` con todos los campos del contrato.

2. **Scenario**: Listar contratos paginados
   - **Given** Existen contratos en la base de datos local.
   - **When** El gestor llama a `GET /api/contratos?page=0&size=20`.
   - **Then** El sistema retorna una página de contratos ordenada por `fechaInicio` DESC.

3. **Scenario**: Listar contratos de un transportista
   - **Given** Existen contratos asociados a un transportista específico.
   - **When** El gestor llama a `GET /api/contratos/transportista/{idTransportista}`.
   - **Then** El sistema retorna sólo los contratos de ese transportista, paginados.

4. **Scenario**: Consultar contrato inexistente
   - **Given** No existe un contrato con el ID solicitado.
   - **When** El gestor realiza la búsqueda.
   - **Then** El sistema retorna `404 Not Found` con mensaje descriptivo.

---

### Edge Cases

- ¿Qué pasa si el Gestor de Tarifas publica el mismo contrato dos veces?
  → Idempotencia por `idContrato` (existePorIdContrato antes de guardar).
- ¿Qué pasa si el evento llega con un `tipoVehiculo` que no existe en el enum?
  → `IllegalArgumentException` no recuperable, SQS reintenta y eventualmente envía a DLQ.
- ¿Qué pasa si el transportista del evento no existe en DB local?
  → Se hace upsert del transportista antes de guardar el contrato.
- ¿Qué pasa si la cola SQS no está disponible?
  → El consumer no levanta (condicional en `spring.cloud.aws.sqs.enabled`).
  Los contratos no se persisten hasta que la cola vuelva. Los endpoints de lectura
  siguen funcionando con los datos ya persistidos.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE consumir eventos `CONTRATO_CREADO` desde SQS y persistirlos localmente.
- **FR-002**: El sistema DEBE garantizar idempotencia: un mismo `idContrato` no se persiste dos veces.
- **FR-003**: El sistema DEBE hacer upsert del transportista si no existe al procesar el evento.
- **FR-004**: El sistema DEBE exponer endpoints de sólo lectura para consultar contratos locales.
- **FR-005**: El sistema DEBE autoacknowledgear mensajes SQS al completar el procesamiento exitosamente.
- **FR-006**: Ante un error no recuperable, el sistema DEBE relanzar la excepción para que SQS reintente.

### Non-Functional Requirements

- **NFR-001**: Idempotencia garantizada en 100% de los casos de mensajes duplicados.
- **NFR-002**: Consultas de contratos en menos de 200 ms.
- **NFR-003**: El consumer usa `@Transactional` para garantizar atomicidad en upsert + guardar.
- **NFR-004**: El endpoint de lectura requiere rol `GESTOR_FINANCIERO`.

### Key Entities

- **[ContratoCreadoEvent]**: Mensaje SQS enviado por Gestor de Tarifas.
  (`tipoEvento`, `idContrato`, `tipoContrato`, `transportistaId`, `nombreTransportista`,
  `tipoVehiculo`, `esPorParada`, `precioParadas`, `precio`, `fechaInicio`, `fechaFinal`, `seguro`)

- **[Contrato]**: Copia local del contrato en Finanzas.
  (`id`, `idContrato`, `tipoContrato`, `transportista`, `tipoVehiculo`, `esPorParada`,
  `precioParadas`, `precio`, `fechaInicio`, `fechaFinal`, `seguro`)

- **[Transportista]**: Copia local del transportista, creada en upsert.
  (`transportistaId`, `nombre`)

- **[Seguro]**: Parte del contrato recibido en el evento.
  (`idSeguro`, `numeroPoliza`, `estado`)

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% de eventos `CONTRATO_CREADO` válidos son persistidos correctamente.
- **SC-002**: 100% de eventos duplicados son ignorados sin error ni reprocesamiento.
- **SC-003**: 0 contratos con transportista nulo o referencia rota tras procesamiento.
- **SC-004**: Endpoints de lectura disponibles y respondiendo en < 200 ms.

---

## Integración con otros módulos

| Módulo | Relación |
|---|---|
| **Gestor de Tarifas** | Owner del contrato. Publica `ContratoCreadoEvent` en SQS. |
| **Liquidación** | Consume `ContratoEntity` local vía FK en `LiquidacionEntity`. |
| **Cierre de Ruta** | Comparte `TransportistaEntity` y `ContratoTransportistaJpaRepository`. |

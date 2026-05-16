# Feature Specification: Novedad estado del paquete — Integración por SQS

**Created**: 23/02/2026  
**Updated**: 2026-05-16  
**Cambio aplicado**: La comunicación entre el Módulo Financiero y el Módulo de Gestión de Paquetes deja de ser sincrónica por HTTP y pasa a ser asíncrona mediante colas AWS SQS.

## User Scenarios & Testing *(mandatory)*

Dado que el Módulo Financiero necesita determinar los pagos a transportistas, el sistema debe recibir eventos asíncronos publicados por el Módulo de Gestión de Paquetes en una cola SQS. Cada evento contiene el estado oficial de un paquete asociado a una ruta cerrada o en proceso de cierre. El Módulo Financiero consume el mensaje, valida el payload, transforma el estado recibido en una regla de pago, actualiza el estado actual del paquete, registra el historial y deja auditoría del procesamiento del mensaje.

El Módulo Financiero **no debe realizar consultas HTTP GET** al Módulo de Gestión de Paquetes para obtener el estado. La fuente de actualización será el evento recibido desde SQS.

---

## User Story 1 - Consumo asíncrono de estado de paquete desde SQS (Priority: P1)

Como Módulo Financiero, quiero consumir eventos de estado de paquete enviados por el Módulo de Gestión mediante SQS, para actualizar el estado financiero del paquete y calcular el porcentaje de pago correspondiente al transportista sin depender de una llamada sincrónica.

**Why this priority**: Es el disparador crítico para la liquidación financiera. Sin el estado real del paquete — ENTREGADO, DEVUELTO, DAÑADO o EXTRAVIADO — no se puede procesar correctamente el cálculo de pago. El uso de SQS evita acoplamiento temporal entre módulos y permite tolerar caídas temporales del Módulo de Gestión.

**Independent Test**: Publicar un mensaje JSON en la cola SQS de novedades de estado de paquete y verificar que el Módulo Financiero consume el mensaje, registra el estado en `HistorialEstado`, actualiza `estadoActual` en `Paquete`, calcula el porcentaje de pago según el enum financiero y registra el procesamiento en `LogSincronizacion` o `LogEventoEstadoPaquete`.

### Acceptance Scenarios

1. **Scenario**: Evento de estado de paquete procesado exitosamente.
   - **Given** Existe un paquete asociado a una ruta en el Módulo Financiero.
   - **When** El Módulo de Gestión publica en SQS un evento con `idRuta`, `idPaquete`, `estado`, `eventId` y `fechaEvento`.
   - **Then** El Módulo Financiero consume el mensaje, valida el estado, actualiza `estadoActual`, registra una entrada en `HistorialEstado` y deja auditoría del mensaje procesado.

2. **Scenario**: Paquete inexistente en el Módulo Financiero.
   - **Given** Llega un evento SQS para un `idPaquete` que no existe localmente.
   - **When** El consumidor procesa el mensaje.
   - **Then** El sistema registra el evento como `ERROR` o `DESCARTADO`, almacena el payload recibido y no crea historial de estado.

3. **Scenario**: Mensaje duplicado.
   - **Given** Ya fue procesado un evento con el mismo `eventId` o `messageId` lógico del Módulo de Gestión.
   - **When** El mismo mensaje llega nuevamente por SQS.
   - **Then** El sistema detecta la duplicidad, no duplica historial, no altera el estado actual y registra el intento como duplicado/idempotente.

4. **Scenario**: Evento desordenado.
   - **Given** El paquete ya tiene un estado actualizado con una `fechaEvento` posterior.
   - **When** Llega un mensaje atrasado con una fecha anterior.
   - **Then** El sistema registra el evento en auditoría, pero no sobrescribe el estado actual ni el último historial válido.

5. **Scenario**: Estado no mapeado.
   - **Given** Llega un evento con estado `EN_INSPECCION` u otro valor no contemplado por el enum financiero.
   - **When** El consumidor valida el payload.
   - **Then** El sistema registra el evento como `ERROR_ESTADO_NO_MAPEADO`, omite el cálculo de pago y conserva el payload para auditoría.

---

## Edge Cases

- ¿Qué ocurre cuando el mensaje SQS tiene JSON inválido?  
  El sistema debe registrar el fallo de deserialización, no persistir historial y permitir que la política de reintentos/DLQ maneje el mensaje según configuración.

- ¿Qué ocurre cuando el Módulo Financiero no puede procesar temporalmente un mensaje?  
  El mensaje no debe eliminarse de la cola hasta que el procesamiento termine correctamente. SQS lo hará visible nuevamente después del `visibilityTimeout` y, tras superar el número máximo de recepciones, debe enviarlo a una DLQ.

- ¿Qué ocurre cuando el estado recibido no mapea con las reglas financieras?  
  El sistema debe omitir el cálculo de pago, registrar el payload y marcar el procesamiento como error funcional no reintentable.

- ¿Qué ocurre cuando llega un evento duplicado?  
  El sistema debe garantizar idempotencia por `eventId` o identificador único del evento publicado por Gestión.

- ¿Qué ocurre cuando llegan eventos fuera de orden?  
  El sistema debe comparar `fechaEvento` o `secuenciaEvento` contra el último estado persistido y evitar sobrescribir estados más recientes.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST consumir mensajes desde una cola SQS configurada para novedades de estado de paquete.
- **FR-002**: System MUST transformar el estado del paquete en una regla de pago: `ENTREGADO` → 100%, `DEVUELTO` → 50%, `DAÑADO` → 0%, `EXTRAVIADO` → 0%.
- **FR-003**: System MUST registrar cada estado válido recibido en la tabla `HistorialEstado` con timestamp de sincronización y fecha original del evento.
- **FR-004**: System MUST mantener sincronizado el estado actual del paquete con el último evento válido procesado.
- **FR-005**: System MUST registrar la auditoría del procesamiento de cada mensaje SQS, incluyendo payload recibido, identificador del mensaje, estado de procesamiento, fecha de recepción y motivo de error cuando aplique.
- **FR-006**: System MUST garantizar idempotencia por `eventId` o identificador único de evento para evitar duplicar historial.
- **FR-007**: System MUST impedir que eventos atrasados sobrescriban un estado más reciente.
- **FR-008**: System MUST enviar a DLQ o permitir redrive para mensajes que fallen por errores técnicos luego de los reintentos configurados.
- **FR-009**: System MUST diferenciar errores funcionales no reintentables — estado no mapeado, paquete inexistente, payload inválido — de errores técnicos reintentables — caída de base de datos, timeout interno, error de infraestructura.

### Key Entities

- **[Paquete]**: Representa un paquete financiero (`idPaquete`, `idRuta`, `estadoActual`, `fechaUltimoEvento`, `version`).
- **[HistorialEstado]**: Representa los cambios válidos de estado de los paquetes (`idPaquete`, `estado`, `fecha`, `fechaEvento`, `eventId`).
- **[LogSincronizacion] / [LogEventoEstadoPaquete]**: Auditoría del procesamiento asíncrono (`idPaquete`, `idRuta`, `eventId`, `sqsMessageId`, `payloadRecibido`, `estadoProcesamiento`, `motivoError`, `fechaRecepcion`, `fechaProcesamiento`, `numeroIntentos`).
- **[EventoEstadoPaquete]**: DTO de mensaje recibido desde SQS (`eventId`, `idRuta`, `idPaquete`, `estado`, `fechaEvento`, `secuenciaEvento`, `origen`).

## Technical Mapping (SQS contract)

### Queue

```text
logistica.novedad-estado-paquete.queue
```

### Dead Letter Queue

```text
logistica.novedad-estado-paquete.dlq
```

### Message body

```json
{
  "eventId": "evt-2026-000001",
  "idRuta": "550e8400-e29b-41d4-a716-446655440000",
  "idPaquete": "123e4567-e89b-12d3-a456-426614174000",
  "estado": "ENTREGADO",
  "fechaEvento": "2026-04-08T15:21:00",
  "secuenciaEvento": 15,
  "origen": "gestion-paquetes"
}
```

### Processing states

| Estado procesamiento | Uso |
|---|---|
| `PROCESADO` | Mensaje válido aplicado al estado financiero. |
| `DUPLICADO` | Evento ya procesado previamente. |
| `DESCARTADO_ATRASADO` | Evento válido, pero anterior al último evento aplicado. |
| `ERROR_PAQUETE_NO_ENCONTRADO` | El paquete no existe localmente. |
| `ERROR_ESTADO_NO_MAPEADO` | Estado recibido no pertenece al enum financiero. |
| `ERROR_PAYLOAD_INVALIDO` | JSON o campos obligatorios inválidos. |
| `ERROR_TECNICO` | Error de infraestructura que puede requerir reintento o DLQ. |

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de los eventos válidos consumidos desde SQS actualizan `estadoActual` y generan historial coherente.
- **SC-002**: El sistema evita el 100% de duplicados por `eventId`.
- **SC-003**: El 100% de estados no mapeados quedan auditados sin generar cálculo de pago.
- **SC-004**: Ningún evento atrasado sobrescribe un estado más reciente.
- **SC-005**: Los errores técnicos reintentables terminan en DLQ luego de agotar los reintentos configurados.

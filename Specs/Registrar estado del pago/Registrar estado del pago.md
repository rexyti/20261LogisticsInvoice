# Feature Specification: Registro asíncrono del estado del pago

**Created**: 21/02/2026 

## User Scenarios & Testing *(mandatory)*

Dado el registro de un estado pago (IdEstadoPago, idPago, estado), el sistema debe permitir registrar y actualizar automáticamente el estado del pago asociado a una liquidación previamente calculada (IdPago, idUsuario, MontoBase, fecha, IdPenalidad, MontoNeto) a través de la recepción asíncrona de eventos provenientes de la entidad financiera.

### User Story 1 - Registrar y actualizar estado del pago vía eventos asíncronos (Priority: P1)

Como sistema de la entidad financiera, quiero enviar informacion y notificaciones de manera asíncrona al sistema principal para registrar o actualizar el estado de un pago específico sin bloquear los procesos del usuario.

**Why this priority**: Permite mantener un registro constante y automatizado del pago sobre las liquidaciones que fueron previamente calculadas, garantizando la consistencia de los datos entre el banco y el sistema interno.

**Independent Test**: Simular el envío de un payload JSON (vía Webhook o encolado de mensajes) con los datos del pago y verificar que el sistema procesa el mensaje en segundo plano y actualiza el estado (Pendiente, Rechazado, Pagado, En proceso) en la base de datos de la liquidación correcta.

**Acceptance Scenarios**:

1. **Scenario**: Registrar el estado inicial de un pago recibido asíncronamente.
   - **Given** Una liquidación calculada y pendiente de iniciar pago.
   - **When** El sistema recibe un evento asíncrono del banco indicando el inicio del pago.
   - **Then** El sistema procesa el evento en segundo plano, registra el estado inicial del pago y lo asocia a la liquidación correspondiente (idLiquidacion, idPago, estado). 
   - **And** Responde a la entidad financiera con un código de éxito (ej. HTTP 202 Accepted) confirmando la recepción del evento.

2. **Scenario**: Registrar el estado de pago de una liquidación previamente registrada (Duplicidad de evento).
   - **Given** Una liquidación con un registro de pago ya iniciado.
   - **When** Se recibe un evento asíncrono idéntico con la misma instrucción de inicio de registro (mismo ID de transacción).
   - **Then** El sistema debe identificar el evento duplicado e ignorarlo o rechazar la instrucción.
   - **And** Evitar la creación de un nuevo registro.
      
3. **Scenario**: Actualizar el estado de pago a finalizado o rechazado.
   - **Given** Una liquidación con un registro de estado iniciado ("En proceso").
   - **When** Se recibe un evento asíncrono de actualización de pago (ej. "Pagado" o "Rechazado").
   - **Then** El sistema procesa el evento y actualiza el estado de la liquidación exitosamente.

---

### Edge Cases

- What happens when el sistema recibe un evento asíncrono sobre una liquidación inexistente?
- How does system handle Debe registrar el error en los logs del sistema, rechazar la actualización y (opcionalmente) encolar el mensaje para una revisión manual o enviar un estado de error al banco.
- What happens when el sistema recibe la instrucción de actualización de estado sobre una liquidación que ya fue marcada como "Pagada"?
- How does system handle Debe ignorar la actualización (idempotencia) y registrar en auditoría que se intentó alterar un pago finalizado.
- What happens when se reciben eventos desordenados (ej. llega el evento "Pagado" antes que el evento "En proceso")?
- How does system handle El sistema debe evaluar el timestamp o la secuencia del evento emitido por el banco para garantizar que un estado definitivo ("Pagado") no sea sobrescrito por un estado transitorio retrasado ("En proceso").
- What happens when se intenta actualizar el estado con el mismo valor actual?
- How does system handle El sistema procesa el evento y responde con éxito a la entidad financiera para confirmar la recepción, pero a nivel de base de datos no realiza cambios (Idempotencia garantizada).
- what happens when El sistema recibe un estado desconocido de pago?
- how does system handle El sistema manda debe mandar "Error: estado invalido" y registrar el fallo en los logs del sistema.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST exponer un endpoint (Webhook) o consumir una cola de mensajería para recibir los eventos de estado de pago de la entidad financiera de forma asíncrona.
- **FR-002**: System MUST procesar los eventos en segundo plano para no bloquear la comunicación con la entidad financiera, respondiendo de inmediato la recepción del mensaje. 
- **FR-003**: System MUST permitir la creación y actualización del estado de pago de una liquidación con base en la información del evento recibido.
- **FR-004**: System MUST garantizar la idempotencia de los eventos (ignorar eventos duplicados con el mismo ID de transacción del banco).
- **FR-005**: System MUST validar que la liquidación exista en la base de datos antes de procesar el registro del evento.
- **FR-006**: System MUST impedir modificaciones y transiciones de estado inválidas cuando el pago ya esté en un estado final (ej. Pagado, Rechazado).

### Key Entities 

- **[Pago]**: Representa la transacción económica realizada (IdPago, idUsuario, MontoBase, fecha, IdPenalidad, MontoNeto, idLiquidación).
- **[Ajustes/Penalidad]**: Representa los ajustes financieros que puede sufrir un transportista (IdAjustes, TipoAjustes).
- **[EstadoPago]**: Representa la condición actual del pago (IdEstadoPago, idPago, estado).
- **[EventoTransaccion]**: Representa el registro o log de la comunicación asíncrona recibida del banco (IdEvento, idPago, payloadRecibido, fechaRecepcion, procesado).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de los eventos válidos de registro de pago enviados por el banco son procesados y registrados correctamente en un tiempo menor a 120 segundos.
- **SC-002**: El sistema evita el 100% de los registros duplicados manejando la idempotencia de los eventos asíncronos.
- **SC-003**: El 100% de los eventos válidos de actualización de pago son registrados correctamente.
- **SC-004**: El sistema evita el 100% de las actualizaciones solapadas o desordenadas gracias al control de timestamps o secuenciales.

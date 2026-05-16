# Feature Specification: Novedad estado del paquete — Frontend con backend basado en SQS

**Created**: 2026-05-04  
**Updated**: 2026-05-16  
**Spec backend base**: `Specs/Novedad estado del paquete/Novedad estado del paquete - SQS.md`  
**Plan técnico**: `Specs/Novedad estado del paquete/plan-frontend-novedad-estado-paquete-SQS.md`

## Contexto

El backend actualiza automáticamente el estado de los paquetes consumiendo eventos publicados por el Módulo de Gestión de Paquetes en una cola SQS. El frontend no dispara sincronización manual, no invoca al Módulo de Gestión y no publica mensajes en SQS. Su rol es mostrar resultados ya persistidos por el Módulo Financiero: historial de cambios de estado y logs de procesamiento de eventos SQS.

La auditoría deja de estar basada en códigos HTTP de una consulta sincrónica. En su lugar, se basa en el estado de procesamiento del mensaje: `PROCESADO`, `DUPLICADO`, `DESCARTADO_ATRASADO`, `ERROR_PAQUETE_NO_ENCONTRADO`, `ERROR_ESTADO_NO_MAPEADO`, `ERROR_PAYLOAD_INVALIDO` o `ERROR_TECNICO`.

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Auditoría global de eventos SQS (Priority: P1)

Como miembro del equipo financiero, quiero ver el listado completo de eventos de estado de paquete procesados desde SQS para identificar paquetes con errores, mensajes duplicados, eventos atrasados o estados no mapeados antes de que afecten el cálculo de liquidación.

**Why this priority**: Sin visibilidad del procesamiento de eventos SQS, el equipo financiero no puede auditar por qué un paquete quedó pendiente, por qué un evento fue descartado o por qué no se calculó el pago.

**Independent Test**: Navegar a la vista de auditoría con logs persistidos en base de datos y verificar que la tabla muestra correctamente: ID Evento, ID Paquete, estado recibido, estado de procesamiento, motivo de error, fecha de recepción y acción para inspeccionar payload.

### Acceptance Scenarios

1. **Scenario**: Visualización de eventos exitosos y fallidos.
   - **Given** Existen logs con estados `PROCESADO`, `DUPLICADO`, `ERROR_ESTADO_NO_MAPEADO` y `ERROR_TECNICO`.
   - **When** El usuario navega a la vista de auditoría.
   - **Then** La tabla muestra todos los registros con distinción visual según el estado de procesamiento.

2. **Scenario**: Inspección del payload recibido.
   - **Given** Un log SQS tiene `payloadRecibido` no nulo.
   - **When** El usuario hace clic en la acción de ver payload.
   - **Then** Se abre un modal mostrando el payload en modo lectura sin alterar el layout de la tabla.

3. **Scenario**: Evento con payload no disponible.
   - **Given** Un log tiene `payloadRecibido` nulo.
   - **When** El usuario intenta ver el payload.
   - **Then** El modal muestra el mensaje `"Sin payload registrado"`.

4. **Scenario**: Evento descartado por orden temporal.
   - **Given** Un evento fue marcado como `DESCARTADO_ATRASADO`.
   - **When** El usuario revisa el log.
   - **Then** La tabla muestra el motivo del descarte y conserva el estado actual del paquete sin presentar inconsistencia visual.

---

### User Story 2 — Historial de estado por paquete (Priority: P1)

Como miembro del equipo financiero, quiero ver el historial completo de cambios de estado de un paquete específico para verificar la trazabilidad de su evolución y el porcentaje de pago que corresponde.

**Independent Test**: Navegar al historial de un paquete con al menos dos entradas de estado distintas y verificar que la línea de tiempo muestra estados en orden cronológico descendente con fecha, `eventId` y porcentaje de pago derivado.

### Acceptance Scenarios

1. **Scenario**: Línea de tiempo de estados.
   - **Given** Un paquete tiene registros en `HistorialEstado` con estados `DEVUELTO` y luego `ENTREGADO`.
   - **When** El usuario navega al historial del paquete.
   - **Then** La línea de tiempo muestra ambos estados en orden cronológico, con etiquetas visuales y porcentajes de pago derivados.

2. **Scenario**: Logs SQS del paquete.
   - **Given** El paquete tiene múltiples logs de eventos SQS.
   - **When** El usuario está en la página de historial del paquete.
   - **Then** La sección inferior muestra la tabla de logs específicos del paquete con `eventId`, `estadoProcesamiento`, fecha y payload.

3. **Scenario**: Paquete sin historial.
   - **Given** Se navega al historial de un `idPaquete` que no tiene entradas en `HistorialEstado`.
   - **When** Se carga la página.
   - **Then** Se muestra el mensaje `"No hay historial de estados registrado para este paquete."`.

---

## Edge Cases

- ¿Qué ocurre cuando el backend devuelve HTTP 503 al consultar logs financieros? → Mostrar indisponibilidad temporal sin romper la vista.
- ¿Qué ocurre cuando `payloadRecibido` contiene JSON malformado? → Mostrarlo como texto plano sin parsearlo.
- ¿Qué ocurre cuando se navega al historial de un paquete inexistente? → Mostrar `"Paquete no encontrado"`.
- ¿Qué ocurre cuando hay muchos eventos? → Usar paginación server-side con `page` y `size`; no cargar todos los registros en memoria.
- ¿Qué ocurre cuando un evento está en `ERROR_TECNICO`? → Mostrarlo como error de procesamiento, no como error HTTP de Gestión.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-F-001**: El sistema DEBE mostrar la lista de logs de eventos SQS consumiendo `GET /api/sincronizacion/logs` con paginación.
- **FR-F-002**: El sistema DEBE mostrar el historial de estados de un paquete consumiendo `GET /api/paquetes/{idPaquete}/historial`.
- **FR-F-003**: El sistema DEBE mostrar los logs SQS de un paquete específico consumiendo `GET /api/sincronizacion/logs/paquetes/{idPaquete}`.
- **FR-F-004**: El sistema DEBE derivar visualmente el resultado a partir de `estadoProcesamiento`, no de `codigoRespuestaHTTP`.
- **FR-F-005**: El sistema DEBE calcular y mostrar el porcentaje de pago en el historial derivándolo del estado: `ENTREGADO`→100%, `DEVUELTO`→50%, `DAÑADO`→0%, `EXTRAVIADO`→0%.
- **FR-F-006**: El sistema NO DEBE exponer botón ni acción para sincronizar manualmente, consultar Gestión o publicar mensajes SQS.
- **FR-F-007**: El sistema DEBE mostrar `payloadRecibido` en modo lectura dentro de un modal; si el valor es nulo, mostrar `"Sin payload registrado"`.
- **FR-F-008**: El sistema DEBE mostrar `motivoError` cuando `estadoProcesamiento` sea un error o descarte.

### Key Entities (vistas)

- **[AuditoriaEventosSqsPage]**: Vista principal de auditoría con tabla paginada de todos los eventos SQS procesados.
- **[PaqueteHistorialPage]**: Vista de detalle de paquete: línea de tiempo + tabla de logs SQS propios.
- **[EventosSqsLogsTable]**: Tabla reutilizable para logs, con badge de estado de procesamiento y acción de inspección payload.
- **[HistorialEstadoTimeline]**: Línea de tiempo de cambios de estado con porcentaje de pago derivado.

---

## Technical Mapping (contratos de API)

### GET /api/sincronizacion/logs

| Parámetro | Tipo | Descripción |
|---|---|---|
| page | int | Número de página base 0 |
| size | int | Registros por página |
| estadoProcesamiento | string | Filtro opcional si backend lo implementa |
| idPaquete | UUID/string | Filtro opcional si backend lo implementa |

Respuesta:

```json
[
  {
    "id": 1,
    "eventId": "evt-2026-000001",
    "sqsMessageId": "71f7c7a8-1f03-4e3d-bc10-9fcf2a00b931",
    "idRuta": "550e8400-e29b-41d4-a716-446655440000",
    "idPaquete": "123e4567-e89b-12d3-a456-426614174000",
    "estadoRecibido": "ENTREGADO",
    "estadoProcesamiento": "PROCESADO",
    "motivoError": null,
    "payloadRecibido": "{\"eventId\":\"evt-2026-000001\",\"estado\":\"ENTREGADO\"}",
    "fechaRecepcion": "2026-04-08T15:21:00",
    "fechaProcesamiento": "2026-04-08T15:21:02"
  }
]
```

### GET /api/paquetes/{idPaquete}/historial

Respuesta:

```json
[
  {
    "id": 1,
    "eventId": "evt-2026-000001",
    "idPaquete": "123e4567-e89b-12d3-a456-426614174000",
    "estado": "ENTREGADO",
    "fecha": "2026-04-08T15:21:02",
    "fechaEvento": "2026-04-08T15:21:00"
  }
]
```

---

## Success Criteria *(mandatory)*

- **SC-F-001**: La vista de auditoría renderiza correctamente con 0, 1 y N eventos SQS sin errores de consola.
- **SC-F-002**: El badge de procesamiento distingue visualmente `PROCESADO`, `DUPLICADO`, `DESCARTADO_ATRASADO`, errores funcionales y errores técnicos.
- **SC-F-003**: El historial de paquete muestra estados en orden cronológico descendente.
- **SC-F-004**: El porcentaje de pago derivado coincide con las reglas del backend.
- **SC-F-005**: Ninguna ruta del frontend expone controles para sincronizar manualmente, consultar Gestión o publicar a SQS.

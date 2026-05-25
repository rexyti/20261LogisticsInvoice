# Feature Specification: Novedad estado del paquete — Frontend

**Created**: 2026-05-04  
**Spec backend base**: `Specs/Novedad estado del paquete/Novedad estado del paquete.md`  
**Plan técnico**: `Specs/Novedad estado del paquete/plan-frontend-novedad-estado-paquete.md`

## Contexto

El backend sincroniza automáticamente el estado de los paquetes desde el Módulo de Gestión durante el proceso de liquidación. El frontend no dispara esa sincronización: su rol es exclusivamente mostrar los resultados ya persistidos — historial de cambios de estado y log de comunicaciones HTTP — para que el equipo financiero pueda auditar y detectar problemas.

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Auditoría global de sincronización (Priority: P1)

Como miembro del equipo financiero, quiero ver el listado completo de logs de sincronización para identificar paquetes con fallos de comunicación o estados no procesados, antes de que afecten el cálculo de liquidación.

**Why this priority**: Sin visibilidad de los fallos de sincronización, el equipo financiero no puede detectar paquetes bloqueados en estado "Pendiente por Sincronización" que bloquearán la liquidación.

**Independent Test**: Navegar a la vista de auditoría con logs persistidos en base de datos y verificar que la tabla muestra correctamente: ID Paquete, Código HTTP, estado resultado derivado y fecha de sincronización.

**Acceptance Scenarios**:

1. **Scenario**: Visualización de logs exitosos y fallidos
   - **Given** Existen logs de sincronización con códigos HTTP 200, 404 y null (timeout).
   - **When** El usuario navega a la vista de auditoría.
   - **Then** La tabla muestra todos los registros con distinción visual entre EXITOSO (verde), PAQUETE NO ENCONTRADO (amarillo) y PENDIENTE POR SINCRONIZACIÓN (gris).

2. **Scenario**: Inspección del JSON recibido
   - **Given** Un log de sincronización tiene `jsonRecibido` no nulo.
   - **When** El usuario hace clic en la acción de ver JSON de un registro.
   - **Then** Se abre un modal mostrando el JSON en modo lectura sin alterar el layout de la tabla.

3. **Scenario**: Log sin respuesta registrada
   - **Given** Un log de sincronización tiene `jsonRecibido` nulo (timeout o error de red).
   - **When** El usuario intenta ver el JSON de ese registro.
   - **Then** El modal muestra el mensaje `"Sin respuesta registrada"`.

---

### User Story 2 — Historial de estado por paquete (Priority: P1)

Como miembro del equipo financiero, quiero ver el historial completo de cambios de estado de un paquete específico para verificar la trazabilidad de su evolución y el porcentaje de pago que corresponde.

**Why this priority**: La auditoría por paquete individual es necesaria cuando una liquidación se cuestiona — el equipo necesita demostrar qué estado tenía el paquete en cada momento.

**Independent Test**: Navegar al historial de un paquete con al menos dos entradas de estado distintas y verificar que la línea de tiempo muestra los estados en orden cronológico descendente con la fecha correcta y el porcentaje de pago derivado.

**Acceptance Scenarios**:

1. **Scenario**: Línea de tiempo de estados
   - **Given** Un paquete tiene registros en HistorialEstado con estados DEVUELTO y luego ENTREGADO.
   - **When** El usuario navega al historial de ese paquete.
   - **Then** La línea de tiempo muestra ambos estados en orden cronológico, con etiquetas visuales diferenciadas y los porcentajes de pago derivados (50% y 100% respectivamente).

2. **Scenario**: Logs de sincronización del paquete
   - **Given** El mismo paquete tiene múltiples logs de sincronización.
   - **When** El usuario está en la página de historial del paquete.
   - **Then** La sección inferior muestra la tabla de logs de sincronización específicos de ese paquete, con código HTTP y fecha.

3. **Scenario**: Paquete sin historial
   - **Given** Se navega al historial de un `idPaquete` que no tiene entradas en HistorialEstado.
   - **When** Se carga la página.
   - **Then** Se muestra el mensaje "No hay historial de estados registrado para este paquete."

---

### Edge Cases

- ¿Qué ocurre cuando el backend devuelve HTTP 503 al consultar los logs? → Mostrar mensaje de indisponibilidad temporal; no romper la vista.
- ¿Qué ocurre cuando el campo `jsonRecibido` contiene un JSON malformado? → Mostrarlo como texto plano en el modal sin intentar parsearlo.
- ¿Qué ocurre cuando se navega al historial de un paquete inexistente (HTTP 404)? → Mostrar el mensaje "Paquete no encontrado" en lugar de la línea de tiempo vacía.
- ¿Qué ocurre cuando hay muchos registros en la tabla de auditoría? → Usar paginación server-side con `page` y `size`; no cargar todos los registros en memoria.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-F-001**: El sistema DEBE mostrar la lista de logs de sincronización consumiendo `GET /api/sincronizacion/logs` con paginación.
- **FR-F-002**: El sistema DEBE mostrar el historial de estados de un paquete consumiendo `GET /api/paquetes/{idPaquete}/historial`.
- **FR-F-003**: El sistema DEBE mostrar el logs de sincronización de un paquete específico consumiendo `GET /api/sincronizacion/logs/paquetes/{idPaquete}`.
- **FR-F-004**: El sistema DEBE derivar visualmente el estado resultado a partir del `codigoRespuestaHTTP` según la regla: 200→EXITOSO, 404→PAQUETE NO ENCONTRADO, null→PENDIENTE POR SINCRONIZACIÓN, ≥500→ERROR DE SERVIDOR.
- **FR-F-005**: El sistema DEBE calcular y mostrar el porcentaje de pago en el historial derivándolo del estado: ENTREGADO→100%, DEVUELTO→50%, DAÑADO→0%, EXTRAVIADO→0%.
- **FR-F-006**: El sistema NO DEBE exponer ningún botón ni acción para disparar la sincronización manualmente.
- **FR-F-007**: El sistema DEBE mostrar `jsonRecibido` en modo lectura dentro de un modal; si el valor es nulo, mostrar `"Sin respuesta registrada"`.

### Key Entities (vistas)

- **[AuditoriaSincronizacionPage]**: Vista principal de auditoría con tabla paginada de todos los logs de sincronización.
- **[PaqueteHistorialPage]**: Vista de detalle de un paquete: línea de tiempo de estados + tabla de logs propios del paquete.
- **[SincronizacionLogsTable]**: Componente tabla reutilizable para logs, con badge de código HTTP y acción de inspección JSON.
- **[HistorialEstadoTimeline]**: Componente de línea de tiempo de cambios de estado con porcentaje de pago derivado.

---

## Technical Mapping (contratos de API)

### GET /api/sincronizacion/logs

| Parámetro | Tipo    | Descripción                         |
|-----------|---------|-------------------------------------|
| page      | int     | Número de página (base 0)           |
| size      | int     | Registros por página (default: 50)  |

Respuesta:
```json
[
  {
    "id": 1,
    "idPaquete": 42,
    "codigoRespuestaHTTP": 200,
    "jsonRecibido": "{\"idPaquete\":42,\"estado\":\"ENTREGADO\"}",
    "fechaSincronizacion": "2026-04-08T15:21:00"
  }
]
```

### GET /api/paquetes/{idPaquete}/historial

| Parámetro  | Tipo | Descripción                        |
|------------|------|------------------------------------|
| idPaquete  | Long | Identificador del paquete          |
| page       | int  | Número de página (base 0)          |
| size       | int  | Registros por página (default: 50) |

Respuesta:
```json
[
  {
    "id": 1,
    "idPaquete": 42,
    "estado": "ENTREGADO",
    "fecha": "2026-04-08T15:20:00"
  }
]
```

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-F-001**: La vista de auditoría debe renderizar correctamente con 0, 1 y N registros sin errores de consola.
- **SC-F-002**: El badge de código HTTP debe distinguir visualmente 2xx, 4xx, 5xx y null en todos los registros de la tabla.
- **SC-F-003**: El historial de un paquete debe mostrar los estados en orden cronológico descendente; un test automatizado valida el orden con al menos dos entradas.
- **SC-F-004**: El porcentaje de pago derivado en el historial debe coincidir con la tabla de reglas del backend (FR-002 del spec backend) para los cuatro estados válidos.
- **SC-F-005**: Ninguna ruta del frontend debe exponer un control para sincronizar manualmente el estado de un paquete.

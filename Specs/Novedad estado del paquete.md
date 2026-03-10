# Feature Specification: Novedad estado del paquete

**Created**: 23/02/2026  

## User Scenarios & Testing *(mandatory)*

Dado que el Módulo Financiero necesita determinar los pagos a transportistas,
el sistema debe realizar consultas sincrónicas al Módulo de Gestión de Paquetes para obtener el estado oficial de cada entrega tras un evento de cierre.

### User Story 1 - Consulta Sincrónica de Estado (Priority: P1)

Como Módulo Financiero, quiero consultar el estado de los paquetes asociados a una ruta mediante el endpoint de Gestión de Paquetes, para calcular el porcentaje de pago correspondiente al transportista.

**Why this priority**: Es el disparador crítico para la liquidación financiera. Sin el estado real (Entregado, Devuelto, Dañado, Extraviado.), no se puede procesar el flujo de caja.

**Independent Test**: consultar un estado para un paquete existente y verificar que el sistema, muestre los datos de interes(idRuta, Idpaquete, Estado).

**Acceptance Scenarios**:

1. **Scenario**: Consulta exitosa de un estado de paquete.
   - **Given** Un proceso de liquidación iniciado para la idRoute X..
   - **When** El sistema invoca sincrónicamente GET /route/{idRoute}/package/{idPaquete}.
   - **Then** El sistema recibe el estado actual y lo persiste en el historial financiero

2. **Scenario**: Paquete inexistente
   - **Given** Un identificador de paquete que no existe en el módulo de gestión.
   - **When** Se intenta una consulta sincronica.
   - **Then** El sistema registra un error de "Paquete no encontrado" y detiene el cálculo de ese paquete específico.

---

### Edge Cases


- What happens when  el Módulo de Gestión no responde en menos de 2 segundos;
- How does system handle el sistema debe reintentar la comunicación antes de marcar la liquidación como "Pendiente por Sincronización".
- What happens when Si el estado recibido no mapea con las reglas de negocio financieras?
- How does system handle el sistema debe omitir el cálculo de pago pero registrar la consulta.

## Requirements *(mandatory)*


### Functional Requirements

- **FR-001**: System MUST realizar peticiones HTTP GET estructuradas como /route/{idRoute}/package/{idPaquete}.
- **FR-002**: System MUST transformar el estado del paquete en una regla de pago (ej: "Entregado" $\rightarrow$ 100% pago, "Devuelto" $\rightarrow$ 50% pago). 
- **FR-003**: System MUST registrar el estado obtenido en la tabla HistorialEstado con un timestamp de sincronización.
- **FR-004**: System MUST mantener sincronizado el estado actual del paquete con el último estado registrado.

### Key Entities 

- **[Paquete]**: Representa un paquete (idPaquete, idRuta, estadoActual)
- **[HistorialEstado]**: Representa los diferentes cambios de estado de los paquetes (idPaquete, Estado, fecha)
- **[LogSincronización]**: Auditoría de la comunicación sincrónica (idPaquete, codigoRespuestaHTTP, jsonRecibido).

## Tecnical Mapping (API contract)
| **Parámetro** | **Tipo**        | **Descripción**                                 |
|---------------|-----------------|-------------------------------------------------|
| idRoute       | UUID / int      | Identificador de la ruta de transporte.         |
| idPaquete     | UUID / int      | Identificador único del paquete.                |
| Response      | **JSON**        | `{ "idPaquete": "...", "estado": "ENTREGADO" }` |

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**:El 100% de los estados persistidos en el módulo financiero deben coincidir con la última respuesta exitosa del Módulo de Gestión.
- **SC-002**: El sistema debe capturar y loguear cualquier respuesta HTTP distinta a 200 OK..
- **SC-003**: El 95% de las peticiones sincrónicas deben completarse en menos de 500ms.


# Feature Specification: Informar estado del paquete

**Created**: 23/02/2026  

## User Scenarios & Testing *(mandatory)*

Dado un informe del estado final de un paquete informado por el módulo de gestión de paquetes, el sistema debe registrar el evento y permitir su consulta en el módulo correspondiente.


### User Story 1 - Registrar estado del paquete (Priority: P1)

Como módulo de logística, quiero informar el estado de un paquete para controlar el estado final del paquete en el sistema.


**Why this priority**:  Esta funcionalidad es prioritaria porque permite reflejar el estado real del paquete en cada etapa del proceso logístico.

**Independent Test**: Enviar un estado para un paquete existente y verificar que el sistema, registre el estado y guarde la fecha y hora del mismo.

**Acceptance Scenarios**:

1. **Scenario**: Actualización exitosa de estado
   - **Given** Un paquete registrado en el sistema.
   - **When** Se recibe una actualización del estado
   - **Then** El sistema registra el nuevo estado (fecha y hora).

2. **Scenario**: Paquete inexistente
   - **Given** Un identificador de paquete no registrado
   - **When** Se recibe una actualización de estado
   - **Then** El sistema rechaza la actualización.
3. **Scenario**: Estado duplicado.
   - **Given** Un paquete con "x" estado.
    - **When** Se recibe el mismo estado.
    - **Then** El sistema no genera un nuevo registro innecesario.
---

### Edge Cases


- What happens when se recibe un estado invalido?
- How does system handle Debe reportar un fallo al momento de ingresar el estado?
- What happens when el sistema no puede persistir el cambioboundary condition?
- How does system handle un fallo en la actualización y se registra el error.

## Requirements *(mandatory)*


### Functional Requirements

- **FR-001**: System MUST registrar actualizaciones de estado para paquetes existentes.
- **FR-002**: System MUST validar transiciones de estado permitidas. 
- **FR-003**: System MUST evitar duplicaciones innecesarias de estado.s"]

### Key Entities 

- **[Paquete]**: Representa un paquete (idPaquete, estadoActual, fechaCreacion)
- **[EstadoPaquete]**: Representa el estado actual del paquete (idEstado, idPaquete, estado, fechaActualizacion)
- **[HistorialEstado]**: Representa los diferentes cambios de estado de los paquetes (idHistorial, estadoAnterior, estadoNuevo, fechaCambio)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de las actualizaciones válidas son registradas correctamente.
- **SC-002**: El sistema evita el 100% de actualizaciones duplicadas.
- **SC-003**: No existen inconsistencias entre estado actual e historial.


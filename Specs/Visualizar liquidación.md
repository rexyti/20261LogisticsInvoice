# Feature Specification: Visualizar liquidación

**Created**: 21/02/2026  

## User Scenarios & Testing *(mandatory)*

Dado la visualización de un cierre de rutas, el sistema debe de enseñar de la liquidación.


### User Story 1 - Visualizar liquidación (Priority: P1)

Como miembro de la entidad financiera ó transporador, quiero que el sistema me permita buscar una liquidación especifica, dependiendo del rol, y visualizar la información registrada para garantizar la trazabilidad de los datos financieros.

**Why this priority**: Permite mantener una trazabilidad de las diferentes liquidaciones fueron previamente calculadas.

**Independent Test**: Consultar a partir de una lista las liquidaciones previamente calculadas y verificar que el sistema muestre correctamente de la misma (Fecha de salida de la liquidación, monto de pago a transportista, penalidad).

**Acceptance Scenarios**:

1. **Scenario**: Visualizar los detalles de una liquidación previamente calculada.
   - **Given** el calculo de la liquidación.
   - **When** Se tenga una liquidación calculada en el sistema.
   - **Then** El sistema permite visualizar en forma de lista los detalles de la liquidación.

2. **Scenario**: buscar una liquidación especifica en la lista.
   - **Given** una lista de liquidaciones
   - **When** Se recibe la liquidación.
   - **Then** El sistema debe mostrar la liquidación deseada.
   
3. **Scenario**: buscar una liquidación que no existente en la lista.
    - **Given** una lista de liquidaciones
    - **When** Se recibe la liquidación que no exista
    - **Then** El sistema debe mostrar un mensaje indicando que la liquidación es inexistente dentro del registro.
4. **Scenario**: buscar una liquidación, siendo transportista, de otro transportista.
    - **Given** una lista de liquidaciones.
    - **When** Se pide una liquidación no perteneciente al transportista que realiza la petición.
    - **Then** El sistema debe mostrar un mensaje indicando que la liquidación es inexistente para el transportista.

---

### Edge Cases


- What happens when el sistema de almacenamiento no esta disponible?
- How does system handle Debe rechazar cualquier búsqueda y emitir un mensaje sobre el estado del sistema.

## Requirements *(mandatory)*


### Functional Requirements

- **FR-001**: System MUST Mostrar las liquidaciones almacenadas en forma de lista organizada.
- **FR-002**: System MUST be able to buscar una liquidación especifica.
- **FR-003**: System MUST Mostrar un mensaje cuando se trate de buscar una lista no existente.
- **FR-004**: System MUST Mostrar un mensaje cuando se trate de buscar una lista que no pertence al transportista que realiza la petición.


### Key Entities 

- **[Liquidación ]**: Representa una liquidación del sistema. (idLiquidación, fechaCreacion, MontodePago, Penalidad).

## Success Criteria *(mandatory)*


### Measurable Outcomes

- **SC-001**: El 100% de los eventos válidos de visualización de liquidación son registrados correctamente.
- **SC-002**: El sistema Evita el 100% de los registros duplicados.


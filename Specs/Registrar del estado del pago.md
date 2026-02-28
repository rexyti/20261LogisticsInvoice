# Feature Specification: Registro del estado del pago

**Created**: 21/02/2026 

## User Scenarios & Testing *(mandatory)*

Dado el registro del estado pago, el sistema debe permitir el ingreso de la estación en la que se encuentre el pago.


### User Story 1 - Registrar estado del pago (Priority: P1)

Como miembro de la entidad financiera , quiero que el sistema me permita registrar el estado de un pago especifico.


**Why this priority**: Permite mantener un registro constante del pago, sobre las liquidaciones que fueron previamente calculadas.

**Independent Test**: A partir de una de las liquidaciones previamente calculadas, registrar el estado del pago

**Acceptance Scenarios**:

1. **Scenario**: Registrar el estado inicial de un pago.
    - **Given** Una liquidación calculada.
   - **When** Se tiene la liquidación ya calculada en el sistema.
   - **Then** El sistema permite registrar el estado del pago de esa liquidación.

2. **Scenario**: Registrar el estado de pago de una liquidación previamente registrada.
   - **Given** una liquidación con registro de pago iniciado.
   - **When** Se recibe la instrucción de registro de pago.
   - **Then** El sistema debe rechazar esta intrucción.
3. **Scenario**:Actualizar el estado de pago.
    - **Given** una liquidación con un registro de estado iniciado.
     - **When** Se recibe la instrucción de actualización de pago.
    - **Then** El sistema debe mostrar un mensaje indicando que la actualización fue un exito.
---

### Edge Cases



- What happens when el sistema recibe la instrucción de registrar un estado una liquidación inexistente.?
 - How does system handle Debe rechazar cualquier registro y mostrar un mensaje de falta de liquidación.
- What happens when el sistema recibe la instrucción de actualización de estado sobre una liquidación paga.
- How does system handle Debe rechazar cualquier actualización y mostrar un mensaje donde se diga que el pago ya fue realizado.

## Requirements *(mandatory)*



### Functional Requirements

- **FR-001**: System MUST Permitir el registro de estado de pago de una liquidación.
- **FR-002**: System MUST  Permitir la actualización del estado de pago de una liquidación. 
- **FR-003**: System MUST be able to Mostrar un mensaje cuando se trate de registrar un estado de pago ya existente.


### Key Entities 

- **[Pago]**: Representa el pago de una liquidación. (idPago, estado, idLiquidación).

## Success Criteria *(mandatory)*


### Measurable Outcomes

- **SC-001**: El 100% de los eventos válidos de registro de pago son registrados correctamente.
- **SC-002**: El sistema Evita el 100% de los registros duplicados.
- **SC-003**: El 100% de los eventos válidos de actualización de pago son registrados correctamente.
- **SC-004**: El sistema Evita el 100% de los actualizaciones solapadas.


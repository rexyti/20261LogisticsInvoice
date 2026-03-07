# Feature Specification: Visualizar estado del pago

**Created**: 24/02/2026  

## User Scenarios & Testing *(mandatory)*

Dado un pago registrado en el sistema asociado a una liquidación, el sistema debe permitir visualizar su estado actual,
incluyendo ajustes o penalizaciones aplicadas.

### User Story 1 - Consultar estado del pago (Priority: P1)

Como usuario deseo visualizar el estado actual de mi pago asociado a una liquidación para conocer si este ya fue procesado, se encuentra pendiente o fue rechazado.

**Why this priority**: Es una funcionalidad crítica porque permite al usuario confirmar si recibirá el dinero o si debe realizar alguna acción adicional.

**Independent Test**: Puede probarse consultando el estado de un pago previamente registrado, verificando que el sistema muestre correctamente su estado.

**Acceptance Scenarios**:

1. **Scenario**: Visualización de pago aprobado
   - **Given** El usuario tiene un pago registrado como aprobado
   - **When** Consulta el estado del pago
   - **Then** El sistema muestra el estado "Pagado"
   - **And** Muestra la liquidación asociada.

2. **Scenario**: Visualización de pago pendiente
   - **Given** El pago aún no ha sido procesado
   - **When** El usuario consulta el estado del pago 
   - **Then** El sistema muestra el estado "Pendiente"

3. **Scenario**: Visualización de pago rechazado
   - **Given** El pago fue rechazado
   - **When** El usuario consulta el estado del pago
   - **Then** El sistema muestra el estado "Rechazado"
   - **And** Muestra el motivo del rechazo.

---

### User Story 2 - Consultar detalle del pago (Priority: P2)

Como usuario deseo visualizar informacion detallada del pago incluyendo ajustes y penalizaciones aplicadas, para entender valores, fechas y métodos utilizados.

**Why this priority**: Aporta transparencia al proceso financiero y evita reclamos por desconocimiento del monto

**Independent Test**: Puede probarse seleccionando un pago específico y verificando que el sistema despliegue toda la información relacionada al pago.

**Acceptance Scenarios**:

1. **Scenario**: Visualización del detalle del pago
   - **Given** Existe un pago asociado al usuario
   - **When** Consulta los detalles del pago.
   - **Then** el sistema muestra monto, fecha, Ajustes/penalidades y estado del pago

---

### Edge Cases



- What happens when el pago no existe o fue eliminado?
- How does system handle muestra un mensaje indicando que no se encontró información del pago.
- What happens when el usuario intenta visualizar un pago que no le pertenece?
- How does system handle debe bloquear el acceso al usuario.

## Requirements *(mandatory)*



### Functional Requirements

- **FR-001**: System MUST permitir al usuario consultar el estado actual de un pago
- **FR-002**: System MUST mostrar estados válidos del pago. 
- **FR-003**: Users MUST be able to visualizar el detalle completo de pago
- **FR-004**: System MUST asociar cada pago a un usuario especifico.
- **FR-005**: System MUST restringir la visualización únicamente al propietario del pago.


### Key Entities *(include if feature involves data)*

- **[Pago ]**: Representa la transacción económica realizada (IdPago,idUsuario,  monto, método de pago, fecha)
- **[Usuario]**: Persona que recibe o consulta el pago. Se relaciona con uno o varios pagos (idUser, nombre)
- **[EstadoPago]**: Representa la condición actual del pago (IdEstadoPago, idPago, estado)

## Success Criteria *(mandatory)*



### Measurable Outcomes

- **SC-001**: El sistema refleja cambios en el estado del pago sin inconsistencias en el 100% de los casos.
- **SC-002**:  El 100% de las consultas válidas muestran correctamente el estado del pago asociado.

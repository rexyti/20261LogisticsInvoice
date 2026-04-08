# Feature Specification: Registrar modelo de contratación

**Created**: 23/02/2026  

## User Scenarios & Testing *(mandatory)*

Dado el registro de un modelo de contratación creado por el gestor de tarifas, el sistema debe permitir ingresar la informacion del contrato y dejarlo disponible para su consulta
y gestión.

### User Story 1 - Registrar el contrato (Priority: P1)

Como gestor de tarifas, quiero registrar un nuevo contrato en el sistema para formalizar y controlar los procesos contractuales.

**Why this priority**:Por funcionalidad crítica, ya que sin el registro de contratos el módulo no cumple su propósito.

**Independent Test**: Se puede probar creando un contrato con datos válidos (idContrato, tipo de contrato, nombre del conductor, Precio por parada, tipo de vehiculo, fecha inicio, fecha final, Seguro) y verificar que el sistema lo almacene correctamente y deje disponible para consulta.

**Acceptance Scenarios**:

1. **Scenario**: Guardar contrato de manera exitosa
   - **Given** El usuario tiene permisos de gestor de tarifas.
   - **When** Ingresa la información obligatoria (idContrato, tipo de contrato, nombre del conductor, tipo de vehículo, Precio por parada, fecha inicio, fecha final, Seguro) del contrato.
   - **Then** El sistema guarda un modelo de contratación.
2. **Scenario**: Registrar un contrato con datos incompletos
    - **Given** Faltan campos de información del contrato.
   - **When** El usuario intenta registrar el contrato.
   - **Then** El sistema rechaza la operación.
3. **Scenario**: Registro duplicado
    - **Given** Ya existe un contrato con el mismo identificador externo
    - **When** Se intenta registrar nuevamente.
    - **Then** El sistema debe evitar el registro duplicado.
    - **And** El sistema muestra un mensaje de error mencionando que el contrato ya existe

---

### User Story 2 - Consultar contrato (Priority: P3)

Como gestor de tarifas, quiero consultar los contratos registrados para revisar su informacion y estado.

**Why this priority**: Permite la visualización de los datos, facilitando el seguimiento y validación del proceso de contratación.

**Independent Test**: Se puede probar consultando un contrato previamente registrado y verificar que el sistema muestre correctamente
todos sus datos.

**Acceptance Scenarios**:

1. **Scenario**: Consultar contrato existente
   - **Given** Un contrato previamente registrado en el sistema.
   - **When** El usuario realiza una búsqueda por identificador o criterio válido.
   - **Then** El sistema muestra la información completa del contrato.
2. **Scenario**: Consultar contrato inexistente 
    - **Given** No existe el contrato con el identificador ingresado.
    - **When** El usuario realiza la búsqueda.
    - **Then** El sistema informa que no se encontraron resultados.

---


### Edge Cases

- What happens when Se registra un contrato con información incompleta
- How does system handle Debe rechazar el registro y mostrar los campos faltantes.
- What happens when la fecha de finalización es anterior a la fecha de inicio?
- How does system handle El sistema debe impedir el registro y mostrar error de validación.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST registrar correctamente la información del contrato.
- **FR-002**: System MUST Validar que el contrato tenga todos los campos  con información. 
- **FR-003**: Users MUST be able to  consultar los datos de los diferentes contratos.
- **FR-004**: System MUST evitar registros duplicados de contratos con el mismo identificador externo.


### Key Entities 

- **[Contrato]**: Representa un modelo de contratación entre la empresa y el trasportista. (idContrato, tipo de contrato, nombre del conductor, PrecioParadas/Precio (el precio  por parada solo existe si el modelo de contrato es por parada en caso contrario pasa a ser Precio), tipo de vehículo, fecha inicio, fecha final)
- **[Usuario]**: Representa un actor que manejara el sistema. (idContrato,idUsuario , nombre)
- **[vehiculo]**: Representa el vehiculo que usara el trasportista. (idVehiculo, idUsuario, tipo)
- **[seguro]**: Representa el seguro del transportista. (idSeguro, idUsuario, Estado)

## Success Criteria *(mandatory)*


### Measurable Outcomes

- **SC-001**: El 100% de los contratos son registrados correctamente en el sistema.
- **SC-002**: El sistema Evita el 100% de los registros duplicados.
- **SC-003**: Se reduce a 0 los incidentes por inconsistencias en los campos obligatorios del contrato.


# Feature Specification: Visualizar cierre de ruta.

**Created**: 21/02/2026 

## User Scenarios & Testing *(mandatory)*

Dado el informe de cierre de rutas, el sistema debe de permitir la visualización de los datos de este.

### User Story 1 - Visualizar cierre de ruta (Priority: P1)

Como miembro del equipo de gestión de tarifas, quiero que el sistema me permita buscar una ruta especifica y visualizar la información registrada para garantizar la trazabilidad de cada ruta cerrada.


**Why this priority**: Permite mantener una trazabilidad de las diferentes rutas que se informaron como cerradas previamente.

**Independent Test**: Consultar a partir de una lista el historial de rutas previamente cerradas y verificar que el sistema muestre correctamente los datos del cierre de cada ruta (Fecha y hora de inicio de ruta, Fecha y hora de finalización de ruta, Paradas, Novedades).

**Acceptance Scenarios**:

1. **Scenario**: Visualizar los detalles de una ruta cerrada de manera exitosa
   - **Given** El registro de ruta cerrada.
   - **When** Se tenga un registro de ruta cerrada almacenado en el sistema.
   - **Then** El sistema permite visualizar en forma de lista los detalles (Fecha y hora de inicio de ruta, Fecha y hora de finalización de ruta, Paradas, Novedades) de las rutas cerrada almacenadas.

2. **Scenario**: Buscar un ruta especifica en la lista.
   - **Given** una lista de rutas
   - **When** Se recibe la ruta que se desea consultar
   - **Then** El sistema debe mostrar la ruta deseada.

3. **Scenario**: Buscar una ruta no existente en la lista.
    - **Given** una lista de rutas
    - **When** Se recibe la ruta que no exista
    - **Then** El sistema debe mostrar un mensaje indicando que la ruta es inexistente dentro del registro.

---


### Edge Cases

- What happens when el sistema de almacenamiento no esta disponible?
- How does system handle Debe rechazar cualquier búsqueda y emitir un mensaje sobre el estado del sistema.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST Mostrar las rutas almacenadas en forma de lista organizada.
- **FR-002**: System MUST be able to buscar una ruta especifica. 
- **FR-003**: Users MUST be able to Mostrar un mensaje cuando se trate de buscar una ruta no existente.
- **FR-004**: System MUST mostrar la información en un esquema Master-Detail
- **FR-005**: System MUST permitir paginación o carga progresiva de rutas.

### Key Entities 


- **[Ruta]**: Representa una ruta operativa del sistema. (idRuta, estado, fechaCreacion, fechaCierre )
- **[EventoCierreRuta]**: Representa el evento recibido desde el módulo externo. ( se relaciona a la ruta, permitiendo una relación de one to one)
- **[Parada]**:  Representa los diferentes puntos donde se para el vehiculo para entregar un paquete. (idRuta, nombre)
- **[NovedadRuta]**:  Representa las novedades que presente una ruta. (idNovedad, idRuta, descripcion)


## Success Criteria *(mandatory)*



### Measurable Outcomes

- **SC-001**: El usuario puede consultar una ruta cerrada en menos de 3 segundos.
- **SC-002**: El 100% de las rutas cerradas muestran información completa.
- **SC-003**: El sistema muestra mensajes claros cuando no existen resultados de busqueda.


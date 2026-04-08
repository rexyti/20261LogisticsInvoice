  # Feature Specification: Visualizar liquidación

**Created**: 21/02/2026  

## User Scenarios & Testing *(mandatory)*

Dada una liquidación previamente calculada y almacenada en el sistema, el sistema debe permitir su visualización


### User Story 1 - Visualizar liquidación (Priority: P1)

Como miembro de la entidad financiera, quiero que el sistema me permita buscar una liquidación específica, y visualizar la información registrada para garantizar la trazabilidad de los datos financieros.

**Why this priority**: Permite mantener una trazabilidad de las diferentes liquidaciones fueron previamente calculadas.

**Independent Test**: Consultar desde una lista las liquidaciones previamente calculadas y verificar que el sistema las enseñe. (IdRutas, FechaInicio, FechaCierre, IdLiquidación, Ajustes(tipo, monto, razón), tipo de vehiculo, PrecioParada, Número de paradas, Monto Bruto, MontoNeto, estadoliquidación, FechaCalculo).

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

4. **Scenario**: Liquidación no disponible
   - **Given** El contrato no posee liquidación calculada
   - **When** El usuario intenta consultarla
   - **Then** El sistema muestra mensaje indicando que la liquidación aún no existe.

---

### Edge Cases


- What happens when el sistema de almacenamiento no esta disponible?
- How does system handle Debe rechazar cualquier búsqueda y emitir un mensaje sobre el estado del sistema.

## Requirements *(mandatory)*


### Functional Requirements

- **FR-001**: System MUST Mostrar las liquidaciones almacenadas en forma de lista organizada.
- **FR-002**: System MUST be able to buscar una liquidación especifica.
- **FR-003**: System MUST Mostrar un mensaje cuando se trate de buscar una liquidación no existente.
- **FR-004**: System Must Verificar si el identificador del usuario cuenta con los permisos necesarios para visualizar liquidaciones de otros usuarios o únicamente las propias.

### Key Entities 

- **[Contrato]**:Representa un contrato entre la empresa y el trasportista (IdContrato, Tipo de contratación).
- **[Ruta]**:Representa una ruta que debe ser liquidada (IdRuta, FechaInicio, FechaCierre).
- **[liquidación]**: Representa el valor de la liquidación (idLiquidacion, idContrato, idRuta, estadoLiquidacion, valorFinal, fechaCalculo, idPenalidad, estadoPenalidad)
- **[Estado del paquete]**: Representa los estados finales de los paquetes y los motivos de su estado final (idPaquete, novedades, estadoFinal)
- **[Ajustes/penalización]**: Representa los ajustes del pago final de la liquidación, esta existe para relacionar los diferentes estados que puede estar el paquetes y las razones por la que falla la entrega (idAjustes, tipo, monto, razón)

## Success Criteria *(mandatory)*


### Measurable Outcomes

- **SC-001**: El 100% de las liquidaciones autorizadas son visualizadas correctamente.
- **SC-002**: El sistema restringe el acceso no autorizado en el 100% de los intentos.


# Feature Specification: Calcular liquidación

**Created**: 23/02/2026  

## User Scenarios & Testing *(mandatory)*

Dado un contrato  y se tiene el informe del estado del paquete que se ha creado, el sistema debe calcular automáticamente la liquidación correspondiente, considerando los valores contractuales,
pagos realizados, penalizaciones y ajustes aplicables.


### User Story 1 - Calcular liquidación automaticamente (Priority: P1)

Como gestor de tarifas, quiero que el sistema calcule automáticamente la liquidación de un contrato para obtener el valor final a pagar o cobrar.


**Why this priority**: Es la funcionalidad principal del módulo. Sin el cálculo de liquidación, el módulo no cumple su propósito.

**Independent Test**:  Seleccionar un contrato finalizado con pagos  y verificar que el sistema genere correctamente el valor de liquidación.

**Acceptance Scenarios**:

1. **Scenario**: Cálculo exitoso de liquidación.
   - **Given** Un contrato creado y el informe del estado del paquete
   - **When** El usuario solicita el cálculo de liquidación.
   - **Then**  El sistema calcula el valor final de la liquidación
   - **And** registra la liquidación asociada al contrato

    
---

### User Story 2 - Recalcular liquidación (Priority: P2)

Como gestor de contratación, quiero recalcular la liquidación si se registran nuevos  ajustes para mantener la información actualizada.

**Why this priority**: Permite la información actualizada cuando se registra nuevos pagos o ajustes posteriores.

**Independent Test**:Se puede probar agregar un nuevo ajuste de pago y ejecutando la opción de recalculo.

**Acceptance Scenarios**:

1. **Scenario**: Recalcular por nueva liquidación.
   - **Given** Una liquidación ya calculada.
   - **When** El usuario solicita recalcular.
   - **Then** El sistema actualiza valor final de la liquidación 
   - **And** Registra auditoria del recalculo.

---



### Edge Cases

- What happens when existe el contrato en una ruta no existente?
- How does system handle Debe reportar un fallo tecnico en la base de datos 
- What happens when si la fecha de finalización es anterior a la fecha de inicio?
- How does system handle El sistema debe impedir el registro y mostrar error de validación.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST calcular automáticamente la liquidación cuando el contrato se encuentra finalizado y el usuario confirma la acción de cálculo.
- **FR-002**: System MUST aplicar penalizaciones y ajustes configurados.  
- **FR-003**: System MUST Registrar auditoría del cálculo y recálculo.
- **FR-004**: System MUST evitar la generación de liquidaciones duplicadas para un mismo contrato.

### Key Entities 

- **[Contrato]**:Representa un contrato entre la empresa y el trasportista.
- **[liquidación]**: Representa el valor de la liquidación (idLiquidacion, idContrato, estadoLiquidacion, valorFinal, fechaCalculo, usuarioCalculo)
- **[Estado del paquete]**: Representa los estados finales de los paquetes y los motivos de su estado final (idPaquete, novedades, estadoFinal)
- **[Ajustes/penalización]**: Representa los ajustes del pago final de la liquidación, esta existe para relacionar los diferentes estados que puede estar el paquete (idAjuste, tipo, monto, razón) 

## Success Criteria *(mandatory)*



### Measurable Outcomes

- **SC-001**: El 100% de los contratos pueden ser liquidados correctamente.
- **SC-002**: No existen discrepancias entre valor calculado y valor almacenado y mostrado.
- **SC-003**: El sistema evita el 100% de liquidaciones duplicadas.


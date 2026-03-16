# Feature Specification: Calcular liquidación

**Created**: 23/02/2026  

## User Scenarios & Testing *(mandatory)*

Dado un contrato  y se tiene el informe del estado del paquete que se ha creado, el sistema debe calcular automáticamente la liquidación correspondiente, considerando los valores contractuales,
pagos realizados, penalizaciones y ajustes aplicables.


### User Story 1 - Calcular liquidación automaticamente (Priority: P1)

Como gestor de tarifas, quiero que el sistema calcule automáticamente la liquidación de un contrato para obtener el valor final a pagar o cobrar.


**Why this priority**: Es la funcionalidad principal del módulo. Sin el cálculo de liquidación, el módulo no cumple su propósito.

**Independent Test**: Seleccionar una ruta finalizada (IdRuta), modelo de contratación (IdContrato, Tipo de modelo de contratación), paquete (IdPaquete, novedades, estadoFinal) y verificar que el sistema genere correctamente el valor de liquidación.

**Acceptance Scenarios**:

1. **Scenario**: Cálculo exitoso de liquidación.
   - **Given** Un modelo de contratación creado, el informe del estado del paquete e informe de cierre de ruta.
   - **When** El sistema reciba toda esa información.
   - **Then**  El sistema calcula el valor final de la liquidación
   - **And** registra la liquidación asociada a la ruta.

    
---

### User Story 2 - Recalcular liquidación (Priority: P2)

Como gestor de contratación, quiero recalcular la liquidación si se registran nuevos ajustes para mantener la información actualizada.

**Why this priority**: Permite la información actualizada cuando se registra nuevos pagos o ajustes posteriores.

**Independent Test**:Se puede probar agregar un nuevo ajuste de pago y ejecutando la opción de recalculo.

**Acceptance Scenarios**:

1. **Scenario**: Recalcular por nueva liquidación.
   - **Given** Una liquidación ya calculada.
   - **When** El usuario solicita recalcular.
   - **Then** El sistema actualiza valor final de la liquidación 
   - **And** Registra auditoria del recalculo.

---

# Proceso de Cálculo de Liquidación

| Fase | Entradas del Sistema | Lógica del Proceso | Salida / Resultado |
| :--- | :--- | :--- | :--- |
| **1. Recolección de Datos** | - Informe de cierre de ruta<br>- Estado de los paquetes<br>- Modelo de contratación | El sistema carga la información de la ruta, asocia el estado final de cada paquete e identifica el modelo de pago del repartidor. | Base de datos preparada con la ruta, paquetes y tipo de liquidación. |
| **2. Validación y Tarifas** | - Datos de paquetes y paradas<br>- Tarifas del contrato | Se aplican las reglas de negocio para determinar qué paquetes/paradas son entregas válidas y se consulta la tarifa a aplicar. | Cantidad de paradas válidas y tarifa definida. |
| **3. Cálculo y Ajustes** | - Paradas válidas / Ruta confirmada<br>- Subtotal calculado | **1.** Se calcula el subtotal (multiplicando paradas x tarifa o verificando ruta completa).<br>**2.** Se aplican ajustes adicionales (bonos, penalizaciones). | Valor final y exacto de la liquidación. |
| **4. Registro Final** | - Valor final calculado | El sistema genera y guarda el registro oficial del cálculo para el repartidor. | Liquidación registrada y lista para el proceso de pago. |

**Resultados Finales del Sistema:**
* Valor total a pagar al repartidor.
* Registro formal de la liquidación.
* Detalle desglosado del cálculo del pago.

---


### Edge Cases

- What happens when existe el modelo de contratación en una ruta no existente?
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

- **[Contrato]**:Representa un contrato entre la empresa y el trasportista (IdContrato, Tipo de contratación).
- **[Ruta]**:Representa una ruta que debe ser liquidada (IdRuta, FechaInicio, FechaCierre).
- **[liquidación]**: Representa el valor de la liquidación (idLiquidacion, idContrato, idRuta, estadoLiquidacion, valorFinal, fechaCalculo, idPenalidad, estadoPenalidad)
- **[Estado del paquete]**: Representa los estados finales de los paquetes y los motivos de su estado final (idPaquete, novedades, estadoFinal)
- **[Ajustes/penalización]**: Representa los ajustes del pago final de la liquidación, esta existe para relacionar los diferentes estados que puede estar el paquete (idPenalización, tipo, monto, razón) 

## Success Criteria *(mandatory)*



### Measurable Outcomes

- **SC-001**: El 100% de los contratos pueden ser liquidados correctamente.
- **SC-002**: No existen discrepancias entre valor calculado y valor almacenado y mostrado.
- **SC-003**: El sistema evita el 100% de liquidaciones duplicadas.


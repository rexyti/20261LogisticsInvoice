# Feature Specification: Calcular liquidación

**Created**: 23/02/2026  

## Global Context 
Una vez que el sistema recibe el cierre de ruta desde el Módulo de Rutas y Flotas, y consulta el estado final de cada paquete al Módulo de Gestión de Paquetes, el sistema calcula automáticamente la liquidación del transportista sin intervención de ningún usuario. 
Este cálculo aplica las reglas del modelo de contratación registrado, los estados de entrega de los paquetes, y las penalizaciones o ajustes configurados.

Existe un segundo flujo, el recálculo, que ocurre únicamente cuando un conductor solicita formalmente una revisión de su liquidación y esa solicitud es aceptada por un administrador. 
En ese caso, el administrador ingresa los nuevos ajustes manualmente en el sistema y presiona un botón para que el sistema recalcule automáticamente el valor final con esos nuevosdatos.
---

## glossary of stakeholder
- **Sistema:**  El módulo financiero actuando de forma automática, sin intervención humana. 
- **Conductor/Transportista:** Persona que realizó la ruta y puede solicitar una revisión de su liquidación.
- **Administrador:** Usuario interno con permisos para revisar solicitudes de recálculo e ingresar ajustes al sistema.
- **Gestor de tarifas:** Usuario interno responsable de configurar los modelos de contratación y tarifas.
- --
## User Scenarios & Testing *(mandatory)*

Dado un contrato  y se tiene el informe del estado del paquete que se ha creado, el sistema debe calcular automáticamente la liquidación correspondiente, considerando los valores contractuales,
pagos realizados, penalizaciones y ajustes aplicables.


### User Story 1 - Calcular liquidación automaticamente (Priority: P1)

Como sistema, debo calcular automáticamente la liquidación del transportista en cuanto reciba el cierre de ruta y el estado final de todos los paquetes asociados, aplicando el modelo de contratación vigente.

**Why this priority**: Es la funcionalidad principal del módulo. Sin el cálculo de liquidación, el módulo no cumple su propósito.

**Independent Test**: Simular la recepcion del evento de cierre de una ruta finalizada (IdRuta), con el modelo de contratación (IdContrato, Tipo de modelo de contratación),  paquete (IdPaquete, novedades, estadoFinal) y verificar que el sistema genere correctamente el valor de liquidación, sin que ningún usuario haya interactuado con el sistema.

**Acceptance Scenarios**:

1. **Scenario**: Cálculo exitoso de liquidación para modelo "Por parada realizada".
   - **Given** que existe un contrato de tipo "Por Parada Realizada" con una tarifa definida por parada, y el sistema recibe el cierre de una ruta con 10 paradas (7 exitosas, 2 fallidas por culpa del cliente, 1 fallida por culpa del transportista).
   - **When** El sistema procesa toda esa información automaticamente.
   - **Then** el sistema calcula: 7 paradas exitosas al 100% + 2 paradas fallidas por cliente al porcentaje configurado (ej. 30% o 50%) + 1 parada fallida por transportista al 0%, aplica penalizaciones si corresponde, y genera el valor final de la liquidación
   - **And** registra la liquidación asociada a la ruta.
   

2. **Scenario:** Cálculo exitoso de liquidación para modelo "Recorrido Completo"
    - **Given** que existe un contrato de tipo "Recorrido Completo" con un valor fijo por ruta, y el sistema recibe el cierre de esa ruta.
    - **When** El sistema procesa toda esa información automaticamente.
    - **Then** el sistema verifica si la ruta fue completada según los criterios del contrato y asigna el valor fijo pactado como valor final de la liquidación.
    - **And** registra la liquidación asociada a la ruta.


3. **Scenario:** Liquidación duplicada bloqueada.
    - **Given**  que ya existe una liquidación para una ruta especifica.
    - **When** El sistema el sistema intenta calcular nuevamente la liquidación para esa misma ruta.
    - **Then** el sistema verifica si la ruta fue completada según los criterios del contrato y asigna el valor fijo pactado como valor final de la liquidación.
    - **And** el sistema detecta el duplicado y  bloquea la operación.

---

### User Story 2 - Recalcular liquidación (Priority: P2)

Como administrador, quiero poder ingresar nuevos ajustes a una liquidación ya calculada y ordenar al sistema que recalcule el valor final, cuando un conductor haya solicitado formalmente una revisión y esa solicitud haya sido aceptada.

**Why this priority**:  Permite corregir o actualizar el valor de una liquidación cuando existen ajustes posteriores al cálculo inicial, garantizando que el transportista reciba un pago justo.

**Independent Test**:Tener una liquidación ya calculada. Simular que un conductor envía una solicitud de revisión y que el administrador la acepta. El administrador ingresa un nuevo ajuste (por ejemplo, un bono adicional) y presiona el botón "Recalcular". Verificar que el sistema actualiza el valor final de la liquidación y registra el cambio en el historial de auditoría.

**Acceptance Scenarios**:

1. **Scenario**: Recálculo exitoso con nuevos ajustes .
   - **Given** Que existe una liquidación en estado "Calculada" y el administrador ha aceptado la solicitud de revisión del conductor.
   - **When** el administrador ingresa uno o más ajustes nuevos (bonos, penalizaciones adicionales, correcciones) y presiona el botón "Recalcular".
   - **Then** el sistema toma el cálculo base original, aplica los nuevos ajustes ingresados por el administrador, genera el nuevo valor final y actualiza el registro de la liquidación
   - **And** registra en el historial de auditoría: quién solicitó el recálculo, quién lo aprobó, qué ajustes fueron ingresados, el valor anterior y el valor nuevo

2. **Scenario:** INtento de recálculo sin solicitud aceptada 
    - **Given** que exite una liquidación calculada pero no hay ninguna solicitud de revisión aceptada para esa liquidación.
    - **When** un usuario intenta accedes a la opción de recálculo.
    - **Then** el sistema no permite la acción y muestra un mensaje indicando que solo es posible recalcular cuando existe una solicitud de revisión aprobada
---

# Proceso de Cálculo de Liquidación

| Fase                          | Entradas del Sistema                                                                | Lógica del Proceso                                                                                                                                                                                                                                        | Salida / Resultado                                                                  |
|:------------------------------|:------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------|
| **1. Recolección de Datos**   | - Informe de cierre de ruta<br>- Estado de los paquetes<br>- Modelo de contratación | El sistema carga la información de la ruta, asocia el estado final de cada paquete e identifica el modelo de pago del repartidor.                                                                                                                         | Base de datos preparada con la ruta, paquetes y tipo de contrato.                   |
| **2. Validación y Tarifas**   | - Paradas, estados de paquetes <br>- Tarifas del contrato                           | El sistema clasifica las paradas (exitosas, fallidas por cliente, fallidas por transportista) y determina la tarifa aplicable según el contrato                                                                                                           | Lista de paradas  clasificadas con su porcentaje de pago válidas y tarifa definida. |
| **3. Cálculo y Ajustes**      | - Paradas clasificadas  <br>- tarifa definidas                                      | **1.** Para "Por Parada": multiplica cantidad de paradas por su porcentaje de pago y por la tarifa. Para "Recorrido Completo": verifica cumplimiento de la ruta y asigna el valor fijo.<br>**2.** Se aplican ajustes adicionales (bonos, penalizaciones). | Subtotal antes de ajustes                                                           |
| **4. Aplicación de ajsustes** | - Subtotal, ajustes y penalizaciones configudaras                                   | Suma o resta los ajustes (bonos, penalizaciones por paquetes dañados, etc.) al subtotal.                                                                                                                                                                  | Valor final neto de la liquidación.                                                 |
| **5. Registro**               | - Valor final calculado                                                             | El sistema guarda la liquidación con todos sus campos, la asocia a la ruta y al contrato, y registra la operación.                                                                                                                                        | Liquidación registrada y lista para el proceso de pago.                             |

**Resultados Finales del Sistema:**
* Valor total a pagar al repartidor.
* Registro formal de la liquidación.
* Detalle desglosado del cálculo del pago.

---


### Edge Cases

- What happens when El modelo de contratación referenciado en la ruta no existe en el sistema?
- How does system handle El sistema detiene el cálculo, marca la liquidación con estado "Error — contrato no encontrado"
- What happens when si la fecha de finalización es anterior a la fecha de inicio?
- How does system handle El sistema rechaza el procesamiento del evento, registra el error en los logs y no genera ninguna liquidación.
- what happes when El estado de un paquete no tiene correspondencia con ninguna regla de pago configurada?
- how does system handle El sistema omite ese paquete del cálculo, lo registra como "paquete sin regla aplicable" y continúa con los demás paquetes de la ruta.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST  calcular automáticamente la liquidación en cuanto reciba el evento de cierre de ruta y el estado final de los paquetes, sin necesidad de acción por parte de ningún usuario.
- **FR-002**: System MUST aplicar el porcentaje de pago correcto según el estado de cada parada (exitosa, fallida por cliente, fallida por transportista) y el modelo de contratación vigente. 
- **FR-003**: System MUST aplicar penalizaciones y ajustes configurados al momento de calcular el valor final.
- **FR-004**: System MUST permitir el recálculo de una liquidación únicamente cuando exista una solicitud de revisión aceptada, y solo tras la intervención del administrador que ingresa los nuevos ajustes y confirma la acción.
- **FR-005**: System MUST registrar una entrada de auditoría por cada cálculo y recálculo realizado, incluyendo: fecha, actor responsable (sistema o administrador), valor anterior y valor nuevo.
- **FR-004**: System MUST evitar la generación de liquidaciones duplicadas para una misma ruta.

### Key Entities 

- **[Contrato]**:Representa un contrato entre la empresa y el trasportista (IdContrato, Tipo de contratación).
- **[Ruta]**:Representa una ruta que debe ser liquidada (IdRuta, FechaInicio, FechaCierre).
- **[liquidación]**: Representa el valor de la liquidación (idLiquidacion, idContrato, idRuta, estadoLiquidacion, valorFinal, fechaCalculo, idPenalidad, estadoPenalidad)
- **[Estado del paquete]**: Representa los estados finales de los paquetes y los motivos de su estado final (idPaquete, novedades, estadoFinal)
- **[Ajustes/penalización]**: Representa los ajustes del pago final de la liquidación, esta existe para relacionar los diferentes estados que puede estar el paquete (idPenalización, tipo, monto, razón) 
- **[HistorialAuditoria]**: Registro de cada operación realizada sobre una liquidación. (idRegistro, idLiquidacion, operacion (Cálculo / Recálculo), valorAnterior, valorNuevo, fechaOperacion, responsable).

## Success Criteria *(mandatory)*



### Measurable Outcomes

- **SC-001**: El 100% de las rutas cerradas con contrato válido generan una liquidación calculada correctamente de forma automática.
- **SC-002**: No existen discrepancias entre el valor calculado, el valor almacenado y el valor mostrado al usuario.
- **SC-003**: El sistema bloquea el 100% de los intentos de generar liquidaciones duplicadas para una misma ruta.
- **SC-004**: El 100% de los recálculos quedan registrados en el historial de auditoría con el valor anterior, el valor nuevo y el responsable de la acción.
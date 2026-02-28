1) Feature Specification: Informar cierre de ruta
Created: 21/02/2026
User Scenarios & Testing (mandatory)
Dado un cierre de ruta informado por el módulo de rutas y flotas, el sistema debe registrar el evento y permitir su consulta en el módulo correspondiente.

User Story 1 – Registrar cierre de ruta automáticamente (Priority: P1)
Como módulo de gestión de rutas, quiero que el sistema registre automáticamente el cierre de una ruta para garantizar la consistencia de la información y su disponibilidad para consulta.
Why this priority: Mantiene la funcionalidad de todo el sistema ya que, sin registrar un cierre, no mantiene una MVP.
Independent Test: Enviar un evento de cierre de ruta desde el módulo de Rutas y Flotas y verificar que el sistema lo registre correctamente en la base de datos, dejándolo disponible para consulta.
Acceptance Scenarios:
1.	Scenario: registrar ruta cerrada de manera exitosa
o	Given Ruta existente activa .
o	When El modulo de rutas y paquetes envía un informe de cierre de ruta.
o	Then El sistema registra el evento de cierre de ruta y lo deja en el sistema para visualizar.
2.	Scenario: Evento de ruta cerrada duplicado
o	Given Una ruta cerrada
o	When Se recibe nuevamente el evento de cierre
o	Then El sistema no debe registrar nuevamente la ruta cerrada
_______________________________________
Edge Cases
•	What happens when se recibe un evento de cierre para una ruta inexistente?
•	How does system handle Debe rechazar el evento y registrar un error
•	¿Qué ocurre cuando la base de datos no está disponible en el momento de registrar el cierre?
•	El sistema debe generar un fallo técnico y mantener el evento en cola.
Requirements (mandatory)
Functional Requirements
•	FR-001: System MUST registrar automáticamente un evento de cierre de ruta cuando lo reciba del modulo de rutas y flotas.
•	FR-002: System MUST Validar que la ruta este en estado “Activa” antes de permiter su cierre.
•	FR-003: Users MUST be able to almacenar fechas y hora del cierre.
•	FR-004: System MUST evitar registros duplicados de cierre para la misma ruta.
•	FR-005: System MUST permitir la consulta de rutas cerradas desde le modulo correspondiente
Key Entities (include if feature involves data)
•	Ruta : Representa una ruta operativa del sistema. (idRuta, estado, fechaCreacion, fechaCierre )
•	EventeCierreRuta: Representa el evento recibido desde el módulo externo. ( se relaciona a la ruta, permitiendo una relación de one to one)
Success Criteria (mandatory)
Measurable Outcomes
•	SC-001: El 100% de los eventos válidos de cierre de ruta son registrados correctamente.
•	SC-002: El sistema Evita el 100% de los registros duplicados
•	SC-003: Se reduce a 0 los incidentes por inconsistencias de estados de rutas cerradas.

_______________________________________________________________________________________________________________

2) Feature Specification: Visualizar cierre de ruta.
Created: 21/02/2026
User Scenarios & Testing (mandatory)
Dado el informe de cierre de rutas, el sistema debe de permitir la visualización de los datos de este. 

User Story 1 – Visualizar cierre de ruta (Priority: P1)
Como miembro del equipo de gestión de tarifas, quiero que el sistema me permita buscar una ruta especifica y visualizar la información registrada para garantizar la trazabilidad de cada ruta cerrada.
Why this priority: Permite mantener una trazabilidad de las diferentes rutas que se informaron como cerradas previamente.
Independent Test: Consultar a partir de una lista el historial de rutas previamente cerradas y verificar que el sistema muestre correctamente los datos del cierre de cada ruta (Fecha y hora de inicio de ruta, Fecha y hora de finalización de ruta, Paradas, Novedades).
Acceptance Scenarios:
1.	Scenario: Visualizar los detalles de una ruta cerrada de manera exitosa
o	Given El registro de ruta cerrada.
o	When Se tenga un registro de ruta cerrada almacenado en el sistema.
o	Then El sistema permite visualizar en forma de lista los detalles de las rutas cerrada almacenadas.
2.	Scenario: buscar un ruta especifica en la lista.
o	Given una lista de rutas
o	When Se recibe la ruta que se desea consultar
o	Then El sistema debe mostrar la ruta deseada.
3.	Scenario: buscar una ruta no existente en la lista.
o	Given una lista de rutas
o	When Se recibe la ruta que no exista
o	Then El sistema debe mostrar un mensaje indicando que la ruta es inexistente dentro del registro.
_______________________________________

Edge Cases
•	What happens when el sistema de almacenamiento no esta disponible?
•	How does system handle Debe rechazar cualquier búsqueda y emitir un mensaje sobre el estado del sistema.

Requirements (mandatory)
Functional Requirements
•	FR-001: System MUST Mostrar las rutas almacenadas en forma de lista organizada.
•	FR-002: System MUST be able to buscar una ruta especifica.
•	FR-003: Users MUST Mostrar un mensaje cuando se trate de buscar una ruta no existente
Key Entities (include if feature involves data)
•	Ruta : Representa una ruta operativa del sistema. (idRuta, estado, fechaCreacion, fechaCierre ).
Success Criteria (mandatory)
Measurable Outcomes
•	SC-001: El 100% de los eventos válidos de cierre de ruta son registrados correctamente.
•	SC-002: El sistema Evita el 100% de los registros duplicados
•	SC-003: Se reduce a 0 los incidentes por inconsistencias de estados de rutas cerradas.

_______________________________________________________________________________________________________________

3) Feature Specification: Registrar modelo de contratación 
Created: 23/02/2026
User Scenarios & Testing (mandatory)
Dado el registro de un modelo de contratación creado por el gestor de tarifas, el sistema debe permitir ingresar la informacion del contrato y dejarlo disponible para su consulta 
y gestión.

User Story 1 - Registrar el contrato (Priority: P1)
Como gestor de tarifas, quiero registrar un nuevo contrato en el sistema para formalizar y controlar los procesos contractuales.
Why this priority: Por funcionalidad crítica, ya que sin el registro de contratos el módulo no cumple su propósito.
Independent Test:  Se puede probar creando un contrato con datos válidos y verificar que el sistema lo almacene correctamente y deje disponible para consulta.
Acceptance Scenarios:
1.	Scenario: registrar contrato de manera exitosa
o	Given El usuario tiene permisos de gestor de tarifas.
o	When Ingresa la información obligatoria del contrato
o	Then El sistema registra el contrato 
2.	Scenario: registrar un contrato con datos incompletos 
o	Given Faltan campos obligatorios del contrato
o	When El usuario intenta registrar el contrato
o	Then El sistema rechaza la operación 
3.  Scenario: registro duplicado 
o	Given Ya existe un contrato con el mismo identificador externo
o	When Se intenta registrar nuevamente 
o	Then El sistema debe evitar el registro duplicado 
________________________________________
User Story 2 – Consultar contrato(Priority: P2)
Como gestor de tarifas, quiero consultar los contratos registrados para revisar su informacion y estado.
Why this priority: Permite la visualización de los datos, facilitando el seguimiento y validación del proceso de contratación.
Independent Test: Se puede probar consultando un contrato previamente registrado y verificar que el sistema muestre correctamente 
todos sus datos, incluyendo el estado actual.
Acceptance Scenarios:
1.	Scenario: Consultar contrato existente
o	Given Un contrato previamente registrado en el sistema.
o	When El usuario realiza una búsqueda por identificador o criterio válido.
o	Then El sistema muestra la información completa del contrato. 
2.	Scenario: Consultar contrato inexistente 
o	Given No existe el contrato con el identificador ingresado.
o	When El usuario realiza la búsqueda.
o	Then El sistema informa que no se encontraron resultados.
3.  Scenario: Filtrar contratos por estados.
o	Given Múltiples contratos registrados con diferentes estados.
o	When El usuario aplica un filtro por estado.
o	Then El sistema muestra únicamente los contratos que coinciden con el criterio seleccionado. 
________________________________________
User Story 3 – Notificación automática (Priority: P3)
Como gestor, quiero recibir una notificación cuando un contrato cambie de estado.
Why this priority:  Permite un seguimiento de los diferentes contratos.
Independent Test: Un contrato se completa, el sistema mandara una notificación de que el contrato paso de estar vigente a completado. 
Acceptance Scenarios:
1.	Scenario: Notificación enviada al cambiar el estado del contrato
o	Given Un contrato registrado en el sistema.
o	When El cambio de estado es confirmado en el sistema. 
o	Then El sistema envía automaticamente una notificación al usuario configurado.
________________________________________
Edge Cases
•	What happens when Se registra un contrato con informacion incompleta 
•	How does system handle Debe rechazar el registro y mostrar los campos faltantes
•	¿Qué ocurre si la fecha de finalización es anterior a la fecha de inicio?
•	El sistema debe impedir el registro y mostrar error de validación.
Requirements (mandatory)
Functional Requirements
•	FR-001: System MUST registrar correctamente la información del contrato.
•	FR-002: System MUST Validar que el contrato tenga todos los campos obligatorios con información.
•	FR-003: Users MUST be able to consultar los datos de los diferentes contratos.
•	FR-004: System MUST evitar registros duplicados de contratos con el mismo identificador externo.
•	FR-005: System MUST permitir la consulta de los diferentes contratos.
Key Entities (include if feature involves data)
•	Contrato : Representa un contrato entre la empresa y el trasportista. (idContrato, estado, fechaCreacion, fechaCierre, horaCierre, novedades )
Measurable Outcomes
•	SC-001: El 100% de los contratos son registrados correctamente en el sistema.
•	SC-002: El sistema Evita el 100% de los registros duplicados
•	SC-003: Se reduce a 0 los incidentes por inconsistencias en los campos obligatorios del contrato.

_______________________________________________________________________________________________________________


4) Feature Specification: Calcular liquidación 
Created: 23/02/2026
User Scenarios & Testing (mandatory)
Dado un contrato  y se tiene el informe del estado del paquete que se ha creado, el sistema debe calcular automáticamente la liquidación correspondiente, considerando los valores contractuales,
pagos realizados, penalizaciones y ajustes aplicables.

User Story 1 - Calcular liquidación automaticamente (Priority: P1)
Como gestor de tarifas, quiero que el sistema calcule automáticamente la liquidación de un contrato para obtener el valor final a pagar o cobrar.
Why this priority: Es la funcionalidad principal del módulo. Sin el cálculo de liquidación, el módulo no cumple su propósito.
Independent Test:  Seleccionar un contrato finalizado con pagos registrados y verificar que el sistema genere correctamente el valor de liquidación.
Acceptance Scenarios:
1.	Scenario: Cálculo exitoso de liquidación.
o	Given Un contrato creado y el informe del estado del paquete
o	When El usuario solicita el cálculo de liquidación.
o	Then El sistema calcula el valor total contractual
________________________________________
User Story 2 – Recalcular liquidación (Priority: P2)
Como gestor de contratación, quiero recalcular la liquidación si se registran nuevos  ajustes para mantener la información actualizada.
Why this priority:  Permite la información actualizada cuando se registra nuevos pagos o ajustes posteriores.
Independent Test: Se puede probar agregar un nuevo pago o ajuste de pago y ejecutando la opción de recalculo.
el sistema debe actualizar correctamente el valor final y conservar el historial del cálculo anterior.
Acceptance Scenarios:
1.	Scenario: Recalcular por nueva liquidación.
o	Given Una liquidación ya calculado.
o	When El usuario solicita recalcular. 
o	Then El sistema actualiza el valor final.
________________________________________

Edge Cases
•	What happens when existe el contrato en una ruta no existente
•	How does system handle Debe reportar un fallo tecnico en la base de datos 
•	¿Qué ocurre si la fecha de finalización es anterior a la fecha de inicio?
•	El sistema debe impedir el registro y mostrar error de validación.
Requirements (mandatory)
Functional Requirements
•	FR-001: System MUST calcular automáticamente la liquidación de un contrato.
•	FR-004: System MUST aplicar penalizaciones y ajustes configurados.
•	FR-005: System MUST Registrar auditoría del cálculo y recálculo.
Key Entities (include if feature involves data)
•	Contrato : Representa un contrato entre la empresa y el trasportista. (idContrato, estado, fechaCreacion, fechaCierre )
• Liquidación : Representa el valor de la liquidación (idLiquidacion, idContrato, valorFinal, fechaCalculo) 
• Ajustes/Penalización : Representa las penalizaciones del pago (idAjuste, tipo, monto)
Measurable Outcomes
•	SC-001: El 100% de los contratos pueden ser liquidados correctamente.
•	SC-002: No existen discrepancias entre valor calculado y valor mostrado.
•	SC-003: El sistema evitan el 100% de liquidaciones duplicadas.

_______________________________________________________________________________________________________________

5) Feature Specification: Informar estado del paquete 
Created: 23/02/2026
User Scenarios & Testing (mandatory)
Dado un informe del estado final de un paquete informado por el módulo de gestión de paquetes, el sistema debe registrar el evento y permitir su consulta en el módulo correspondiente.

User Story 1 - Registrar estado del paquete (Priority: P1)
Como módulo de logística, quiero informar el estado de un paquete para controlar el estado final del paquete en el sistema.
Why this priority: Esta funcionalidad es prioritaria porque permite reflejar el estado real del paquete en cada etapa del proceso logístico. 
Independent Test:  Enviar un estado para un paquete existente y verificar que el sistema, registre el estado y guarde la fecha y hora del mismo.
Acceptance Scenarios:
1.	Scenario: Actualización exitosa de estado
o	Given Un paquete registrado en el sistema.
o	When Se recibe una actualización del estado.
o	Then El sistema registra el nuevo estado (fecha y hora).
2. 	Scenario: Paquete inexistente
o	Given Un identificador de paquete no registrado
o	When Se recibe una actualización de estado
o	Then El sistema rechaza la actualización.
3. 	Scenario: Estado duplicado.
o	Given Un paquete con "x" estado.
o	When Se recibe el mismo estado.
o	Then El sistema no genera un nuevo registro innecesario.
_______________________________________

Edge Cases
•	What happens when si se recibe un estado invalido
•	How does system handle Debe reportar un fallo al momento de ingresar el estado
•	¿Qué ocurre si el sistema no puede persistir el cambio?
•	El sistema reporta un fallo en la actualización y se registra el error.
Requirements (mandatory)
Functional Requirements
•	FR-001: System MUST registrar actualizaciones de estado para paquetes existentes.
•	FR-004: System MUST validar transiciones de estado permitidas.
•	FR-005: System MUSTevitar duplicaciones innecesarias de estado.
Key Entities (include if feature involves data)
•	Paquete : Representa un paquete (idPaquete, estadoActual, fechaCreacion)
• EstadoPaquete : Representa el estado actual del paquete (idEstado, idPaquete, estado, fechaActualizacion) 
• HistorialEstado : Representa los diferentes cambios de estado de los paquetes (idHistorial, estadoAnterior, estadoNuevo, fechaCambio)
Measurable Outcomes
•	SC-001: El 100% de las actualizaciones válidas son registradas correctamente.
•	SC-002: El sistema evita el 100% de actualizaciones duplicadas.
•	SC-003: No existen inconsistencias entre estado actual e historial.

_______________________________________________________________________________________________________________

6) Feature Specification: Visualizar liquidación
Created: 21/02/2026
User Scenarios & Testing (mandatory)
Dado la visualización de un cierre de rutas, el sistema debe de enseñar de la liquidación.


User Story 1 – Visualizar liquidación (Priority: P1)
Como miembro de la entidad financiera ó transporador, quiero que el sistema me permita buscar una liquidación especifica, dependiendo del rol, y visualizar la información registrada para garantizar la trazabilidad de los datos financieros.
Why this priority: Permite mantener una trazabilidad de las diferentes liquidaciones fueron previamente calculadas.
Independent Test: Consultar a partir de una lista las liquidaciones previamente calculadas y verificar que el sistema muestre correctamente de la misma (Fecha de salida de la liquidación, monto de pago a transportista, penalidad).
Acceptance Scenarios:
1.	Scenario: Visualizar los detalles de una liquidación previamente calculada. 
o	Given el calculo de la liquidación.
o	When Se tenga una liquidación calcuada en el sistema.
o	Then El sistema permite visualizar en forma de lista los detalles de la liquidación.
2.	Scenario: buscar una liquidación especifica en la lista.
o	Given una lista de liquidaciones
o	When Se recibe la liquidación.
o	Then El sistema debe mostrar la liquidación deseada.
3.	Scenario: buscar una liquidación que no existente en la lista.
o	Given una lista de liquidaciones
o	When Se recibe la liquidación que no exista
o	Then El sistema debe mostrar un mensaje indicando que la liquidación es inexistente dentro del registro.
4. Scenario: buscar una liquidación, siendo transportista, de otro transportista.
o	Given una lista de liquidaciones.
o	When Se pide una liquidación no perteneciente al transportista que realiza la petición.
o	Then El sistema debe mostrar un mensaje indicando que la liquidación es inexistente para el transportista.
_______________________________________

Edge Cases
•	What happens when el sistema de almacenamiento no esta disponible?
•	How does system handle Debe rechazar cualquier búsqueda y emitir un mensaje sobre el estado del sistema.

Requirements (mandatory)
Functional Requirements
•	FR-001: System MUST Mostrar las liquidaciones almacenadas en forma de lista organizada.
•	FR-002: System MUST be able to buscar una liquidación especifica.
•	FR-003: Users MUST Mostrar un mensaje cuando se trate de buscar una lista no existente.
•	FR-004: Users MUST Mostrar un mensaje cuando se trate de buscar una lista que no pertence al transportista que realiza la petición.
Key Entities (include if feature involves data)
•	Liquidación : Representa una liuqidación del sistema. (idLiquidación, fechaCreacion, MontodePago, Penalidad).
Success Criteria (mandatory)
Measurable Outcomes
•	SC-001: El 100% de los eventos válidos de visualización de liquidación son registrados correctamente.
•	SC-002: El sistema Evita el 100% de los registros duplicados.

_______________________________________________________________________________________________________________

7) Feature Specification: Registro del estado del pago
Created: 21/02/2026
User Scenarios & Testing (mandatory)
Dado el registro del estado pago, el sistema debe permitir el ingreso de la estación en la que se encuentre el pago.


User Story 1 – Registrar estado del pago (Priority: P1)
Como miembro de la entidad financiera , quiero que el sistema me permita registrar el estado de un pago especifico.
Independent Test: Registrar el estado del pago, a partir de una lista de las liquidaciones previamente calculadas.
Why this priority: Permite mantener un registro constante del pago, sobre las liquidaciones que fueron previamente calculadas.
Acceptance Scenarios:
1.	Scenario: Registrar el estado inicial de un pago. 
o	Given la liquidación calculada.
o	When Se tenga una liquidación calcuada en el sistema.
o	Then El sistema permite registrar el estado del pago de esa liquidación.
2.	Scenario: Registrar el estado de pago de una liquidación previamente registrada.
o	Given una liquidación con registro de pago iniciado.
o	When Se recibe la instrucción de registro de pago.
o	Then El sistema debe rechazar esta intrucción.
3.	Scenario: Actualizar el estado de pago.
o	Given una liquidación con un registro de estado iniciado. 
o	When Se recibe la instrucción de actualización de pago. 
o	Then El sistema debe mostrar un mensaje indicando que la actualización fue un exito.
4. Scenario: Actualizar el estado de pago, para un pago ya realizado.
o	Given una liquidación que fue pagada.
o	When Se recibe la instrucción de actualización de pago.
o	Then El sistema debe mostrar un mensaje indicando que la liquidación ya fue pagada y no se puede realizar una actualización.
_______________________________________
Edge Cases
•	What happens when el sistema recibe la instrucción de registrar un estado una liquidación inexistente. 
•	How does system handle Debe rechazar cualquier registro y mostrar un mensaje de falta de liquidación.
• What happens when el sistema recibe la instrucción de actualización de estado sobre una liquidación paga. 
•	How does system handle Debe rechazar cualquier actualización y mostrar un mensaje donde se diga que el pago ya fue realizado.


Requirements (mandatory)
Functional Requirements
•	FR-001: System MUST Permitir el registro de estado de pago de una liquidación.
•	FR-002: System MUST Permitir la actualización del estado de pago de una liquidación.
•	FR-003: Users MUST Mostrar un mensaje cuando se trate de registrar un estado de pago ya existente.
•	FR-004: Users MUST Mostrar un mensaje cuando se trate de actualizar un estado de pago para una liquidación pagada.
Key Entities (include if feature involves data)
•	Pago : Representa el pago de una liquidación. (idPago, estado, idLiquidación).
Success Criteria (mandatory)
Measurable Outcomes
•	SC-001: El 100% de los eventos válidos de registro de pago son registrados correctamente.
•	SC-002: El sistema Evita el 100% de los registros duplicados.
•	SC-003: El 100% de los eventos válidos de actualización de pago son registrados correctamente.
•	SC-004: El sistema Evita el 100% de los actualizaciones solapadas.

_______________________________________________________________________________________________________________

8) Feature Specification: Visualizar liquidación
Created: 21/02/2026
User Scenarios & Testing (mandatory)
Dado la visualización de un cierre de rutas, el sistema debe de enseñar de la liquidación.


User Story 1 – Visualizar liquidación (Priority: P3)
Como transporador, quiero que el sistema me permita buscar una liquidación especifica, y visualizar el estado del pago de la misma para garantizar la trazabilidad de los datos financieros.
Independent Test: Consultar a partir de una lista las liquidaciones previamente calculadas y verificar que el sistema muestre correctamente de la misma el estado del pago.
Acceptance Scenarios:
1.	Scenario: Visualizar los detalles del estado de pago de una liquidación. 
o	Given liquidación calculada.
o	When Se tenga un registro de estado de pago para la liquidación.
o	Then El sistema permite visualizar el estado de pago de la liquidación.
2.	Scenario: buscar el estado de pago de una liquidación especifica en la lista.
o	Given una lista de liquidaciones.
o	When Se recibe la liquidación.
o	Then El sistema debe mostrar el estado de pago de la liquidación deseada.
3.	Scenario: buscar una liquidación que no existente en la lista.
o	Given una lista de liquidaciones.
o	When Se recibe la liquidación que no exista.
o	Then El sistema debe mostrar un mensaje indicando que la liquidación es inexistente dentro del registro.
4. Scenario: buscar una liquidación, siendo transportista, de otro transportista.
o	Given una lista de liquidaciones.
o	When Se pide una liquidación no perteneciente al transportista que realiza la petición.
o	Then El sistema debe mostrar un mensaje indicando que la liquidación es inexistente para el transportista.
5. Scenario: buscar una liquidación que no tiene estado de pago registrado.
o	Given una lista de liquidaciones.
o	When Se pide una liquidación que no tiene estado de pago registrado.
o	Then El sistema debe mostrar un mensaje indicando que la liquidación aún no ha iniciado su proceso de pago.

_______________________________________

Edge Cases
•	What happens when el sistema de almacenamiento no esta disponible?
•	How does system handle Debe rechazar cualquier búsqueda y emitir un mensaje sobre el estado del sistema.


Requirements (mandatory)
Functional Requirements
•	FR-001: System MUST Mostrar las liquidaciones con registro de estado de pago almacenadas en forma de lista organizada.
•	FR-002: System MUST be able to buscar una liquidación con registro de estado de pagoespecifica.
•	FR-003: Users MUST Mostrar un mensaje cuando se trate de buscar una liquidación no existente.
•	FR-004: Users MUST Mostrar un mensaje cuando se trate de buscar una liquidación que no pertence al transportista que realiza la petición.
Key Entities (include if feature involves data)
•	Pago : Representa el pago de una liquidación. (idPago, estado, idLiquidación).
Success Criteria (mandatory)
Measurable Outcomes
•	SC-001: El 100% de los eventos válidos de visualización de estado de pago son registrados correctamente.
•	SC-002: El sistema Evita el 100% de los registros duplicados.




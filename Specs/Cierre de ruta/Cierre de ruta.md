# Feature Specification: Cierre de ruta

**Created**: 21/02/2026  

## User Scenarios & Testing *(mandatory)*

Dado un evento de cierre de ruta enviado por el Módulo de Flotas y Rutas, 
el Módulo Financiero debe persistir la información detallada para habilitar el cálculo de pagos y descuentos al conductor.
 

### User Story 1 - Procesar Informe de Cierre para Liquidación (Priority: P1)

Como Módulo Financiero, quiero recibir el resumen detallado de la ruta (vehículo, conductor, contrato, paradas y motivos de falla) para calcular automáticamente la liquidación del transportista.

**Why this priority**: Es el insumo principal para el flujo de caja y pagos a terceros.

**Independent Test**: Enviar un evento de cierre de ruta desde el módulo de Rutas y Flotas y verificar que el sistema lo procese correctamente en la base de datos, dejándolo disponible para consulta.

**Acceptance Scenarios**:

1. **Scenario**: Registro exitoso de cierre detallado
   - **Given** Una ruta operada por un conductor con un modelo de contrato específico.
   - **When** Se recibe el evento   `RUTA_CERRADA` con el detalle de paradas.
   - **Then**  El sistema crea el registro de la ruta y desglosa las paradas (exitosas vs fallidas) para aplicar las reglas de negocio financieras.

2. **Scenario**: Gestión de Motivos de Falla.
   - **Given** Una parada marcada como "FALLIDA".
   - **When** El evento incluye el `motivo_falla`.
   - **Then** El sistema financiero determina si la falla es por el cliente o el trasportista.
  
 #### Clasificación de Fallos en Entrega (Novedades)

| Tipo de Fallo                  | Responsable                                                        | Ejemplos Comunes                                                                                               |
|:-------------------------------|:-------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------|
| **Fallo por el Cliente**       | Destinatario / Cliente final <br/> **% de pago entre 30% - 50%**   | Dirección incorrecta o incompleta, cliente ausente en el domicilio, rechaza recibir el paquete, local cerrado. |
| **Fallo por el Transportador** | Repartidor / Conductor<br/> **% de pago nulo sufre una penalidad** | Paquete dañado por mal manejo, no visitó la dirección reportada, pérdida del paquete, retraso injustificado.   |
| **Dañado en ruta**             | Repartidor / Conductor <br/>**% de pago nulo**                  | Daño de mercancia durante el envio, Daños por mala conservación                                                |

### Evento asíncrono con el modulo de rutas y flotas:
**Tipo:** Evento asíncrono (sin respuesta esperada)  
**Disparador:** Cierre de ruta (manual, automático o forzado por despachador)  
**Descripción:** El Módulo de facturación y liquidacíon recibe del Módulo de planificación de rutas el resumen completo de la ruta para que se calcule la liquidación del conductor.

```json
{
  "tipo_evento": "RUTA_CERRADA",
  "ruta_id": "UUID",
  "fecha_hora_inicio_transito": "2026-03-06T07:45:00",
  "fecha_hora_cierre": "2026-03-06T18:00:00",
  "conductor": {
  "conductor_id": "UUID",
  "nombre": "Juan Pérez",
  "modelo_contrato": "Recorrido completo | Por Parada Realizada"  
  },
  "vehiculo": {
    "vehiculo_id": "UUID",
    "tipo": "MOTO | VAN | NHR | TURBO"
  },
  "paradas": [
    {
    "parada_id": "UUID",
    "estado": "EXITOSA | FALLIDA",
    "motivo_no_entrega": "DIRECCIÓN_ERRONEA | CLIENTE_AUSENTE | RECHAZADO | ZONA DE DIFÍCIL ACESSO / ORDEN PÚBLICO"
    }
  ]
}
```
---

### Edge Cases



- What happens when el modelo de contrato es nulo o desconocido?
- How does system handle notificar al equipo financiero para revisión manual.
- What happens when el tipo de vehículo no existe en el catálogo financiero?
- How does system handle Se debe registrar el cierre pero marcar la ruta con alerta de "Tarifa de vehiculo no encontrada".
## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST procesar eventos asíncronos de cierre de ruta e identificar el modelo de contrato del conductor
- **FR-002**: System MUST clasificar las paradas fallidas según el motivo para determinar si generan cobro o penalidad.
- **FR-003**: System MUST asegurar que el `idVehiculo` y su `tipo` correspondan a la tabla de tarifas vigente
- **FR-004**: System MUST ignorar los duplicados basándose en el ruta_id.



### Key Entities 

- **[Ruta]**: Representa una ruta operativa del sistema. (idRuta, idTransportista, tipoVehiculo, modeloContrato).
- **[Parada]**:  Representa los diferentes puntos donde se para el vehiculo para entregar un paquete. (idParada, Estado, motivoFalla).
- **[transportista]:** Representa el transportista que realiza la ruta. (idTransportista, nombre)
## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de las paradas fallidas deben tener un motivo asociado para ser procesadas por el motor de pagos.
- **SC-002**: El sistema debe procesar el evento y dejarlo listo para consulta en menos de 5 segundos tras la recepción.
- **SC-003**: El total de paradas (Exitosas + Fallidas) debe coincidir con el resumen enviado en el evento.

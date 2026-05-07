# Frontend Spec: Registrar estado del pago

**Created**: 2026-05-07  
**Spec base**: [Registrar estado del pago.md](./Registrar%20estado%20del%20pago.md)  
**Plan backend base**: [plan-registro-estado-pago.md](./plan-registro-estado-pago.md)

---

## 1. Contexto de usuario

El frontend de esta feature tiene como responsabilidad principal **consultar y mostrar el estado del pago asociado a una liquidación previamente calculada**.

El registro y actualización del estado de pago **no son acciones manuales del usuario desde React**. Según el flujo definido para la feature, el estado del pago se registra y actualiza automáticamente cuando el backend recibe eventos asíncronos provenientes de la entidad financiera.

Por tanto, la UI no debe tener un botón para registrar, actualizar, sincronizar o confirmar manualmente el pago. El frontend únicamente refleja el estado actual que devuelve el backend y permite visualizar la trazabilidad de los eventos recibidos.

---

## 2. Roles y acceso

| Pantalla | ROLE_ADMIN | ROLE_GESTOR_TARIFAS | ROLE_TRANSPORTISTA |
|:---------|:----------:|:-------------------:|:------------------:|
| Consultar estado actual del pago | Sí | Sí | Sí, solo sus pagos |
| Consultar eventos de transacción | Sí | Sí | No |
| Ver errores funcionales del procesamiento | Sí | Sí | Solo mensaje general |
| Registrar estado de pago manualmente | No | No | No |
| Actualizar estado de pago manualmente | No | No | No |

---

## 3. Flujos de usuario

### Flujo A — Usuario consulta el estado actual del pago

```text
[Inicio] → /pagos/:idPago/estado
  ├── Estado PENDIENTE → Muestra pago pendiente de procesamiento
  ├── Estado EN_PROCESO → Muestra pago en proceso por la entidad financiera
  ├── Estado PAGADO → Muestra pago finalizado exitosamente
  ├── Estado RECHAZADO → Muestra pago rechazado
  ├── Pago no encontrado → Muestra mensaje "No se encontró el pago indicado"
  └── Error de red → Muestra mensaje de error general
```

### Flujo B — Administrador o gestor consulta trazabilidad de eventos

```text
[Inicio] → /pagos/:idPago/eventos
  ├── Carga lista de eventos recibidos desde la entidad financiera
  ├── Muestra estado de procesamiento de cada evento
  ├── Muestra eventos duplicados, rechazados o con error
  └── Permite regresar al estado actual del pago
```

### Flujo C — Consulta de estado de pago desde una liquidación

```text
[Inicio] → /liquidaciones/:idLiquidacion/pago
  ├── Consulta el estado de pago asociado a la liquidación
  ├── Si existe pago → redirige o muestra resumen de pago
  ├── Si no existe pago → muestra "Esta liquidación aún no tiene pago asociado"
  └── Si el usuario es administrador o gestor → muestra acceso a eventos de transacción
```

---

## 4. Pantallas

### 4.1 Pantalla: Estado actual del pago (`/pagos/:idPago/estado`)

**Descripción**: Vista principal para consultar el estado actual del pago asociado a una liquidación.

**Estados de pantalla:**

| Estado de UI | Qué muestra |
|:-------------|:------------|
| Cargando | Skeleton loader en tarjeta de resumen |
| PENDIENTE | Mensaje: "El pago está pendiente de iniciar procesamiento." |
| EN_PROCESO | Mensaje: "El pago está siendo procesado por la entidad financiera." |
| PAGADO | Mensaje: "El pago fue completado exitosamente." |
| RECHAZADO | Mensaje: "El pago fue rechazado por la entidad financiera." |
| 404 | Mensaje: "No se encontró el pago indicado." |
| Error de red | Mensaje: "No fue posible consultar el estado del pago. Intenta nuevamente." |

**Tarjeta de resumen muestra:**

- ID Pago
- ID Liquidación
- Estado actual del pago
- Fecha de última actualización
- Última secuencia procesada
- Badge visual del estado

**Acciones disponibles:**

- Botón "Ver eventos de transacción" solo para `ROLE_ADMIN` y `ROLE_GESTOR_TARIFAS`
- Botón "Volver a liquidación" si la navegación proviene desde una liquidación

**Acciones prohibidas:**

- No debe existir botón "Registrar pago"
- No debe existir botón "Actualizar estado"
- No debe existir botón "Confirmar pago"
- No debe existir formulario para modificar manualmente el estado

---

### 4.2 Pantalla: Eventos de transacción (`/pagos/:idPago/eventos`)

**Descripción**: Vista administrativa para consultar la trazabilidad de los eventos asíncronos recibidos desde la entidad financiera.

**Estados de pantalla:**

| Estado de UI | Qué muestra |
|:-------------|:------------|
| Cargando | Skeleton loader en tabla |
| Con eventos | Tabla de eventos recibidos |
| Sin eventos | Mensaje: "Este pago aún no tiene eventos registrados." |
| 403 | Redirección a `/403` |
| 404 | Mensaje: "No se encontró el pago indicado." |
| Error de red | Mensaje: "No fue posible cargar los eventos del pago." |

**Tabla de eventos muestra:**

- ID Evento
- ID Transacción Banco
- Estado solicitado
- Estado de procesamiento
- Fecha de recepción
- Fecha del evento banco
- Secuencia
- Mensaje de error, si existe

**Estados de procesamiento esperados:**

- `RECIBIDO`
- `PROCESADO`
- `DUPLICADO`
- `RECHAZADO`
- `ERROR`

**Reglas visuales:**

- Un evento `PROCESADO` se muestra como exitoso.
- Un evento `DUPLICADO` se muestra como idempotente, no como fallo crítico.
- Un evento `RECHAZADO` se muestra como advertencia.
- Un evento `ERROR` se muestra como error funcional o técnico.
- El transportista no puede acceder a esta pantalla.

---

### 4.3 Pantalla: Estado de pago desde liquidación (`/liquidaciones/:idLiquidacion/pago`)

**Descripción**: Vista puente para consultar el pago asociado a una liquidación previamente calculada.

**Estados de pantalla:**

| Estado de UI | Qué muestra |
|:-------------|:------------|
| Cargando | Skeleton loader |
| Pago asociado | Resumen del estado del pago |
| Sin pago asociado | Mensaje: "Esta liquidación aún no tiene pago asociado." |
| Liquidación no encontrada | Mensaje: "No se encontró la liquidación indicada." |
| Error de red | Mensaje: "No fue posible consultar el pago de la liquidación." |

Esta vista depende de que el backend exponga el endpoint complementario de consulta por liquidación. Si el endpoint no existe, la ruta debe quedar bloqueada hasta coordinación con backend.

---

## 5. Mensajes y feedback

| Situación | Tipo | Mensaje |
|:----------|:-----|:--------|
| Pago pendiente | Inline | "El pago está pendiente de iniciar procesamiento." |
| Pago en proceso | Inline | "El pago está siendo procesado por la entidad financiera." |
| Pago pagado | Inline | "El pago fue completado exitosamente." |
| Pago rechazado | Inline | "El pago fue rechazado por la entidad financiera." |
| Pago no encontrado | Inline | "No se encontró el pago indicado." |
| Liquidación sin pago asociado | Inline | "Esta liquidación aún no tiene pago asociado." |
| Eventos no encontrados | Inline | "Este pago aún no tiene eventos registrados." |
| Error 403 | Redirect | Redirige a `/403` |
| Error 404 | Inline | "No se encontró el pago indicado." |
| Error 503 | Toast | "El servicio no está disponible. Intenta nuevamente en unos momentos." |
| Error de red | Toast | "No fue posible comunicarse con el servidor." |
| Evento duplicado | Badge / texto | "Evento duplicado: ya fue recibido previamente." |
| Evento desordenado | Badge / texto | "Evento rechazado por secuencia o fecha no válida." |
| Estado inválido | Badge / texto | "Evento rechazado por estado de pago no soportado." |

---

## 6. Criterios de aceptación frontend

Mapeados a los criterios de éxito del spec:

| SC del Spec | Criterio frontend |
|:------------|:------------------|
| SC-001 | Los eventos válidos procesados por backend se reflejan correctamente en la UI cuando el usuario consulta el estado del pago. |
| SC-002 | Los eventos duplicados se muestran como duplicados o idempotentes, sin sugerir al usuario que se creó otro pago. |
| SC-003 | El cambio de estado devuelto por backend se muestra exactamente como fuente de verdad. |
| SC-004 | La tabla de eventos permite identificar eventos desordenados, rechazados o con error según timestamp o secuencia procesada. |
| — | El frontend no contiene formularios ni botones para registrar o actualizar manualmente el estado del pago. |
| — | La trazabilidad de eventos solo está disponible para `ROLE_ADMIN` y `ROLE_GESTOR_TARIFAS`. |
| — | El transportista solo puede consultar el estado del pago que le corresponde. |

---

## 7. Restricciones de diseño

- **No existe botón "Registrar estado de pago"** en ninguna pantalla.
- **No existe botón "Actualizar estado de pago"** en ninguna pantalla.
- **No existe formulario manual de pago** para usuarios internos.
- El frontend no simula ni envía eventos del banco.
- El webhook pertenece al backend y a la entidad financiera, no a la UI.
- El backend es la única fuente de verdad para estado, idempotencia, eventos duplicados, eventos desordenados y transiciones inválidas.
- La UI solo formatea y presenta los datos devueltos por el backend.
- La tabla de eventos es de solo lectura.
- Los errores técnicos deben mostrarse con mensajes entendibles, sin exponer stack traces ni detalles internos.

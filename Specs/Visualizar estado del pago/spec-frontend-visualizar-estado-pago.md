# Frontend Spec: Visualizar estado del pago

**Created**: 2026-05-07  
**Spec base**: [Visualizar estado del pago.md](./Visualizar%20estado%20del%20pago.md)  
**Plan backend base**: [plan-visualizar-estado-pago.md](./plan-visualizar-estado-pago.md)

---

## 1. Contexto de usuario

El frontend de esta feature permite que un usuario autorizado consulte el estado actual de sus pagos asociados a liquidaciones previamente calculadas.

La funcionalidad es exclusivamente de lectura. El usuario no registra pagos, no modifica estados financieros y no fuerza cambios de estado desde la UI. El frontend muestra la información persistida y validada por backend: estado del pago, liquidación asociada, motivo de rechazo cuando aplique, detalle financiero, ajustes o penalizaciones y comprobante descargable.

El acceso debe estar restringido al propietario del pago o a usuarios internos autorizados. Si un usuario intenta visualizar un pago que no le pertenece, la UI debe bloquear la visualización con base en la respuesta del backend.

---

## 2. Roles y acceso

| Pantalla | ROLE_TRANSPORTISTA | ROLE_ADMIN | ROLE_GESTOR_TARIFAS |
|:---------|:------------------:|:----------:|:-------------------:|
| Listado de pagos propios | Sí | Sí | Sí |
| Consultar estado de un pago propio | Sí | Sí | Sí |
| Consultar detalle de un pago propio | Sí | Sí | Sí |
| Descargar comprobante propio | Sí | Sí | Sí |
| Consultar pagos de otros usuarios | No | Sí, si backend lo permite | Sí, si backend lo permite |
| Modificar estado del pago | No | No | No |

---

## 3. Flujos de usuario

### Flujo A — Usuario consulta listado de pagos

```text
[Inicio] → /pagos
  ├── Carga pagos visibles para el usuario autenticado
  ├── Muestra ID Pago, ID Liquidación, fecha, monto y estado
  ├── Permite filtrar o buscar dentro de los pagos disponibles
  └── Usuario selecciona un pago → /pagos/:id
```

### Flujo B — Usuario consulta estado de un pago

```text
[Inicio] → /pagos/:id
  ├── Estado PAGADO → Muestra estado "Pagado" y liquidación asociada
  ├── Estado PENDIENTE → Muestra estado "Pendiente"
  ├── Estado EN_PROCESO → Muestra estado "En proceso"
  ├── Estado RECHAZADO → Muestra estado "Rechazado" y motivo del rechazo
  ├── Pago no encontrado → Muestra mensaje de pago no encontrado
  └── Acceso no autorizado → Muestra mensaje de acceso restringido o redirige a /403
```

### Flujo C — Usuario consulta detalle y descarga comprobante

```text
[Inicio] → /pagos/:id/detalle
  ├── Muestra MontoBase, MontoNeto, fecha, ajustes/penalidades, estado, idRuta e idLiquidación
  ├── Usuario presiona "Descargar comprobante"
  │   ├── Éxito → descarga PDF generado por backend
  │   ├── 403 → muestra mensaje de acceso restringido
  │   ├── 404 → muestra mensaje de pago no encontrado
  │   └── 503 → muestra mensaje de servicio no disponible
  └── Usuario puede volver al listado o al estado del pago
```

---

## 4. Pantallas

### 4.1 Pantalla: Listado de pagos (`/pagos`)

**Descripción**: Vista donde el usuario consulta los pagos asociados a su cuenta o los pagos autorizados según su rol.

**Estados de pantalla:**

| Estado | Qué muestra la UI |
|:-------|:------------------|
| Cargando | Skeleton loader en tabla o tarjetas |
| Con pagos | Tabla/listado de pagos |
| Sin pagos | Mensaje: "No tienes pagos registrados." |
| Error de red | Mensaje: "No fue posible cargar los pagos. Intenta nuevamente." |
| Servicio no disponible | Toast: "El servicio no está disponible. Intenta nuevamente en unos momentos." |

**Tabla de pagos muestra:**

- ID Pago
- ID Liquidación
- Fecha del pago
- Monto
- Estado del pago
- Acción "Ver detalle"

**Filtros básicos:**

- Búsqueda por ID Pago
- Búsqueda por ID Liquidación
- Filtro por estado: `PENDIENTE`, `EN_PROCESO`, `PAGADO`, `RECHAZADO`

---

### 4.2 Pantalla: Estado del pago (`/pagos/:id`)

**Descripción**: Vista principal para consultar el estado actual de un pago específico.

**Estados de pantalla:**

| Estado de pago | Qué muestra la UI |
|:---------------|:------------------|
| PAGADO | Mensaje "Pagado", liquidación asociada y acceso al detalle |
| PENDIENTE | Mensaje "Pendiente" y explicación de que aún no ha sido procesado |
| EN_PROCESO | Mensaje "En proceso" y explicación de que está siendo gestionado |
| RECHAZADO | Mensaje "Rechazado" y motivo del rechazo |
| 404 | Mensaje: "No se encontró información del pago." |
| 403 | Mensaje: "No tienes permisos para visualizar este pago." o redirección a `/403` |
| 503 | Toast: "El servicio no está disponible. Intenta nuevamente en unos momentos." |

**Tarjeta de estado muestra:**

- ID Pago
- ID Liquidación
- Estado actual
- Fecha de pago o última actualización
- Monto del pago
- Motivo de rechazo, si aplica
- Liquidación asociada, si aplica

**Acciones disponibles:**

- Botón "Ver detalle"
- Botón "Volver al listado"

**Acciones prohibidas:**

- No existe botón "Actualizar estado"
- No existe botón "Confirmar pago"
- No existe formulario de edición

---

### 4.3 Pantalla: Detalle del pago (`/pagos/:id/detalle`)

**Descripción**: Vista detallada para comprender los valores, fechas, ajustes y penalizaciones aplicadas al pago.

**Estados de pantalla:**

| Estado | Qué muestra la UI |
|:-------|:------------------|
| Cargando | Skeleton loader en tarjeta de resumen y tabla de ajustes |
| Con detalle | Resumen financiero + tabla de ajustes/penalidades + botón de comprobante |
| Sin ajustes | Mensaje: "Este pago no tiene ajustes o penalidades aplicadas." |
| 404 | Mensaje: "No se encontró información del pago." |
| 403 | Mensaje: "No tienes permisos para visualizar este pago." o redirección a `/403` |
| Error de red | Toast: "No fue posible cargar el detalle del pago." |

**Resumen financiero muestra:**

- ID Pago
- ID Ruta
- ID Liquidación
- Estado del pago
- Fecha
- MontoBase
- MontoNeto

**Tabla de ajustes/penalidades muestra:**

- ID Ajuste/Penalidad, si backend lo expone
- Tipo de ajuste
- Monto, si backend lo expone
- Descripción o motivo, si backend lo expone

**Botón de descarga:**

- Texto: "Descargar comprobante"
- Llama al endpoint de descarga de PDF
- Muestra spinner mientras descarga
- Deshabilita doble clic durante la descarga
- Muestra error funcional si falla

---

## 5. Mensajes y feedback

| Situación | Tipo | Mensaje |
|:----------|:-----|:--------|
| Pago pagado | Inline | "El pago fue procesado correctamente." |
| Pago pendiente | Inline | "El pago se encuentra pendiente de procesamiento." |
| Pago en proceso | Inline | "El pago se encuentra en proceso." |
| Pago rechazado | Inline | "El pago fue rechazado. Motivo: {motivoRechazo}." |
| Pago no encontrado | Inline | "No se encontró información del pago." |
| Acceso no autorizado | Inline / Redirect | "No tienes permisos para visualizar este pago." |
| Sin pagos | Empty state | "No tienes pagos registrados." |
| Sin ajustes | Empty state | "Este pago no tiene ajustes o penalidades aplicadas." |
| Descarga exitosa | Toast | "Comprobante descargado correctamente." |
| Error al descargar | Toast | "No fue posible descargar el comprobante." |
| Error 503 | Toast | "El servicio no está disponible. Intenta nuevamente en unos momentos." |
| Error de red | Toast | "No fue posible comunicarse con el servidor." |

---

## 6. Criterios de aceptación frontend

Mapeados al spec:

| SC del Spec | Criterio frontend |
|:------------|:------------------|
| SC-001 | La UI refleja el estado del pago devuelto por backend sin calcularlo ni inferirlo localmente. |
| SC-002 | El 100% de las consultas válidas muestran estado, monto, fecha e identificadores asociados correctamente. |
| FR-001 | El usuario puede consultar el estado actual de un pago desde `/pagos/:id`. |
| FR-002 | La UI muestra correctamente los estados válidos `PAGADO`, `PENDIENTE`, `EN_PROCESO` y `RECHAZADO`. |
| FR-003 | El usuario puede visualizar el detalle completo del pago. |
| FR-005 | La UI bloquea o redirige cuando backend responde que el pago no pertenece al usuario. |
| FR-006 | El usuario puede descargar el comprobante PDF generado por backend. |
| — | No existe ninguna acción frontend para modificar el estado del pago. |

---

## 7. Restricciones de diseño

- El frontend no modifica estados de pago.
- El frontend no genera el comprobante PDF; solo descarga el archivo generado por backend.
- El frontend no decide si un usuario puede ver un pago; debe respetar la autorización del backend.
- Los montos se muestran en formato de moneda local, sin recalcular valores.
- El motivo de rechazo solo se muestra si el backend lo devuelve.
- La información de liquidación asociada se muestra únicamente si backend la incluye o la permite consultar.
- Los errores internos no deben exponerse al usuario.
- El intento de acceso no autorizado lo registra backend; frontend solo muestra el bloqueo correspondiente.

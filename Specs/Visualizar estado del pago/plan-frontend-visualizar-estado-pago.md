# Plan Técnico Frontend: Visualizar estado del pago

**Fecha**: 2026-05-07  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Visualizar-Estado-Pago`  
**Plan backend base**: [plan-visualizar-estado-pago.md](./plan-visualizar-estado-pago.md)  
**Spec frontend**: [spec-frontend-visualizar-estado-pago.md](./spec-frontend-visualizar-estado-pago.md)  
**Frontend objetivo**: Listado de pagos, consulta de estado, detalle financiero y descarga de comprobante PDF

---

## 1. Resumen

Este plan define el frontend asociado a la visualización del estado del pago.

La funcionalidad es de consulta. React no crea pagos, no modifica estados y no ejecuta procesos financieros. La UI consume endpoints de lectura para mostrar el estado actual del pago, el detalle financiero y permitir la descarga del comprobante generado por backend.

La seguridad depende del backend. La UI debe respetar respuestas `403 Forbidden` cuando un usuario intente consultar un pago que no le pertenece y mostrar mensajes funcionales sin exponer detalles técnicos.

---

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool**: Vite  
**Cliente HTTP**: Axios  
**Formularios**: No aplica para edición; solo búsqueda/filtros de consulta  
**Validación cliente**: Validación de parámetros de ruta, filtros y manejo de errores HTTP  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT  
**Roles esperados**: `ROLE_TRANSPORTISTA`, `ROLE_ADMIN`, `ROLE_GESTOR_TARIFAS`

---

## 3. Endpoints backend consumidos

### 3.1 Listar pagos visibles por usuario autenticado

```http
GET /api/pagos
```

Uso: mostrar el listado de pagos que el usuario autenticado puede consultar.

Parámetros opcionales sugeridos:

```http
GET /api/pagos?estado=PAGADO&page=0&size=10
```

### 3.2 Consultar estado actual de un pago

```http
GET /api/pagos/{id}
```

Uso: mostrar el estado actual del pago, liquidación asociada cuando aplique y motivo de rechazo cuando aplique.

### 3.3 Consultar detalle completo del pago

```http
GET /api/pagos/{id}/detalle
```

Uso: mostrar MontoBase, MontoNeto, fecha, ajustes/penalidades, estado, IDRuta e IDLiquidación.

### 3.4 Descargar comprobante del pago

```http
GET /api/pagos/{id}/comprobante
```

Uso: descargar PDF generado por backend.

La llamada debe solicitar respuesta binaria:

```js
responseType: 'blob'
```

---

## 4. Contratos de datos esperados

### 4.1 Response de listado de pagos

```json
[
  {
    "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
    "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
    "fecha": "2026-04-26T10:35:00",
    "monto": 230000,
    "estado": "PAGADO"
  }
]
```

### 4.2 Response de estado del pago

```json
{
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "RECHAZADO",
  "fecha": "2026-04-26T10:35:00",
  "monto": 230000,
  "motivoRechazo": "Cuenta bancaria inválida"
}
```

Valores esperados para `estado`:

```text
PAGADO
PENDIENTE
EN_PROCESO
RECHAZADO
```

### 4.3 Response de detalle del pago

```json
{
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idRuta": "7e46a0df-9e2a-42c3-9fd1-2d2d4aa9d999",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "PAGADO",
  "montoBase": 250000,
  "montoNeto": 230000,
  "fecha": "2026-04-26T10:35:00",
  "ajustesPenalidades": [
    {
      "idAjuste": "uuid",
      "tipoAjuste": "PENALIDAD",
      "monto": 20000,
      "descripcion": "Paquete con novedad"
    }
  ]
}
```

### 4.4 Response de comprobante

```text
Content-Type: application/pdf
```

La UI debe descargarlo como archivo PDF.

Nombre sugerido del archivo:

```text
comprobante-pago-{idPago}.pdf
```

---

## 5. Estructura de archivos

```text
frontend/src/
├── modules/
│   └── pagos/
│       ├── components/
│       │   ├── PagosTable.jsx                  # Tabla/listado de pagos visibles
│       │   ├── PagoEstadoCard.jsx              # Tarjeta de estado actual
│       │   ├── PagoEstadoBadge.jsx             # Badge visual del estado
│       │   ├── PagoDetalleCard.jsx             # Resumen financiero del pago
│       │   ├── AjustesPenalidadesTable.jsx     # Tabla de ajustes y penalidades
│       │   ├── DescargarComprobanteButton.jsx  # Botón con manejo de blob PDF
│       │   ├── PagoFilters.jsx                 # Filtros por estado o búsqueda
│       │   ├── PagoEmptyState.jsx              # Estados vacíos
│       │   └── PagoErrorState.jsx              # Errores funcionales
│       │
│       ├── pages/
│       │   ├── PagosListPage.jsx               # /pagos
│       │   ├── PagoEstadoPage.jsx              # /pagos/:id
│       │   └── PagoDetallePage.jsx             # /pagos/:id/detalle
│       │
│       ├── services/
│       │   └── pagoVisualizacionService.js     # Llamadas HTTP de consulta y descarga
│       │
│       ├── hooks/
│       │   ├── usePagosList.js                 # Carga del listado
│       │   ├── usePagoEstado.js                # Carga del estado
│       │   ├── usePagoDetalle.js               # Carga del detalle
│       │   └── useDescargarComprobante.js      # Descarga binaria del PDF
│       │
│       └── utils/
│           ├── pagoEstadoLabels.js             # Labels legibles para estados
│           ├── formatMoney.js                  # Formato visual de moneda
│           └── downloadBlob.js                 # Utilidad para descarga de archivo
│
└── shared/
    ├── components/                             # Botones, badges, skeletons, alerts
    └── services/
        └── apiClient.js                        # Axios con interceptores JWT
```

---

## 6. Criterios de éxito frontend

Mapeados al spec:

| SC / FR | Criterio frontend |
|:--------|:------------------|
| SC-001 | La UI refleja el estado devuelto por backend sin recalcular ni modificar datos. |
| SC-002 | Las consultas válidas muestran correctamente estado, fecha, monto e identificadores. |
| FR-001 | El usuario puede consultar el estado actual de un pago. |
| FR-002 | La UI soporta visualmente `PAGADO`, `PENDIENTE`, `EN_PROCESO` y `RECHAZADO`. |
| FR-003 | El usuario puede visualizar el detalle financiero completo del pago. |
| FR-005 | La UI bloquea visualización cuando backend retorna 403 por pago ajeno. |
| FR-006 | El usuario puede descargar el comprobante PDF generado por backend. |
| — | No existen acciones frontend para modificar estados de pago. |

---

## Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/pagos` con estructura de carpetas `components`, `pages`, `services`, `hooks` y `utils`.
- [ ] F002 Registrar ruta `/pagos` en el router global para usuarios autenticados.
- [ ] F003 Registrar ruta `/pagos/:id` para consultar estado de pago específico.
- [ ] F004 Registrar ruta `/pagos/:id/detalle` para consultar detalle del pago.
- [ ] F005 Crear `pagoVisualizacionService.js` con métodos:
  - `getPagos(params)`
  - `getPagoEstado(idPago)`
  - `getPagoDetalle(idPago)`
  - `downloadComprobante(idPago)`
- [ ] F006 Configurar `downloadComprobante(idPago)` con `responseType: 'blob'`.
- [ ] F007 Crear `pagoEstadoLabels.js` para centralizar etiquetas de estados.
- [ ] F008 Crear `formatMoney.js` para formatear montos en moneda local sin alterar el valor.
- [ ] F009 Crear `downloadBlob.js` para descarga controlada de archivos PDF.
- [ ] F010 Verificar que no exista ningún método en services para modificar estados de pago.

---

## Fase 2: Listado de pagos

- [ ] F011 Implementar `usePagosList.js`: llama a `getPagos(params)` y expone `{ pagos, isLoading, error, refetch }`.
- [ ] F012 Implementar `PagosListPage.jsx`:
  - Carga los pagos visibles para el usuario autenticado.
  - Muestra skeleton loader durante la carga.
  - Muestra tabla de pagos si existen registros.
  - Muestra empty state si no hay pagos.
  - Maneja errores de red y servicio no disponible.
- [ ] F013 Implementar `PagosTable.jsx` con columnas:
  - ID Pago
  - ID Liquidación
  - Fecha
  - Monto
  - Estado
  - Acción "Ver detalle"
- [ ] F014 Implementar `PagoFilters.jsx` con:
  - búsqueda por ID Pago
  - búsqueda por ID Liquidación
  - filtro por estado
- [ ] F015 Integrar `PagoEstadoBadge.jsx` en la tabla.
- [ ] F016 Confirmar que la tabla no muestra acciones de edición, actualización o confirmación de pago.

---

## Fase 3: Consulta del estado actual del pago

- [ ] F017 Implementar `usePagoEstado.js`: llama a `getPagoEstado(idPago)` y expone `{ data, isLoading, error }`.
- [ ] F018 Implementar `PagoEstadoPage.jsx`:
  - Lee `id` desde parámetros de ruta.
  - Carga estado con `usePagoEstado`.
  - Muestra skeleton loader durante la carga.
  - Renderiza `PagoEstadoCard` cuando existe data.
  - Muestra mensaje 404 cuando no existe información del pago.
  - Muestra mensaje 403 cuando el usuario no tiene permisos.
  - Muestra toast o error inline para fallas 503.
- [ ] F019 Implementar `PagoEstadoCard.jsx` mostrando:
  - ID Pago
  - ID Liquidación
  - Estado actual
  - Fecha
  - Monto
  - Motivo de rechazo si estado es `RECHAZADO`
- [ ] F020 Implementar `PagoEstadoBadge.jsx` con soporte para:
  - `PAGADO`
  - `PENDIENTE`
  - `EN_PROCESO`
  - `RECHAZADO`
- [ ] F021 Mostrar liquidación asociada cuando el estado sea `PAGADO` y backend la incluya.
- [ ] F022 Mostrar motivo de rechazo cuando el estado sea `RECHAZADO` y backend lo incluya.
- [ ] F023 Agregar botón "Ver detalle" que navegue a `/pagos/:id/detalle`.
- [ ] F024 Confirmar que no exista botón "Actualizar estado" o "Confirmar pago".

---

## Fase 4: Detalle financiero del pago

- [ ] F025 Implementar `usePagoDetalle.js`: llama a `getPagoDetalle(idPago)` y expone `{ data, isLoading, error }`.
- [ ] F026 Implementar `PagoDetallePage.jsx`:
  - Lee `id` desde parámetros de ruta.
  - Carga detalle con `usePagoDetalle`.
  - Muestra skeleton loader durante la carga.
  - Renderiza `PagoDetalleCard`.
  - Renderiza `AjustesPenalidadesTable`.
  - Muestra botón de descarga de comprobante.
  - Maneja 403, 404 y 503.
- [ ] F027 Implementar `PagoDetalleCard.jsx` mostrando:
  - ID Pago
  - ID Ruta
  - ID Liquidación
  - Estado
  - Fecha
  - MontoBase
  - MontoNeto
- [ ] F028 Implementar `AjustesPenalidadesTable.jsx` con columnas:
  - ID Ajuste/Penalidad, si existe
  - Tipo de ajuste
  - Monto
  - Descripción o motivo
- [ ] F029 Mostrar mensaje "Este pago no tiene ajustes o penalidades aplicadas." si el array viene vacío.
- [ ] F030 Formatear montos solo visualmente; no redondear ni recalcular.

---

## Fase 5: Descarga del comprobante PDF

- [ ] F031 Implementar `useDescargarComprobante.js`:
  - llama a `downloadComprobante(idPago)`
  - expone `{ descargar, isLoading, error }`
  - maneja respuesta tipo blob
- [ ] F032 Implementar `DescargarComprobanteButton.jsx`:
  - Muestra botón "Descargar comprobante"
  - Deshabilita el botón durante descarga
  - Muestra spinner mientras descarga
  - Ejecuta `downloadBlob`
- [ ] F033 Implementar nombre de archivo:
  - `comprobante-pago-{idPago}.pdf`
- [ ] F034 Mostrar toast "Comprobante descargado correctamente." cuando la descarga sea exitosa.
- [ ] F035 Mostrar toast "No fue posible descargar el comprobante." cuando falle.
- [ ] F036 Manejar 403: mostrar "No tienes permisos para descargar este comprobante."
- [ ] F037 Manejar 404: mostrar "No se encontró información del pago."
- [ ] F038 Manejar 503: mostrar "El servicio no está disponible. Intenta nuevamente en unos momentos."

---

## Fase 6: Estados y errores globales

- [ ] F039 Manejar HTTP 400: mostrar "La solicitud no es válida."
- [ ] F040 Manejar HTTP 403: mostrar "No tienes permisos para visualizar este pago." o redirigir a `/403`.
- [ ] F041 Manejar HTTP 404: mostrar "No se encontró información del pago."
- [ ] F042 Manejar HTTP 503: toast "El servicio no está disponible. Intenta nuevamente en unos momentos."
- [ ] F043 Manejar errores de red: toast "No fue posible comunicarse con el servidor."
- [ ] F044 Implementar skeleton loaders en:
  - listado de pagos
  - tarjeta de estado
  - detalle financiero
  - tabla de ajustes/penalidades
- [ ] F045 Evitar exponer nombres de excepciones, stack traces o errores técnicos internos.
- [ ] F046 Mantener mensajes diferenciados para pago inexistente, pago ajeno y servicio no disponible.

---

## Fase 7: Seguridad y restricciones UI

- [ ] F047 Verificar que el listado muestre únicamente lo que backend retorna para el usuario autenticado.
- [ ] F048 Verificar que un `403` impida renderizar datos parciales del pago.
- [ ] F049 Verificar que no exista ningún formulario de edición del estado del pago.
- [ ] F050 Verificar que no exista botón "Actualizar estado".
- [ ] F051 Verificar que no exista botón "Confirmar pago".
- [ ] F052 Verificar que la descarga de comprobante también respete errores 403.
- [ ] F053 Verificar que los montos mostrados coincidan exactamente con los devueltos por backend.
- [ ] F054 Verificar que el frontend no construya estados derivados a partir de reglas propias.

---

## Fase 8: Pruebas frontend

- [ ] F055 Test: `PagosListPage` muestra skeleton loader mientras carga.
- [ ] F056 Test: `PagosListPage` muestra tabla cuando existen pagos.
- [ ] F057 Test: `PagosListPage` muestra empty state cuando no hay pagos.
- [ ] F058 Test: `PagosTable` muestra ID Pago, ID Liquidación, fecha, monto y estado.
- [ ] F059 Test: filtro por estado actualiza la consulta o filtra correctamente según implementación.
- [ ] F060 Test: `PagoEstadoPage` muestra estado `PAGADO` y liquidación asociada cuando backend lo devuelve.
- [ ] F061 Test: `PagoEstadoPage` muestra estado `PENDIENTE`.
- [ ] F062 Test: `PagoEstadoPage` muestra estado `EN_PROCESO`.
- [ ] F063 Test: `PagoEstadoPage` muestra estado `RECHAZADO` con motivo de rechazo.
- [ ] F064 Test: `PagoEstadoPage` muestra mensaje de pago no encontrado ante 404.
- [ ] F065 Test: `PagoEstadoPage` muestra bloqueo ante 403.
- [ ] F066 Test: `PagoDetallePage` muestra MontoBase, MontoNeto, fecha, ajustes/penalidades y estado.
- [ ] F067 Test: `AjustesPenalidadesTable` muestra mensaje vacío cuando no hay ajustes.
- [ ] F068 Test: `DescargarComprobanteButton` solicita blob PDF.
- [ ] F069 Test: descarga exitosa muestra toast correcto.
- [ ] F070 Test: error de descarga 403 muestra mensaje de permisos.
- [ ] F071 Test: error de descarga 404 muestra mensaje de pago no encontrado.
- [ ] F072 Test: errores 503 muestran mensaje de servicio no disponible.
- [ ] F073 Test: no existe botón "Actualizar estado" en ninguna vista.
- [ ] F074 Test: no existe botón "Confirmar pago" en ninguna vista.
- [ ] F075 Test: no existe formulario para modificar estado del pago.
- [ ] F076 Test: los montos no son recalculados ni redondeados por la UI.

---

## 8. Dependencias y orden de ejecución

1. **Fase 1 primero**: estructura del módulo, rutas, servicio y utilidades deben existir antes de crear componentes.
2. **Fase 2 antes que Fase 3**: el listado permite navegar a la consulta de estado.
3. **Fase 3 antes que Fase 4**: el estado del pago es la base para acceder al detalle.
4. **Fase 4 antes que Fase 5**: la descarga del comprobante vive dentro del detalle.
5. **Fase 6 aplica transversalmente**: los errores HTTP deben manejarse en todas las pantallas.
6. **Fase 7 valida restricciones absolutas**: la UI no puede modificar estados de pago.
7. **Fase 8 asegura cumplimiento**: las pruebas deben validar visualización, seguridad, descarga y ausencia de edición.

---

## 9. Restricción crítica

El frontend no debe implementar acciones de modificación del pago. No deben existir llamadas `POST`, `PUT`, `PATCH` o `DELETE` relacionadas con el estado del pago desde este módulo.

Los únicos consumos esperados son de consulta y descarga:

```http
GET /api/pagos
GET /api/pagos/{id}
GET /api/pagos/{id}/detalle
GET /api/pagos/{id}/comprobante
```

La generación del comprobante PDF ocurre en backend. React solo descarga el archivo.

# Plan Técnico Frontend: Visualizar liquidación

**Fecha**: 2026-05-07  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Visualizar-Liquidacion`  
**Plan backend base**: [plan-visualizar-liquidacion.md](./plan-visualizar-liquidacion.md)  
**Spec frontend**: [spec-frontend-visualizar-liquidacion.md](./spec-frontend-visualizar-liquidacion.md)  
**Frontend objetivo**: Listado paginado de liquidaciones, búsqueda por identificadores, detalle financiero y manejo seguro de permisos y errores

---

## 1. Resumen

Este plan define el frontend asociado a la visualización de liquidaciones previamente calculadas.

La UI permite consultar una lista organizada de liquidaciones, buscar por identificadores de negocio y visualizar el detalle completo de una liquidación autorizada. La funcionalidad no realiza cálculo, recálculo ni modificación de datos financieros.

El frontend debe consumir únicamente endpoints de lectura ya validados por backend, respetando la paginación, los permisos del usuario autenticado y los mensajes diferenciados para liquidación inexistente, liquidación aún no calculada, acceso no autorizado e indisponibilidad del sistema de almacenamiento.

---

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool**: Vite  
**Cliente HTTP**: Axios  
**Formularios**: No aplica para edición; solo filtros y búsqueda  
**Validación cliente**: Validación básica de filtros, parámetros de ruta y manejo de errores HTTP  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT  
**Roles esperados**: `ROLE_TRANSPORTISTA`, `ROLE_ADMIN`, `ROLE_GESTOR_TARIFAS`, `ROLE_FINANCIERO`

---

## 3. Endpoints backend consumidos

### 3.1 Listar liquidaciones autorizadas

```http
GET /api/liquidaciones
```

Uso: obtener lista paginada de liquidaciones visibles para el usuario autenticado.

Parámetros sugeridos:

```http
GET /api/liquidaciones?page=0&size=10&sort=fechaCalculo,desc
```

Parámetros de búsqueda sugeridos:

```http
GET /api/liquidaciones?idLiquidacion={idLiquidacion}
GET /api/liquidaciones?idRuta={idRuta}
GET /api/liquidaciones?estadoLiquidacion=CALCULADA
```

### 3.2 Consultar detalle de liquidación

```http
GET /api/liquidaciones/{id}
```

Uso: mostrar el detalle completo de una liquidación específica autorizada.

---

## 4. Contratos de datos esperados

### 4.1 Response de listado paginado

```json
{
  "items": [
    {
      "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
      "idRuta": "7e46a0df-9e2a-42c3-9fd1-2d2d4aa9d999",
      "fechaInicio": "2026-04-26T08:00:00",
      "fechaCierre": "2026-04-26T16:00:00",
      "estadoLiquidacion": "CALCULADA",
      "fechaCalculo": "2026-04-26T16:30:00",
      "montoBruto": 250000,
      "montoNeto": 230000
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

> Si el backend usa el formato estándar de Spring Page (`content`, `number`, `size`, `totalElements`, `totalPages`), el hook debe adaptarse sin cambiar la UI.

### 4.2 Response de detalle de liquidación

```json
{
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "idContrato": "CTR-001",
  "idRuta": "7e46a0df-9e2a-42c3-9fd1-2d2d4aa9d999",
  "fechaInicio": "2026-04-26T08:00:00",
  "fechaCierre": "2026-04-26T16:00:00",
  "estadoLiquidacion": "CALCULADA",
  "fechaCalculo": "2026-04-26T16:30:00",
  "tipoVehiculo": "Camión",
  "precioParada": 25000,
  "numeroParadas": 10,
  "montoBruto": 250000,
  "montoNeto": 230000,
  "ajustes": [
    {
      "tipo": "PENALIDAD",
      "monto": 20000,
      "razon": "Entrega fallida atribuible al transportista"
    }
  ]
}
```

### 4.3 Errores esperados

```json
{
  "mensaje": "La liquidación no existe dentro del registro."
}
```

```json
{
  "mensaje": "La liquidación aún no existe para este contrato o ruta."
}
```

```json
{
  "mensaje": "No tienes permisos para visualizar esta liquidación."
}
```

---

## 5. Estructura de archivos

```text
frontend/src/
├── modules/
│   └── liquidaciones/
│       ├── components/
│       │   ├── LiquidacionesTable.jsx           # Tabla paginada de liquidaciones
│       │   ├── LiquidacionFilters.jsx           # Búsqueda por idLiquidacion, idRuta y estado
│       │   ├── LiquidacionResumenCard.jsx       # Resumen financiero del detalle
│       │   ├── LiquidacionRutaCard.jsx          # Datos de ruta y contrato
│       │   ├── AjustesLiquidacionTable.jsx      # Tabla de ajustes/penalizaciones
│       │   ├── LiquidacionEstadoBadge.jsx       # Badge visual de estado
│       │   ├── LiquidacionEmptyState.jsx        # Estados vacíos
│       │   └── LiquidacionErrorState.jsx        # Errores funcionales
│       │
│       ├── pages/
│       │   ├── LiquidacionesListPage.jsx        # /liquidaciones
│       │   └── LiquidacionDetallePage.jsx       # /liquidaciones/:id
│       │
│       ├── services/
│       │   └── liquidacionVisualizacionService.js
│       │
│       ├── hooks/
│       │   ├── useLiquidacionesList.js          # Listado, filtros y paginación
│       │   └── useLiquidacionDetalle.js         # Detalle por id
│       │
│       └── utils/
│           ├── liquidacionEstadoLabels.js       # Etiquetas de estados
│           ├── normalizePageResponse.js         # Adaptador para Page/DTO paginado
│           └── formatMoney.js                   # Formato visual de moneda
│
└── shared/
    ├── components/                              # Botones, tablas, badges, skeletons
    └── services/
        └── apiClient.js                         # Axios con interceptores JWT
```

---

## 6. Criterios de éxito frontend

| SC / FR | Criterio frontend |
|:--------|:------------------|
| FR-001 | El listado muestra liquidaciones almacenadas en forma organizada y paginada. |
| FR-002 | La búsqueda permite localizar una liquidación por ID Liquidación o ID Ruta. |
| FR-003 | La UI muestra mensaje claro cuando una liquidación no existe. |
| FR-004 | La UI respeta `403` y no renderiza datos de liquidaciones no autorizadas. |
| SC-001 | Las liquidaciones autorizadas se visualizan con campos financieros y operativos completos. |
| SC-002 | Los accesos no autorizados quedan bloqueados desde backend y reflejados correctamente en UI. |
| — | La UI diferencia liquidación inexistente de liquidación aún no calculada. |
| — | No existen acciones para calcular, recalcular, editar o eliminar liquidaciones. |

---

## Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/liquidaciones` con carpetas `components`, `pages`, `services`, `hooks` y `utils`.
- [ ] F002 Registrar ruta `/liquidaciones` en el router global para usuarios autenticados.
- [ ] F003 Registrar ruta `/liquidaciones/:id` en el router global para usuarios autenticados.
- [ ] F004 Configurar protección de rutas mediante JWT y Route Guard.
- [ ] F005 Crear `liquidacionVisualizacionService.js` con métodos:
  - `getLiquidaciones(params)`
  - `getLiquidacionDetalle(idLiquidacion)`
- [ ] F006 Verificar que el service no tenga llamadas `POST`, `PUT`, `PATCH` ni `DELETE`.
- [ ] F007 Crear `liquidacionEstadoLabels.js`.
- [ ] F008 Crear `normalizePageResponse.js` para adaptar respuestas tipo `items` o `content`.
- [ ] F009 Crear `formatMoney.js` para formateo visual de montos sin recalcular.

---

## Fase 2: Listado paginado de liquidaciones

- [ ] F010 Implementar `useLiquidacionesList.js`:
  - recibe filtros y paginación;
  - llama a `getLiquidaciones(params)`;
  - expone `{ liquidaciones, pagination, isLoading, error, refetch }`.
- [ ] F011 Implementar `LiquidacionesListPage.jsx`:
  - carga liquidaciones autorizadas;
  - muestra skeleton loader durante carga;
  - renderiza filtros;
  - renderiza tabla;
  - maneja empty state;
  - maneja errores 403, 404 y 503.
- [ ] F012 Implementar `LiquidacionesTable.jsx` con columnas:
  - ID Liquidación
  - ID Ruta
  - Fecha inicio
  - Fecha cierre
  - Estado liquidación
  - Fecha cálculo
  - Monto bruto
  - Monto neto
  - Acción "Ver detalle"
- [ ] F013 Implementar `LiquidacionEstadoBadge.jsx` para estado de liquidación.
- [ ] F014 Implementar paginación conectada a la respuesta backend.
- [ ] F015 Ordenar por fecha de cálculo descendente por defecto, si backend lo soporta.
- [ ] F016 Mostrar mensaje "No hay liquidaciones disponibles para visualizar." cuando el listado venga vacío.
- [ ] F017 Confirmar que el listado no contiene acciones de cálculo, recálculo, edición o eliminación.

---

## Fase 3: Búsqueda y filtros

- [ ] F018 Implementar `LiquidacionFilters.jsx` con:
  - input de ID Liquidación;
  - input de ID Ruta;
  - select de estado de liquidación, si backend lo soporta;
  - botón "Buscar";
  - botón "Limpiar filtros".
- [ ] F019 Validar que al menos un campo de búsqueda tenga valor antes de ejecutar búsqueda específica, si se decide bloquear búsquedas vacías.
- [ ] F020 Manejar parámetros inválidos con mensaje: "El identificador ingresado no tiene un formato válido."
- [ ] F021 Al buscar una liquidación inexistente, mostrar "La liquidación no existe dentro del registro."
- [ ] F022 Al buscar una liquidación aún no calculada, mostrar "La liquidación aún no existe para este contrato o ruta."
- [ ] F023 Al buscar una liquidación sin permisos, mostrar 403 o redirigir a `/403`.
- [ ] F024 Permitir limpiar filtros y volver al listado paginado inicial.

---

## Fase 4: Detalle de liquidación

- [ ] F025 Implementar `useLiquidacionDetalle.js`:
  - recibe `idLiquidacion`;
  - llama a `getLiquidacionDetalle(idLiquidacion)`;
  - expone `{ data, isLoading, error }`.
- [ ] F026 Implementar `LiquidacionDetallePage.jsx`:
  - lee `id` desde parámetros de ruta;
  - carga detalle;
  - muestra skeleton loader;
  - renderiza resumen financiero;
  - renderiza datos de ruta;
  - renderiza tabla de ajustes;
  - maneja 403, 404, no calculada y 503.
- [ ] F027 Implementar `LiquidacionResumenCard.jsx` mostrando:
  - ID Liquidación
  - ID Contrato
  - ID Ruta
  - Estado liquidación
  - Fecha cálculo
  - Monto bruto
  - Monto neto
- [ ] F028 Implementar `LiquidacionRutaCard.jsx` mostrando:
  - Fecha inicio
  - Fecha cierre
  - Tipo de vehículo
  - Precio parada
  - Número de paradas
- [ ] F029 Implementar `AjustesLiquidacionTable.jsx` con columnas:
  - Tipo
  - Monto
  - Razón
- [ ] F030 Mostrar mensaje "Esta liquidación no tiene ajustes o penalizaciones aplicadas." si no hay ajustes.
- [ ] F031 Agregar botón "Volver al listado".
- [ ] F032 Confirmar que no exista botón "Calcular", "Recalcular" ni "Editar liquidación".

---

## Fase 5: Estados y errores globales

- [ ] F033 Manejar HTTP 400: mostrar "La solicitud no es válida."
- [ ] F034 Manejar HTTP 403: mostrar "No tienes permisos para visualizar esta liquidación." o redirigir a `/403`.
- [ ] F035 Manejar HTTP 404: mostrar "La liquidación no existe dentro del registro."
- [ ] F036 Manejar caso de liquidación aún no calculada con mensaje diferenciado.
- [ ] F037 Manejar HTTP 503: mostrar "El sistema de almacenamiento no está disponible. Intenta nuevamente más tarde."
- [ ] F038 Manejar errores de red: toast "No fue posible comunicarse con el servidor."
- [ ] F039 Implementar skeleton loaders en:
  - tabla de liquidaciones;
  - filtros si aplica;
  - resumen de liquidación;
  - datos de ruta;
  - tabla de ajustes.
- [ ] F040 Evitar mostrar stack traces, nombres de excepciones o detalles técnicos internos.

---

## Fase 6: Seguridad y consistencia UI

- [ ] F041 Verificar que el listado solo muestre liquidaciones devueltas por backend para el usuario autenticado.
- [ ] F042 Verificar que un error 403 no deje datos parciales renderizados.
- [ ] F043 Verificar que los montos coincidan exactamente con backend.
- [ ] F044 Verificar que la UI no calcule monto bruto, monto neto ni ajustes.
- [ ] F045 Verificar que la UI no infiera permisos localmente para mostrar datos no devueltos por backend.
- [ ] F046 Verificar que no existan llamadas de escritura en el módulo.
- [ ] F047 Verificar que los filtros no permitan inyección de parámetros no soportados por el backend.
- [ ] F048 Verificar que la paginación use los valores reales de `totalElements` y `totalPages`.

---

## Fase 7: Pruebas frontend

- [ ] F049 Test: `LiquidacionesListPage` muestra skeleton loader mientras carga.
- [ ] F050 Test: `LiquidacionesListPage` muestra tabla cuando existen liquidaciones.
- [ ] F051 Test: `LiquidacionesListPage` muestra empty state cuando no hay liquidaciones.
- [ ] F052 Test: `LiquidacionesTable` muestra ID Ruta, fechas, ID Liquidación, estado, monto bruto y monto neto.
- [ ] F053 Test: paginación cambia de página correctamente.
- [ ] F054 Test: búsqueda por ID Liquidación actualiza resultados.
- [ ] F055 Test: búsqueda por ID Ruta actualiza resultados.
- [ ] F056 Test: búsqueda inexistente muestra "La liquidación no existe dentro del registro."
- [ ] F057 Test: liquidación aún no calculada muestra mensaje diferenciado.
- [ ] F058 Test: error 403 muestra bloqueo o redirección a `/403`.
- [ ] F059 Test: error 503 muestra mensaje de almacenamiento no disponible.
- [ ] F060 Test: `LiquidacionDetallePage` muestra resumen financiero completo.
- [ ] F061 Test: `LiquidacionDetallePage` muestra datos de ruta.
- [ ] F062 Test: `AjustesLiquidacionTable` muestra tipo, monto y razón.
- [ ] F063 Test: `AjustesLiquidacionTable` muestra empty state cuando no hay ajustes.
- [ ] F064 Test: botón "Volver al listado" navega correctamente.
- [ ] F065 Test: no existe botón "Calcular" en ninguna vista.
- [ ] F066 Test: no existe botón "Recalcular" en ninguna vista.
- [ ] F067 Test: no existe botón "Editar liquidación" en ninguna vista.
- [ ] F068 Test: `liquidacionVisualizacionService.js` no contiene llamadas `POST`, `PUT`, `PATCH` ni `DELETE`.
- [ ] F069 Test: los montos renderizados no son recalculados por la UI.
- [ ] F070 Test: ante 403 no se renderizan datos parciales.

---

## 8. Dependencias y orden de ejecución

1. **Fase 1 primero**: crear módulo, rutas, service y utilidades base.
2. **Fase 2 antes que Fase 3**: el listado paginado debe funcionar antes de agregar filtros avanzados.
3. **Fase 3 antes que Fase 4**: la búsqueda debe permitir llegar correctamente al detalle.
4. **Fase 4 antes que Fase 5**: el detalle es necesario para validar todos los estados de error.
5. **Fase 5 transversal**: los errores deben ser consistentes entre listado, búsqueda y detalle.
6. **Fase 6 valida seguridad**: la UI debe respetar por completo las decisiones de autorización del backend.
7. **Fase 7 asegura cumplimiento**: las pruebas deben cubrir visualización, búsqueda, detalle, permisos, errores y ausencia de acciones de escritura.

---

## 9. Restricción crítica

Este módulo frontend solo puede consumir endpoints de lectura:

```http
GET /api/liquidaciones
GET /api/liquidaciones/{id}
```

No deben existir acciones de cálculo, recálculo, edición, eliminación ni modificación de estados dentro de esta feature.

El cálculo de liquidación pertenece a otro flujo del sistema y la visualización solo muestra liquidaciones ya generadas y autorizadas por backend.

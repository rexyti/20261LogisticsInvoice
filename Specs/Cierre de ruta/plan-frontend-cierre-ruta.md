# Plan Técnico Frontend: Cierre de ruta

**Fecha**: 2026-05-04  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Cierre-Ruta`  
**Plan backend base**: [plan-cierre-de-ruta.md](./plan-cierre-de-ruta.md)  
**Spec frontend**: [spec-frontend-cierre-ruta.md](./spec-frontend-cierre-ruta.md)  
**Frontend objetivo**: Dashboard financiero de rutas procesadas con detalle de paradas, responsables y alertas

---

## 1. Resumen

Este plan define la implementación frontend para la funcionalidad de **Cierre de ruta** dentro del Módulo 3 de Facturación y Liquidación.

El cierre de ruta no es disparado manualmente por React. El backend recibe eventos asíncronos `RUTA_CERRADA` desde el Módulo de Flotas y Rutas mediante mensajería. La responsabilidad del frontend es permitir al equipo financiero consultar las rutas ya procesadas, identificar rutas con inconsistencias (`CONTRATO_NULO`, `TARIFA_NO_ENCONTRADA`) y revisar el detalle de paradas, motivos de falla, responsables financieros y porcentajes de pago.

La interfaz es de **solo lectura**. React consume únicamente endpoints de lectura expuestos por el backend y no simula, publica ni reprocesa eventos de cierre.

---

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool**: Vite  
**Cliente HTTP**: Axios  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT enviado por interceptor Axios  
**Rol requerido**: `ROLE_ADMIN` (equipo financiero)  
**Arquitectura**: Feature-based structure por módulos  
**Destino**: Docker / AWS-ready

---

## 3. Endpoints backend consumidos

### 3.1 Consultar ruta procesada por identificador

```http
GET /api/rutas/{id}
```

Uso: detalle completo de una ruta cerrada — transportista, vehículo, modelo de contrato, paradas con responsables financieros y alertas.

### 3.2 Listar rutas procesadas

```http
GET /api/rutas?page={page}&size={size}&fechaInicio={fechaInicio}&fechaFin={fechaFin}&estado={estado}
```

Uso: alimentar el dashboard financiero con paginación y filtros por fecha y estado de procesamiento.

> Nota técnica: `GET /api/rutas/{id}` está definido como obligatorio en el plan backend (T019). `GET /api/rutas` es propuesto por el frontend para el dashboard. Debe coordinarse con backend antes de implementar F007. Si el endpoint no existe, bloquear esa fase hasta confirmarlo.

---

## 4. Contratos de datos esperados

### 4.1 Ruta list item

```json
{
  "idRuta": "uuid",
  "idTransportista": "uuid",
  "nombreTransportista": "string",
  "tipoVehiculo": "MOTO | VAN | NHR | TURBO",
  "modeloContrato": "POR_PARADA | RECORRIDO_COMPLETO",
  "estadoProcesamiento": "PROCESADA | CON_ALERTAS | ERROR | DUPLICADA",
  "totalParadas": 12,
  "paradasExitosas": 9,
  "paradasFallidas": 3,
  "fechaCierre": "2026-04-08T14:30:00",
  "alertas": ["CONTRATO_NULO", "TARIFA_NO_ENCONTRADA"]
}
```

### 4.2 Detalle de ruta

```json
{
  "idRuta": "uuid",
  "transportista": {
    "idTransportista": "uuid",
    "nombre": "string"
  },
  "vehiculo": {
    "tipoVehiculo": "NHR",
    "vehiculoId": "uuid"
  },
  "modeloContrato": "POR_PARADA",
  "estadoProcesamiento": "CON_ALERTAS",
  "fechaInicio": "2026-04-08T07:45:00",
  "fechaCierre": "2026-04-08T18:00:00",
  "paradas": [
    {
      "idParada": "uuid",
      "estado": "EXITOSA | FALLIDA",
      "motivoNoEntrega": "CLIENTE_AUSENTE | DIRECCION_ERRONEA | RECHAZADO | ZONA_DIFICIL_ACCESO | PAQUETE_DANADO | PERDIDA_PAQUETE",
      "responsableFalla": "CLIENTE | TRANSPORTISTA | FUERZA_MAYOR",
      "porcentajePago": 50
    }
  ],
  "alertas": ["TARIFA_NO_ENCONTRADA"]
}
```

> `tipoVehiculo` acepta `MOTO | VAN | NHR | TURBO` (no `CAMION`). Coordinar con backend para validar el enum exacto.  
> `responsableFalla` determina el color del badge en `MotivoFallaBadge`: CLIENTE = amarillo, TRANSPORTISTA = rojo, FUERZA_MAYOR = gris.

---

## 5. Estructura de archivos

```text
frontend/src/
├── app/
│   ├── router.jsx
│   └── routes.js
│
├── modules/
│   └── rutas/
│       ├── components/
│       │   ├── RutaTable.jsx            # Tabla del dashboard con columnas clave
│       │   ├── RutaFilters.jsx          # Filtros: fecha inicio, fecha fin, estado
│       │   ├── RutaStatusBadge.jsx      # Badge de estado de procesamiento
│       │   ├── ParadasTable.jsx         # Tabla de paradas con responsable y % pago
│       │   ├── MotivoFallaBadge.jsx     # Badge de motivo: color por responsableFalla
│       │   └── RutaAlertPanel.jsx       # Panel de alertas CONTRATO_NULO / TARIFA_NO_ENCONTRADA
│       │
│       ├── pages/
│       │   ├── RutasDashboardPage.jsx   # Listado paginado de rutas
│       │   └── RutaDetailPage.jsx       # Detalle de una ruta
│       │
│       ├── services/
│       │   └── rutasService.js          # getLista(params), getDetalle(id)
│       │
│       ├── hooks/
│       │   ├── useRutas.js              # Paginación, filtros, estado de carga
│       │   └── useRutaDetail.js         # Detalle de ruta por id
│       │
│       └── utils/
│           └── rutaFormatters.js        # Formateo de tipoVehiculo, modeloContrato, estadoProcesamiento
│
└── shared/
    ├── components/
    │   ├── DataTable.jsx
    │   ├── EmptyState.jsx
    │   ├── ErrorState.jsx
    │   ├── LoadingState.jsx
    │   └── PageHeader.jsx
    │
    ├── services/
    │   └── apiClient.js                 # Axios con interceptor JWT
    │
    └── utils/
        ├── dateFormatters.js            # Formateo de fechas ISO a local
        └── moneyFormatters.js           # Formateo de porcentajes y montos
```

---

## 6. Criterios de éxito frontend

Mapeados a los SC del spec:

| SC del Spec | Criterio frontend                                                                                                         |
|:------------|:--------------------------------------------------------------------------------------------------------------------------|
| SC-001      | La UI muestra un indicador visual en paradas fallidas sin motivo asociado (no puede ocurrir si backend lo valida, pero la UI lo refleja si llega). |
| SC-003      | El detalle de ruta muestra el total de paradas (exitosas + fallidas) y la UI lo presenta sin recalcular por su cuenta.    |
| —           | No existe ningún botón de reprocesamiento manual del cierre en ninguna vista.                                             |
| —           | La ruta `/rutas` y `/rutas/:id` son inaccesibles para roles distintos de `ROLE_ADMIN` (redirect a `/403`).               |
| —           | Las alertas `CONTRATO_NULO` y `TARIFA_NO_ENCONTRADA` se muestran con distinción visual clara en dashboard y detalle.     |

---

## Fase 1: Configuración base frontend

- [ ] F001 Crear módulo `modules/rutas` con su estructura de carpetas completa.
- [ ] F002 Registrar ruta `/rutas` en el router con Route Guard que valida `ROLE_ADMIN`; redirige a `/403` si el rol no corresponde.
- [ ] F003 Registrar ruta `/rutas/:idRuta` con la misma restricción de rol.
- [ ] F004 Crear `rutasService.js` con métodos `getRutas(params)` y `getRutaDetalle(id)`.
- [ ] F005 Confirmar que `apiClient.js` incluye interceptor JWT antes de construir sobre él.
- [ ] F006 Crear `rutaFormatters.js` con funciones de formateo: `formatTipoVehiculo`, `formatModeloContrato`, `formatEstadoProcesamiento`. No usar strings crudos del backend en la UI.
- [ ] F007 Crear (si no existen) los componentes compartidos: `DataTable`, `EmptyState`, `ErrorState`, `LoadingState`, `PageHeader`. No duplicar si ya existen en `shared/`.

**Checkpoint**: El frontend puede navegar hacia el dashboard y preparar llamadas HTTP autenticadas. Route Guards activos.

---

## Fase 2: Dashboard de rutas procesadas

- [ ] F008 Confirmar con backend que `GET /api/rutas` con paginación y filtros está implementado antes de construir esta fase.
- [ ] F009 Implementar `useRutas.js`: maneja página, tamaño, filtros de fecha y estado. Expone `{ rutas, totalPages, isLoading, error, setFiltros }`.
- [ ] F010 Implementar `RutaFilters.jsx` con filtros: fecha inicio, fecha fin, estado de procesamiento. Validar que fecha fin no sea anterior a fecha inicio con mensaje inline.
- [ ] F011 Implementar `RutaTable.jsx` con columnas:
  - ID Ruta
  - Transportista
  - Tipo de vehículo (formateado con `rutaFormatters`)
  - Modelo de contrato (formateado)
  - Total de paradas
  - Estado de procesamiento (`RutaStatusBadge`)
  - Alertas (iconos de advertencia si array no vacío)
  - Fecha de cierre (formateado con `dateFormatters`)
  - Acción: navegar a detalle
- [ ] F012 Implementar `RutaStatusBadge.jsx`: `PROCESADA` = verde, `CON_ALERTAS` = amarillo, `ERROR` = rojo, `DUPLICADA` = gris.
- [ ] F013 Implementar navegación desde cada fila hacia `/rutas/:idRuta`.
- [ ] F014 Mostrar `EmptyState` cuando no existan rutas o los filtros no retornen resultados.
- [ ] F015 Mostrar `ErrorState` ante errores HTTP.

**Checkpoint**: El equipo financiero puede listar rutas cerradas, filtrar por fecha y estado, y acceder al detalle.

---

## Fase 3: Detalle de ruta y paradas

- [ ] F016 Implementar `useRutaDetail.js`: consume `GET /api/rutas/{id}`, expone `{ ruta, isLoading, error }`.
- [ ] F017 Implementar `RutaDetailPage.jsx` con secciones:
  - Encabezado: ID Ruta, estado de procesamiento, fechas de inicio y cierre
  - Datos del transportista: nombre, ID
  - Datos del vehículo: tipo (formateado), ID
  - Modelo de contrato
  - Panel de alertas (si existen)
  - Tabla de paradas
- [ ] F018 Implementar `ParadasTable.jsx` con columnas: ID Parada, Estado, Motivo de no entrega, Responsable financiero (`MotivoFallaBadge`), Porcentaje de pago (formateado con `moneyFormatters`).
- [ ] F019 Implementar `MotivoFallaBadge.jsx`: diferencia los 3 tipos de responsabilidad según el spec:
  - `CLIENTE` → badge amarillo (pago 30–50%)
  - `TRANSPORTISTA` → badge rojo (pago 0% + penalidad)
  - `FUERZA_MAYOR` → badge gris (por definir con negocio)
- [ ] F020 Implementar `RutaAlertPanel.jsx` con mensajes diferenciados:
  - `CONTRATO_NULO` → "El modelo de contrato de esta ruta es nulo. Requiere revisión manual del equipo financiero."
  - `TARIFA_NO_ENCONTRADA` → "No se encontró tarifa para el tipo de vehículo de esta ruta. Requiere revisión manual del equipo financiero."
- [ ] F021 Confirmar que no existe ningún botón de reprocesamiento o acción sobre el cierre en esta página.

**Checkpoint**: El detalle muestra la información financiera procesada sin alterar el flujo asíncrono del backend.

---

## Fase 4: Estados, errores y seguridad

- [ ] F022 Manejar HTTP 401: redirigir a pantalla de autenticación o mostrar mensaje de sesión expirada.
- [ ] F023 Manejar HTTP 403: redirigir a `/403`.
- [ ] F024 Manejar HTTP 404: mensaje inline "Ruta no encontrada."
- [ ] F025 Manejar HTTP 500: mensaje "Ocurrió un error en el servidor. Intentá nuevamente."
- [ ] F026 Manejar HTTP 503: mensaje "El servicio no está disponible. Intentá nuevamente en unos momentos."
- [ ] F027 Mostrar skeleton loaders en la tabla del dashboard y en el detalle mientras se cargan datos.
- [ ] F028 Validar en `RutaFilters.jsx` que la fecha fin no sea anterior a la fecha inicio. Mostrar mensaje inline, no toast.

---

## Fase 5: Pruebas frontend

- [ ] F029 Test de `rutasService.js`: construcción correcta de URLs con parámetros de paginación y filtros.
- [ ] F030 Test de `RutaTable.jsx`: render de columnas principales y navegación hacia detalle.
- [ ] F031 Test de `RutaFilters.jsx`: validación de rango de fechas invertido muestra mensaje inline.
- [ ] F032 Test de `RutaStatusBadge.jsx`: color correcto para cada estado (`PROCESADA`, `CON_ALERTAS`, `ERROR`, `DUPLICADA`).
- [ ] F033 Test de `MotivoFallaBadge.jsx`: badge correcto para `CLIENTE`, `TRANSPORTISTA` y `FUERZA_MAYOR`.
- [ ] F034 Test de `RutaAlertPanel.jsx`: mensaje específico para `CONTRATO_NULO` y `TARIFA_NO_ENCONTRADA`.
- [ ] F035 Test de `RutaDetailPage.jsx`: render de secciones de transportista, vehículo y paradas.
- [ ] F036 Test de paginación: `useRutas.js` actualiza la página correctamente al cambiar parámetros.
- [ ] F037 Test de errores HTTP 401, 403, 404 y 503 muestran el mensaje correcto sin romper la UI.
- [ ] F038 Test: no existe ningún botón de acción o reprocesamiento en el detalle de ruta.

---

## 7. Dependencias y orden de ejecución

1. **Fase 1 primero**: el módulo, el router con Route Guard y el servicio deben existir antes que cualquier componente.
2. **Confirmar `GET /api/rutas` con backend antes de Fase 2**: si no está implementado, la Fase 2 se bloquea. El detalle (`GET /api/rutas/{id}`) sí está confirmado como obligatorio en el plan backend (T019).
3. **Fase 2 antes que Fase 3**: el dashboard es el punto de entrada al detalle.
4. **`rutaFormatters.js` antes de cualquier componente visual**: los componentes no usan strings crudos del backend.
5. **No crear acciones de procesamiento manual**: el cierre de ruta se origina por mensajería backend. Esta restricción es absoluta y debe verificarse en code review.
6. **El backend mantiene la fuente de verdad**: clasificación de responsables, porcentajes de pago y alertas vienen del backend. La UI solo los presenta.
# Plan Técnico Frontend: Novedad estado del paquete

**Fecha**: 2026-05-04  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Novedad-Estado-Paquete`  
**Plan backend base**: `Specs/Novedad estado del paquete/plan-novedad-estado-paquete.md`  
**Spec frontend**: `Specs/Novedad estado del paquete/spec-frontend-novedad-estado-paquete.md`  
**Frontend objetivo**: Vista de auditoría de sincronización de paquetes

## 1. Resumen

Este plan define el frontend para consultar y auditar las novedades del estado de paquete obtenidas por el backend desde el Módulo de Gestión de Paquetes.

La sincronización del estado del paquete no debe ser ejecutada manualmente desde React. Según el flujo técnico del backend, la consulta sincrónica al servicio externo se activa automáticamente durante el proceso de liquidación. Por tanto, el frontend se limita a mostrar resultados persistidos: historial de estados, logs de sincronización, paquetes con estado pendiente por sincronización y errores HTTP recibidos desde el servicio externo.

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool recomendado**: Vite  
**Cliente HTTP**: Axios  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT por interceptor Axios  
**Arquitectura**: Feature-based structure

## 3. Endpoints backend consumidos

Los siguientes endpoints están implementados en `PaqueteController` y son los únicos contratos válidos para el frontend.

### 3.1 Consultar historial de estado de paquete

```http
GET /api/paquetes/{idPaquete}/historial?page={page}&size={size}
```

Uso frontend:

- Mostrar línea de tiempo de cambios de estado.
- Validar trazabilidad del estado actual.

### 3.2 Consultar logs de sincronización por paquete

```http
GET /api/sincronizacion/logs/paquetes/{idPaquete}?page={page}&size={size}
```

Uso frontend:

- Mostrar respuestas HTTP del Módulo de Gestión para un paquete específico.
- Revisar JSON recibido, códigos de error, timeouts o estados no mapeados.

### 3.3 Listar todos los logs de sincronización

```http
GET /api/sincronizacion/logs?page={page}&size={size}
```

Uso frontend:

- Alimentar vista de auditoría financiera global.
- Identificar fallos recurrentes en la sincronización.
- Revisar paquetes pendientes por sincronización.

> Nota: el backend actual solo soporta paginación (`page`, `size`). Los filtros por `estado`, `codigoHttp` e `idRuta` **no están disponibles** en esta versión del backend. El frontend debe implementar filtrado en cliente sobre los registros paginados, o esperar una futura extensión del endpoint.

## 4. Estructura propuesta frontend

```text
frontend/
├── src/
│   ├── modules/
│   │   ├── paquetes/
│   │   │   ├── components/
│   │   │   │   ├── PaqueteEstadoBadge.jsx
│   │   │   │   ├── HistorialEstadoTimeline.jsx
│   │   │   │   └── PaqueteResumenCard.jsx
│   │   │   │
│   │   │   ├── pages/
│   │   │   │   └── PaqueteHistorialPage.jsx
│   │   │   │
│   │   │   ├── services/
│   │   │   │   └── paquetesService.js
│   │   │   │
│   │   │   └── hooks/
│   │   │       ├── useHistorialPaquete.js
│   │   │       └── useLogsPaquete.js
│   │   │
│   │   └── auditoria/
│   │       ├── components/
│   │       │   ├── SincronizacionLogsTable.jsx
│   │       │   ├── SincronizacionFilters.jsx
│   │       │   ├── CodigoHttpBadge.jsx
│   │       │   └── JsonPayloadModal.jsx
│   │       │
│   │       ├── pages/
│   │       │   └── AuditoriaSincronizacionPage.jsx
│   │       │
│   │       ├── services/
│   │       │   └── auditoriaService.js
│   │       │
│   │       └── hooks/
│   │           └── useLogsSincronizacion.js
│   │
│   └── shared/
│       ├── components/
│       └── services/
│           └── apiClient.js
```

## 5. Contratos de datos reales (desde backend implementado)

### 5.1 Historial de estado

Respuesta de `GET /api/paquetes/{idPaquete}/historial`:

```json
[
  {
    "id": 1,
    "idPaquete": 42,
    "estado": "ENTREGADO",
    "fecha": "2026-04-08T15:20:00"
  }
]
```

> `porcentajePago` **no está en este DTO**. Si se necesita mostrar el porcentaje, derivarlo en frontend según la tabla de reglas del Enum `EstadoPaquete` (ENTREGADO→100%, DEVUELTO→50%, DAÑADO→0%, EXTRAVIADO→0%).

### 5.2 Log de sincronización

Respuesta de `GET /api/sincronizacion/logs` y `GET /api/sincronizacion/logs/paquetes/{idPaquete}`:

```json
[
  {
    "id": 1,
    "idPaquete": 42,
    "codigoRespuestaHTTP": 200,
    "jsonRecibido": "{\"idPaquete\":42,\"estado\":\"ENTREGADO\"}",
    "fechaSincronizacion": "2026-04-08T15:21:00"
  }
]
```

> Los campos `idRuta`, `estadoResultado` y `mensajeError` **no existen en el DTO actual**. El estado del resultado (EXITOSO, PENDIENTE, ERROR, etc.) debe inferirse del `codigoRespuestaHTTP` y del contenido del `jsonRecibido`.

### 5.3 Regla de inferencia de estado resultado (frontend)

El frontend debe aplicar la siguiente lógica para derivar el estado resultado visible al usuario:

| Condición                                  | Estado resultado mostrado      |
|--------------------------------------------|-------------------------------|
| `codigoRespuestaHTTP == 200`               | EXITOSO                        |
| `codigoRespuestaHTTP == 404`               | PAQUETE NO ENCONTRADO          |
| `codigoRespuestaHTTP == null` (timeout)    | PENDIENTE POR SINCRONIZACIÓN   |
| `codigoRespuestaHTTP >= 500`               | ERROR DE SERVIDOR              |
| Otro código distinto a 200                 | ERROR HTTP {código}            |

## 6. Fase 1: Configuración del módulo

- [ ] F001 Crear módulos `modules/paquetes` y `modules/auditoria`.
- [ ] F002 Registrar ruta `/paquetes/:idPaquete/historial`.
- [ ] F003 Registrar ruta `/auditoria/sincronizacion-paquetes`.
- [ ] F004 Crear `paquetesService.js` con funciones para:
  - `getHistorialPaquete(idPaquete, page, size)` → `GET /api/paquetes/{idPaquete}/historial`
  - `getLogsPaquete(idPaquete, page, size)` → `GET /api/sincronizacion/logs/paquetes/{idPaquete}`
- [ ] F005 Crear `auditoriaService.js` con función:
  - `getLogsGlobales(page, size)` → `GET /api/sincronizacion/logs`
- [ ] F006 Reutilizar `apiClient.js` con JWT, manejo de errores y base URL por variable de entorno.

## 7. Fase 2: Vista de auditoría de sincronización

- [ ] F007 Implementar `useLogsSincronizacion.js` con paginación (page/size). Los filtros por código HTTP e idPaquete se aplican en cliente sobre la página cargada.
- [ ] F008 Implementar `SincronizacionFilters.jsx` con filtros en cliente por:
  - ID Paquete (búsqueda sobre datos cargados)
  - Código HTTP (búsqueda sobre datos cargados)
  - Rango de fechas (sobre `fechaSincronizacion`)
- [ ] F009 Implementar `SincronizacionLogsTable.jsx` con columnas:
  - ID Paquete
  - Código HTTP
  - Estado resultado (derivado según sección 5.3)
  - Fecha sincronización
  - Acción para ver JSON recibido
- [ ] F010 Implementar `CodigoHttpBadge.jsx` con distinción visual entre 2xx (verde), 4xx (amarillo), 5xx (rojo) y null/timeout (gris).
- [ ] F011 Implementar `JsonPayloadModal.jsx` para visualizar `jsonRecibido` en modo lectura sin romper el layout.
- [ ] F012 Resaltar visualmente registros con código HTTP diferente a 200 (null, 4xx, 5xx).

## 8. Fase 3: Historial por paquete

- [ ] F013 Implementar `useHistorialPaquete.js` para consumir historial con paginación.
- [ ] F014 Implementar `useLogsPaquete.js` para consumir logs específicos del paquete.
- [ ] F015 Implementar `PaqueteHistorialPage.jsx` con dos secciones:
  - Línea de tiempo de estados.
  - Tabla de logs de sincronización del paquete.
- [ ] F016 Implementar `HistorialEstadoTimeline.jsx` mostrando estado, porcentaje de pago derivado en frontend y fecha.
- [ ] F017 Implementar `PaqueteEstadoBadge.jsx` para estados `ENTREGADO`, `DEVUELTO`, `DAÑADO`, `EXTRAVIADO` y la etiqueta de pendiente cuando el código HTTP es nulo.
- [ ] F018 No incluir botón para sincronizar manualmente desde frontend.

## 9. Fase 4: Manejo funcional de errores

- [ ] F019 Mostrar mensaje claro cuando no existan logs para el paquete consultado.
- [ ] F020 Mostrar mensaje `Paquete no encontrado` ante HTTP 404 del propio backend financiero.
- [ ] F021 Mostrar mensaje de acceso denegado ante HTTP 403.
- [ ] F022 Mostrar mensaje de indisponibilidad temporal ante HTTP 503.
- [ ] F023 Mostrar `jsonRecibido` en modo lectura dentro del modal; si el valor es nulo, mostrar `"Sin respuesta registrada"`.
- [ ] F024 Evitar que datos técnicos extensos oculten la información principal del usuario.

## 10. Fase 5: Pruebas frontend

- [ ] F025 Test de `SincronizacionLogsTable.jsx` validando render correcto de los campos del DTO real.
- [ ] F026 Test de la lógica de derivación de estado resultado en `CodigoHttpBadge.jsx` (200, 404, null, 500).
- [ ] F027 Test de `JsonPayloadModal.jsx` validando visualización segura del JSON y manejo de valor nulo.
- [ ] F028 Test de `HistorialEstadoTimeline.jsx` validando orden cronológico y cálculo del porcentaje de pago en frontend.
- [ ] F029 Test de estados vacíos y errores HTTP del backend (403, 404, 503).

## 11. Dependencias y orden de ejecución

1. Primero debe existir el `apiClient` compartido con interceptor JWT.
2. Luego se implementa la vista de auditoría global de logs.
3. Después se implementa el historial por paquete.
4. No se debe construir acción manual de sincronización; el backend define que la consulta la dispara el proceso de liquidación.
5. Los filtros avanzados por `idRuta` y `estadoResultado` quedan pendientes hasta que el backend extienda el endpoint `/api/sincronizacion/logs` con esos query params.

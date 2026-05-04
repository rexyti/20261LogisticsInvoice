# Plan Técnico Frontend: Novedad estado del paquete

**Fecha**: 2026-05-03  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Novedad-Estado-Paquete`  
**Plan backend base**: `Specs/Novedad estado del paquete/plan-novedad-estado-paquete.md`  
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

### 3.1 Consultar historial de estado de paquete

```http
GET /api/paquetes/{idPaquete}/historial
```

Uso frontend:

- Mostrar línea de tiempo de cambios de estado.
- Validar trazabilidad del estado actual.

### 3.2 Consultar logs de sincronización

```http
GET /api/paquetes/{idPaquete}/logs-sincronizacion
```

Uso frontend:

- Mostrar respuestas HTTP del Módulo de Gestión.
- Revisar JSON recibido, códigos de error, timeouts o estados no mapeados.

### 3.3 Listar logs de sincronización con filtros

```http
GET /api/auditoria/sincronizacion-paquetes?page={page}&size={size}&estado={estado}&codigoHttp={codigoHttp}&idRuta={idRuta}
```

Uso frontend:

- Alimentar vista de auditoría financiera.
- Identificar fallos recurrentes.
- Revisar paquetes pendientes por sincronización.

> Nota técnica: el plan backend menciona explícitamente casos de uso para `ObtenerHistorialUseCase` y `ObtenerLogsSincronizacionUseCase`. Los endpoints anteriores formalizan el contrato frontend necesario para consumir esos casos de uso.

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

## 5. Contratos de datos esperados

### 5.1 Historial de estado

```json
[
  {
    "idHistorial": "uuid",
    "idPaquete": "uuid",
    "estado": "ENTREGADO",
    "porcentajePago": 100,
    "fecha": "2026-04-08T15:20:00"
  }
]
```

### 5.2 Log de sincronización

```json
{
  "idLog": "uuid",
  "idPaquete": "uuid",
  "idRuta": "uuid",
  "codigoRespuestaHttp": 200,
  "estadoResultado": "EXITOSO | PENDIENTE_SINCRONIZACION | ERROR | ESTADO_NO_MAPEADO",
  "jsonRecibido": "{...}",
  "mensajeError": null,
  "fechaRegistro": "2026-04-08T15:21:00"
}
```

## 6. Fase 1: Configuración del módulo

- [ ] F001 Crear módulos `modules/paquetes` y `modules/auditoria`.
- [ ] F002 Registrar ruta `/paquetes/:idPaquete/historial`.
- [ ] F003 Registrar ruta `/auditoria/sincronizacion-paquetes`.
- [ ] F004 Crear `paquetesService.js` para historial y logs por paquete.
- [ ] F005 Crear `auditoriaService.js` para listado global de logs.
- [ ] F006 Reutilizar `apiClient.js` con JWT, manejo de errores y base URL por variable de entorno.

## 7. Fase 2: Vista de auditoría de sincronización

- [ ] F007 Implementar `useLogsSincronizacion.js` con paginación y filtros.
- [ ] F008 Implementar `SincronizacionFilters.jsx` con filtros por:
  - ID Ruta
  - ID Paquete
  - Estado resultado
  - Código HTTP
  - Rango de fechas
- [ ] F009 Implementar `SincronizacionLogsTable.jsx` con columnas:
  - ID Ruta
  - ID Paquete
  - Código HTTP
  - Estado resultado
  - Mensaje de error
  - Fecha registro
  - Acción para ver JSON
- [ ] F010 Implementar `CodigoHttpBadge.jsx` con distinción visual entre 2xx, 4xx, 5xx y timeout.
- [ ] F011 Implementar `JsonPayloadModal.jsx` para visualizar `jsonRecibido` sin romper el layout.
- [ ] F012 Resaltar registros `PENDIENTE_SINCRONIZACION` y `ESTADO_NO_MAPEADO`.

## 8. Fase 3: Historial por paquete

- [ ] F013 Implementar `useHistorialPaquete.js` para consumir historial.
- [ ] F014 Implementar `useLogsPaquete.js` para consumir logs específicos del paquete.
- [ ] F015 Implementar `PaqueteHistorialPage.jsx` con dos secciones:
  - Línea de tiempo de estados.
  - Tabla de logs de sincronización.
- [ ] F016 Implementar `HistorialEstadoTimeline.jsx` mostrando estado, porcentaje de pago y fecha.
- [ ] F017 Implementar `PaqueteEstadoBadge.jsx` para estados `ENTREGADO`, `DEVUELTO`, `DAÑADO`, `EXTRAVIADO` y `PENDIENTE_SINCRONIZACION`.
- [ ] F018 No incluir botón para sincronizar manualmente desde frontend.

## 9. Fase 4: Manejo funcional de errores

- [ ] F019 Mostrar mensaje claro cuando no existan logs para los filtros aplicados.
- [ ] F020 Mostrar mensaje `Paquete no encontrado` ante HTTP 404.
- [ ] F021 Mostrar mensaje de acceso denegado ante HTTP 403.
- [ ] F022 Mostrar mensaje de indisponibilidad temporal ante HTTP 503.
- [ ] F023 Validar que el JSON recibido se muestre en modo lectura.
- [ ] F024 Evitar que datos técnicos extensos oculten la información principal del usuario.

## 10. Fase 5: Pruebas frontend

- [ ] F025 Test de `SincronizacionLogsTable.jsx` validando render de logs.
- [ ] F026 Test de filtros de auditoría.
- [ ] F027 Test de `JsonPayloadModal.jsx` validando visualización segura del JSON.
- [ ] F028 Test de `HistorialEstadoTimeline.jsx` validando orden cronológico.
- [ ] F029 Test de estados vacíos y errores 403, 404, 503.

## 11. Dependencias y orden de ejecución

1. Primero debe existir el `apiClient` compartido.
2. Luego se implementa auditoría global de logs.
3. Después se implementa historial por paquete.
4. No se debe construir acción manual de sincronización, porque el backend define que la consulta la dispara el proceso de liquidación.
5. El frontend depende de endpoints estables para historial y logs.

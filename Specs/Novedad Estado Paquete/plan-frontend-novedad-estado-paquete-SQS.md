# Plan Técnico Frontend: Novedad estado del paquete — Auditoría de eventos SQS

**Fecha**: 2026-05-04  
**Updated**: 2026-05-16  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Novedad-Estado-Paquete`  
**Plan backend base**: `Specs/Novedad estado del paquete/plan-novedad-estado-paquete-SQS.md`  
**Spec frontend**: `Specs/Novedad estado del paquete/spec-frontend-novedad-estado-paquete-SQS.md`  
**Frontend objetivo**: Vista de auditoría de eventos SQS e historial de paquete

## 1. Resumen

Este plan define el frontend para consultar y auditar las novedades del estado de paquete procesadas por el backend desde AWS SQS.

La sincronización ya no es una consulta sincrónica HTTP al Módulo de Gestión. El backend consume eventos SQS, los valida, actualiza el estado del paquete y guarda logs de procesamiento. React solo muestra información persistida en el Módulo Financiero: historial de estados, logs de eventos SQS, paquetes con errores funcionales, mensajes duplicados, eventos atrasados y errores técnicos.

El frontend no debe contener botón de sincronización manual, no debe invocar al Módulo de Gestión de Paquetes, no debe publicar mensajes en SQS y no debe depender de códigos HTTP provenientes de Gestión.

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool recomendado**: Vite  
**Cliente HTTP**: Axios  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT por interceptor Axios  
**Arquitectura**: Feature-based structure

## 3. Endpoints backend consumidos

Los endpoints siguen siendo de lectura sobre el Módulo Financiero. La diferencia principal es que los logs representan eventos SQS, no respuestas HTTP del Módulo de Gestión.

### 3.1 Consultar historial de estado de paquete

```http
GET /api/paquetes/{idPaquete}/historial?page={page}&size={size}
```

Uso frontend:

- Mostrar línea de tiempo de cambios de estado.
- Validar trazabilidad del estado actual.
- Mostrar `eventId` y `fechaEvento` cuando el backend los exponga.

### 3.2 Consultar logs SQS por paquete

```http
GET /api/sincronizacion/logs/paquetes/{idPaquete}?page={page}&size={size}
```

Uso frontend:

- Mostrar eventos SQS relacionados con un paquete específico.
- Revisar payload recibido, estado de procesamiento, duplicados, descartes y errores.

### 3.3 Listar todos los logs SQS

```http
GET /api/sincronizacion/logs?page={page}&size={size}
```

Uso frontend:

- Alimentar vista de auditoría financiera global.
- Identificar fallos recurrentes de procesamiento.
- Revisar eventos pendientes, duplicados, atrasados o no mapeados.

> Nota: si el backend aún solo soporta paginación (`page`, `size`), los filtros por `estadoProcesamiento`, `idPaquete`, `idRuta` y fechas se aplican en cliente sobre la página cargada. Si se extiende el endpoint, deben migrarse a filtros server-side.

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
│   │   │   ├── pages/
│   │   │   │   └── PaqueteHistorialPage.jsx
│   │   │   ├── services/
│   │   │   │   └── paquetesService.js
│   │   │   └── hooks/
│   │   │       ├── useHistorialPaquete.js
│   │   │       └── useLogsPaquete.js
│   │   │
│   │   └── auditoria/
│   │       ├── components/
│   │       │   ├── EventosSqsLogsTable.jsx
│   │       │   ├── EventosSqsFilters.jsx
│   │       │   ├── EstadoProcesamientoBadge.jsx
│   │       │   └── PayloadEventoModal.jsx
│   │       ├── pages/
│   │       │   └── AuditoriaEventosSqsPage.jsx
│   │       ├── services/
│   │       │   └── auditoriaService.js
│   │       └── hooks/
│   │           └── useEventosSqsLogs.js
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
    "id": 1,
    "eventId": "evt-2026-000001",
    "idPaquete": "123e4567-e89b-12d3-a456-426614174000",
    "estado": "ENTREGADO",
    "fecha": "2026-04-08T15:21:02",
    "fechaEvento": "2026-04-08T15:21:00"
  }
]
```

> Si `porcentajePago` no viene en el DTO, derivarlo en frontend: `ENTREGADO`→100%, `DEVUELTO`→50%, `DAÑADO`→0%, `EXTRAVIADO`→0%.

### 5.2 Log de evento SQS

```json
[
  {
    "id": 1,
    "eventId": "evt-2026-000001",
    "sqsMessageId": "71f7c7a8-1f03-4e3d-bc10-9fcf2a00b931",
    "idRuta": "550e8400-e29b-41d4-a716-446655440000",
    "idPaquete": "123e4567-e89b-12d3-a456-426614174000",
    "estadoRecibido": "ENTREGADO",
    "estadoProcesamiento": "PROCESADO",
    "motivoError": null,
    "payloadRecibido": "{\"eventId\":\"evt-2026-000001\",\"estado\":\"ENTREGADO\"}",
    "fechaRecepcion": "2026-04-08T15:21:00",
    "fechaProcesamiento": "2026-04-08T15:21:02"
  }
]
```

### 5.3 Regla de inferencia visual

El frontend debe usar `estadoProcesamiento`, no `codigoRespuestaHTTP`.

| Estado procesamiento | Estado visual |
|---|---|
| `PROCESADO` | Procesado correctamente |
| `DUPLICADO` | Duplicado / idempotente |
| `DESCARTADO_ATRASADO` | Descartado por evento atrasado |
| `ERROR_PAQUETE_NO_ENCONTRADO` | Paquete no encontrado |
| `ERROR_ESTADO_NO_MAPEADO` | Estado no mapeado |
| `ERROR_PAYLOAD_INVALIDO` | Payload inválido |
| `ERROR_TECNICO` | Error técnico / revisar DLQ |

## 6. Fase 1: Configuración del módulo

- [ ] F001 Crear módulos `modules/paquetes` y `modules/auditoria`.
- [ ] F002 Registrar ruta `/paquetes/:idPaquete/historial`.
- [ ] F003 Registrar ruta `/auditoria/eventos-paquetes`.
- [ ] F004 Crear `paquetesService.js` con funciones:
  - `getHistorialPaquete(idPaquete, page, size)` → `GET /api/paquetes/{idPaquete}/historial`.
  - `getLogsPaquete(idPaquete, page, size)` → `GET /api/sincronizacion/logs/paquetes/{idPaquete}`.
- [ ] F005 Crear `auditoriaService.js` con función:
  - `getEventosSqsGlobales(page, size, filters)` → `GET /api/sincronizacion/logs`.
- [ ] F006 Reutilizar `apiClient.js` con JWT, manejo de errores y base URL por variable de entorno.

## 7. Fase 2: Vista de auditoría de eventos SQS

- [ ] F007 Implementar `useEventosSqsLogs.js` con paginación `page/size`.
- [ ] F008 Implementar `EventosSqsFilters.jsx` con filtros en cliente por:
  - ID Paquete.
  - ID Ruta.
  - Event ID.
  - Estado recibido.
  - Estado de procesamiento.
  - Rango de fechas sobre `fechaRecepcion`.
- [ ] F009 Implementar `EventosSqsLogsTable.jsx` con columnas:
  - Event ID.
  - ID Paquete.
  - ID Ruta.
  - Estado recibido.
  - Estado procesamiento.
  - Motivo error.
  - Fecha recepción.
  - Acción para ver payload.
- [ ] F010 Implementar `EstadoProcesamientoBadge.jsx` para distinguir visualmente éxito, duplicado, descarte, error funcional y error técnico.
- [ ] F011 Implementar `PayloadEventoModal.jsx` para visualizar `payloadRecibido` en modo lectura.
- [ ] F012 Resaltar visualmente registros con `ERROR_TECNICO`, `ERROR_ESTADO_NO_MAPEADO` y `ERROR_PAYLOAD_INVALIDO`.

## 8. Fase 3: Historial por paquete

- [ ] F013 Implementar `useHistorialPaquete.js` para consumir historial con paginación.
- [ ] F014 Implementar `useLogsPaquete.js` para consumir logs SQS específicos del paquete.
- [ ] F015 Implementar `PaqueteHistorialPage.jsx` con dos secciones:
  - Línea de tiempo de estados.
  - Tabla de logs SQS del paquete.
- [ ] F016 Implementar `HistorialEstadoTimeline.jsx` mostrando estado, porcentaje de pago derivado, fecha aplicada, fecha evento y `eventId`.
- [ ] F017 Implementar `PaqueteEstadoBadge.jsx` para estados `ENTREGADO`, `DEVUELTO`, `DAÑADO`, `EXTRAVIADO`.
- [ ] F018 No incluir botón para sincronizar manualmente, consultar Gestión ni publicar evento SQS.

## 9. Fase 4: Manejo funcional de errores

- [ ] F019 Mostrar mensaje claro cuando no existan logs para el paquete consultado.
- [ ] F020 Mostrar mensaje `Paquete no encontrado` ante HTTP 404 del backend financiero.
- [ ] F021 Mostrar mensaje de acceso denegado ante HTTP 403.
- [ ] F022 Mostrar mensaje de indisponibilidad temporal ante HTTP 503 del backend financiero.
- [ ] F023 Mostrar `payloadRecibido` en modo lectura; si es nulo, mostrar `"Sin payload registrado"`.
- [ ] F024 Si `payloadRecibido` está malformado, mostrar texto plano sin intentar parsearlo.
- [ ] F025 Evitar que payloads extensos oculten la información principal del usuario.

## 10. Fase 5: Pruebas frontend

- [ ] F026 Test de `EventosSqsLogsTable.jsx` validando render correcto del DTO SQS.
- [ ] F027 Test de `EstadoProcesamientoBadge.jsx` para `PROCESADO`, `DUPLICADO`, `DESCARTADO_ATRASADO`, `ERROR_ESTADO_NO_MAPEADO`, `ERROR_TECNICO`.
- [ ] F028 Test de `PayloadEventoModal.jsx` validando JSON, texto malformado y valor nulo.
- [ ] F029 Test de `HistorialEstadoTimeline.jsx` validando orden cronológico y cálculo de porcentaje.
- [ ] F030 Test de estados vacíos y errores HTTP del backend financiero: 403, 404, 503.
- [ ] F031 Test que verifique que no existen botones ni rutas de sincronización manual.

## 11. Dependencias y orden de ejecución

1. Primero debe existir `apiClient` compartido con interceptor JWT.
2. Luego se implementa la vista de auditoría global de eventos SQS.
3. Después se implementa el historial por paquete.
4. No se debe construir acción manual de sincronización.
5. Los filtros avanzados quedan como cliente o server-side según disponibilidad del backend.
6. Toda referencia visual a `codigoRespuestaHTTP`, timeout HTTP, WireMock o respuesta HTTP del Módulo de Gestión debe eliminarse de esta feature.

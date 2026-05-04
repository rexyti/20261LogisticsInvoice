# Plan Técnico Frontend: Visualizar estado del pago

**Fecha**: 2026-05-03  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Visualizar-Estado-Pago`  
**Plan backend base**: `Specs/Visualizar estado del pago/plan-visualizar-estado-pago.md`  
**Frontend objetivo**: Listado de pagos, detalle de pago y descarga de comprobante PDF

## 1. Resumen

Este plan define la implementación frontend para visualizar el estado de pagos asociados a liquidaciones previamente calculadas.

La funcionalidad es de consulta: el usuario autorizado puede listar pagos visibles, consultar el detalle completo, ver ajustes o penalizaciones y descargar el comprobante PDF generado por backend. El frontend no calcula estados, no modifica pagos y no genera comprobantes por cuenta propia.

El control de acceso y el registro de intentos no autorizados deben resolverse en backend. La UI debe interpretar correctamente respuestas 403, 404 y 503.

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool recomendado**: Vite  
**Cliente HTTP**: Axios  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT  
**Descarga de archivos**: Blob desde Axios  
**Arquitectura**: Feature-based structure

## 3. Endpoints backend consumidos

### 3.1 Listar pagos visibles

```http
GET /api/pagos?page={page}&size={size}&estado={estado}&fechaInicio={fechaInicio}&fechaFin={fechaFin}
```

### 3.2 Consultar estado de pago específico

```http
GET /api/pagos/{id}
```

### 3.3 Consultar detalle completo del pago

```http
GET /api/pagos/{id}/detalle
```

### 3.4 Descargar comprobante

```http
GET /api/pagos/{id}/comprobante
```

Respuesta esperada: `application/pdf`.

## 4. Estructura propuesta frontend

```text
frontend/
├── src/
│   ├── modules/
│   │   └── pagos/
│   │       ├── components/
│   │       │   ├── PagosTable.jsx
│   │       │   ├── PagosFilters.jsx
│   │       │   ├── PagoEstadoBadge.jsx
│   │       │   ├── PagoDetailCard.jsx
│   │       │   ├── PagoAjustesTable.jsx
│   │       │   ├── DownloadComprobanteButton.jsx
│   │       │   └── PagoSearchBar.jsx
│   │       │
│   │       ├── pages/
│   │       │   ├── PagosListPage.jsx
│   │       │   └── PagoDetailPage.jsx
│   │       │
│   │       ├── services/
│   │       │   └── pagosConsultaService.js
│   │       │
│   │       └── hooks/
│   │           ├── usePagos.js
│   │           ├── usePagoDetail.js
│   │           └── useComprobantePago.js
│   │
│   └── shared/
│       ├── components/
│       └── services/
│           └── apiClient.js
```

## 5. Contratos de datos esperados

### 5.1 Item de pago

```json
{
  "idPago": "uuid",
  "idLiquidacion": "uuid",
  "fecha": "2026-04-26T10:35:00",
  "monto": 230000,
  "estado": "PAGADO",
  "motivoRechazo": null
}
```

### 5.2 Detalle de pago

```json
{
  "idPago": "uuid",
  "idLiquidacion": "uuid",
  "idRuta": "uuid",
  "montoBase": 250000,
  "montoNeto": 230000,
  "fecha": "2026-04-26T10:35:00",
  "estado": "PAGADO",
  "ajustesPenalidades": [
    {
      "idAjuste": "uuid",
      "tipoAjuste": "PENALIDAD",
      "monto": 20000,
      "motivo": "Paquete dañado"
    }
  ]
}
```

## 6. Fase 1: Configuración del módulo

- [ ] F001 Crear o reutilizar módulo `modules/pagos`.
- [ ] F002 Registrar ruta `/pagos`.
- [ ] F003 Registrar ruta `/pagos/:idPago`.
- [ ] F004 Crear `pagosConsultaService.js` con métodos:
  - `listarPagos`
  - `obtenerPago`
  - `obtenerDetallePago`
  - `descargarComprobante`
- [ ] F005 Configurar Axios para descargar blobs PDF.
- [ ] F006 Reutilizar helpers de formato monetario y fechas.

## 7. Fase 2: Listado de pagos

- [ ] F007 Implementar `usePagos.js` con paginación, filtros y búsqueda.
- [ ] F008 Implementar `PagosFilters.jsx` con filtros por estado y rango de fechas.
- [ ] F009 Implementar `PagoSearchBar.jsx` para búsqueda por ID Pago o ID Liquidación.
- [ ] F010 Implementar `PagosTable.jsx` con columnas:
  - ID Pago
  - ID Liquidación
  - Fecha
  - Monto
  - Estado
  - Motivo de rechazo si aplica
  - Acciones
- [ ] F011 Implementar `PagoEstadoBadge.jsx` para `PAGADO`, `PENDIENTE`, `RECHAZADO`, `EN_PROCESO`.
- [ ] F012 Navegar al detalle desde cada fila.

## 8. Fase 3: Detalle de pago

- [ ] F013 Implementar `usePagoDetail.js`.
- [ ] F014 Implementar `PagoDetailPage.jsx`.
- [ ] F015 Implementar `PagoDetailCard.jsx` mostrando:
  - ID Pago
  - ID Liquidación
  - ID Ruta
  - Monto Base
  - Monto Neto
  - Fecha
  - Estado
- [ ] F016 Implementar `PagoAjustesTable.jsx` con ajustes y penalizaciones.
- [ ] F017 Mostrar motivo de rechazo cuando estado sea `RECHAZADO`.
- [ ] F018 Mostrar liquidación asociada cuando estado sea `PAGADO`.

## 9. Fase 4: Descarga de comprobante

- [ ] F019 Implementar `useComprobantePago.js`.
- [ ] F020 Implementar `DownloadComprobanteButton.jsx`.
- [ ] F021 Consumir `GET /api/pagos/{id}/comprobante` con `responseType: 'blob'`.
- [ ] F022 Crear descarga segura con nombre sugerido `comprobante-pago-{idPago}.pdf`.
- [ ] F023 Mostrar estado de carga mientras se genera o descarga el PDF.
- [ ] F024 Mostrar error funcional si el comprobante no puede generarse.
- [ ] F025 No construir PDF en frontend; el archivo debe venir del backend.

## 10. Fase 5: Seguridad, estados y errores

- [ ] F026 Manejar HTTP 403 con mensaje de acceso no autorizado.
- [ ] F027 Manejar HTTP 404 con mensaje de pago no encontrado.
- [ ] F028 Manejar HTTP 503 con mensaje de sistema no disponible.
- [ ] F029 Mostrar estado vacío si el usuario no tiene pagos visibles.
- [ ] F030 No mostrar datos parciales si backend niega acceso.
- [ ] F031 Mantener paginación y filtros al volver desde el detalle.

## 11. Fase 6: Pruebas frontend

- [ ] F032 Test de listado de pagos visibles.
- [ ] F033 Test de filtros por estado.
- [ ] F034 Test de detalle con ajustes y penalizaciones.
- [ ] F035 Test de estado `PAGADO` mostrando liquidación asociada.
- [ ] F036 Test de estado `RECHAZADO` mostrando motivo.
- [ ] F037 Test de descarga de PDF con blob.
- [ ] F038 Test de errores 403, 404 y 503.

## 12. Dependencias y orden de ejecución

1. Listado antes de detalle.
2. Detalle antes de descarga de comprobante.
3. Descarga sólo consume PDF del backend.
4. Control de acceso no debe replicarse como única barrera en frontend.
5. La UI debe reflejar mensajes del backend para pago inexistente, acceso no autorizado e indisponibilidad.

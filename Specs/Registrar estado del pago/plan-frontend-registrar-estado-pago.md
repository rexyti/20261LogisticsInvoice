# Plan Técnico Frontend: Registrar estado del pago

**Fecha**: 2026-05-03  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Registrar-Estado-Pago`  
**Plan backend base**: `Specs/Registrar estado del pago/plan-registro-estado-pago.md`  
**Frontend objetivo**: Consulta operativa y auditoría de eventos de pago procesados

## 1. Resumen

Este plan define el frontend relacionado con el registro y sincronización del estado de pago.

El registro del estado de pago se origina mediante eventos asíncronos enviados por una entidad financiera al webhook backend. React no debe enviar eventos financieros manualmente ni simular la entidad bancaria. La responsabilidad del frontend es permitir a usuarios autorizados consultar el estado actual del pago y revisar los eventos de transacción recibidos, procesados, duplicados, rechazados o con error.

La UI debe servir como panel de auditoría operativa y soporte financiero, no como origen del flujo de pago.

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool recomendado**: Vite  
**Cliente HTTP**: Axios  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT  
**Arquitectura**: Feature-based structure

## 3. Endpoints backend consumidos

### 3.1 Consultar estado actual de un pago

```http
GET /api/v1/pagos/{idPago}/estado
```

### 3.2 Consultar eventos de transacción de un pago

```http
GET /api/v1/pagos/{idPago}/eventos
```

### 3.3 Consultar estado de pago desde liquidación

```http
GET /api/v1/liquidaciones/{idLiquidacion}/pago/estado
```

> Nota técnica: el webhook `POST /api/v1/pagos/webhook/estado` pertenece a integración backend con la entidad financiera. No debe ser consumido por la UI operativa.

## 4. Estructura propuesta frontend

```text
frontend/
├── src/
│   ├── modules/
│   │   └── pagos/
│   │       ├── components/
│   │       │   ├── PagoEstadoCard.jsx
│   │       │   ├── PagoEstadoBadge.jsx
│   │       │   ├── EventosTransaccionTable.jsx
│   │       │   ├── EventoProcesamientoBadge.jsx
│   │       │   ├── EventoDetalleModal.jsx
│   │       │   └── BuscarPagoForm.jsx
│   │       │
│   │       ├── pages/
│   │       │   ├── PagoEstadoPage.jsx
│   │       │   └── PagoEventosPage.jsx
│   │       │
│   │       ├── services/
│   │       │   └── pagosService.js
│   │       │
│   │       └── hooks/
│   │           ├── usePagoEstado.js
│   │           └── useEventosPago.js
│   │
│   └── shared/
│       ├── components/
│       └── services/
│           └── apiClient.js
```

## 5. Contratos de datos esperados

### 5.1 Estado actual del pago

```json
{
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "PAGADO",
  "fechaUltimaActualizacion": "2026-04-26T10:35:00",
  "ultimaSecuenciaProcesada": 2
}
```

### 5.2 Evento de transacción

```json
{
  "idEvento": "evt-20260426-001",
  "idTransaccionBanco": "txn-bank-0001",
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estadoSolicitado": "EN_PROCESO",
  "estadoProcesamiento": "PROCESADO",
  "fechaRecepcion": "2026-04-26T10:30:01",
  "fechaEventoBanco": "2026-04-26T10:30:00",
  "secuencia": 1,
  "mensajeError": null
}
```

## 6. Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/pagos`.
- [ ] F002 Registrar ruta `/pagos/estado` para búsqueda por ID Pago.
- [ ] F003 Registrar ruta `/pagos/:idPago/eventos` para trazabilidad.
- [ ] F004 Crear `pagosService.js` con métodos:
  - `obtenerEstadoPago(idPago)`
  - `obtenerEventosPago(idPago)`
  - `obtenerEstadoPagoPorLiquidacion(idLiquidacion)`
- [ ] F005 Reutilizar `apiClient.js` con JWT.
- [ ] F006 Crear badges para estados de pago y estados de procesamiento.

## 7. Fase 2: Consulta de estado actual

- [ ] F007 Implementar `BuscarPagoForm.jsx` para ingresar ID Pago o ID Liquidación.
- [ ] F008 Implementar `usePagoEstado.js` con carga, error y resultado.
- [ ] F009 Implementar `PagoEstadoPage.jsx`.
- [ ] F010 Implementar `PagoEstadoCard.jsx` mostrando:
  - ID Pago
  - ID Liquidación
  - Estado actual
  - Fecha última actualización
  - Última secuencia procesada
- [ ] F011 Implementar `PagoEstadoBadge.jsx` para `PENDIENTE`, `EN_PROCESO`, `PAGADO`, `RECHAZADO`.
- [ ] F012 Mostrar acción de navegación hacia eventos del pago.

## 8. Fase 3: Eventos de transacción

- [ ] F013 Implementar `useEventosPago.js`.
- [ ] F014 Implementar `PagoEventosPage.jsx`.
- [ ] F015 Implementar `EventosTransaccionTable.jsx` con columnas:
  - ID Evento
  - ID Transacción Banco
  - Estado solicitado
  - Estado procesamiento
  - Fecha recepción
  - Fecha evento banco
  - Secuencia
  - Mensaje error
- [ ] F016 Implementar `EventoProcesamientoBadge.jsx` para `RECIBIDO`, `PROCESADO`, `DUPLICADO`, `RECHAZADO`, `ERROR`.
- [ ] F017 Implementar `EventoDetalleModal.jsx` para ver detalle técnico sin saturar la tabla.
- [ ] F018 Ordenar eventos por secuencia y fecha de recepción.

## 9. Fase 4: Restricciones funcionales

- [ ] F019 No exponer formulario para invocar `POST /api/v1/pagos/webhook/estado`.
- [ ] F020 No permitir modificar manualmente estado de pago desde frontend.
- [ ] F021 Mostrar claramente eventos duplicados como idempotentes si backend los reporta así.
- [ ] F022 Mostrar errores funcionales registrados durante procesamiento asíncrono.
- [ ] F023 Manejar pagos inexistentes con mensaje claro.

## 10. Fase 5: Estados y errores

- [ ] F024 Manejar HTTP 400 para ID malformado.
- [ ] F025 Manejar HTTP 403 para acceso no autorizado.
- [ ] F026 Manejar HTTP 404 para pago o liquidación inexistente.
- [ ] F027 Manejar HTTP 409 cuando backend reporte conflicto funcional.
- [ ] F028 Manejar HTTP 503 para indisponibilidad.
- [ ] F029 Mostrar estado vacío si el pago existe pero no tiene eventos visibles.

## 11. Fase 6: Pruebas frontend

- [ ] F030 Test de búsqueda por ID Pago.
- [ ] F031 Test de búsqueda por ID Liquidación.
- [ ] F032 Test de render de estados de pago.
- [ ] F033 Test de tabla de eventos.
- [ ] F034 Test de eventos duplicados y con error.
- [ ] F035 Test de ausencia de botón para webhook.
- [ ] F036 Test de errores 400, 403, 404 y 503.

## 12. Dependencias y orden de ejecución

1. Servicio de pagos antes de páginas.
2. Consulta de estado antes de eventos.
3. Eventos después de tener ID Pago confirmado.
4. No consumir webhook desde frontend.
5. Backend mantiene idempotencia, transiciones y procesamiento asíncrono.

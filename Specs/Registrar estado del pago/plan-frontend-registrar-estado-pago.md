# Plan Técnico Frontend: Registrar estado del pago

**Fecha**: 2026-05-07  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Registrar-Estado-Pago`  
**Plan backend base**: [plan-registro-estado-pago.md](./plan-registro-estado-pago.md)  
**Spec frontend**: [spec-frontend-registrar-estado-pago.md](./spec-frontend-registrar-estado-pago.md)  
**Frontend objetivo**: Consulta del estado actual del pago, trazabilidad de eventos asíncronos y visualización segura de errores de procesamiento

---

## 1. Resumen

Este plan define el frontend asociado al registro asíncrono del estado del pago.

El registro y actualización del estado de pago **no deben ser disparados por React**. La entidad financiera envía eventos al backend mediante webhook o mecanismo asíncrono, y el backend procesa esos eventos en segundo plano.

La UI únicamente permite:

1. Consultar el estado actual del pago.
2. Consultar la trazabilidad de eventos recibidos desde la entidad financiera.
3. Visualizar estados de procesamiento, duplicados, eventos rechazados y errores funcionales.
4. Consultar el estado de pago desde una liquidación, si el backend expone ese endpoint.

No debe existir ningún formulario para crear, actualizar o confirmar manualmente estados de pago.

---

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool**: Vite  
**Cliente HTTP**: Axios  
**Formularios**: No aplica para esta feature, porque la UI no registra ni actualiza pagos  
**Validación cliente**: Validación de parámetros de ruta y manejo de errores HTTP  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT  
**Roles esperados**: `ROLE_ADMIN`, `ROLE_GESTOR_TARIFAS`, `ROLE_TRANSPORTISTA`

---

## 3. Endpoints backend consumidos

### 3.1 Consultar estado actual de un pago

```http
GET /api/v1/pagos/{idPago}/estado
```

Uso: mostrar el estado actual del pago asociado a una liquidación.

### 3.2 Consultar eventos recibidos para un pago

```http
GET /api/v1/pagos/{idPago}/eventos
```

Uso: mostrar trazabilidad de eventos asíncronos recibidos desde la entidad financiera. Solo para `ROLE_ADMIN` y `ROLE_GESTOR_TARIFAS`.

### 3.3 Consultar estado de pago desde una liquidación

```http
GET /api/v1/liquidaciones/{idLiquidacion}/pago/estado
```

Uso: consultar el pago asociado a una liquidación previamente calculada.

> Este endpoint es complementario y debe coordinarse con backend. Si no existe aún, la ruta `/liquidaciones/:idLiquidacion/pago` debe quedar bloqueada o documentada como pendiente.

### 3.4 Endpoint que NO debe consumir React

```http
POST /api/v1/pagos/webhook/estado
```

Uso real: recepción de eventos asíncronos enviados por la entidad financiera.

> Este endpoint no debe ser llamado desde componentes, hooks, páginas ni servicios de React. Su consumo corresponde al banco, pruebas técnicas de backend o Postman, no al frontend productivo.

---

## 4. Contratos de datos esperados

### 4.1 Response de estado actual del pago

```json
{
  "idPago": "8b76a9f5-46f1-4d4f-9a5f-23b4b7cb9812",
  "idLiquidacion": "3a8d8c2f-3322-43f1-a96d-9e7e81f62d91",
  "estado": "PAGADO",
  "fechaUltimaActualizacion": "2026-04-26T10:35:00",
  "ultimaSecuenciaProcesada": 2
}
```

Valores esperados para `estado`:

```text
PENDIENTE
EN_PROCESO
PAGADO
RECHAZADO
```

### 4.2 Response de eventos de transacción

```json
[
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
]
```

Valores esperados para `estadoProcesamiento`:

```text
RECIBIDO
PROCESADO
DUPLICADO
RECHAZADO
ERROR
```

### 4.3 Response de pago no encontrado

```json
{
  "mensaje": "Pago no encontrado"
}
```

La UI debe mostrar un mensaje claro y no exponer detalles internos del backend.

---

## 5. Estructura de archivos

```text
frontend/src/
├── modules/
│   └── pagos/
│       ├── components/
│       │   ├── PagoEstadoResumenCard.jsx        # Tarjeta: idPago, idLiquidacion, estado, fecha, secuencia
│       │   ├── PagoEstadoBadge.jsx              # Badge visual según estado de pago
│       │   ├── EventosTransaccionTable.jsx      # Tabla de eventos asíncronos
│       │   ├── EventoProcesamientoBadge.jsx     # Badge visual según estado de procesamiento
│       │   ├── PagoEmptyState.jsx               # Estado vacío para pago sin eventos o liquidación sin pago
│       │   └── PagoErrorState.jsx               # Mensajes de error controlados
│       │
│       ├── pages/
│       │   ├── PagoEstadoPage.jsx               # Vista principal del estado actual del pago
│       │   ├── PagoEventosPage.jsx              # Vista administrativa de trazabilidad
│       │   └── LiquidacionPagoEstadoPage.jsx    # Consulta de pago desde liquidación
│       │
│       ├── services/
│       │   └── pagoService.js                   # Llamadas HTTP de consulta
│       │
│       ├── hooks/
│       │   ├── usePagoEstado.js                 # Carga del estado actual del pago
│       │   ├── usePagoEventos.js                # Carga de eventos de transacción
│       │   └── useLiquidacionPagoEstado.js      # Carga de pago asociado a liquidación
│       │
│       └── utils/
│           ├── pagoEstadoLabels.js              # Labels legibles para estados de pago
│           └── eventoProcesamientoLabels.js     # Labels legibles para estados de eventos
│
└── shared/
    ├── components/                              # Botones, badges, skeletons, estados vacíos
    └── services/
        └── apiClient.js                         # Axios con interceptores JWT
```

---

## 6. Criterios de éxito frontend

Mapeados al spec:

| SC del Spec | Criterio frontend |
|:------------|:------------------|
| SC-001 | Cuando backend procesa eventos válidos, la UI refleja el estado actualizado al consultar el pago. |
| SC-002 | Los eventos duplicados se visualizan como `DUPLICADO` o evento idempotente sin duplicar información de pago. |
| SC-003 | Los estados `PENDIENTE`, `EN_PROCESO`, `PAGADO` y `RECHAZADO` se muestran correctamente. |
| SC-004 | La tabla de eventos muestra secuencia, fecha del evento banco y estado de procesamiento para identificar eventos desordenados. |
| — | React no llama al endpoint `POST /api/v1/pagos/webhook/estado`. |
| — | No existen formularios de creación o actualización manual de estado de pago. |
| — | La trazabilidad de eventos está restringida a `ROLE_ADMIN` y `ROLE_GESTOR_TARIFAS`. |

---

## Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/pagos` con estructura de carpetas `components`, `pages`, `services`, `hooks` y `utils`.
- [ ] F002 Registrar ruta `/pagos/:idPago/estado` en el router global.
- [ ] F003 Registrar ruta `/pagos/:idPago/eventos` protegida para `ROLE_ADMIN` y `ROLE_GESTOR_TARIFAS`; redirigir a `/403` si el rol no corresponde.
- [ ] F004 Registrar ruta `/liquidaciones/:idLiquidacion/pago` solo si el backend expone la consulta de pago por liquidación.
- [ ] F005 Crear `pagoService.js` con métodos:
  - `getPagoEstado(idPago)`
  - `getPagoEventos(idPago)`
  - `getPagoEstadoByLiquidacion(idLiquidacion)`
- [ ] F006 Verificar que `pagoService.js` no incluya ningún método para consumir `POST /api/v1/pagos/webhook/estado`.
- [ ] F007 Crear utilidades `pagoEstadoLabels.js` y `eventoProcesamientoLabels.js` para centralizar etiquetas visibles.

---

## Fase 2: Visualización del estado actual del pago

- [ ] F008 Implementar `usePagoEstado.js`: llama a `getPagoEstado(idPago)` y expone `{ data, isLoading, error }`.
- [ ] F009 Implementar `PagoEstadoPage.jsx`:
  - Lee `idPago` desde parámetros de ruta.
  - Carga el estado mediante `usePagoEstado`.
  - Muestra skeleton loader mientras carga.
  - Renderiza `PagoEstadoResumenCard` cuando existe data.
  - Muestra mensaje 404 cuando el pago no existe.
  - Muestra error general cuando hay fallo de red o servidor.
  - Muestra botón "Ver eventos de transacción" solo para `ROLE_ADMIN` y `ROLE_GESTOR_TARIFAS`.
- [ ] F010 Implementar `PagoEstadoResumenCard.jsx` mostrando:
  - ID Pago
  - ID Liquidación
  - Estado actual
  - Fecha última actualización
  - Última secuencia procesada
- [ ] F011 Implementar `PagoEstadoBadge.jsx` para mostrar visualmente:
  - `PENDIENTE`
  - `EN_PROCESO`
  - `PAGADO`
  - `RECHAZADO`
- [ ] F012 Manejar estados vacíos y errores mediante `PagoEmptyState.jsx` y `PagoErrorState.jsx`.
- [ ] F013 Confirmar que no existe botón "Registrar pago", "Actualizar estado" o "Confirmar pago" en esta vista.

---

## Fase 3: Trazabilidad de eventos de transacción

- [ ] F014 Implementar `usePagoEventos.js`: llama a `getPagoEventos(idPago)` y expone `{ eventos, isLoading, error }`.
- [ ] F015 Implementar `PagoEventosPage.jsx`:
  - Lee `idPago` desde parámetros de ruta.
  - Valida acceso por rol.
  - Carga eventos con `usePagoEventos`.
  - Muestra skeleton loader durante la carga.
  - Renderiza `EventosTransaccionTable`.
  - Muestra mensaje vacío si no hay eventos.
  - Muestra mensaje 404 si el pago no existe.
- [ ] F016 Implementar `EventosTransaccionTable.jsx` con columnas:
  - ID Evento
  - ID Transacción Banco
  - Estado solicitado
  - Estado procesamiento
  - Fecha recepción
  - Fecha evento banco
  - Secuencia
  - Mensaje error
- [ ] F017 Implementar `EventoProcesamientoBadge.jsx` para:
  - `RECIBIDO`
  - `PROCESADO`
  - `DUPLICADO`
  - `RECHAZADO`
  - `ERROR`
- [ ] F018 Manejar evento `DUPLICADO` como idempotencia, no como fallo crítico.
- [ ] F019 Manejar evento `RECHAZADO` como advertencia funcional.
- [ ] F020 Manejar evento `ERROR` como error funcional o técnico visible para administrador/gestor.

---

## Fase 4: Consulta de pago desde liquidación

- [ ] F021 Implementar `useLiquidacionPagoEstado.js`: llama a `getPagoEstadoByLiquidacion(idLiquidacion)` si el endpoint existe.
- [ ] F022 Implementar `LiquidacionPagoEstadoPage.jsx`:
  - Lee `idLiquidacion` desde parámetros de ruta.
  - Consulta el estado de pago asociado.
  - Muestra skeleton loader durante la carga.
  - Si hay pago asociado, renderiza `PagoEstadoResumenCard`.
  - Si no hay pago asociado, muestra "Esta liquidación aún no tiene pago asociado."
  - Si la liquidación no existe, muestra "No se encontró la liquidación indicada."
- [ ] F023 Si el endpoint `GET /api/v1/liquidaciones/{idLiquidacion}/pago/estado` no existe, documentar la dependencia y no crear una llamada falsa desde frontend.
- [ ] F024 Integrar navegación desde la vista de liquidación calculada hacia el estado de pago solo cuando exista `idPago` o endpoint por liquidación disponible.

---

## Fase 5: Estados y errores globales

- [ ] F025 Manejar HTTP 400: mostrar "La solicitud no es válida."
- [ ] F026 Manejar HTTP 403: redirigir a `/403`.
- [ ] F027 Manejar HTTP 404 en estado de pago: mostrar "No se encontró el pago indicado."
- [ ] F028 Manejar HTTP 404 en pago por liquidación: mostrar "Esta liquidación aún no tiene pago asociado." o "No se encontró la liquidación indicada", según respuesta backend.
- [ ] F029 Manejar HTTP 409 en eventos: mostrar "El evento fue rechazado por conflicto de estado o duplicidad."
- [ ] F030 Manejar HTTP 503: toast "El servicio no está disponible. Intenta nuevamente en unos momentos."
- [ ] F031 Manejar errores de red: toast "No fue posible comunicarse con el servidor."
- [ ] F032 Implementar skeleton loaders en:
  - tarjeta de resumen de pago
  - tabla de eventos
  - consulta de pago por liquidación
- [ ] F033 Evitar mostrar stack traces, nombres de excepciones internas o mensajes técnicos no tratados.

---

## Fase 6: Restricciones de seguridad y consistencia UI

- [ ] F034 Verificar que `ROLE_TRANSPORTISTA` no pueda acceder a `/pagos/:idPago/eventos`.
- [ ] F035 Verificar que `ROLE_TRANSPORTISTA` solo vea pagos asociados a sus liquidaciones, si el backend expone esa restricción.
- [ ] F036 Verificar que `ROLE_ADMIN` y `ROLE_GESTOR_TARIFAS` puedan ver trazabilidad de eventos.
- [ ] F037 Verificar que ninguna pantalla tenga formularios de creación o actualización de estado de pago.
- [ ] F038 Verificar que ningún hook o service llame al endpoint de webhook.
- [ ] F039 Verificar que la UI trate `DUPLICADO` como evento idempotente y no como creación duplicada.
- [ ] F040 Verificar que la UI respete el estado devuelto por backend sin recalcular ni inferir transiciones por cuenta propia.

---

## Fase 7: Pruebas frontend

- [ ] F041 Test: `PagoEstadoPage` muestra skeleton loader mientras carga.
- [ ] F042 Test: `PagoEstadoPage` muestra resumen cuando el estado es `PENDIENTE`.
- [ ] F043 Test: `PagoEstadoPage` muestra resumen cuando el estado es `EN_PROCESO`.
- [ ] F044 Test: `PagoEstadoPage` muestra resumen cuando el estado es `PAGADO`.
- [ ] F045 Test: `PagoEstadoPage` muestra resumen cuando el estado es `RECHAZADO`.
- [ ] F046 Test: `PagoEstadoPage` muestra mensaje "No se encontró el pago indicado" cuando backend retorna 404.
- [ ] F047 Test: `PagoEstadoPage` muestra botón "Ver eventos de transacción" solo para `ROLE_ADMIN` y `ROLE_GESTOR_TARIFAS`.
- [ ] F048 Test: `PagoEventosPage` redirige a `/403` para `ROLE_TRANSPORTISTA`.
- [ ] F049 Test: `PagoEventosPage` renderiza tabla de eventos para `ROLE_ADMIN`.
- [ ] F050 Test: `EventosTransaccionTable` muestra eventos `PROCESADO`, `DUPLICADO`, `RECHAZADO` y `ERROR`.
- [ ] F051 Test: evento `DUPLICADO` se muestra como idempotente y no como error crítico.
- [ ] F052 Test: evento `RECHAZADO` con mensaje de error se muestra en la columna correspondiente.
- [ ] F053 Test: `LiquidacionPagoEstadoPage` muestra estado de pago cuando existe pago asociado.
- [ ] F054 Test: `LiquidacionPagoEstadoPage` muestra mensaje cuando la liquidación no tiene pago asociado.
- [ ] F055 Test: no existe botón "Registrar pago" en ninguna vista.
- [ ] F056 Test: no existe botón "Actualizar estado" en ninguna vista.
- [ ] F057 Test: `pagoService.js` no contiene llamada a `POST /api/v1/pagos/webhook/estado`.
- [ ] F058 Test: errores HTTP 403, 404, 409 y 503 muestran el comportamiento esperado.

---

## 7. Dependencias y orden de ejecución

1. **Fase 1 primero**: el módulo, rutas y servicios de consulta deben existir antes de implementar pantallas.
2. **Fase 2 antes que Fase 3**: la vista de estado actual es la base para la trazabilidad.
3. **Fase 3 requiere autorización**: la tabla de eventos debe protegerse por rol desde el primer momento.
4. **Fase 4 depende de backend**: la consulta por liquidación solo debe implementarse si existe endpoint real.
5. **Fase 5 centraliza errores**: los errores HTTP deben manejarse de forma coherente en todas las pantallas.
6. **Fase 6 valida restricciones absolutas**: la UI no registra ni actualiza estados de pago.
7. **Fase 7 asegura cumplimiento**: los tests deben comprobar tanto la visualización como la ausencia de acciones manuales.

---

## 8. Restricción crítica

React no debe consumir este endpoint en producción:

```http
POST /api/v1/pagos/webhook/estado
```

Ese endpoint pertenece al flujo backend-banco. Puede usarse en Postman o pruebas de backend para simular eventos, pero no debe estar presente en servicios, hooks ni componentes del frontend.

La única responsabilidad de esta feature en frontend es consultar y visualizar:

```http
GET /api/v1/pagos/{idPago}/estado
GET /api/v1/pagos/{idPago}/eventos
GET /api/v1/liquidaciones/{idLiquidacion}/pago/estado
```

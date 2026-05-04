# Plan Técnico Frontend: Calcular liquidación

**Fecha**: 2026-05-04  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Calcular-Liquidacion`  
**Plan backend base**: [plan-calcular-liquidacion.md](./plan-calcular-liquidacion.md)  
**Spec frontend**: [spec-frontend-calcular-liquidacion.md](./spec-frontend-calcular-liquidacion.md)  
**Frontend objetivo**: Visualización de liquidación calculada, historial de auditoría y panel administrativo de recálculo autorizado

---

## 1. Resumen

Este plan define el frontend asociado al cálculo automático de liquidaciones.

El cálculo inicial **no debe ser disparado por React**. El sistema lo activa automáticamente al recibir el evento de cierre de ruta. La UI únicamente consulta y muestra la liquidación ya calculada.

El único flujo con acción desde frontend es el recálculo administrativo, permitido únicamente si existe una solicitud de revisión aceptada. El administrador también puede ver el historial de auditoría completo de cada liquidación.

Los transportistas ven solo el resultado de su liquidación. Los administradores ven el resultado, el historial de auditoría y pueden operar el recálculo.

---

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool**: Vite  
**Cliente HTTP**: Axios  
**Formularios**: React Hook Form  
**Validación cliente**: Zod  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT  
**Roles esperados**: `ROLE_ADMIN`, `ROLE_TRANSPORTISTA`

---

## 3. Endpoints backend consumidos

### 3.1 Consultar liquidación calculada

```http
GET /api/liquidaciones/{id}
```

Uso: mostrar resumen y ajustes de la liquidación ya calculada.

### 3.2 Consultar historial de auditoría

```http
GET /api/liquidaciones/{id}/auditoria
```

Uso: mostrar al administrador el historial completo de operaciones (cálculo y recálculos) sobre una liquidación. Solo accesible para `ROLE_ADMIN`.

> Este endpoint debe coordinarse con el backend; si no existe aún, bloquearlo hasta que esté disponible.

### 3.3 Validar solicitud de revisión aceptada

```http
GET /api/liquidaciones/{id}/revision-aceptada
```

Uso: determinar si debe mostrarse el formulario de recálculo. Bloquear UI si no existe solicitud aceptada.

### 3.4 Recalcular liquidación

```http
PUT /api/liquidaciones/{id}/recalcular
```

Uso: enviar nuevos ajustes para el recálculo. Protegido para `ROLE_ADMIN` únicamente.

---

## 4. Contratos de datos esperados

### 4.1 Response de detalle de liquidación

```json
{
  "idLiquidacion": "uuid",
  "idRuta": "uuid",
  "idContrato": "uuid",
  "estadoLiquidacion": "CALCULADA",
  "descripcionError": null,
  "montoBruto": 250000,
  "montoNeto": 230000,
  "fechaCalculo": "2026-04-08T16:00:00",
  "ajustes": [
    {
      "id": "uuid",
      "tipo": "PENALIDAD",
      "monto": 20000,
      "motivo": "Paquete dañado"
    }
  ]
}
```

> `estadoLiquidacion` puede ser: `CALCULADA`, `ERROR`, `EN_REVISION`. La UI debe manejar los tres.  
> `descripcionError` se incluye cuando el estado es `ERROR`.

### 4.2 Response de auditoría

```json
[
  {
    "idRegistro": "uuid",
    "operacion": "CALCULO",
    "valorAnterior": null,
    "valorNuevo": 230000,
    "fechaOperacion": "2026-04-08T16:00:00",
    "responsable": "SISTEMA"
  },
  {
    "idRegistro": "uuid",
    "operacion": "RECALCULO",
    "valorAnterior": 230000,
    "valorNuevo": 245000,
    "fechaOperacion": "2026-04-10T10:30:00",
    "responsable": "admin@empresa.com"
  }
]
```

### 4.3 Request de recálculo

```json
{
  "ajustes": [
    {
      "tipo": "BONIFICACION",
      "monto": 15000,
      "motivo": "Revisión aprobada por soporte"
    }
  ]
}
```

---

## 5. Estructura de archivos

```text
frontend/src/
├── modules/
│   └── liquidacion/
│       ├── components/
│       │   ├── LiquidacionResumenCard.jsx       # Tarjeta: id, ruta, estado, montos, fecha
│       │   ├── LiquidacionDetalleTable.jsx      # Tabla desglosada de paradas/paquetes (si el backend la expone)
│       │   ├── AjustesTable.jsx                 # Tabla de ajustes: tipo, monto, motivo
│       │   ├── AuditoriaTable.jsx               # Tabla de auditoría (solo admin)
│       │   ├── RecalculoForm.jsx                # Formulario dinámico de ajustes para recálculo
│       │   ├── AjusteFormRow.jsx                # Fila individual del formulario de ajuste
│       │   └── RecalculoConfirmModal.jsx        # Modal de confirmación antes del PUT
│       │
│       ├── pages/
│       │   ├── LiquidacionResultadoPage.jsx     # Vista principal de liquidación
│       │   └── RecalcularLiquidacionPage.jsx    # Panel administrativo de recálculo
│       │
│       ├── services/
│       │   └── liquidacionService.js            # Llamadas HTTP al backend
│       │
│       ├── hooks/
│       │   ├── useLiquidacionDetalle.js         # Carga y estado de la liquidación
│       │   ├── useAuditoriaLiquidacion.js       # Carga del historial de auditoría
│       │   ├── useRevisionAceptada.js           # Valida si existe revisión aceptada
│       │   └── useRecalculoLiquidacion.js       # Envío del PUT de recálculo
│       │
│       └── validators/
│           └── recalculoSchema.js               # Validación Zod del formulario de ajustes
│
└── shared/
    ├── components/                              # Botones, modales, badges genéricos
    └── services/
        └── apiClient.js                         # Axios con interceptores JWT
```

---

## 6. Criterios de éxito frontend

Mapeados a los SC del spec:

| SC del Spec | Criterio frontend                                                                                        |
|:------------|:---------------------------------------------------------------------------------------------------------|
| SC-002      | El valor mostrado en UI coincide exactamente con el devuelto por el backend (sin redondeos propios).     |
| SC-004      | La tabla de auditoría muestra todas las entradas del backend: valor anterior, nuevo, fecha, responsable. |
| —           | El botón "Calcular" no existe en ninguna pantalla. La UI nunca llama a un endpoint de cálculo.           |
| —           | El formulario de recálculo solo se muestra si el backend confirma solicitud de revisión aceptada.        |
| —           | La pantalla de recálculo es inaccesible para `ROLE_TRANSPORTISTA` (redirect a `/403`).                   |

---

## Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/liquidacion` con su estructura de carpetas completa.
- [ ] F002 Registrar ruta `/liquidaciones/:id` en el router global (accesible para ambos roles).
- [ ] F003 Registrar ruta `/liquidaciones/:id/recalcular` en el router con Route Guard que valida `ROLE_ADMIN`; redirige a `/403` si el rol no corresponde.
- [ ] F004 Crear `liquidacionService.js` con los cuatro métodos: `getLiquidacion(id)`, `getAuditoria(id)`, `getRevisionAceptada(id)`, `recalcularLiquidacion(id, ajustes)`.
- [ ] F005 Crear `recalculoSchema.js` con validación Zod: lista mínima de 1 ajuste, tipo requerido (enum), monto positivo obligatorio, motivo no vacío obligatorio.

---

## Fase 2: Visualización de liquidación calculada

- [ ] F006 Implementar `useLiquidacionDetalle.js`: llama a `getLiquidacion(id)`, expone `{ data, isLoading, error }`.
- [ ] F007 Implementar `LiquidacionResultadoPage.jsx`:
  - Carga la liquidación con `useLiquidacionDetalle`.
  - Muestra skeleton loader durante la carga.
  - Renderiza `LiquidacionResumenCard`, `AjustesTable` cuando el estado es `CALCULADA`.
  - Muestra mensaje de error descriptivo cuando el estado es `ERROR` (incluir `descripcionError`).
  - Muestra mensaje "Liquidación no encontrada" para HTTP 404.
  - Renderiza `AuditoriaTable` solo si el usuario tiene `ROLE_ADMIN`.
  - Renderiza botón "Recalcular" solo si `ROLE_ADMIN` y estado es `CALCULADA`.
- [ ] F008 Implementar `LiquidacionResumenCard.jsx` mostrando: ID Liquidación, ID Ruta, Estado (badge de color), Monto bruto, Monto neto, Fecha cálculo.
- [ ] F009 Implementar `AjustesTable.jsx`: columnas Tipo, Monto, Motivo. Muestra mensaje vacío si no hay ajustes.
- [ ] F010 Implementar `LiquidacionDetalleTable.jsx`: tabla con el desglose por paradas/paquetes si el backend lo expone en el response. Si el backend no incluye ese detalle en este endpoint, omitir el componente y documentarlo.
- [ ] F011 Manejar estado `ERROR` de liquidación: mostrar mensaje "Esta liquidación presenta un error: {descripcionError}. Contactá al administrador." sin mostrar montos.
- [ ] F012 Confirmar que no existe ningún botón de cálculo manual en esta página.

---

## Fase 3: Historial de auditoría (solo ROLE_ADMIN)

- [ ] F013 Implementar `useAuditoriaLiquidacion.js`: llama a `getAuditoria(id)`, expone `{ registros, isLoading, error }`.
- [ ] F014 Implementar `AuditoriaTable.jsx`: columnas Operación, Valor anterior, Valor nuevo, Fecha, Responsable. Muestra mensaje vacío si el array viene vacío.
- [ ] F015 Integrar `AuditoriaTable` en `LiquidacionResultadoPage.jsx` condicional a `ROLE_ADMIN`.
- [ ] F016 Manejar error al cargar auditoría de forma independiente (no bloquea la carga principal de la liquidación).

---

## Fase 4: Validación previa al recálculo

- [ ] F017 Implementar `useRevisionAceptada.js`: llama a `getRevisionAceptada(id)`, expone `{ aceptada, isLoading, error }`.
- [ ] F018 En `RecalcularLiquidacionPage.jsx`, al montar el componente, consultar `useRevisionAceptada` antes de mostrar cualquier formulario.
- [ ] F019 Si no existe solicitud aceptada: mostrar mensaje "No es posible recalcular. Esta liquidación no tiene una solicitud de revisión aceptada." y botón "Volver" a `/liquidaciones/:id`.
- [ ] F020 Si existe solicitud aceptada: mostrar resumen de la liquidación (readonly) y el formulario de ajustes.

---

## Fase 5: Panel de recálculo administrativo

- [ ] F021 Implementar `RecalculoForm.jsx`: lista dinámica de `AjusteFormRow`, botón "+ Agregar ajuste", botón "Eliminar" por fila, botón "Recalcular" habilitado solo si el formulario es válido según Zod.
- [ ] F022 Implementar `AjusteFormRow.jsx`: select de Tipo (PENALIDAD / BONIFICACION), input de Monto (número positivo), input de Motivo (texto obligatorio con validación inline).
- [ ] F023 Implementar `RecalculoConfirmModal.jsx`: muestra resumen de ajustes ingresados, botones "Confirmar" y "Cancelar". El botón "Recalcular" del formulario abre el modal; el `PUT` se ejecuta solo al confirmar en el modal.
- [ ] F024 Implementar `useRecalculoLiquidacion.js`: envía el `PUT`, expone `{ recalcular, isLoading, error, success }`.
- [ ] F025 Al confirmar en el modal: mostrar spinner en botón "Confirmar", deshabilitar todos los campos mientras el PUT está en vuelo.
- [ ] F026 Al éxito del recálculo: mostrar toast "Liquidación recalculada correctamente." y redirigir automáticamente a `/liquidaciones/:id`.
- [ ] F027 Al error del backend: mostrar toast de error con el mensaje del backend. No redirigir; dejar el formulario con los datos ingresados.

---

## Fase 6: Estados y errores globales

- [ ] F028 Manejar HTTP 400: toast "Los ajustes ingresados no son válidos. Revisá los montos y motivos."
- [ ] F029 Manejar HTTP 403: redirigir a `/403`.
- [ ] F030 Manejar HTTP 404: mensaje inline "No se encontró ninguna liquidación con el ID indicado."
- [ ] F031 Manejar HTTP 409: toast "No es posible ejecutar el recálculo en este momento. Verificá el estado de la solicitud."
- [ ] F032 Manejar HTTP 503: toast "El servicio no está disponible. Intentá nuevamente en unos momentos."
- [ ] F033 Implementar skeleton loaders en: tarjeta de resumen, tabla de ajustes, tabla de auditoría, y mientras se valida la revisión aceptada.

---

## Fase 7: Pruebas frontend

- [ ] F034 Test: `LiquidacionResultadoPage` muestra resumen y ajustes cuando el estado es `CALCULADA`.
- [ ] F035 Test: `LiquidacionResultadoPage` muestra mensaje de error descriptivo cuando el estado es `ERROR`.
- [ ] F036 Test: `LiquidacionResultadoPage` muestra mensaje "no encontrada" cuando el backend retorna 404.
- [ ] F037 Test: el botón "Calcular" no existe en ninguna vista.
- [ ] F038 Test: `AuditoriaTable` se renderiza solo cuando el rol es `ROLE_ADMIN`.
- [ ] F039 Test: el botón "Recalcular" se renderiza solo cuando el rol es `ROLE_ADMIN` y el estado es `CALCULADA`.
- [ ] F040 Test: `RecalcularLiquidacionPage` bloquea el formulario y muestra mensaje si no hay revisión aceptada.
- [ ] F041 Test: la ruta `/liquidaciones/:id/recalcular` redirige a `/403` para `ROLE_TRANSPORTISTA`.
- [ ] F042 Test: validación de `motivo` obligatorio en `AjusteFormRow`.
- [ ] F043 Test: validación de `monto` positivo en `AjusteFormRow`.
- [ ] F044 Test: `RecalculoConfirmModal` se abre al presionar "Recalcular" y no envía el PUT hasta confirmar.
- [ ] F045 Test: recálculo exitoso muestra toast y redirige a `/liquidaciones/:id`.
- [ ] F046 Test: errores HTTP 403, 404 y 409 muestran el mensaje correcto sin romper la UI.

---

## 8. Dependencias y orden de ejecución

1. **Fase 1 primero**: el módulo, el router y el servicio deben existir antes que cualquier componente.
2. **Fase 2 antes que Fase 3**: la vista principal de liquidación es prerequisito de la sección de auditoría.
3. **Fase 4 antes que Fase 5**: la validación de revisión aceptada debe funcionar antes de construir el formulario de recálculo.
4. **Fase 5 antes que Fase 6**: los estados y errores globales se validan en conjunto con el flujo completo de recálculo.
5. **El frontend no dispara el cálculo automático**: ningún componente de React llama a un endpoint de cálculo. Esta restricción es absoluta y debe verificarse en code review.
6. **Backend mantiene la fuente de verdad**: cálculo, auditoría, permisos y validaciones viven en el backend. La UI solo refleja ese estado.
7. **`LiquidacionDetalleTable.jsx`**: confirmar con el backend si el response de `GET /api/liquidaciones/{id}` incluye el desglose por paradas antes de implementar el componente.
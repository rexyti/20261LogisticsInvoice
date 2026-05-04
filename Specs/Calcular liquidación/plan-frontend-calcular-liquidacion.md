# Plan Técnico Frontend: Calcular liquidación

**Fecha**: 2026-05-03  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Calcular-Liquidacion`  
**Plan backend base**: `Specs/Calcular liquidación/plan-calcular-liquidacion.md`  
**Frontend objetivo**: Visualización de liquidación calculada y panel administrativo de recálculo autorizado

## 1. Resumen

Este plan define el frontend asociado al cálculo automático de liquidaciones.

El cálculo inicial no debe ser disparado por React. Según el backend, el cálculo se activa automáticamente cuando el sistema recibe el evento de cierre de ruta y consulta los estados finales de los paquetes. La UI únicamente consulta y muestra la liquidación ya calculada.

El único flujo con acción desde frontend es el recálculo administrativo, permitido únicamente si existe una solicitud de revisión aceptada. En ese caso, el administrador puede ingresar ajustes, confirmar la operación y consumir el endpoint de recálculo.

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool recomendado**: Vite  
**Cliente HTTP**: Axios  
**Formularios**: React Hook Form  
**Validación cliente**: Zod o Yup  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT  
**Roles esperados**: `ROLE_ADMIN`, `ROLE_TRANSPORTISTA`

## 3. Endpoints backend consumidos

### 3.1 Consultar liquidación calculada

```http
GET /api/liquidaciones/{id}
```

Uso frontend:

- Mostrar resultado de liquidación calculada.
- Consultar detalle desde vistas de liquidación o pago.

### 3.2 Recalcular liquidación

```http
PUT /api/liquidaciones/{id}/recalcular
```

Uso frontend:

- Permitir que un administrador ingrese ajustes y solicite recálculo.
- Debe estar protegido para `ROLE_ADMIN`.

### 3.3 Validar solicitud de revisión aceptada

```http
GET /api/liquidaciones/{id}/revision-aceptada
```

Uso frontend:

- Determinar si debe mostrarse el formulario de recálculo.
- Bloquear UI si no existe solicitud aceptada.

> Nota técnica: el backend define la validación como obligatoria antes del recálculo. Este endpoint permite que la UI refleje esa regla antes de intentar el `PUT`.

## 4. Estructura propuesta frontend

```text
frontend/
├── src/
│   ├── modules/
│   │   └── liquidacion/
│   │       ├── components/
│   │       │   ├── LiquidacionResumenCard.jsx
│   │       │   ├── LiquidacionDetalleTable.jsx
│   │       │   ├── AjustesTable.jsx
│   │       │   ├── RecalculoForm.jsx
│   │       │   ├── AjusteFormRow.jsx
│   │       │   └── RecalculoConfirmModal.jsx
│   │       │
│   │       ├── pages/
│   │       │   ├── LiquidacionResultadoPage.jsx
│   │       │   └── RecalcularLiquidacionPage.jsx
│   │       │
│   │       ├── services/
│   │       │   └── liquidacionService.js
│   │       │
│   │       ├── hooks/
│   │       │   ├── useLiquidacionDetalle.js
│   │       │   ├── useRevisionAceptada.js
│   │       │   └── useRecalculoLiquidacion.js
│   │       │
│   │       └── validators/
│   │           └── recalculoSchema.js
│   │
│   └── shared/
│       ├── components/
│       └── services/
│           └── apiClient.js
```

## 5. Contratos de datos esperados

### 5.1 Detalle de liquidación

```json
{
  "idLiquidacion": "uuid",
  "idRuta": "uuid",
  "estadoLiquidacion": "CALCULADA",
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

### 5.2 Request de recálculo

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

## 6. Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/liquidacion`.
- [ ] F002 Registrar ruta `/liquidaciones/:id` para resultado.
- [ ] F003 Registrar ruta `/liquidaciones/:id/recalcular` protegida para admin.
- [ ] F004 Crear `liquidacionService.js` con métodos de detalle, validación de revisión y recálculo.
- [ ] F005 Crear `recalculoSchema.js` con validación de ajustes.

## 7. Fase 2: Visualización de liquidación calculada

- [ ] F006 Implementar `useLiquidacionDetalle.js`.
- [ ] F007 Implementar `LiquidacionResultadoPage.jsx`.
- [ ] F008 Implementar `LiquidacionResumenCard.jsx` mostrando:
  - ID Liquidación
  - ID Ruta
  - Estado
  - Monto bruto
  - Monto neto
  - Fecha cálculo
- [ ] F009 Implementar `AjustesTable.jsx` con tipo, monto y motivo.
- [ ] F010 Mostrar mensaje cuando la liquidación aún no exista o no esté calculada.
- [ ] F011 No incluir botón de calcular liquidación manual.

## 8. Fase 3: Validación previa al recálculo

- [ ] F012 Implementar `useRevisionAceptada.js`.
- [ ] F013 Al cargar `/liquidaciones/:id/recalcular`, consultar si existe solicitud aceptada.
- [ ] F014 Si no existe solicitud aceptada, bloquear formulario y mostrar mensaje funcional.
- [ ] F015 Si existe solicitud aceptada, mostrar formulario de ajustes.
- [ ] F016 Validar rol `ROLE_ADMIN` en la ruta y en componentes visibles.

## 9. Fase 4: Panel de recálculo administrativo

- [ ] F017 Implementar `RecalculoForm.jsx` con lista dinámica de ajustes.
- [ ] F018 Implementar `AjusteFormRow.jsx` con campos:
  - Tipo ajuste
  - Monto
  - Motivo
- [ ] F019 Validar que motivo sea obligatorio.
- [ ] F020 Validar que monto sea positivo.
- [ ] F021 Implementar `RecalculoConfirmModal.jsx` antes de enviar el `PUT`.
- [ ] F022 Implementar `useRecalculoLiquidacion.js` para enviar solicitud y manejar respuesta.
- [ ] F023 Mostrar resultado actualizado al finalizar.
- [ ] F024 Mostrar error si backend rechaza por ausencia de revisión aceptada.

## 10. Fase 5: Estados y errores

- [ ] F025 Manejar HTTP 400 para ajustes inválidos.
- [ ] F026 Manejar HTTP 403 si usuario no es administrador.
- [ ] F027 Manejar HTTP 404 si liquidación no existe.
- [ ] F028 Manejar HTTP 409 si el recálculo no es permitido por estado.
- [ ] F029 Manejar HTTP 503 para indisponibilidad del sistema.
- [ ] F030 Implementar skeleton loaders en detalle y recálculo.

## 11. Fase 6: Pruebas frontend

- [ ] F031 Test de detalle de liquidación calculada.
- [ ] F032 Test de ausencia de botón de cálculo manual.
- [ ] F033 Test de bloqueo del formulario si no hay revisión aceptada.
- [ ] F034 Test de validación de motivo obligatorio.
- [ ] F035 Test de modal de confirmación.
- [ ] F036 Test de recálculo exitoso.
- [ ] F037 Test de errores 403, 404 y 409.

## 12. Dependencias y orden de ejecución

1. Detalle de liquidación antes que recálculo.
2. Validación de revisión aceptada antes de formulario.
3. Formulario de recálculo sólo para `ROLE_ADMIN`.
4. No exponer cálculo automático como acción manual en UI.
5. El backend mantiene la fuente de verdad de cálculo, auditoría y permisos.

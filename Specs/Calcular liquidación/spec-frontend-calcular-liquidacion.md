# Frontend Spec: Calcular liquidación

**Created**: 2026-05-04  
**Spec base**: [Calcular liquidación.md](./Calcular%20liquidaci%C3%B3n.md)  
**Plan de implementación**: [plan-frontend-calcular-liquidacion.md](./plan-frontend-calcular-liquidacion.md)

---

## 1. Contexto de usuario

El frontend de esta feature tiene dos responsabilidades:

1. **Mostrar el resultado** de la liquidación calculada automáticamente por el sistema al recibir el cierre de ruta. El usuario nunca activa este cálculo desde la UI.
2. **Permitir al administrador recalcular** una liquidación existente, pero únicamente cuando existe una solicitud de revisión aceptada. Fuera de ese caso, la UI bloquea la acción.

El transportista solo puede ver su liquidación. El administrador puede ver la liquidación y operar el recálculo.

---

## 2. Roles y acceso

| Pantalla                    | ROLE_TRANSPORTISTA | ROLE_ADMIN |
|:----------------------------|:------------------:|:----------:|
| Ver resultado de liquidación | ✅                 | ✅          |
| Ver historial de auditoría   | ❌                 | ✅          |
| Acceder al panel de recálculo | ❌                | ✅          |
| Ejecutar recálculo           | ❌                 | ✅          |

---

## 3. Flujos de usuario

### Flujo A — Transportista consulta su liquidación

```
[Inicio] → /liquidaciones/:id
  ├── Estado CALCULADA → Muestra resumen + detalle de ajustes
  ├── Estado ERROR → Muestra mensaje de error con código y descripción // esto en el apartado visual no deberia mencionarse, en el front debe mostrar un error sin codigo solo con la descripcion del error
  └── Estado no encontrada (404) → Muestra mensaje "Liquidación no encontrada"
```

### Flujo B — Administrador visualiza liquidación con historial

```
[Inicio] → /liquidaciones/:id
  ├── Muestra resumen + detalle de ajustes (igual que transportista)
  ├── Muestra tabla de historial de auditoría
  └── Muestra botón "Recalcular" si la liquidación está en estado CALCULADA
      └── Botón navega a /liquidaciones/:id/recalcular
```

### Flujo C — Administrador recalcula liquidación

```
[Accede a /liquidaciones/:id/recalcular]
  ├── Consulta si existe solicitud de revisión aceptada
  │   ├── NO existe → Muestra mensaje de bloqueo, sin formulario
  │   └── SÍ existe → Muestra formulario de ajustes
  │       ├── Agrega uno o más ajustes (tipo, monto, motivo obligatorio)
  │       ├── Presiona "Recalcular"
  │       └── Modal de confirmación
  │           ├── Confirma → PUT /api/liquidaciones/{id}/recalcular
  │           │   ├── Éxito → Redirige a /liquidaciones/:id con nuevo valor
  │           │   └── Error backend → Muestra mensaje de error
  │           └── Cancela → Vuelve al formulario sin cambios
```

---

## 4. Pantallas

### 4.1 Pantalla: Resultado de liquidación (`/liquidaciones/:id`)

**Descripción**: Vista principal de consulta. El usuario llega aquí desde el listado de liquidaciones o desde un enlace directo.

**Estados de pantalla:**

| Estado         | Qué muestra la UI                                                        |
|:---------------|:-------------------------------------------------------------------------|
| Cargando       | Skeleton loader en tarjeta de resumen y tabla de ajustes                  |
| CALCULADA      | Tarjeta de resumen + tabla de ajustes + (admin) historial de auditoría   |
| ERROR          | Mensaje: "Esta liquidación presenta un error: {descripcion}. Contacta al administrador." |
| 404            | Mensaje: "No se encontró ninguna liquidación con el ID indicado."        |
| Error de red   | Mensaje: "No fue posible cargar la liquidación. Intenta nuevamente."     |

**Tarjeta de resumen muestra:**
- ID Liquidación
- ID Ruta
- Nombre del transportista (si el backend lo incluye en el response)
- Estado de la liquidación (con badge de color)
- Monto bruto
- Monto neto
- Fecha de cálculo

**Tabla de ajustes muestra:**
- Tipo (PENALIDAD / BONIFICACION)
- Monto
- Motivo

**Tabla de historial de auditoría (solo ROLE_ADMIN):**
- Operación (Cálculo / Recálculo)
- Valor anterior
- Valor nuevo
- Fecha
- Responsable (Sistema o nombre del administrador)

**Botón "Recalcular" (solo ROLE_ADMIN, solo si estado = CALCULADA):**
- Navega a `/liquidaciones/:id/recalcular`

---

### 4.2 Pantalla: Panel de recálculo (`/liquidaciones/:id/recalcular`)

**Descripción**: Exclusiva para administradores. Permite ingresar ajustes adicionales y ejecutar el recálculo.

**Acceso bloqueado si:** el usuario no tiene `ROLE_ADMIN` → redirige a `/403`.

**Estados de pantalla:**

| Estado                           | Qué muestra la UI                                                                         |
|:---------------------------------|:------------------------------------------------------------------------------------------|
| Cargando validación              | Skeleton loader o spinner mientras consulta si hay revisión aceptada                      |
| Sin solicitud aceptada           | Mensaje: "No es posible recalcular. Esta liquidación no tiene una solicitud de revisión aceptada." + botón "Volver" |
| Con solicitud aceptada           | Información resumida de la liquidación + Formulario de ajustes                            |
| Modal de confirmación abierto    | Resumen de ajustes ingresados + "¿Confirmar recálculo?" + botones Confirmar / Cancelar    |
| Procesando (spinner)             | Botón deshabilitado con spinner mientras el PUT está en vuelo                             |
| Éxito                            | Toast/notificación de éxito + redirección automática a `/liquidaciones/:id`               |
| Error del backend                | Toast/notificación de error con el mensaje recibido del backend                           |

**Formulario de ajustes:**
- Lista dinámica de filas (mínimo 1)
- Cada fila: Tipo ajuste (select: PENALIDAD / BONIFICACION), Monto (número positivo), Motivo (texto obligatorio)
- Botón "+ Agregar ajuste" para nuevas filas
- Botón "Eliminar" por fila
- Botón "Recalcular" (habilitado solo si formulario es válido)

---

## 5. Mensajes y feedback

| Situación                                | Tipo     | Mensaje                                                                              |
|:-----------------------------------------|:---------|:-------------------------------------------------------------------------------------|
| Recálculo exitoso                        | Toast ✅ | "Liquidación recalculada correctamente."                                             |
| Sin solicitud de revisión aceptada       | Inline ⚠️ | "No es posible recalcular. Esta liquidación no tiene una solicitud de revisión aceptada." |
| Error 400 (ajuste inválido)              | Toast ❌  | "Los ajustes ingresados no son válidos. Revisá los montos y motivos."               |
| Error 403 (sin permisos)                 | Redirect | Redirige a `/403`                                                                    |
| Error 404 (liquidación no existe)        | Inline   | "No se encontró ninguna liquidación con el ID indicado."                             |
| Error 409 (recálculo no permitido)       | Toast ❌  | "No es posible ejecutar el recálculo en este momento. Verificá el estado de la solicitud." |
| Error 503 (servicio no disponible)       | Toast ❌  | "El servicio no está disponible. Intentá nuevamente en unos momentos."              |
| Motivo vacío en ajuste                   | Inline   | "El motivo del ajuste es obligatorio."                                              |
| Monto inválido (negativo o cero)         | Inline   | "El monto debe ser un valor positivo."                                              |

---

## 6. Criterios de aceptación frontend

Mapeados a los criterios de éxito del spec:

| SC del Spec | Criterio frontend                                                                                      |
|:------------|:-------------------------------------------------------------------------------------------------------|
| SC-002      | El valor mostrado en la UI coincide exactamente con el devuelto por el backend (sin redondeos propios). |
| SC-004      | La tabla de auditoría muestra todas las entradas devueltas por el backend, incluyendo valor anterior, nuevo, fecha y responsable. |
| —           | El botón de calcular manual no existe en ninguna vista. La UI nunca llama a ningún endpoint que dispare el cálculo. |
| —           | El formulario de recálculo solo se muestra si el backend confirma solicitud de revisión aceptada.       |
| —           | La pantalla de recálculo es inaccesible para `ROLE_TRANSPORTISTA` (redirect a `/403`).                 |

---

## 7. Restricciones de diseño

- **No existe botón "Calcular"** en ninguna pantalla. La UI nunca dispara el cálculo inicial.
- **Los montos se muestran en formato de moneda local** (sin conversión propia, solo formateo visual).
- **La auditoría es de solo lectura** en la UI; ningún usuario puede editarla ni eliminarla desde el frontend.
- **El modal de confirmación es obligatorio** antes de ejecutar el recálculo; el botón "Recalcular" solo abre el modal.
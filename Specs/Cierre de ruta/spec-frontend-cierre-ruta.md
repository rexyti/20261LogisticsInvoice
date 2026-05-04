# Frontend Spec: Cierre de ruta

**Created**: 2026-05-04  
**Spec base**: [Cierre de ruta.md](./Cierre%20de%20ruta.md)  
**Plan de implementación**: [plan-frontend-cierre-ruta.md](./plan-frontend-cierre-ruta.md)

---

## 1. Contexto de usuario

El frontend de esta feature es una interfaz de **solo lectura** para el equipo financiero. El cierre de ruta es procesado automáticamente por el backend al recibir el evento `RUTA_CERRADA`. La UI permite consultar rutas ya procesadas, identificar alertas y revisar el desglose financiero de cada parada.

El usuario nunca puede reprocesar, corregir ni publicar eventos desde la UI.

---

## 2. Roles y acceso

| Pantalla              | ROLE_ADMIN |
|:----------------------|:----------:|
| Dashboard de rutas    | ✅          |
| Detalle de ruta       | ✅          |

Cualquier otro rol recibe redirect a `/403`.

---

## 3. Flujos de usuario

### Flujo A — Equipo financiero consulta el dashboard

```
[Inicio] → /rutas
  ├── Aplica filtros (fecha inicio, fecha fin, estado)
  ├── Ve tabla de rutas procesadas con estados y alertas
  └── Navega al detalle de una ruta → /rutas/:idRuta
```

### Flujo B — Equipo financiero revisa una ruta con alertas

```
[/rutas/:idRuta]
  ├── Ve encabezado: estado CON_ALERTAS con panel de alertas visible
  ├── Lee el mensaje específico: CONTRATO_NULO o TARIFA_NO_ENCONTRADA
  ├── Ve tabla de paradas con responsables financieros y % de pago
  └── No puede realizar ninguna acción sobre la ruta
```

---

## 4. Pantallas

### 4.1 Dashboard de rutas (`/rutas`)

**Descripción**: Vista principal del equipo financiero. Lista todas las rutas cerradas procesadas por el backend.

**Estados de pantalla:**

| Estado        | Qué muestra la UI                                                        |
|:--------------|:-------------------------------------------------------------------------|
| Cargando      | Skeleton loader en la tabla                                               |
| Con resultados | Tabla paginada de rutas con filtros activos                             |
| Sin resultados | Mensaje "No se encontraron rutas con los filtros aplicados."            |
| Error de red  | Mensaje "No fue posible cargar las rutas. Intentá nuevamente."          |

**Filtros disponibles:**
- Fecha inicio (date picker)
- Fecha fin (date picker, no puede ser anterior a fecha inicio)
- Estado de procesamiento (select: Todos / PROCESADA / CON_ALERTAS / ERROR / DUPLICADA)

**Tabla de rutas — columnas:**
- ID Ruta
- Transportista
- Tipo de vehículo
- Modelo de contrato
- Total de paradas
- Estado (badge de color)
- Alertas (iconos si existen)
- Fecha de cierre
- Acciones (ver detalle)

**Colores de estado:**
- `PROCESADA` → verde
- `CON_ALERTAS` → amarillo
- `ERROR` → rojo
- `DUPLICADA` → gris

---

### 4.2 Detalle de ruta (`/rutas/:idRuta`)

**Descripción**: Vista de detalle de una ruta procesada. El equipo financiero usa esta pantalla para verificar que el cierre fue procesado correctamente antes de que se calcule la liquidación.

**Estados de pantalla:**

| Estado       | Qué muestra la UI                                                  |
|:-------------|:-------------------------------------------------------------------|
| Cargando     | Skeleton loader en todas las secciones                             |
| Encontrada   | Secciones completas: encabezado, transportista, vehículo, paradas |
| 404          | Mensaje "Ruta no encontrada."                                      |
| Error de red | Mensaje "No fue posible cargar el detalle de esta ruta."          |

**Secciones del detalle:**

1. **Encabezado**: ID Ruta, estado de procesamiento (badge), fecha inicio, fecha cierre
2. **Transportista**: nombre, ID
3. **Vehículo**: tipo (MOTO / VAN / NHR / TURBO), ID
4. **Modelo de contrato**: POR_PARADA o RECORRIDO_COMPLETO
5. **Panel de alertas** (visible solo si `alertas` no está vacío):
   - `CONTRATO_NULO` → "El modelo de contrato de esta ruta es nulo. Requiere revisión manual del equipo financiero."
   - `TARIFA_NO_ENCONTRADA` → "No se encontró tarifa para el tipo de vehículo de esta ruta. Requiere revisión manual del equipo financiero."
6. **Tabla de paradas**: ID Parada, Estado, Motivo de no entrega, Responsable financiero (badge), Porcentaje de pago

**Colores del badge de responsable (MotivoFallaBadge):**

| Responsable     | Color  | Porcentaje mostrado | Descripción                    |
|:----------------|:-------|:--------------------|:-------------------------------|
| `CLIENTE`       | Amarillo | 30% – 50%         | Fallo atribuible al destinatario |
| `TRANSPORTISTA` | Rojo   | 0% + penalidad      | Fallo o daño por el conductor  |
| `FUERZA_MAYOR`  | Gris   | Por definir         | Zona de difícil acceso / OP    |

**No existe ningún botón de acción** (no reprocesar, no corregir, no publicar).

---

## 5. Mensajes y feedback

| Situación                         | Tipo   | Mensaje                                                                                          |
|:----------------------------------|:-------|:-------------------------------------------------------------------------------------------------|
| Sin rutas en dashboard            | Inline | "No se encontraron rutas con los filtros aplicados."                                             |
| Ruta no encontrada (404)          | Inline | "Ruta no encontrada."                                                                            |
| Error de red                      | Inline | "No fue posible cargar la información. Intentá nuevamente."                                      |
| Error 500                         | Inline | "Ocurrió un error en el servidor. Intentá nuevamente."                                           |
| Error 503                         | Inline | "El servicio no está disponible. Intentá nuevamente en unos momentos."                           |
| Error 401                         | Redirect | Redirige a autenticación o muestra sesión expirada                                             |
| Error 403                         | Redirect | Redirige a `/403`                                                                              |
| Alerta CONTRATO_NULO              | Panel  | "El modelo de contrato de esta ruta es nulo. Requiere revisión manual del equipo financiero."    |
| Alerta TARIFA_NO_ENCONTRADA       | Panel  | "No se encontró tarifa para el tipo de vehículo de esta ruta. Requiere revisión manual."        |
| Fecha fin anterior a fecha inicio | Inline | "La fecha fin no puede ser anterior a la fecha de inicio."                                       |

---

## 6. Criterios de aceptación frontend

| SC del Spec | Criterio frontend                                                                                                              |
|:------------|:-------------------------------------------------------------------------------------------------------------------------------|
| SC-001      | La tabla de paradas muestra todas las paradas; paradas fallidas siempre tienen motivo y responsable visible.                   |
| SC-003      | El total de paradas del encabezado coincide con el conteo de filas en la tabla de paradas (la UI no recalcula).               |
| —           | No existe ningún botón de acción en ninguna vista del módulo de rutas.                                                         |
| —           | Las alertas `CONTRATO_NULO` y `TARIFA_NO_ENCONTRADA` tienen mensajes distintos y no se mezclan en un mensaje genérico.        |
| —           | El badge `MotivoFallaBadge` diferencia visualmente los tres tipos de responsabilidad financiera.                               |

---

## 7. Restricciones de diseño

- **Solo lectura absoluta**: ningún botón, formulario ni acción que modifique datos.
- **Formateo visual en el cliente**: los enums del backend (`NHR`, `POR_PARADA`, etc.) se muestran como texto legible usando `rutaFormatters.js`.
- **Los porcentajes de pago no se calculan en el frontend**: vienen del backend y se muestran tal cual.
- **Las alertas son informativas**: la UI las muestra pero no ofrece flujo de resolución (la resolución ocurre fuera del sistema o en otro módulo).
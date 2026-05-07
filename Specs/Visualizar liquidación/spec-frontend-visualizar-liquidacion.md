# Frontend Spec: Visualizar liquidación

**Created**: 2026-05-07  
**Spec base**: [Visualizar liquidación.md](./Visualizar%20liquidaci%C3%B3n.md)  
**Plan backend base**: [plan-visualizar-liquidacion.md](./plan-visualizar-liquidacion.md)

---

## 1. Contexto de usuario

El frontend de esta feature permite visualizar liquidaciones previamente calculadas y almacenadas en el sistema.

La funcionalidad es exclusivamente de consulta. El usuario puede ver una lista organizada de liquidaciones, buscar una liquidación específica y acceder a su detalle financiero. El frontend no calcula liquidaciones, no recalcula valores, no modifica estados y no crea registros.

La UI debe respetar estrictamente los permisos definidos por backend. Si el usuario solo puede ver sus propias liquidaciones, el listado y el detalle deben limitarse a lo autorizado por la API. Si el usuario tiene permisos globales, podrá consultar liquidaciones de otros usuarios según el alcance permitido.

---

## 2. Roles y acceso

| Pantalla | ROLE_TRANSPORTISTA | ROLE_ADMIN | ROLE_GESTOR_TARIFAS | ROLE_FINANCIERO |
|:---------|:------------------:|:----------:|:-------------------:|:---------------:|
| Listado de liquidaciones propias | Sí | Sí | Sí | Sí |
| Buscar liquidación propia | Sí | Sí | Sí | Sí |
| Ver detalle de liquidación propia | Sí | Sí | Sí | Sí |
| Ver liquidaciones de otros usuarios | No | Sí, si backend lo permite | Sí, si backend lo permite | Sí, si backend lo permite |
| Modificar liquidación | No | No | No | No |
| Recalcular liquidación | No en esta feature | No en esta feature | No en esta feature | No en esta feature |

---

## 3. Flujos de usuario

### Flujo A — Usuario consulta lista de liquidaciones

```text
[Inicio] → /liquidaciones
  ├── Carga liquidaciones autorizadas para el usuario autenticado
  ├── Muestra listado paginado y ordenado
  ├── Usuario puede buscar por ID Liquidación o ID Ruta
  ├── Usuario selecciona una liquidación
  └── Navega a /liquidaciones/:id
```

### Flujo B — Usuario busca una liquidación específica

```text
[Inicio] → /liquidaciones
  ├── Ingresa ID Liquidación o ID Ruta
  ├── Ejecuta búsqueda
  │   ├── Existe y tiene permisos → muestra resultado
  │   ├── Existe pero no tiene permisos → muestra acceso restringido o redirige a /403
  │   ├── No existe → muestra "La liquidación no existe dentro del registro"
  │   └── Aún no fue calculada → muestra "La liquidación aún no existe"
```

### Flujo C — Usuario visualiza detalle de liquidación

```text
[Inicio] → /liquidaciones/:id
  ├── Carga detalle completo
  ├── Muestra ruta, fechas, contrato, vehículo, paradas, montos y estado
  ├── Muestra ajustes/penalizaciones
  ├── Si ocurre 404 → muestra liquidación inexistente
  ├── Si ocurre liquidación aún no calculada → muestra mensaje diferenciado
  ├── Si ocurre 403 → bloquea visualización
  └── Si ocurre 503 → informa indisponibilidad del sistema
```

---

## 4. Pantallas

### 4.1 Pantalla: Listado de liquidaciones (`/liquidaciones`)

**Descripción**: Vista principal donde se muestran las liquidaciones almacenadas en forma organizada, paginada y filtrable.

**Estados de pantalla:**

| Estado | Qué muestra la UI |
|:-------|:------------------|
| Cargando | Skeleton loader en tabla |
| Con liquidaciones | Tabla paginada de liquidaciones |
| Sin liquidaciones | Mensaje: "No hay liquidaciones disponibles para visualizar." |
| Búsqueda sin resultados | Mensaje: "La liquidación no existe dentro del registro." |
| Liquidación aún no generada | Mensaje: "La liquidación aún no existe." |
| Acceso no autorizado | Mensaje: "No tienes permisos para visualizar esta liquidación." o redirección a `/403` |
| Almacenamiento no disponible | Mensaje: "El sistema de almacenamiento no está disponible. Intenta nuevamente más tarde." |

**Tabla de liquidaciones muestra:**

- ID Liquidación
- ID Ruta
- Fecha inicio
- Fecha cierre
- Estado liquidación
- Fecha cálculo
- Monto bruto
- Monto neto
- Acción "Ver detalle"

**Filtros y búsqueda:**

- Buscar por ID Liquidación
- Buscar por ID Ruta
- Filtrar por estado de liquidación, si backend lo soporta
- Paginación con número de página y tamaño definido
- Ordenamiento por fecha de cálculo descendente por defecto

---

### 4.2 Pantalla: Detalle de liquidación (`/liquidaciones/:id`)

**Descripción**: Vista de detalle financiero y operativo de una liquidación previamente calculada.

**Estados de pantalla:**

| Estado | Qué muestra la UI |
|:-------|:------------------|
| Cargando | Skeleton loader en tarjetas y tablas |
| Con detalle | Resumen financiero + datos de ruta + ajustes |
| Sin ajustes | Mensaje: "Esta liquidación no tiene ajustes o penalizaciones aplicadas." |
| 403 | Mensaje de acceso restringido o redirección a `/403` |
| 404 | Mensaje: "La liquidación no existe dentro del registro." |
| No calculada | Mensaje: "La liquidación aún no existe para este contrato o ruta." |
| 503 | Mensaje: "El sistema de almacenamiento no está disponible. Intenta nuevamente más tarde." |

**Resumen de liquidación muestra:**

- ID Liquidación
- ID Ruta
- ID Contrato, si backend lo expone
- Estado de liquidación
- Fecha cálculo
- Monto bruto
- Monto neto

**Datos de ruta muestra:**

- Fecha inicio
- Fecha cierre
- Tipo de vehículo
- Precio por parada, si aplica
- Número de paradas

**Tabla de ajustes/penalizaciones muestra:**

- Tipo
- Monto
- Razón

**Acciones disponibles:**

- Volver al listado
- Copiar ID Liquidación, si se decide habilitar como utilidad visual

**Acciones prohibidas:**

- No existe botón "Calcular"
- No existe botón "Recalcular"
- No existe botón "Editar liquidación"
- No existe formulario para modificar montos o ajustes

---

## 5. Mensajes y feedback

| Situación | Tipo | Mensaje |
|:----------|:-----|:--------|
| Sin liquidaciones | Empty state | "No hay liquidaciones disponibles para visualizar." |
| Liquidación encontrada | Visual | Muestra detalle completo de la liquidación. |
| Liquidación inexistente | Inline | "La liquidación no existe dentro del registro." |
| Liquidación aún no calculada | Inline | "La liquidación aún no existe para este contrato o ruta." |
| Acceso no autorizado | Inline / Redirect | "No tienes permisos para visualizar esta liquidación." |
| Sistema de almacenamiento no disponible | Inline / Toast | "El sistema de almacenamiento no está disponible. Intenta nuevamente más tarde." |
| Error de red | Toast | "No fue posible comunicarse con el servidor." |
| Sin ajustes | Empty state | "Esta liquidación no tiene ajustes o penalizaciones aplicadas." |
| Parámetro inválido | Inline | "El identificador ingresado no tiene un formato válido." |

---

## 6. Criterios de aceptación frontend

Mapeados al spec:

| SC / FR | Criterio frontend |
|:--------|:------------------|
| FR-001 | La UI muestra liquidaciones almacenadas en una lista organizada y paginada. |
| FR-002 | La UI permite buscar una liquidación específica por ID Liquidación o ID Ruta. |
| FR-003 | La UI muestra mensaje claro cuando la liquidación no existe. |
| FR-004 | La UI respeta la autorización del backend y bloquea visualización ante 403. |
| SC-001 | El 100% de liquidaciones autorizadas devueltas por backend se visualizan correctamente. |
| SC-002 | El frontend no muestra datos de liquidaciones no autorizadas cuando backend restringe el acceso. |
| — | La UI distingue entre liquidación inexistente y liquidación aún no calculada. |
| — | La UI no calcula, recalcula ni modifica liquidaciones. |

---

## 7. Restricciones de diseño

- El frontend no calcula liquidaciones.
- El frontend no recalcula liquidaciones.
- El frontend no modifica ajustes, penalidades, montos ni estados.
- El backend es la única fuente de verdad de datos, permisos y estados.
- La paginación debe respetar la respuesta del backend.
- Los montos se muestran en formato de moneda local, sin alterar precisión.
- La búsqueda no debe exponer datos parciales si backend retorna 403.
- Los errores técnicos internos no deben mostrarse al usuario.
- La UI debe diferenciar claramente:
  - liquidación inexistente;
  - liquidación aún no calculada;
  - acceso no autorizado;
  - almacenamiento no disponible.

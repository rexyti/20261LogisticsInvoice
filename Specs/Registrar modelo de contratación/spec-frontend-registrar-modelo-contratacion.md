# Frontend Spec: Registrar modelo de contratación

**Created**: 2026-05-07  
**Spec base**: [Registrar modelo de contratación.md](./Registrar%20modelo%20de%20contrataci%C3%B3n.md)  
**Plan de implementación backend**: [plan-registrar-modelo-contratacion.md](./plan-registrar-modelo-contratacion.md)  
**Plan frontend relacionado**: [plan-frontend-registrar-modelo-contratacion.md](./plan-frontend-registrar-modelo-contratacion.md)

---

## 1. Contexto de usuario

El frontend de esta feature permite al **gestor de tarifas** registrar y consultar modelos de contratación asociados a transportistas.

La UI debe cubrir dos responsabilidades principales:

1. **Registrar un contrato** con los campos obligatorios definidos por la especificación, validando datos incompletos, fechas incoherentes y reglas condicionales de precio según el tipo de contrato.
2. **Consultar contratos registrados** por identificador para revisar su información completa y confirmar que quedaron disponibles para gestión.

El backend mantiene la fuente de verdad para la persistencia, la validación de duplicados y las reglas de negocio. El frontend valida de forma anticipada para mejorar la experiencia del usuario, pero nunca reemplaza las validaciones del backend.

---

## 2. Roles y acceso

| Pantalla / Acción                         | ROLE_GESTOR_TARIFAS | Otros roles |
|:------------------------------------------|:-------------------:|:-----------:|
| Ver formulario de nuevo contrato           | Sí                  | No          |
| Registrar contrato                         | Sí                  | No          |
| Consultar contrato por identificador       | Sí                  | No          |
| Ver detalle completo del contrato          | Sí                  | No          |

**Regla de acceso:** si el usuario no tiene `ROLE_GESTOR_TARIFAS`, la UI debe redirigir a `/403` o mostrar una pantalla de acceso no autorizado, según el comportamiento global del frontend.

---

## 3. Flujos de usuario

### Flujo A — Gestor registra un contrato válido

```text
[Inicio] → /contratos/nuevo
  ├── UI muestra formulario de registro
  ├── Usuario ingresa campos obligatorios
  ├── Selecciona tipo de contrato
  │   ├── Por Parada → UI muestra campo Precio por parada
  │   └── Recorrido Completo → UI muestra campo Precio
  ├── Usuario presiona "Guardar contrato"
  ├── Modal de confirmación
  │   ├── Cancela → vuelve al formulario sin perder datos
  │   └── Confirma → POST /api/contratos
  │       ├── Éxito 201 → Toast de éxito + redirección a detalle
  │       └── Error backend → muestra mensaje de error
```

### Flujo B — Gestor intenta registrar contrato incompleto

```text
[Inicio] → /contratos/nuevo
  ├── Usuario deja campos obligatorios vacíos
  ├── Presiona "Guardar contrato"
  └── UI bloquea el envío y muestra errores inline por campo
```

Si el backend también devuelve campos faltantes en HTTP 400, la UI debe mapearlos y mostrarlos debajo del campo correspondiente.

### Flujo C — Gestor intenta registrar contrato duplicado

```text
[Inicio] → /contratos/nuevo
  ├── Usuario ingresa un idContrato ya existente
  ├── Confirma registro
  ├── POST /api/contratos
  └── Backend responde HTTP 409
      └── UI muestra mensaje: "El contrato con este identificador ya existe."
```

### Flujo D — Gestor consulta contrato existente

```text
[Inicio] → /contratos/buscar
  ├── Usuario ingresa identificador del contrato
  ├── Presiona "Buscar"
  ├── GET /api/contratos/{idContrato}
  └── Éxito → UI muestra tarjeta de resumen + detalle completo
```

### Flujo E — Gestor consulta contrato inexistente

```text
[Inicio] → /contratos/buscar
  ├── Usuario ingresa identificador no existente
  ├── Presiona "Buscar"
  ├── GET /api/contratos/{idContrato}
  └── HTTP 404 → UI muestra "No se encontraron resultados para el contrato indicado."
```

---

## 4. Pantallas

### 4.1 Pantalla: Registrar contrato (`/contratos/nuevo`)

**Descripción:** pantalla principal para crear un nuevo modelo de contratación.

**Campos del formulario:**

| Campo                | Tipo UI             | Obligatorio | Regla principal |
|:---------------------|:--------------------|:-----------:|:----------------|
| ID Contrato           | Text input          | Sí          | No vacío. Debe ser único según backend. |
| Tipo de contrato      | Select              | Sí          | Valores esperados: `POR_PARADA`, `RECORRIDO_COMPLETO` o equivalentes definidos por backend. |
| Nombre conductor      | Text input          | Sí          | No vacío. |
| Tipo de vehículo      | Select / Text input | Sí          | No vacío. Debe coincidir con catálogo si existe. |
| ID Usuario            | Text input / Select | Sí          | Identifica al transportista o conductor asociado. |
| ID Vehículo           | Text input / Select | Sí          | Vehículo asociado al contrato. |
| ID Seguro             | Text input / Select | Sí          | Seguro asociado al transportista. |
| Estado seguro         | Badge / Select      | Sí          | Debe mostrarse o seleccionarse según disponibilidad del backend. |
| Fecha inicio          | Date picker         | Sí          | Fecha válida. |
| Fecha final           | Date picker         | Sí          | Debe ser posterior a fecha inicio. |
| Precio por parada     | Number input        | Condicional | Obligatorio si el contrato es por parada. Valor mayor a cero. |
| Precio                | Number input        | Condicional | Obligatorio si el contrato es recorrido completo. Valor mayor a cero. |

**Estados de pantalla:**

| Estado                     | Qué muestra la UI |
|:---------------------------|:------------------|
| Inicial                    | Formulario vacío con botón Guardar deshabilitado hasta que el formulario sea válido. |
| Formulario inválido         | Errores inline debajo de cada campo. |
| Confirmación abierta        | Modal con resumen del contrato antes del envío. |
| Guardando                   | Botón deshabilitado con spinner; campos bloqueados para evitar doble envío. |
| Éxito                       | Toast: "Contrato guardado exitosamente." y navegación al detalle. |
| Error 400                   | Errores por campo o mensaje general de validación. |
| Error 409                   | Toast o alerta inline: "El contrato con este identificador ya existe." |
| Error de red / 503          | Toast: "No fue posible guardar el contrato. Intenta nuevamente." |

**Reglas visuales importantes:**

- Solo debe mostrarse el campo de precio correspondiente al tipo de contrato seleccionado.
- Si el usuario cambia de tipo de contrato, la UI debe limpiar el precio que ya no aplica para evitar enviar valores contradictorios.
- El botón "Guardar contrato" no debe ejecutar el POST directamente; primero debe abrir un modal de confirmación.
- La UI no debe permitir doble envío mientras la petición está en curso.

---

### 4.2 Pantalla: Buscar contrato (`/contratos/buscar`)

**Descripción:** pantalla para consultar un contrato registrado por identificador.

**Elementos:**

- Input de búsqueda por `idContrato`.
- Botón "Buscar".
- Estado de carga mientras se consulta el backend.
- Resultado del contrato en tarjeta o panel de detalle.
- Mensaje vacío cuando no exista resultado.

**Estados de pantalla:**

| Estado             | Qué muestra la UI |
|:-------------------|:------------------|
| Inicial            | Input de búsqueda vacío. |
| Buscando           | Spinner o skeleton loader. |
| Encontrado         | Tarjeta de resumen + detalle completo del contrato. |
| No encontrado      | Mensaje: "No se encontraron resultados para el contrato indicado." |
| Error de red       | Mensaje: "No fue posible consultar el contrato. Intenta nuevamente." |

---

### 4.3 Pantalla: Detalle de contrato (`/contratos/:idContrato`)

**Descripción:** vista de solo lectura para revisar la información completa del contrato.

**Información visible:**

- ID Contrato.
- Tipo de contrato.
- Nombre del conductor.
- Tipo de vehículo.
- Usuario asociado.
- Vehículo asociado.
- Seguro asociado y estado del seguro.
- Fecha inicio.
- Fecha final.
- Precio por parada o precio fijo según tipo de contrato.

**Acciones disponibles:**

- Volver a búsqueda.
- Registrar nuevo contrato.

No se incluye edición porque la especificación base solo contempla registro y consulta.

---

## 5. Mensajes y feedback

| Situación                                   | Tipo     | Mensaje |
|:--------------------------------------------|:---------|:--------|
| Registro exitoso                             | Toast    | "Contrato guardado exitosamente." |
| Campos obligatorios faltantes                | Inline   | "Este campo es obligatorio." |
| Fecha final anterior o igual a fecha inicio  | Inline   | "La fecha final debe ser posterior a la fecha de inicio." |
| Precio por parada faltante                   | Inline   | "El precio por parada es obligatorio para contratos por parada." |
| Precio fijo faltante                         | Inline   | "El precio es obligatorio para contratos de recorrido completo." |
| Precio inválido                              | Inline   | "El valor debe ser mayor a cero." |
| Contrato duplicado                           | Toast    | "El contrato con este identificador ya existe." |
| Contrato no encontrado                       | Inline   | "No se encontraron resultados para el contrato indicado." |
| Sin permisos                                 | Redirect | Redirige a `/403`. |
| Servicio no disponible                       | Toast    | "El servicio no está disponible. Intenta nuevamente en unos momentos." |

---

## 6. Criterios de aceptación frontend

Mapeados a la especificación base:

| Criterio base | Criterio frontend |
|:--------------|:------------------|
| FR-001 / SC-001 | La UI permite enviar un contrato válido y muestra confirmación cuando el backend responde exitosamente. |
| FR-002 / SC-003 | La UI bloquea el envío si faltan campos obligatorios o si las fechas/precios no cumplen las reglas. |
| FR-003 | La UI permite consultar contratos por identificador y mostrar toda la información devuelta por backend. |
| FR-004 / SC-002 | La UI muestra claramente el error de duplicado cuando backend responde HTTP 409. |
| Edge case fechas | La UI impide enviar contratos cuya fecha final sea anterior o igual a la fecha de inicio. |
| Edge case incompletos | La UI muestra los campos faltantes de forma específica, no solo un error genérico. |

---

## 7. Restricciones de diseño

- La UI no debe asumir que el contrato fue creado hasta recibir confirmación exitosa del backend.
- El frontend no debe resolver duplicados localmente; solo puede advertir o mostrar el error que proviene del backend.
- Los valores monetarios se muestran en formato de moneda local únicamente como formato visual; no se redondean ni transforman para lógica de negocio.
- No se implementa edición ni eliminación de contratos porque no están contempladas en la especificación base.
- Las validaciones del frontend son preventivas; las reglas finales pertenecen al backend.
- El detalle del contrato debe ser de solo lectura.

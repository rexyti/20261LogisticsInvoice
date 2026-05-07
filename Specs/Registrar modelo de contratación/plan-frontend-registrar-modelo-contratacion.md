# Plan Técnico Frontend: Registrar modelo de contratación

**Fecha**: 2026-05-07  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Registrar-Modelo-Contratacion`  
**Plan backend base**: [plan-registrar-modelo-contratacion.md](./plan-registrar-modelo-contratacion.md)  
**Spec frontend**: [spec-frontend-registrar-modelo-contratacion.md](./spec-frontend-registrar-modelo-contratacion.md)  
**Frontend objetivo**: Registro y consulta de modelos de contratación por parte del gestor de tarifas

---

## 1. Resumen

Este plan define la implementación frontend para la feature **Registrar modelo de contratación**.

El módulo permitirá al `ROLE_GESTOR_TARIFAS` registrar contratos de transportistas y consultar contratos existentes por identificador. La UI debe respetar las reglas del backend: campos obligatorios, prevención de duplicados, validación de fechas y validación condicional del precio según el tipo de contrato.

La feature no contempla edición ni eliminación de contratos. Por tanto, el frontend se limita a:

1. Formulario de registro de contrato.
2. Consulta por identificador.
3. Visualización de detalle del contrato.
4. Manejo claro de errores y estados de carga.

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
**Rol esperado**: `ROLE_GESTOR_TARIFAS`

---

## 3. Endpoints backend consumidos

### 3.1 Registrar contrato

```http
POST /api/contratos
```

Uso: enviar el formulario de creación de contrato.

**Comportamientos esperados:**

- `201 Created`: contrato registrado correctamente.
- `400 Bad Request`: campos obligatorios faltantes, fechas inválidas o precio condicional inválido.
- `403 Forbidden`: usuario sin rol autorizado.
- `409 Conflict`: contrato duplicado por `idContrato`.
- `503 Service Unavailable`: servicio no disponible.

### 3.2 Consultar contrato por identificador

```http
GET /api/contratos/{idContrato}
```

Uso: consultar y mostrar la información completa de un contrato registrado.

**Comportamientos esperados:**

- `200 OK`: contrato encontrado.
- `403 Forbidden`: usuario sin rol autorizado.
- `404 Not Found`: contrato inexistente.
- `503 Service Unavailable`: servicio no disponible.

### 3.3 Búsqueda por criterio ampliado

```http
GET /api/contratos?criterio={valor}
```

Uso: solo debe implementarse si el backend expone un endpoint de búsqueda ampliada. La especificación permite búsqueda por identificador o criterio válido, pero el plan backend base deja explícito el endpoint por `idContrato`. Si el endpoint ampliado no existe, esta opción queda bloqueada hasta coordinación con backend.

---

## 4. Contratos de datos esperados

### 4.1 Request de registro de contrato

```json
{
  "idContrato": "CONT-2026-001",
  "tipoContrato": "POR_PARADA",
  "nombreConductor": "Carlos Pérez",
  "idUsuario": "USR-001",
  "idVehiculo": "VEH-001",
  "idSeguro": "SEG-001",
  "tipoVehiculo": "Camioneta",
  "fechaInicio": "2026-05-01",
  "fechaFinal": "2026-12-31",
  "precioParadas": 15000,
  "precio": null
}
```

**Regla condicional:**

- Si `tipoContrato = POR_PARADA`, `precioParadas` es obligatorio y `precio` debe enviarse como `null` o no enviarse, según contrato final del backend.
- Si `tipoContrato = RECORRIDO_COMPLETO`, `precio` es obligatorio y `precioParadas` debe enviarse como `null` o no enviarse.

### 4.2 Response de contrato registrado o consultado

```json
{
  "idContrato": "CONT-2026-001",
  "tipoContrato": "POR_PARADA",
  "nombreConductor": "Carlos Pérez",
  "idUsuario": "USR-001",
  "nombreUsuario": "Carlos Pérez",
  "idVehiculo": "VEH-001",
  "tipoVehiculo": "Camioneta",
  "idSeguro": "SEG-001",
  "estadoSeguro": "ACTIVO",
  "fechaInicio": "2026-05-01",
  "fechaFinal": "2026-12-31",
  "precioParadas": 15000,
  "precio": null
}
```

### 4.3 Response de error esperado

```json
{
  "message": "El contrato con este identificador ya existe",
  "fields": []
}
```

Para errores de validación, se espera una estructura que permita mapear errores a campos:

```json
{
  "message": "Datos inválidos",
  "fields": [
    {
      "field": "fechaFinal",
      "message": "La fecha final debe ser posterior a la fecha de inicio"
    }
  ]
}
```

---

## 5. Estructura de archivos

```text
frontend/src/
├── modules/
│   └── contratos/
│       ├── components/
│       │   ├── ContratoForm.jsx                    # Formulario principal de creación
│       │   ├── ContratoFormFields.jsx              # Campos agrupados del formulario
│       │   ├── ContratoPrecioFields.jsx            # Precio condicional según tipo contrato
│       │   ├── ContratoConfirmModal.jsx            # Modal de confirmación antes del POST
│       │   ├── ContratoResumenCard.jsx             # Tarjeta de resumen del contrato
│       │   ├── ContratoDetalle.jsx                 # Vista completa de solo lectura
│       │   └── ContratoSearchBox.jsx               # Input + botón para búsqueda por idContrato
│       │
│       ├── pages/
│       │   ├── CrearContratoPage.jsx               # Ruta /contratos/nuevo
│       │   ├── BuscarContratoPage.jsx              # Ruta /contratos/buscar
│       │   └── ContratoDetallePage.jsx             # Ruta /contratos/:idContrato
│       │
│       ├── services/
│       │   └── contratoService.js                  # Llamadas HTTP al backend
│       │
│       ├── hooks/
│       │   ├── useCrearContrato.js                 # Envío POST y estados de creación
│       │   ├── useContratoDetalle.js               # Consulta GET por idContrato
│       │   └── useBuscarContrato.js                # Estado de búsqueda desde formulario
│       │
│       └── validators/
│           └── contratoSchema.js                   # Validación Zod del formulario
│
└── shared/
    ├── components/
    │   ├── Button.jsx
    │   ├── Input.jsx
    │   ├── Select.jsx
    │   ├── DateInput.jsx
    │   ├── Modal.jsx
    │   ├── Toast.jsx
    │   └── Badge.jsx
    │
    └── services/
        └── apiClient.js                            # Axios con interceptores JWT
```

---

## 6. Criterios de éxito frontend

| Criterio base | Criterio frontend |
|:--------------|:------------------|
| SC-001 | El gestor puede registrar contratos válidos y visualizar confirmación de éxito. |
| SC-002 | Cuando backend devuelve duplicado, la UI muestra el mensaje específico de contrato existente. |
| SC-003 | El formulario bloquea el envío cuando hay campos obligatorios vacíos o reglas inválidas. |
| FR-003 | El gestor puede consultar un contrato por identificador y ver toda la información retornada. |
| Edge case fechas | La UI valida que `fechaFinal` sea posterior a `fechaInicio` antes de enviar. |
| Edge case incompletos | Los errores se muestran por campo y no como alerta genérica únicamente. |

---

## Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/contratos` con carpetas `components`, `pages`, `services`, `hooks` y `validators`.
- [ ] F002 Registrar ruta `/contratos/nuevo` en el router global, protegida por `ROLE_GESTOR_TARIFAS`.
- [ ] F003 Registrar ruta `/contratos/buscar` en el router global, protegida por `ROLE_GESTOR_TARIFAS`.
- [ ] F004 Registrar ruta `/contratos/:idContrato` para mostrar detalle de contrato, protegida por `ROLE_GESTOR_TARIFAS`.
- [ ] F005 Crear `contratoService.js` con métodos `crearContrato(payload)` y `getContratoById(idContrato)`.
- [ ] F006 Configurar manejo base de errores HTTP del módulo usando el `apiClient.js` compartido.

---

## Fase 2: Validación del formulario

- [ ] F007 Crear `contratoSchema.js` usando Zod con validación de campos obligatorios: `idContrato`, `tipoContrato`, `nombreConductor`, `idUsuario`, `idVehiculo`, `idSeguro`, `tipoVehiculo`, `fechaInicio`, `fechaFinal`.
- [ ] F008 Agregar validación de fechas: `fechaFinal` debe ser posterior a `fechaInicio`.
- [ ] F009 Agregar validación condicional: si `tipoContrato` es `POR_PARADA`, `precioParadas` es obligatorio y mayor a cero.
- [ ] F010 Agregar validación condicional: si `tipoContrato` es `RECORRIDO_COMPLETO`, `precio` es obligatorio y mayor a cero.
- [ ] F011 Garantizar que al cambiar el tipo de contrato se limpie el campo de precio que ya no aplica.
- [ ] F012 Mapear errores de backend por campo cuando HTTP 400 incluya lista `fields`.

---

## Fase 3: Registro de contrato

- [ ] F013 Implementar `useCrearContrato.js`: expone `{ crearContrato, isLoading, error, success }`.
- [ ] F014 Implementar `CrearContratoPage.jsx` con React Hook Form y resolver de Zod.
- [ ] F015 Implementar `ContratoForm.jsx` como contenedor del formulario.
- [ ] F016 Implementar `ContratoFormFields.jsx` con los campos generales del contrato.
- [ ] F017 Implementar `ContratoPrecioFields.jsx` para mostrar `precioParadas` o `precio` según `tipoContrato`.
- [ ] F018 Implementar `ContratoConfirmModal.jsx`; el botón "Guardar contrato" abre el modal y el POST solo se ejecuta al confirmar.
- [ ] F019 Deshabilitar formulario, botón de guardar y confirmación mientras el POST esté en ejecución.
- [ ] F020 Al éxito `201`, mostrar toast "Contrato guardado exitosamente." y redirigir a `/contratos/{idContrato}`.
- [ ] F021 Al error `409`, mostrar toast "El contrato con este identificador ya existe." y mantener el formulario con los datos ingresados.
- [ ] F022 Al error `400`, mostrar errores inline cuando sea posible; si no hay detalle por campo, mostrar alerta general de validación.

---

## Fase 4: Consulta de contrato

- [ ] F023 Implementar `useContratoDetalle.js`: llama a `getContratoById(idContrato)` y expone `{ data, isLoading, error }`.
- [ ] F024 Implementar `useBuscarContrato.js` para controlar input de búsqueda, submit y navegación al detalle.
- [ ] F025 Implementar `BuscarContratoPage.jsx` con `ContratoSearchBox.jsx`.
- [ ] F026 En búsqueda, bloquear submit si `idContrato` está vacío.
- [ ] F027 Mostrar spinner o skeleton mientras se consulta el contrato.
- [ ] F028 Si backend responde `404`, mostrar mensaje inline: "No se encontraron resultados para el contrato indicado.".
- [ ] F029 Si el contrato existe, navegar a `/contratos/{idContrato}` o renderizar el detalle dentro de la misma página, según patrón global del proyecto.

---

## Fase 5: Detalle de contrato

- [ ] F030 Implementar `ContratoDetallePage.jsx` leyendo `idContrato` desde los parámetros de ruta.
- [ ] F031 Implementar `ContratoResumenCard.jsx` con ID contrato, tipo de contrato, conductor, tipo de vehículo, fechas y precio aplicable.
- [ ] F032 Implementar `ContratoDetalle.jsx` como vista de solo lectura con datos de usuario, vehículo y seguro.
- [ ] F033 Mostrar únicamente el precio que corresponde al tipo de contrato.
- [ ] F034 Formatear fechas en formato local legible sin cambiar el valor recibido del backend.
- [ ] F035 Formatear montos en moneda local solo para presentación visual.
- [ ] F036 Añadir acciones "Volver a búsqueda" y "Registrar nuevo contrato".
- [ ] F037 Confirmar que no existen botones de edición o eliminación en esta vista.

---

## Fase 6: Estados y errores globales

- [ ] F038 Manejar HTTP 400: mostrar errores por campo o mensaje "Los datos ingresados no son válidos. Revisa la información del contrato.".
- [ ] F039 Manejar HTTP 403: redirigir a `/403`.
- [ ] F040 Manejar HTTP 404: mostrar "No se encontraron resultados para el contrato indicado.".
- [ ] F041 Manejar HTTP 409: mostrar "El contrato con este identificador ya existe.".
- [ ] F042 Manejar HTTP 503: mostrar "El servicio no está disponible. Intenta nuevamente en unos momentos.".
- [ ] F043 Manejar error de red: mostrar "No fue posible comunicarse con el servidor. Verifica tu conexión.".
- [ ] F044 Implementar skeleton loaders en registro, búsqueda y detalle cuando aplique.
- [ ] F045 Garantizar que los errores de consulta no borren el estado del formulario de registro.

---

## Fase 7: Pruebas frontend

- [ ] F046 Test: `CrearContratoPage` renderiza todos los campos obligatorios.
- [ ] F047 Test: el botón "Guardar contrato" se bloquea cuando faltan campos obligatorios.
- [ ] F048 Test: si `tipoContrato` es `POR_PARADA`, se muestra `precioParadas` y no se muestra `precio`.
- [ ] F049 Test: si `tipoContrato` es `RECORRIDO_COMPLETO`, se muestra `precio` y no se muestra `precioParadas`.
- [ ] F050 Test: fecha final anterior o igual a fecha inicio muestra error inline.
- [ ] F051 Test: precio menor o igual a cero muestra error inline.
- [ ] F052 Test: `ContratoConfirmModal` se abre antes del POST y no envía hasta confirmar.
- [ ] F053 Test: registro exitoso muestra toast y redirige a `/contratos/{idContrato}`.
- [ ] F054 Test: error HTTP 409 muestra mensaje de duplicado sin perder los datos del formulario.
- [ ] F055 Test: error HTTP 400 mapea errores por campo si backend los devuelve.
- [ ] F056 Test: `BuscarContratoPage` bloquea búsqueda con input vacío.
- [ ] F057 Test: búsqueda exitosa muestra o navega al detalle del contrato.
- [ ] F058 Test: búsqueda con HTTP 404 muestra mensaje de no encontrado.
- [ ] F059 Test: `ContratoDetallePage` muestra solo el precio aplicable al tipo de contrato.
- [ ] F060 Test: rutas del módulo redirigen a `/403` si el usuario no tiene `ROLE_GESTOR_TARIFAS`.
- [ ] F061 Test: no existen botones de editar o eliminar contrato en las pantallas de esta feature.

---

## 8. Dependencias y orden de ejecución

1. **Fase 1 primero**: las rutas, el servicio HTTP y la estructura del módulo deben existir antes de construir componentes.
2. **Fase 2 antes de Fase 3**: las validaciones con Zod son prerequisito del formulario de registro.
3. **Fase 3 antes de Fase 5**: la pantalla de detalle se usa como destino después del registro exitoso.
4. **Fase 4 antes de finalizar Fase 5**: el detalle debe poder cargarse tanto desde la búsqueda como desde la redirección posterior al registro.
5. **Fase 6 transversal**: el manejo de errores se valida en registro, búsqueda y detalle.
6. **Backend mantiene la fuente de verdad**: duplicados, persistencia y validaciones finales pertenecen al backend. El frontend solo previene errores comunes y muestra los resultados.
7. **No implementar edición ni eliminación**: no forman parte del alcance de esta feature y deben quedar fuera de la UI.
8. **Endpoint ampliado de búsqueda**: solo implementar `GET /api/contratos?criterio={valor}` si backend lo expone formalmente. Mientras tanto, usar `GET /api/contratos/{idContrato}`.

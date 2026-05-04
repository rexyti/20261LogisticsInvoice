# Plan Técnico Frontend: Registrar modelo de contratación

**Fecha**: 2026-05-03  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Registrar-Modelo-Contratacion`  
**Plan backend base**: `Specs/Registrar modelo de contratación/plan-registrar-modelo-contratacion.md`  
**Frontend objetivo**: Formulario administrativo de contratos y búsqueda de contratos

## 1. Resumen

Este plan define la implementación frontend para registrar y consultar modelos de contratación de transportistas.

El backend establece reglas estrictas: campos obligatorios, prevención de duplicados, coherencia temporal de fechas y validación condicional del precio según el tipo de contrato. El frontend debe anticipar estas validaciones para mejorar la experiencia del usuario, pero nunca reemplazar la validación del backend.

La interfaz principal será un formulario de creación de contrato y una vista de búsqueda/consulta de contrato existente.

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool recomendado**: Vite  
**Cliente HTTP**: Axios  
**Formularios**: React Hook Form  
**Validación cliente**: Zod o Yup  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT por interceptor Axios  
**Roles esperados**: `ROLE_GESTOR_TARIFAS`

## 3. Endpoints backend consumidos

### 3.1 Registrar contrato

```http
POST /api/contratos
```

Uso frontend:

- Enviar formulario de nuevo contrato.
- Manejar respuestas 201, 400 y 409.

### 3.2 Consultar contrato

```http
GET /api/contratos/{idContrato}
```

Uso frontend:

- Buscar un contrato existente.
- Mostrar datos asociados a conductor, vehículo y seguro si el backend los expone en `ContratoResponseDTO`.

## 4. Estructura propuesta frontend

```text
frontend/
├── src/
│   ├── modules/
│   │   └── contratos/
│   │       ├── components/
│   │       │   ├── ContratoForm.jsx
│   │       │   ├── ContratoSearch.jsx
│   │       │   ├── ContratoDetailCard.jsx
│   │       │   ├── TipoContratoSelect.jsx
│   │       │   ├── PrecioContratoFields.jsx
│   │       │   └── ContratoValidationAlert.jsx
│   │       │
│   │       ├── pages/
│   │       │   ├── CrearContratoPage.jsx
│   │       │   └── BuscarContratoPage.jsx
│   │       │
│   │       ├── services/
│   │       │   └── contratosService.js
│   │       │
│   │       ├── hooks/
│   │       │   ├── useContratoForm.js
│   │       │   └── useContratoSearch.js
│   │       │
│   │       └── validators/
│   │           └── contratoSchema.js
│   │
│   └── shared/
│       ├── components/
│       └── services/
│           └── apiClient.js
```

## 5. Contratos de datos esperados

### 5.1 Request de creación

```json
{
  "idContrato": "CON-001",
  "tipoContrato": "POR_PARADA",
  "nombreConductor": "Carlos Pérez",
  "precioParadas": 3500,
  "precio": null,
  "tipoVehiculo": "MOTO",
  "fechaInicio": "2026-05-01",
  "fechaFinal": "2026-12-31",
  "idUsuario": "uuid",
  "idVehiculo": "uuid"
}
```

### 5.2 Response de contrato

```json
{
  "idContrato": "CON-001",
  "tipoContrato": "POR_PARADA",
  "nombreConductor": "Carlos Pérez",
  "precioParadas": 3500,
  "precio": null,
  "tipoVehiculo": "MOTO",
  "fechaInicio": "2026-05-01",
  "fechaFinal": "2026-12-31"
}
```

## 6. Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/contratos`.
- [ ] F002 Registrar ruta `/contratos/nuevo`.
- [ ] F003 Registrar ruta `/contratos/buscar`.
- [ ] F004 Crear `contratosService.js` con métodos `crearContrato` y `buscarContratoPorId`.
- [ ] F005 Crear `contratoSchema.js` con validaciones equivalentes a las reglas del backend.
- [ ] F006 Asegurar protección de rutas para usuario con rol `ROLE_GESTOR_TARIFAS`.

## 7. Fase 2: Formulario de creación de contrato

- [ ] F007 Implementar `ContratoForm.jsx` usando React Hook Form.
- [ ] F008 Implementar campos obligatorios:
  - ID Contrato
  - Tipo Contrato
  - Nombre Conductor
  - Tipo Vehículo
  - Fecha Inicio
  - Fecha Final
  - ID Usuario
  - ID Vehículo
- [ ] F009 Implementar `TipoContratoSelect.jsx` con opciones:
  - `POR_PARADA`
  - `RECORRIDO_COMPLETO`
- [ ] F010 Implementar `PrecioContratoFields.jsx` con lógica condicional:
  - Si tipo contrato es `POR_PARADA`, mostrar y exigir `precioParadas`.
  - Si tipo contrato es `RECORRIDO_COMPLETO`, mostrar y exigir `precio`.
- [ ] F011 Validar que `fechaFinal` sea estrictamente mayor que `fechaInicio`.
- [ ] F012 Validar montos positivos.
- [ ] F013 Mostrar errores debajo de cada campo.
- [ ] F014 Deshabilitar envío mientras el formulario sea inválido o esté enviando.
- [ ] F015 Mostrar confirmación `Contrato guardado exitosamente` ante HTTP 201.

## 8. Fase 3: Manejo de errores backend

- [ ] F016 Mapear HTTP 400 a errores de validación visibles por campo cuando el backend retorne lista de campos.
- [ ] F017 Mapear HTTP 409 al mensaje `El contrato con este identificador ya existe`.
- [ ] F018 Mapear HTTP 403 a `No tienes permisos para registrar contratos`.
- [ ] F019 Mapear HTTP 500/503 a mensaje de error temporal.
- [ ] F020 Preservar datos del formulario cuando ocurra un error no exitoso.

## 9. Fase 4: Consulta de contrato

- [ ] F021 Implementar `ContratoSearch.jsx` con campo `idContrato`.
- [ ] F022 Implementar `useContratoSearch.js` para manejar búsqueda, carga, error y resultado.
- [ ] F023 Implementar `ContratoDetailCard.jsx` mostrando:
  - ID Contrato
  - Tipo Contrato
  - Nombre Conductor
  - Tipo Vehículo
  - Precio aplicable
  - Fechas de vigencia
- [ ] F024 Mostrar `No se encontraron resultados` ante HTTP 404.
- [ ] F025 Evitar mostrar entidades internas o campos técnicos no incluidos en el DTO público.

## 10. Fase 5: Pruebas frontend

- [ ] F026 Test de validación de fechas invertidas.
- [ ] F027 Test de precio condicional para `POR_PARADA`.
- [ ] F028 Test de precio condicional para `RECORRIDO_COMPLETO`.
- [ ] F029 Test de envío exitoso y notificación.
- [ ] F030 Test de error 409 por contrato duplicado.
- [ ] F031 Test de búsqueda exitosa.
- [ ] F032 Test de búsqueda sin resultados.

## 11. Dependencias y orden de ejecución

1. Primero crear servicio y esquema de validación.
2. Luego implementar formulario.
3. Después integrar errores backend.
4. Finalmente implementar búsqueda.
5. La validación frontend debe coincidir con backend, pero backend sigue siendo la fuente de verdad.

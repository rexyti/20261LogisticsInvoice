# Plan Técnico Frontend: Visualizar liquidación

**Fecha**: 2026-05-03  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Visualizar-Liquidación`  
**Plan backend base**: `Specs/Visualizar liquidación/plan-visualizar-liquidacion.md`  
**Frontend objetivo**: Listado, búsqueda y detalle de liquidaciones

## 1. Resumen

Este plan define el frontend para visualizar liquidaciones previamente calculadas y almacenadas.

La interfaz debe permitir listar liquidaciones, buscar por identificadores relevantes, consultar el detalle completo y mostrar mensajes diferenciados para liquidación inexistente, liquidación aún no calculada, acceso no autorizado e indisponibilidad del almacenamiento.

El frontend no debe filtrar permisos de forma confiable por sí solo. Debe reflejar las respuestas del backend, ya que el control de acceso se aplica en backend según el usuario autenticado.

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool recomendado**: Vite  
**Cliente HTTP**: Axios  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT  
**Arquitectura**: Feature-based structure  
**Paginación**: Obligatoria en listados

## 3. Endpoints backend consumidos

### 3.1 Listar liquidaciones

```http
GET /api/liquidaciones?page={page}&size={size}&sort={sort}&idRuta={idRuta}&estado={estado}
```

### 3.2 Obtener detalle de liquidación

```http
GET /api/liquidaciones/{id}
```

## 4. Estructura propuesta frontend

```text
frontend/
├── src/
│   ├── modules/
│   │   └── liquidaciones/
│   │       ├── components/
│   │       │   ├── LiquidacionesTable.jsx
│   │       │   ├── LiquidacionesFilters.jsx
│   │       │   ├── LiquidacionEstadoBadge.jsx
│   │       │   ├── LiquidacionDetailCard.jsx
│   │       │   ├── LiquidacionAjustesTable.jsx
│   │       │   └── LiquidacionSearchBar.jsx
│   │       │
│   │       ├── pages/
│   │       │   ├── LiquidacionesListPage.jsx
│   │       │   └── LiquidacionDetailPage.jsx
│   │       │
│   │       ├── services/
│   │       │   └── liquidacionesService.js
│   │       │
│   │       └── hooks/
│   │           ├── useLiquidaciones.js
│   │           └── useLiquidacionDetail.js
│   │
│   └── shared/
│       ├── components/
│       └── services/
│           └── apiClient.js
```

## 5. Contratos de datos esperados

### 5.1 Item de listado

```json
{
  "idLiquidacion": "uuid",
  "idRuta": "uuid",
  "fechaInicio": "2026-04-01T08:00:00",
  "fechaCierre": "2026-04-01T14:00:00",
  "tipoVehiculo": "MOTO",
  "precioParada": 3500,
  "numeroParadas": 12,
  "montoBruto": 42000,
  "montoNeto": 39000,
  "estadoLiquidacion": "CALCULADA",
  "fechaCalculo": "2026-04-01T15:00:00"
}
```

### 5.2 Detalle

```json
{
  "idLiquidacion": "uuid",
  "idRuta": "uuid",
  "fechaInicio": "2026-04-01T08:00:00",
  "fechaCierre": "2026-04-01T14:00:00",
  "tipoVehiculo": "MOTO",
  "precioParada": 3500,
  "numeroParadas": 12,
  "montoBruto": 42000,
  "montoNeto": 39000,
  "estadoLiquidacion": "CALCULADA",
  "fechaCalculo": "2026-04-01T15:00:00",
  "ajustes": [
    {
      "tipo": "PENALIDAD",
      "monto": 3000,
      "razon": "Paquete dañado"
    }
  ]
}
```

## 6. Fase 1: Configuración del módulo

- [ ] F001 Crear módulo `modules/liquidaciones`.
- [ ] F002 Registrar ruta `/liquidaciones`.
- [ ] F003 Registrar ruta `/liquidaciones/:id`.
- [ ] F004 Crear `liquidacionesService.js` con métodos `listarLiquidaciones` y `obtenerLiquidacion`.
- [ ] F005 Reutilizar `apiClient.js` con token JWT.
- [ ] F006 Definir helpers para formateo monetario y fechas.

## 7. Fase 2: Listado de liquidaciones

- [ ] F007 Implementar `useLiquidaciones.js` con estado de página, tamaño, filtros, carga y error.
- [ ] F008 Implementar `LiquidacionesFilters.jsx` con filtros por ID Ruta, estado y rango de fecha.
- [ ] F009 Implementar `LiquidacionSearchBar.jsx` para búsqueda rápida por ID Liquidación o ID Ruta.
- [ ] F010 Implementar `LiquidacionesTable.jsx` con columnas exigidas por spec:
  - ID Ruta
  - Fecha Inicio
  - Fecha Cierre
  - ID Liquidación
  - Tipo de vehículo
  - Precio Parada
  - Número de paradas
  - Monto Bruto
  - Monto Neto
  - Estado Liquidación
  - Fecha Cálculo
- [ ] F011 Implementar paginación.
- [ ] F012 Navegar al detalle desde cada fila.

## 8. Fase 3: Detalle de liquidación

- [ ] F013 Implementar `useLiquidacionDetail.js`.
- [ ] F014 Implementar `LiquidacionDetailPage.jsx`.
- [ ] F015 Implementar `LiquidacionDetailCard.jsx` con los campos principales.
- [ ] F016 Implementar `LiquidacionAjustesTable.jsx` con tipo, monto y razón.
- [ ] F017 Diferenciar visualmente estados de liquidación.
- [ ] F018 Mostrar trazabilidad financiera completa sin exponer campos internos de entidad.

## 9. Fase 4: Estados funcionales y errores

- [ ] F019 Mostrar `Liquidación inexistente` ante búsqueda sin registro.
- [ ] F020 Mostrar `Liquidación aún no calculada` cuando backend diferencie ese escenario.
- [ ] F021 Mostrar `No tienes permisos para consultar esta liquidación` ante HTTP 403.
- [ ] F022 Mostrar `Sistema temporalmente no disponible` ante HTTP 503.
- [ ] F023 Validar identificadores antes de consultar para evitar solicitudes malformadas.
- [ ] F024 Mantener filtros aplicados al volver desde el detalle.

## 10. Fase 5: Pruebas frontend

- [ ] F025 Test de render del listado con todos los campos exigidos.
- [ ] F026 Test de búsqueda por ID Liquidación.
- [ ] F027 Test de búsqueda por ID Ruta.
- [ ] F028 Test de paginación.
- [ ] F029 Test de detalle con ajustes.
- [ ] F030 Test de mensajes de liquidación inexistente y aún no calculada.
- [ ] F031 Test de HTTP 403 y 503.

## 11. Dependencias y orden de ejecución

1. Listado antes que detalle.
2. Paginación antes que filtros avanzados.
3. Detalle después de estabilizar DTO del backend.
4. Errores funcionales deben alinearse con `GlobalExceptionHandler` backend.
5. El backend sigue siendo responsable de permisos y alcance de datos.

# Plan Técnico Frontend: Cierre de ruta

**Fecha**: 2026-05-03  
**Rama base**: `develop-docker`  
**Feature backend relacionada**: `feature/mod3-Cierre-Ruta`  
**Plan backend base**: `Specs/Cierre de ruta/plan-cierre-de-ruta.md`  
**Frontend objetivo**: Dashboard financiero de rutas procesadas

## 1. Resumen

Este plan define la implementación frontend para la funcionalidad de **Cierre de ruta** dentro del Módulo 3 de Facturación y Liquidación.

El cierre de ruta no es disparado manualmente por React. El backend recibe eventos asíncronos `RUTA_CERRADA` desde el Módulo de Flotas y Rutas mediante mensajería. La responsabilidad del frontend es permitir al equipo financiero consultar las rutas ya procesadas, identificar rutas con inconsistencias y revisar el detalle de paradas, motivos de falla, responsables financieros y alertas asociadas.

La interfaz debe respetar la arquitectura definida en los planes backend: React consume únicamente endpoints de lectura ya expuestos por el backend y no debe simular, publicar ni reprocesar eventos de cierre.

## 2. Contexto técnico frontend

**Lenguaje**: JavaScript  
**Framework**: React 18+  
**Build tool recomendado**: Vite  
**Cliente HTTP**: Axios  
**Testing**: Jest / React Testing Library  
**Autenticación**: JWT enviado por interceptor Axios  
**Arquitectura**: Feature-based structure por módulos  
**Destino de despliegue**: Docker / AWS-ready

## 3. Endpoints backend consumidos

### 3.1 Consultar ruta procesada por identificador

```http
GET /api/rutas/{id}
```

Uso frontend:

- Consultar el detalle de una ruta cerrada ya procesada.
- Mostrar datos de ruta, transportista, vehículo, modelo de contrato y paradas.
- Visualizar motivos de falla y responsables financieros.

### 3.2 Listar rutas procesadas

```http
GET /api/rutas?page={page}&size={size}&fechaInicio={fechaInicio}&fechaFin={fechaFin}&estado={estado}
```

Uso frontend:

- Alimentar el dashboard financiero de rutas.
- Permitir filtros por fecha, estado de procesamiento y alertas.
- Soportar paginación.

> Nota técnica: el plan backend menciona explícitamente `GET /api/rutas/{id}` como obligatorio. Para el dashboard listado, este plan propone `GET /api/rutas` como endpoint de soporte frontend, manteniendo consistencia con la tarea backend de dashboard y filtros.

## 4. Estructura propuesta frontend

```text
frontend/
├── src/
│   ├── app/
│   │   ├── router.jsx
│   │   └── routes.js
│   │
│   ├── modules/
│   │   └── rutas/
│   │       ├── components/
│   │       │   ├── RutaTable.jsx
│   │       │   ├── RutaFilters.jsx
│   │       │   ├── RutaStatusBadge.jsx
│   │       │   ├── ParadasTable.jsx
│   │       │   ├── MotivoFallaBadge.jsx
│   │       │   └── RutaAlertPanel.jsx
│   │       │
│   │       ├── pages/
│   │       │   ├── RutasDashboardPage.jsx
│   │       │   └── RutaDetailPage.jsx
│   │       │
│   │       ├── services/
│   │       │   └── rutasService.js
│   │       │
│   │       ├── hooks/
│   │       │   ├── useRutas.js
│   │       │   └── useRutaDetail.js
│   │       │
│   │       └── utils/
│   │           └── rutaFormatters.js
│   │
│   ├── shared/
│   │   ├── components/
│   │   │   ├── DataTable.jsx
│   │   │   ├── EmptyState.jsx
│   │   │   ├── ErrorState.jsx
│   │   │   ├── LoadingState.jsx
│   │   │   └── PageHeader.jsx
│   │   │
│   │   ├── services/
│   │   │   └── apiClient.js
│   │   │
│   │   └── utils/
│   │       ├── dateFormatters.js
│   │       └── moneyFormatters.js
│   │
│   └── styles/
│       └── globals.css
```

## 5. Contratos de datos esperados

### 5.1 Ruta list item

```json
{
  "idRuta": "uuid",
  "idTransportista": "uuid",
  "nombreTransportista": "string",
  "tipoVehiculo": "MOTO | VAN | CAMION",
  "modeloContrato": "POR_PARADA | RECORRIDO_COMPLETO",
  "estadoProcesamiento": "PROCESADA | CON_ALERTAS | ERROR | DUPLICADA",
  "totalParadas": 12,
  "fechaCierre": "2026-04-08T14:30:00",
  "alertas": ["CONTRATO_NULO", "TARIFA_NO_ENCONTRADA"]
}
```

### 5.2 Detalle de ruta

```json
{
  "idRuta": "uuid",
  "transportista": {
    "idTransportista": "uuid",
    "nombre": "string"
  },
  "vehiculo": {
    "tipoVehiculo": "MOTO",
    "placa": "ABC123"
  },
  "modeloContrato": "POR_PARADA",
  "estadoProcesamiento": "PROCESADA",
  "paradas": [
    {
      "idParada": "uuid",
      "estado": "EXITOSA | FALLIDA",
      "motivoNoEntrega": "CLIENTE_AUSENTE",
      "responsableFalla": "CLIENTE",
      "porcentajePago": 50
    }
  ],
  "alertas": []
}
```

## 6. Fase 1: Configuración base frontend

**Propósito**: Preparar la base de navegación, consumo HTTP y layout para el dashboard financiero.

- [ ] F001 Crear módulo `modules/rutas` siguiendo estructura feature-based.
- [ ] F002 Configurar ruta `/rutas` para el dashboard de rutas procesadas.
- [ ] F003 Configurar ruta `/rutas/:idRuta` para el detalle de una ruta.
- [ ] F004 Crear `rutasService.js` usando `apiClient` compartido.
- [ ] F005 Asegurar que `apiClient.js` incluya interceptor JWT para endpoints protegidos.
- [ ] F006 Definir componentes compartidos de carga, error y estado vacío reutilizables.

**Checkpoint**: El frontend puede navegar hacia el dashboard y preparar llamadas HTTP autenticadas hacia rutas.

## 7. Fase 2: Dashboard de rutas procesadas

**Propósito**: Permitir al equipo financiero consultar rutas cerradas procesadas por el backend.

- [ ] F007 Implementar `useRutas.js` para manejar paginación, filtros y estados de carga.
- [ ] F008 Implementar `RutaFilters.jsx` con filtros por fecha inicial, fecha final y estado de procesamiento.
- [ ] F009 Implementar `RutaTable.jsx` con columnas:
  - ID Ruta
  - Transportista
  - Tipo de vehículo
  - Modelo de contrato
  - Total de paradas
  - Estado de procesamiento
  - Fecha de cierre
  - Acciones
- [ ] F010 Implementar `RutaStatusBadge.jsx` para diferenciar visualmente `PROCESADA`, `CON_ALERTAS`, `ERROR` y `DUPLICADA`.
- [ ] F011 Implementar navegación desde cada fila hacia `/rutas/:idRuta`.
- [ ] F012 Mostrar `EmptyState` cuando no existan rutas procesadas o los filtros no retornen resultados.
- [ ] F013 Mostrar `ErrorState` ante errores 401, 403, 404, 500 o 503.

**Checkpoint**: El usuario autorizado puede listar rutas cerradas, filtrar resultados y acceder al detalle.

## 8. Fase 3: Detalle de ruta y paradas

**Propósito**: Mostrar la información procesada que servirá como insumo para la liquidación.

- [ ] F014 Implementar `useRutaDetail.js` para consumir `GET /api/rutas/{id}`.
- [ ] F015 Implementar `RutaDetailPage.jsx` con secciones:
  - Resumen de ruta
  - Datos del transportista
  - Datos del vehículo
  - Modelo de contrato
  - Alertas de procesamiento
  - Tabla de paradas
- [ ] F016 Implementar `ParadasTable.jsx` con columnas:
  - ID Parada
  - Estado
  - Motivo de no entrega
  - Responsable financiero
  - Porcentaje de pago
- [ ] F017 Implementar `MotivoFallaBadge.jsx` para marcar motivos de falla críticos.
- [ ] F018 Implementar `RutaAlertPanel.jsx` para advertencias como contrato nulo o tarifa no encontrada.
- [ ] F019 Evitar cualquier botón o acción que permita reprocesar manualmente el cierre desde frontend.

**Checkpoint**: El detalle muestra la información financiera relevante sin alterar el flujo asíncrono del backend.

## 9. Fase 4: Estados, errores y seguridad

**Propósito**: Garantizar una experiencia clara ante estados funcionales y fallas.

- [ ] F020 Manejar HTTP 401 redirigiendo a autenticación o mostrando sesión expirada.
- [ ] F021 Manejar HTTP 403 con mensaje de acceso no autorizado.
- [ ] F022 Manejar HTTP 404 con mensaje `Ruta no encontrada`.
- [ ] F023 Manejar HTTP 503 con mensaje de indisponibilidad temporal del sistema.
- [ ] F024 Mostrar skeleton loaders durante consultas.
- [ ] F025 Validar que los filtros de fecha no permitan rangos invertidos.

## 10. Fase 5: Pruebas frontend

- [ ] F026 Test de `rutasService.js` validando construcción correcta de URLs.
- [ ] F027 Test de `RutaTable.jsx` validando render de columnas principales.
- [ ] F028 Test de `RutaFilters.jsx` validando emisión de filtros.
- [ ] F029 Test de `RutaDetailPage.jsx` validando render de paradas y alertas.
- [ ] F030 Test de errores para 401, 403, 404 y 503.

## 11. Dependencias y orden de ejecución

1. Configurar `apiClient` antes de servicios específicos.
2. Implementar listado antes de detalle.
3. Implementar detalle antes de filtros avanzados.
4. No crear acciones de procesamiento manual, porque el cierre de ruta se origina por mensajería backend.
5. Integrar el dashboard únicamente cuando el backend tenga estable `GET /api/rutas/{id}` y, si se implementa, `GET /api/rutas`.

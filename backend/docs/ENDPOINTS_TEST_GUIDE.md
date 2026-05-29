# Guía de prueba de endpoints — 20261LogisticsInvoice

**Base URL:** `http://localhost:8080`  
**Swagger UI:** `http://localhost:8080/swagger-ui.html`

## Usuarios de prueba (JWT)

| Email | Password | Rol | UUID (sub) |
|-------|----------|-----|------------|
| `admin@test.com` | `123456` | `ADMIN` | `00000000-0000-0000-0000-000000000001` |
| `gestor@test.com` | `123456` | `GESTOR_FINANCIERO` | `00000000-0000-0000-0000-000000000002` |
| `transportista@test.com` | `123456` | `TRANSPORTISTA` | `a1b2c3d4-e5f6-7890-1234-567890abcdef` |

---

## Endpoints

| # | Endpoint | Método | Auth requerida | Datos de prueba disponibles | Params / ID de ejemplo | Notas |
|---|----------|--------|---------------|----------------------------|------------------------|-------|
| 1 | `/auth/login` | POST | Sin auth | Usuarios hardcodeados (ver tabla arriba) | `{"email":"admin@test.com","password":"123456"}` | Devuelve `access_token` para usar en Authorize |
| 2 | `/api/v1/rutas` | GET | Cualquier rol | 4 rutas insertadas | `?estado=OK` o `?estado=REQUIERE_REVISION` | Paginado, 20 por página por defecto |
| 3 | `/api/v1/rutas/{id}` | GET | Cualquier rol | 4 rutas | `id=39000001-0000-0000-0000-000000000001` (CAMION, OK) · `39000001-0000-0000-0000-000000000002` (FURGON, REQUIERE_REVISION) | Incluye lista de paradas |
| 4 | `/api/v1/rutas/cerrar` | POST | Sin auth | — | Ver payload en `test_database_seed.sql` (sección RW4) | Genera nueva ruta + dispara cálculo de liquidación |
| 5 | `/api/contratos` | GET | GESTOR_FINANCIERO | 5 contratos | `?page=0&size=10` | Usar token de `gestor@test.com` |
| 6 | `/api/contratos/{idContrato}` | GET | GESTOR_FINANCIERO | 5 contratos | `idContrato=CON-2024-001` · `CON-2024-002` · `CON-2024-003` | `idContrato` es el ID de negocio (string), no el UUID |
| 7 | `/api/contratos` | POST | GESTOR_FINANCIERO | — | Ver payload en `test_database_seed.sql` (sección contratos) | Crear `CON-2024-006` como ejemplo |
| 8 | `/api/eventos/cierre-ruta` | POST | Sin auth | 5 liquidaciones ya calculadas | Ver payload en `test_database_seed.sql` (sección liquidaciones) | Crea liquidación al recibir evento de cierre |
| 9 | `/api/liquidaciones` | GET | ADMIN · GESTOR_FINANCIERO · TRANSPORTISTA | 5 liquidaciones | `?page=0&size=10&sortBy=fechaCalculo&sortDir=desc` | El rol TRANSPORTISTA filtra por su `usuarioId` |
| 10 | `/api/liquidaciones/{id}` | GET | ADMIN · GESTOR_FINANCIERO · TRANSPORTISTA | 5 liquidaciones | `id=f6000001-0000-0000-0000-000000000002` (RECALCULADA con ajustes) | Devuelve ajustes anidados |
| 11 | `/api/liquidaciones/buscar` | GET | ADMIN · GESTOR_FINANCIERO · TRANSPORTISTA | 5 liquidaciones | `?estado=CALCULADA` · `?estado=PAGADA` | Filtros como query params (`VisualizarLiquidacionFiltroDTO`) |
| 12 | `/api/liquidaciones/{id}/recalcular` | PUT | **Solo ADMIN** | LIQ1 y LIQ4 en estado CALCULADA | `id=f6000001-0000-0000-0000-000000000001` | Usar token de `admin@test.com`; ver payload en seed |
| 13 | `/api/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar` | POST | Cualquier rol | 7 paquetes, 4 rutas | `idRuta=39000001-0000-0000-0000-000000000001` `idPaquete=5b000001-0000-0000-0000-000000000001` | Llama a API externa; puede devolver PAQUETE_NO_ENCONTRADO si servicio no está disponible |
| 14 | `/api/paquetes/{idPaquete}/historial` | GET | Cualquier rol | 7 paquetes con historial | `idPaquete=5b000001-0000-0000-0000-000000000003` (DEVUELTO, 3 estados) · `5b000001-0000-0000-0000-000000000005` (DANADO) | Paginado, 50 por página por defecto |
| 15 | `/api/sincronizacion/logs` | GET | Cualquier rol | 5 logs | `?page=0&size=50` | Lista todos los logs de sincronización |
| 16 | `/api/sincronizacion/logs/paquetes/{idPaquete}` | GET | Cualquier rol | 5 logs (3 paquetes distintos) | `idPaquete=5b000001-0000-0000-0000-000000000001` | Filtra logs por paquete |
| 17 | `/api/v1/pagos/{idPago}/estado` | GET | Cualquier rol | 4 pagos | `idPago=6c000001-0000-0000-0000-000000000001` (PENDIENTE) · `6c000001-0000-0000-0000-000000000002` (PAGADO) | Estado actual del pago |
| 18 | `/api/v1/pagos/{idPago}/eventos` | GET | Cualquier rol | 4 pagos, 9 eventos | `idPago=6c000001-0000-0000-0000-000000000002` (3 eventos: PENDIENTE→EN_PROCESO→PAGADO) | Historial de transacciones bancarias |
| 19 | `/api/v1/liquidaciones/{idLiquidacion}/pago/estado` | GET | Cualquier rol | 4 pagos | `idLiquidacion=f6000001-0000-0000-0000-000000000002` | Obtiene pago asociado a una liquidación |
| 20 | `/api/v1/pagos/webhook/estado` | POST | Sin auth | — | Ver payload en `test_database_seed.sql` (sección eventos_transaccion) | Simula webhook bancario; usa `id_transaccion_banco` único |
| 21 | `/api/pagos` | GET | Cualquier rol (filtra por JWT sub) | 4 pagos (2 del transportista) | Usar token de `transportista@test.com` → devuelve PG1 + PG3 | El filtro por usuario viene del claim `sub` del JWT |
| 22 | `/api/pagos/{id}` | GET | Cualquier rol (verifica ownership) | 4 pagos | `id=6c000001-0000-0000-0000-000000000001` con token transportista | Detalle de un pago específico |

---

## Flujos de prueba completos

### Flujo 1 — Ciclo completo de una liquidación
```
1. POST /auth/login (admin)
2. POST /api/v1/rutas/cerrar          → crea ruta + dispara liquidación
3. GET  /api/liquidaciones             → verifica que aparece la nueva liquidación
4. GET  /api/liquidaciones/{id}        → ver detalle
5. PUT  /api/liquidaciones/{id}/recalcular → ajustar valores
6. GET  /api/liquidaciones/{id}        → verificar estado RECALCULADA con ajustes
```

### Flujo 2 — Ciclo de pago
```
1. POST /auth/login (cualquier rol)
2. GET  /api/v1/liquidaciones/{idLiquidacion}/pago/estado → PENDIENTE
3. POST /api/v1/pagos/webhook/estado  → envía evento banco (EN_PROCESO)
4. GET  /api/v1/pagos/{idPago}/estado → verifica cambio de estado
5. GET  /api/v1/pagos/{idPago}/eventos → ver historial de transacciones
```

### Flujo 3 — Transportista visualiza sus liquidaciones y pagos
```
1. POST /auth/login (transportista@test.com)
2. GET  /api/liquidaciones             → filtra sus liquidaciones (usuario_id)
3. GET  /api/pagos                     → filtra sus pagos (sub del JWT)
4. GET  /api/pagos/{id}               → detalle de un pago
```

### Flujo 4 — Sincronización de paquetes
```
1. POST /auth/login (cualquier rol)
2. POST /api/rutas/{idRuta}/paquetes/{idPaquete}/sincronizar
3. GET  /api/paquetes/{idPaquete}/historial  → ver nuevos estados
4. GET  /api/sincronizacion/logs/paquetes/{idPaquete} → ver log HTTP
```

---

## IDs de referencia rápida

| Entidad | ID | Descripción |
|---------|-----|-------------|
| **Ruta** (write) | `39000001-0000-0000-0000-000000000001` | CAMION, estado OK |
| **Ruta** (write) | `39000001-0000-0000-0000-000000000002` | FURGON, REQUIERE_REVISION |
| **Contrato** (id_contrato) | `CON-2024-001` | Por parada, CAMION |
| **Contrato** (id_contrato) | `CON-2024-002` | Recorrido completo, FURGON |
| **Liquidación** | `f6000001-0000-0000-0000-000000000001` | CALCULADA, lista para recalcular |
| **Liquidación** | `f6000001-0000-0000-0000-000000000002` | RECALCULADA, con ajustes |
| **Liquidación** | `f6000001-0000-0000-0000-000000000003` | PAGADA |
| **Liquidación** | `f6000001-0000-0000-0000-000000000005` | ERROR |
| **Paquete** | `5b000001-0000-0000-0000-000000000001` | ENTREGADO, 2 estados en historial |
| **Paquete** | `5b000001-0000-0000-0000-000000000003` | DEVUELTO, 3 estados en historial |
| **Pago** | `6c000001-0000-0000-0000-000000000001` | PENDIENTE |
| **Pago** | `6c000001-0000-0000-0000-000000000002` | PAGADO, flujo completo (3 eventos) |
| **Pago** | `6c000001-0000-0000-0000-000000000003` | RECHAZADO |
| **Transportista** | `a1000001-0000-0000-0000-000000000001` | Carlos López Pérez |

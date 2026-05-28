# Documentación del Flujo — Módulo de Logística

## Descripción general

El módulo recibe eventos de cierre de ruta y estados de paquetes desde colas SQS, valida la información con servicios externos (conductor y contrato), y genera una liquidación para el transportista según el tipo de contratación y el estado final de cada paquete.

---

## Arquitectura general

```
AWS SQS
  ├── Cola cierre de ruta       → RutaCerradaConsumer
  ├── Cola módulo de paquetes   → EstadoPaqueteConsumer
  └── Cola contratos            → ContratoConsumer (del profesor, puede no estar disponible)

Servicios externos (via Postman Mock Server en local)
  ├── Validación conductor      → GET /conductores/{id}
  └── Voucher de pago banco     → POST /pagos
```

---

## Flujo 1 — Recepción de estados de paquetes

### Descripción
El módulo de paquetes envía por SQS el estado final de cada paquete con su `idPaquete` e `idRuta`. Esta información se guarda en BD y sirve como **fuente de verdad** para la liquidación.

### Flujo
```
Cola novedad-estado-paquete (SQS)
        │
        ▼
EstadoPaqueteConsumer.consumir()
        ├── Deserializa JsonNode → EstadoPaqueteMensajeDTO
        └── ProcesarNovedadEstadoPaqueteUseCase.ejecutar()
                ├── Valida que el evento no sea null
                ├── Resuelve el estado con EstadoPaqueteService
                │       ¿estado reconocido?
                │           NO → lanza IllegalArgumentException
                ├── Crea o actualiza Paquete en BD
                │       (idPaquete, idRuta, estadoActual)
                └── Guarda HistorialEstado en BD
```

### Estados posibles del módulo de paquetes
| Estado recibido | Significado |
|---|---|
| `ENTREGADO` | Paquete entregado exitosamente |
| `DEVUELTO` | Paquete devuelto (responsabilidad del cliente) |
| `DANADO` | Paquete dañado (responsabilidad del transportista) |
| `EXTRAVIADO` | Paquete extraviado (responsabilidad del transportista) |

---

## Flujo 2 — Recepción de contratos

### Descripción
El módulo del profesor envía contratos de conductores por SQS. Si no está disponible, se usa un mock con tarifas configuradas en `application.properties`.

### Flujo
```
Cola contratos-creados (SQS)
        │
        ▼
ProcesarContratoCreadoUseCase.ejecutar()
        ├── ¿contrato ya existe en BD?
        │       SÍ → log "duplicado ignorado", termina
        ├── Busca o crea Transportista en BD
        └── Guarda Contrato en BD
                (tipoContrato, esPorParada, precioParadas, precio, seguro)
```

### Si el profesor no tiene disponible la cola
Se usa el `modeloContrato` que viene en el evento de cierre de ruta junto con tarifas configuradas:

```properties
app.mock.tarifa.por-parada=15000
app.mock.tarifa.recorrido-completo=80000
```

---

## Flujo 3 — Recepción y procesamiento del cierre de ruta

### Descripción
El módulo de cierre de ruta envía por SQS el evento `RUTA_CERRADA` con toda la información de la ruta, el transportista, el vehículo y las paradas.

### Flujo completo

#### Fase 1 — Recepción del mensaje SQS
```
Cola logistics-cierre-ruta (SQS)
        │
        ▼
RutaCerradaConsumer.consumir()
        ├── Deserializa JsonNode → RutaCerradaMensajeDTO
        │       (rutaId, fechas, conductor, vehículo, paradas)
        └── ProcesarRutaCerradaUseCase.ejecutar()
```

#### Fase 2 — Procesamiento del cierre de ruta
```
ProcesarRutaCerradaUseCase.ejecutar()
        │
        ├── ¿rutaId ya existe en BD?
        │       SÍ → log "evento duplicado ignorado", termina
        │
        ├── Mapea evento → RutaCerrada (dominio)
        ├── Busca Transportista en BD
        │       ¿existe? SÍ → reutiliza
        │                NO → crea nuevo
        ├── Asigna transportista a la ruta
        ├── ClasificacionRutaService.clasificar(ruta)
        │
        ├── ruta.procesar(ahora)
        │       ├── validarEstructura()
        │       │       rutaId no nulo
        │       │       transportista no nulo
        │       │       paradas no vacías
        │       │
        │       ├── validarParadas()
        │       │       cada parada tiene: estado, paqueteId
        │       │       si FALLIDA → debe tener motivoFalla
        │       │
        │       └── evaluarEstado()
        │               ¿modeloContrato == null? → REQUIERE_REVISION (alerta: CONTRATO_NULO)
        │               ¿tipoVehiculo == null?   → REQUIERE_REVISION (alerta: VEHICULO_DESCONOCIDO)
        │               todo correcto            → OK
        │
        ├── Guarda RutaCerrada en BD con estadoProcesamiento
        ├── Publica RutaCerradaProcesadaEvent (interno Spring)
        ├── Limpia eventos de dominio
        └── ack.acknowledge() → confirma a SQS
```

---

## Flujo 4 — Generación de liquidación

### Descripción
Al publicarse el `RutaCerradaProcesadaEvent`, el `LiquidacionEventHandler` toma el control y ejecuta el flujo completo de liquidación.

### Flujo completo
```
RutaCerradaProcesadaEvent
        │
        ▼
LiquidacionEventHandler.handle()
        │
        ├── ¿estadoProcesamiento == REQUIERE_REVISION?
        │       SÍ → log warning, termina (no se genera liquidación)
        │
        ├── 1. Obtiene RutaCerrada completa desde BD
        │
        ├── 2. Valida conductor (Postman Mock Server en local)
        │       GET /conductores/{transportistaId}
        │       ├── conductor ACTIVO    → continúa 
        │       ├── conductor INACTIVO  → log warning, termina
        │       └── conductor no existe → log error, termina
        │
        ├── 3. Resuelve contrato
        │       ¿Existe contrato real del profesor en BD?
        │           SÍ → usa tarifa y tipo reales
        │                ¿Existe ContratoTarifa sincronizada?
        │                    SÍ → usa la existente
        │                    NO → sincroniza y guarda nueva ContratoTarifa
        │           NO → usa modeloContrato del evento de cierre de ruta
        │                + tarifa de application.properties (mock)
        │                → guarda ContratoTarifa mock
        │
        ├── 4. Cruza paradas con módulo de paquetes
        │       Por cada parada del cierre de ruta:
        │
        │           ¿paqueteId existe en BD módulo paquetes? (fuente de verdad)
        │               SÍ → usa estado del módulo de paquetes
        │                     "ENTREGADO"  → ENTREGADO
        │                     "DEVUELTO"   → FALLIDO_CLIENTE  (pago parcial)
        │                     "DANADO"     → FALLIDO_TRANSPORTISTA (sin pago)
        │                     "EXTRAVIADO" → FALLIDO_TRANSPORTISTA (sin pago)
        │
        │               NO → fallback: usa estado del cierre de ruta
        │                     EXITOSA                              → ENTREGADO
        │                     FALLIDA + responsable CLIENTE        → FALLIDO_CLIENTE
        │                         (CLIENTE_AUSENTE, DIRECCION_ERRONEA, RECHAZADO)
        │                     FALLIDA + responsable TRANSPORTISTA  → FALLIDO_TRANSPORTISTA
        │                         (ZONA_DIFICIL_ACCESO)
        │                     NOVEDAD + DEVOLUCION                 → FALLIDO_CLIENTE
        │                     NOVEDAD + PAQUETE_DANADO             → FALLIDO_TRANSPORTISTA
        │                     NOVEDAD + PERDIDA_PAQUETE            → FALLIDO_TRANSPORTISTA
        │                     PENDIENTE                            → FALLIDO_TRANSPORTISTA
        │                     SIN_GESTION_CONDUCTOR                → FALLIDO_TRANSPORTISTA
        │                     EXCLUIDA_DESPACHO                    → FALLIDO_TRANSPORTISTA
        │
        └── 5. Calcula y guarda liquidación
                LiquidacionCalcularUseCase.execute()
```

---

## Flujo 5 — Cálculo de la liquidación

### Descripción
Según el tipo de contratación del conductor, se aplica una estrategia diferente para calcular el valor de la liquidación.

### Estrategia POR_PARADA
Calcula el valor por cada paquete individualmente:

| Estado paquete | Factor de pago |
|---|---|
| `ENTREGADO` | 1.0 (pago completo) |
| `FALLIDO_CLIENTE` | 0.5 (pago parcial) |
| `FALLIDO_TRANSPORTISTA` | 0.0 (sin pago) |

```
valorBase = Σ (tarifa × factor) por cada paquete válido
```

### Estrategia RECORRIDO_COMPLETO
```
¿Todos los paquetes fueron ENTREGADO?
    SÍ → valorBase = tarifa fija del contrato
    NO → valorBase = $0
```

### Proceso de guardado
```
LiquidacionCalcularUseCase.execute()
        ├── Obtiene ContratoTarifa de BD
        ├── ¿Ya existe liquidación para esta ruta?
        │       SÍ → lanza DuplicadaException (idempotencia)
        │
        ├── Aplica estrategia (POR_PARADA o RECORRIDO_COMPLETO)
        ├── Crea Liquidacion
        │       id, idRuta, idContrato, valorBase, valorFinal
        │       estado: CALCULADA
        │       fechaCalculo: ahora
        ├── Guarda Liquidacion en BD
        └── Guarda AuditoriaLiquidacion en BD
```

---

## Flujo 6 — Pago al transportista (voucher)

### Descripción
Una vez creada la liquidación, se inicia el proceso de pago al banco del profesor via webhook. El banco responde de forma asíncrona con un voucher.

### Flujo
```
Webhook llega al endpoint
        │
        ▼
PagoService.recibirEvento()
        ├── Responde 202 inmediatamente
        └── EventoPagoAsyncPort.procesarAsync() (hilo separado)
                │
                ▼
        PagoService.procesarEvento()
                ├── ¿evento duplicado? → termina (idempotencia)
                ├── Guarda EventoTransaccion (estado: RECIBIDO)
                ├── ¿liquidación existe en BD?
                │       NO → guarda error, termina
                ├── ¿pago existe?
                │       NO → crea pago con estado PENDIENTE
                │       SÍ → valida transición de estado
                │            valida secuencia (no desordenada)
                └── Actualiza estado del pago
                        Guarda HistorialEstadoPago
                        Guarda EventoTransaccion (estado: PROCESADO)
```

### Estados del pago
```
PENDIENTE → EN_PROCESO → PAGADO
                       → RECHAZADO
```

---

## Mocks de servicios externos

| Servicio | Entorno local | Entorno producción |
|---|---|---|
| Validación conductor | Postman Mock Server | Servicio real del profesor |
| Voucher banco | Postman Mock Server | Servicio real del profesor |
| Cola contratos | Tarifas de `application.properties` | Cola SQS del profesor |

### Configuración local (`application-local.yml`)
```yaml
app:
  mock:
    tarifa:
      por-parada: 15000
      recorrido-completo: 80000
  servicios:
    conductores:
      base-url: https://6cfdb063-a734-47f4-911b-c0a53a6124c6.mock.pstmn.io
    banco:
      base-url: https://6cfdb063-a734-47f4-911b-c0a53a6124c6.mock.pstmn.io
      timeout-segundos: 10
      api-key: ${POSTMAN_API_KEY}
```

---

## Resultado final en BD tras procesar una ruta

| Tabla | Qué se guarda |
|---|---|
| `ruta_cerrada` | Ruta con `estadoProcesamiento` (OK / REQUIERE_REVISION) |
| `transportista_ruta` | Transportista creado o reutilizado |
| `novedad_estado_paquete` | Estado final de cada paquete por rutaId |
| `contrato_tarifa` | Contrato sincronizado (real o mock) |
| `liquidacion` | Liquidación con `valorFinal` calculado, estado `CALCULADA` |
| `auditoria_liquidacion` | Registro del cálculo con monto final |
| `pago` | Estado del pago al transportista |
| `historial_estado_pago` | Historial de transiciones del pago |
| `evento_transaccion` | Registro de cada evento de pago recibido |

---

## Casos de error contemplados

| Caso | Comportamiento |
|---|---|
| Evento de ruta duplicado | Se ignora, log info |
| Ruta con `modeloContrato` nulo | Estado `REQUIERE_REVISION`, sin liquidación |
| Ruta con `tipoVehiculo` nulo | Estado `REQUIERE_REVISION`, sin liquidación |
| Conductor inactivo | Log warning, sin liquidación |
| Conductor no encontrado | Log error, sin liquidación |
| Sin paquetes en BD al liquidar | Log error, sin liquidación |
| Liquidación duplicada | Lanza `DuplicadaException` |
| Estado de paquete desconocido | Lanza `IllegalArgumentException` |
| Evento de pago duplicado | Se ignora (idempotencia por `idTransaccionBanco`) |
| Evento de pago desordenado | Se rechaza, log warning |
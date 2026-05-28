# Documentación Técnica — 20261LogisticsInvoice (Backend)

## Descripción general

Sistema de liquidación de transportistas para una empresa de logística. Recibe eventos de cierre de ruta y estados de paquetes desde colas SQS de AWS, valida con servicios externos (conductor y banco), y genera liquidaciones automáticas según el tipo de contratación.

---

## Stack tecnológico

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 (Oracle OpenJDK) |
| Framework | Spring Boot |
| Build | Gradle (Wrapper 8.x) |
| Base de datos | PostgreSQL |
| Migraciones | Flyway |
| Mensajería | AWS SQS (via awspring) |
| Servicios externos | REST (RestClient de Spring) |
| Mocks externos | Postman Mock Server |
| Contenedores locales | Docker + LocalStack |
| Seguridad | JWT |
| Documentación API | Swagger / OpenAPI |

---

## Arquitectura

El proyecto sigue **arquitectura hexagonal (ports & adapters)** dividida en tres capas:

```
src/main/java/com/logistica/
  ├── domain/          ← reglas de negocio puras, sin dependencias de Spring
  ├── application/     ← casos de uso, puertos, DTOs de aplicación
  └── infrastructure/  ← adaptadores, persistencia, REST, SQS, seguridad
```

---

## Estructura de carpetas

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/logistica/
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── cierreRuta/          ← modelos, enums, eventos, repositorios, servicios
│   │   │   │   ├── conductor/           ← modelo Conductor, puerto ConductorGateway
│   │   │   │   ├── contratos/           ← modelos Contrato, Transportista, Seguro
│   │   │   │   ├── liquidacion/         ← modelos Liquidacion, Paquete, ContratoTarifa, estrategias
│   │   │   │   ├── novedadEstadoPaquete/← modelos y repositorios de estados de paquetes
│   │   │   │   ├── registrarEstadoPago/ ← modelos de pago, puerto BancoGateway
│   │   │   │   ├── visualizarEstadoPago/← modelos del read model de pagos
│   │   │   │   ├── visualizarLiquidacion/← modelos del read model de liquidaciones
│   │   │   │   └── shared/              ← enums compartidos (TipoVehiculo), excepciones base
│   │   │   │
│   │   │   ├── application/
│   │   │   │   ├── auth/                ← DTOs de login/token
│   │   │   │   ├── cierreRuta/          ← ProcesarRutaCerradaUseCase, puertos, mappers
│   │   │   │   ├── conductor/           ← ValidarConductorUseCase
│   │   │   │   ├── contratos/           ← ProcesarContratoCreadoUseCase
│   │   │   │   ├── liquidacion/         ← LiquidacionCalcularUseCase, StrategyFactory
│   │   │   │   ├── novedadEstadoPaquete/← ProcesarNovedadEstadoPaqueteUseCase
│   │   │   │   ├── registrarEstadoPago/ ← PagoService (recibir, procesar, actualizar)
│   │   │   │   ├── visualizarEstadoPago/← consultas de estado de pago
│   │   │   │   └── visualizarLiquidacion/← consultas de liquidaciones
│   │   │   │
│   │   │   └── infrastructure/
│   │   │       ├── auth/                ← servicio JWT, controlador de login
│   │   │       ├── cierreRuta/          ← adaptadores, persistencia JPA, controladores REST, handlers
│   │   │       ├── conductor/           ← ConductorGatewayImpl, config RestClient, controlador
│   │   │       ├── contratos/           ← adaptadores, persistencia JPA, controladores REST
│   │   │       ├── liquidacion/         ← LiquidacionEventHandler, persistencia JPA, controladores
│   │   │       ├── messaging/           ← consumers SQS (RutaCerradaConsumer, EstadoPaqueteConsumer)
│   │   │       ├── novedadEstadoPaquete/← persistencia JPA, controladores
│   │   │       ├── registrarEstadoPago/ ← BancoGatewayImpl, async config, persistencia JPA, webhook
│   │   │       ├── shared/              ← config global (CORS, seguridad JWT), TarifaMockProperties
│   │   │       ├── visualizarEstadoPago/← adaptadores, persistencia JPA, controladores
│   │   │       └── visualizarLiquidacion/← adaptadores, persistencia JPA, controladores
│   │   │
│   │   └── resources/
│   │       ├── application.properties       ← config base
│   │       ├── application.yml              ← perfil activo por defecto: local
│   │       ├── application-local.yml        ← BD local, AWS real, Postman Mock Server
│   │       ├── application-dev.properties   ← BD local, LocalStack
│   │       ├── application-docker.yml       ← BD Docker, LocalStack
│   │       ├── application-prod.properties  ← variables de entorno de producción
│   │       └── db/migration/
│   │           ├── V1__init_clean_schema.sql
│   │           ├── V2__seed_test_data.sql
│   │           ├── V3__add_fecha_hora_gestion_to_parada.sql
│   │           └── V4__add_voucher_to_pagos.sql
│   │
│   └── test/
│       └── java/com/logistica/
│           ├── application/             ← tests unitarios de casos de uso
│           ├── conductor/               ← tests unitarios e integración del conductor
│           ├── domain/                  ← tests unitarios del dominio (cierreRuta, estrategias)
│           ├── infrastructure/          ← tests de consumers, repositorios y controladores
│           ├── NovedadEstadoPaquete/    ← tests de integración del módulo de paquetes
│           ├── RegistrarEstadoPago/     ← tests unitarios e integración del módulo de pagos
│           ├── VisualizarEstadoPago/    ← tests del módulo de visualización de pagos
│           └── VisualizarLiquidación/   ← tests del módulo de visualización de liquidaciones
│
├── docs/payloads/       ← ejemplos de payloads JSON para pruebas
├── localstack-init/     ← scripts de inicialización de colas SQS en LocalStack
├── build.gradle
├── gradlew
├── docker-compose.yml
└── Dockerfile
```

---

## Módulos del sistema

El sistema está organizado en **9 módulos funcionales**, cada uno con su propio contexto en las tres capas:

| Módulo | Qué hace |
|---|---|
| `cierreRuta` | Recibe y procesa eventos de cierre de ruta desde SQS |
| `conductor` | Valida conductores contra servicio externo (Postman Mock) |
| `contratos` | Recibe y almacena contratos desde SQS del profesor |
| `liquidacion` | Calcula la liquidación del transportista |
| `novedadEstadoPaquete` | Recibe y almacena estados de paquetes desde SQS |
| `registrarEstadoPago` | Procesa eventos de pago del banco vía webhook (asíncrono) |
| `visualizarEstadoPago` | Consulta el estado actual de pagos (read model) |
| `visualizarLiquidacion` | Consulta liquidaciones (read model) |
| `auth` | Autenticación JWT |

---

## Esquema de base de datos

### Tablas principales

| Tabla | Módulo | Descripción |
|---|---|---|
| `transportista` | cierreRuta / contratos | Conductores (compartida entre módulos) |
| `ruta` | cierreRuta | Write model del cierre de ruta |
| `parada` | cierreRuta | Paradas de cada ruta con estado y motivo |
| `contratos` | contratos / liquidacion | Contratos de conductores (también almacena ContratoTarifa) |
| `seguros` | contratos | Pólizas de seguro asociadas a contratos |
| `vehiculos` | contratos | Vehículos de los transportistas |
| `rutas` | visualizarLiquidacion | Read model de rutas para consultas |
| `liquidaciones` | liquidacion | Write + read model de liquidaciones (tabla compartida) |
| `ajustes` | liquidacion | Bonos y penalizaciones de liquidaciones |
| `auditoria_liquidacion` | liquidacion | Historial de cálculos y recálculos |
| `paquetes` | novedadEstadoPaquete | Estado actual de paquetes por ruta |
| `historial_estados` | novedadEstadoPaquete | Historial de estados de cada paquete |
| `log_sincronizacion` | novedadEstadoPaquete | Log de sincronizaciones con servicio externo |
| `pagos` | registrarEstadoPago | Write + read model de pagos (tabla compartida) |
| `estados_pago` | registrarEstadoPago | Historial de transiciones de estado de pago |
| `eventos_transaccion` | registrarEstadoPago | Registro de cada webhook recibido del banco |
| `liquidaciones_referencia` | registrarEstadoPago | Referencia de liquidaciones para validación de pagos |
| `eventos` | visualizarEstadoPago | Read model de eventos de pago |

### Migraciones Flyway

| Archivo | Qué hace |
|---|---|
| `V1__init_clean_schema.sql` | Crea todas las tablas e índices |
| `V2__seed_test_data.sql` | Inserta datos de prueba para desarrollo |
| `V3__add_fecha_hora_gestion_to_parada.sql` | Agrega columna `fecha_hora_gestion` a `parada` |
| `V4__add_voucher_to_pagos.sql` | Agrega `numero_voucher` y `fecha_procesamiento` a `pagos` |

---

## Colas SQS

| Cola | Consumer | Qué recibe |
|---|---|---|
| `logistics-cierre-ruta` | `RutaCerradaConsumer` | Evento `RUTA_CERRADA` con paradas y conductor |
| `novedad-estado-paquete` | `EstadoPaqueteConsumer` | Estado final de cada paquete con idRuta |
| `contratos-creados` | *(pendiente del profesor)* | Contratos de conductores con tarifas |

> La cola de contratos es del profesor y puede no estar disponible. En ese caso se usa el mock de tarifas configurado en `application.properties`.

---

## Servicios externos

| Servicio | Puerto (dominio) | Implementación | Mock |
|---|---|---|---|
| Validación conductor | `ConductorGateway` | `ConductorGatewayImpl` (RestClient) | Postman Mock Server |
| Pago al banco | `BancoGateway` | `BancoGatewayImpl` (RestClient) | Postman Mock Server |

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

## Perfiles de configuración

| Perfil | Cuándo usarlo | BD | SQS |
|---|---|---|---|
| `local` | Desarrollo con AWS real y Postman Mock | PostgreSQL local | AWS real |
| `dev` | Desarrollo con LocalStack | PostgreSQL local | LocalStack |
| `docker` | Contenedores con Docker Compose | PostgreSQL en Docker | LocalStack |
| `prod` | Producción | Variables de entorno | AWS real |

---

## Casos de uso implementados

### cierreRuta
- `ProcesarRutaCerradaUseCase` — procesa el evento SQS, valida la ruta, clasifica y guarda

### conductor
- `ValidarConductorUseCase` — consulta el servicio externo y valida que el conductor esté activo

### contratos
- `ProcesarContratoCreadoUseCase` — guarda contratos recibidos por SQS con idempotencia

### liquidacion
- `LiquidacionCalcularUseCase` — calcula la liquidación aplicando la estrategia del contrato
- `StrategyFactory` — selecciona la estrategia según el tipo de contratación
- `PorParadaStrategy` — tarifa × factor por cada paquete (1.0 entregado, 0.5 fallido cliente, 0.0 fallido transportista)
- `RecorridoCompletoStrategy` — tarifa fija si todos entregados, $0 si alguno falló

### novedadEstadoPaquete
- `ProcesarNovedadEstadoPaqueteUseCase` — guarda el estado final del paquete con historial

### registrarEstadoPago
- `PagoService` — recibe webhook, procesa asíncronamente, valida idempotencia y transiciones de estado

### visualizarEstadoPago
- Consultas de estado actual e historial de pagos por idPago o idLiquidacion

### visualizarLiquidacion
- Consultas de liquidaciones con filtros, paginación y detalle de ajustes

---

## Evento interno de dominio

Cuando se procesa un cierre de ruta correctamente, se publica un evento interno de Spring:

```
RutaCerrada guardada
        │
        ▼ (Spring ApplicationEventPublisher)
RutaCerradaProcesadaEvent
        │
        ▼
LiquidacionEventHandler  ← genera la liquidación automáticamente
RutaEventHandler         ← loguea el resultado
```

---

## Flujo de liquidación automática

El `LiquidacionEventHandler` ejecuta estos pasos al recibir `RutaCerradaProcesadaEvent`:

1. Verifica que el estado sea `OK` (si es `REQUIERE_REVISION`, no genera liquidación)
2. Busca la `RutaCerrada` completa en BD
3. Valida el conductor vía Postman Mock Server
4. Resuelve el contrato (real de BD o mock con tarifas de properties)
5. Cruza las paradas del cierre de ruta con los estados del módulo de paquetes
6. Construye `RutaLiquidacion` con estados verificados
7. Llama a `LiquidacionCalcularUseCase.execute()`

### Cruce de estados de paquetes

Por cada parada del cierre de ruta:

| ¿Existe en BD módulo paquetes? | Estado usado | Fuente |
|---|---|---|
| SÍ | Estado del módulo de paquetes | Fuente de verdad |
| NO | Estado del cierre de ruta (fallback) | Aproximación |

### Mapeo de estados (módulo de paquetes → liquidación)

| Estado módulo paquetes | Estado liquidación |
|---|---|
| `ENTREGADO` | `ENTREGADO` |
| `DEVUELTO` | `FALLIDO_CLIENTE` |
| `DANADO` | `FALLIDO_TRANSPORTISTA` |
| `EXTRAVIADO` | `FALLIDO_TRANSPORTISTA` |

### Mapeo fallback (cierre de ruta → liquidación)

| EstadoParada | MotivoFalla | Estado liquidación |
|---|---|---|
| `EXITOSA` | - | `ENTREGADO` |
| `FALLIDA` | `CLIENTE_AUSENTE`, `DIRECCION_ERRONEA`, `RECHAZADO` | `FALLIDO_CLIENTE` |
| `FALLIDA` | `ZONA_DIFICIL_ACCESO` | `FALLIDO_TRANSPORTISTA` |
| `NOVEDAD` | `DEVOLUCION` | `FALLIDO_CLIENTE` |
| `NOVEDAD` | `PAQUETE_DANADO`, `PERDIDA_PAQUETE` | `FALLIDO_TRANSPORTISTA` |
| `PENDIENTE`, `SIN_GESTION_CONDUCTOR`, `EXCLUIDA_DESPACHO` | - | `FALLIDO_TRANSPORTISTA` |

---

## Seguridad

- Autenticación vía **JWT**
- Secret configurado en `app.jwt.secret`
- Endpoints públicos: webhook de pago, cierre de ruta
- Endpoints protegidos: liquidaciones, pagos, contratos (requieren token)

---

## Pruebas

### Estructura de tests

```
test/
  ├── application/usecases/          ← tests unitarios de casos de uso
  ├── conductor/
  │   ├── unit/                      ← tests unitarios del conductor
  │   └── integration/               ← tests de integración del conductor
  ├── domain/
  │   ├── cierreRuta/models/         ← tests del modelo RutaCerrada
  │   ├── services/                  ← tests de servicios de dominio
  │   └── strategies/                ← tests de PorParadaStrategy y RecorridoCompletoStrategy
  ├── infrastructure/
  │   ├── cierreRuta/messaging/      ← tests del consumer SQS
  │   ├── persistence/repositories/  ← tests de repositorios JPA
  │   └── web/controllers/           ← tests de controladores REST
  ├── NovedadEstadoPaquete/          ← tests de integración del módulo de paquetes
  ├── RegistrarEstadoPago/
  │   ├── unit/                      ← tests unitarios del PagoService
  │   ├── integration/               ← tests de integración del webhook
  │   └── infrastructure/controllers/← tests de controladores de pago
  ├── VisualizarEstadoPago/          ← tests de consultas de estado de pago
  └── VisualizarLiquidación/         ← tests de consultas de liquidaciones
```

### Ejecutar tests
```bash
# Todos los tests
./gradlew test

# Sin tests de integración (más rápido)
./gradlew test -x test

# Solo un módulo
./gradlew test --tests "com.logistica.RegistrarEstadoPago.*"
```

---

## Cómo correr el proyecto

### Localmente con AWS real
```bash
# 1. Configurar variables de entorno
export AWS_ACCESS_KEY_ID=tu_access_key
export AWS_SECRET_ACCESS_KEY=tu_secret_key
export POSTMAN_API_KEY=tu_api_key

# 2. Correr con perfil local
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Con Docker Compose (LocalStack)
```bash
docker-compose up
```

### Compilar sin tests
```bash
./gradlew build -x test
```

---

## Datos de prueba (Flyway V2)

El seed incluye datos listos para probar todos los endpoints:

| Entidad | Registros | Casos cubiertos |
|---|---|---|
| Transportistas | 3 | Carlos López, María García, Juan Rodríguez |
| Contratos | 5 | POR_PARADA y RECORRIDO_COMPLETO, vigentes y vencidos |
| Rutas | 4 write + 5 read | OK, REQUIERE_REVISION |
| Liquidaciones | 5 | CALCULADA, RECALCULADA, PAGADA, ERROR |
| Ajustes | 3 | BONO y PENALIZACION |
| Paquetes | 7 | ENTREGADO, DEVUELTO, DANADO |
| Pagos | 4 | PENDIENTE, PAGADO, RECHAZADO, EN_PROCESO |
| Eventos transacción | 9 | Incluye caso de evento DUPLICADO |




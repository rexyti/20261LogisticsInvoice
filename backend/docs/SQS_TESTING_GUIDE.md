# Guía Técnica: Pruebas de Comunicación por Eventos con SQS y LocalStack

**Proyecto:** 20261 Logistics Invoice — Backend
**Stack:** Spring Boot 3.2 · Java 21 · Spring Cloud AWS 3.x · LocalStack 3.3 · Docker Compose

---

## Índice

1. [Arquitectura conceptual del flujo de eventos](#1-arquitectura-conceptual)
2. [Qué es LocalStack y cómo simula AWS SQS](#2-localstack-y-sqs)
3. [Estructura del proyecto de mensajería](#3-estructura-del-proyecto)
4. [Orden de arranque](#4-orden-de-arranque)
5. [Levantar LocalStack con Docker Compose](#5-levantar-localstack-con-docker-compose)
6. [Verificar el contenedor LocalStack](#6-verificar-el-contenedor-localstack)
7. [Verificar y crear colas SQS](#7-verificar-y-crear-colas-sqs)
8. [Iniciar Spring Boot con el perfil correcto](#8-iniciar-spring-boot-con-el-perfil-correcto)
9. [Verificar que los listeners están activos](#9-verificar-que-los-listeners-están-activos)
10. [Enviar mensajes a cada cola](#10-enviar-mensajes-a-cada-cola)
11. [Payloads JSON completos por evento](#11-payloads-json-completos-por-evento)
12. [Logs esperados si todo funciona](#12-logs-esperados-si-todo-funciona)
13. [Validar que el use case se ejecutó](#13-validar-que-el-use-case-se-ejecutó)
14. [Errores comunes y cómo resolverlos](#14-errores-comunes-y-cómo-resolverlos)
15. [Depurar problemas de comunicación](#15-depurar-problemas-de-comunicación)
16. [Buenas prácticas para pruebas de integración](#16-buenas-prácticas-para-pruebas-de-integración)
17. [Referencia rápida — Cheat Sheet](#17-referencia-rápida--cheat-sheet)

---

## 1. Arquitectura Conceptual

Antes de levantar cualquier servicio, es fundamental entender el flujo completo de un evento.

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         FLUJO DE UN EVENTO                               │
│                                                                          │
│  Sistema externo          LocalStack / AWS             Spring Boot App   │
│  (Producer)               (Broker SQS)                 (Consumer)        │
│                                                                          │
│  ┌──────────┐  JSON    ┌──────────────────┐   poll    ┌────────────────┐ │
│  │ Producer │ ──────►  │   Cola SQS       │ ◄──────── │  @SqsListener  │ │
│  │ externo  │          │                  │           │                │ │
│  └──────────┘          │ ruta-cerrada-    │  mensaje  │  Consumer      │ │
│                        │ queue            │ ─────────►│  (infra layer) │ │
│                        │                  │           └───────┬────────┘ │
│                        │ contrato-creado- │                   │ mapea    │
│                        │ queue            │           ┌───────▼────────┐ │
│                        │                  │           │    Mapper      │ │
│                        │ novedad-estado-  │           │ MensajeDTO →   │ │
│                        │ paquete-queue    │           │ ApplicationDTO │ │
│                        └──────────────────┘           └───────┬────────┘ │
│                                                               │ delega   │
│                                                       ┌───────▼────────┐ │
│                                                       │     Port       │ │
│                                                       │  (interfaz)    │ │
│                                                       └───────┬────────┘ │
│                                                               │          │
│                                                       ┌───────▼────────┐ │
│                                                       │   UseCase      │ │
│                                                       │ (lógica de     │ │
│                                                       │  negocio)      │ │
│                                                       └───────┬────────┘ │
│                                                               │          │
│                                                       ┌───────▼────────┐ │
│                                                       │  Repositorio   │ │
│                                                       │  (PostgreSQL)  │ │
│                                                       └────────────────┘ │
└──────────────────────────────────────────────────────────────────────────┘
```

**Puntos clave del flujo:**

1. Un sistema externo (o tú desde la terminal) publica un mensaje JSON en una cola SQS.
2. Spring Cloud AWS hace **polling automático** de la cola cada pocos segundos.
3. El `@SqsListener` recibe el mensaje y lo deserializa al **DTO de infraestructura**.
4. El **Mapper** convierte el DTO de infraestructura al DTO de aplicación.
5. El **Consumer** llama al **Port** (interfaz) sin conocer la implementación concreta.
6. El **UseCase** (que implementa el Port) ejecuta la lógica de negocio.
7. Si todo sale bien, el Consumer llama a `ack.acknowledge()` → SQS **elimina** el mensaje.
8. Si hay error, el mensaje **permanece** en la cola y SQS lo reintenta automáticamente.

---

## 2. LocalStack y SQS

### ¿Qué es LocalStack?

LocalStack es una herramienta que emula servicios de AWS en tu máquina local. En este proyecto
se usa exclusivamente para emular **Amazon SQS** (Simple Queue Service).

```
┌─────────────────────────────────────────────┐
│           Tu máquina (localhost)            │
│                                             │
│  ┌─────────────────────┐                   │
│  │   Docker Container  │                   │
│  │   localstack:3.3.0  │                   │
│  │                     │  puerto 4566       │
│  │   SQS emulado   ◄───┼───────────────────┼── Spring Boot
│  │                     │                   │
│  └─────────────────────┘                   │
└─────────────────────────────────────────────┘
```

### Diferencias clave entre LocalStack y AWS real

| Aspecto | AWS real | LocalStack |
|---|---|---|
| URL del endpoint | `https://sqs.us-east-1.amazonaws.com` | `http://localhost:4566` |
| Credenciales | IAM reales | Cualquier valor (`test`/`test`) |
| Región | Real | `us-east-1` (configurada) |
| Persistencia | Sí | No (se pierde al reiniciar el contenedor) |
| Costo | Por uso | Gratuito |
| Latencia | ~100ms+ | <5ms |

### ¿Por qué usar LocalStack en desarrollo?

- No necesitas una cuenta de AWS para trabajar.
- Todo el equipo usa el mismo entorno reproducible.
- Los tests de integración son rápidos y no generan costos.
- El CI/CD puede ejecutar los mismos tests sin acceso a AWS real.

---

## 3. Estructura del Proyecto de Mensajería

```
infrastructure/
└── messaging/
    ├── config/
    │   ├── SqsClientConfig.java        # Crea el SqsAsyncClient (lee el YAML)
    │   └── SqsConsumerConfig.java      # Configura la factory (ACK manual, concurrencia 10)
    │
    ├── consumers/                      # Adaptadores de entrada (Hexagonal Architecture)
    │   ├── RutaCerradaConsumer.java    # @SqsListener → ruta-cerrada-queue
    │   ├── EstadoPaqueteConsumer.java  # @SqsListener → novedad-estado-paquete-queue
    │   └── ContratoCreadoConsumer.java # @SqsListener → contrato-creado-queue
    │
    ├── dtos/                           # Espejo exacto del JSON que llega por la cola
    │   ├── RutaCerradaMensajeDTO.java
    │   ├── TransportistaMensajeDTO.java    # Unificado: acepta conductor_id y transportista_id
    │   ├── VehiculoMensajeDTO.java
    │   ├── ParadaMensajeDTO.java
    │   ├── EstadoPaqueteMensajeDTO.java
    │   ├── ContratoCreadoMensajeDTO.java
    │   └── SeguroMensajeDTO.java
    │
    └── mappers/                        # Convierten DTO de infra → DTO de aplicación
        ├── RutaCerradaMensajeMapper.java
        ├── EstadoPaqueteMensajeMapper.java
        └── ContratoCreadoMensajeMapper.java

application/
├── cierreRuta/ports/in/
│   └── ProcesarRutaCerradaPort.java        # Interfaz que implementa el UseCase
├── novedadEstadoPaquete/ports/in/
│   └── ProcesarEstadoPaquetePort.java
└── contratos/ports/in/
    └── ProcesarContratoCreadoPort.java
```

### Propiedades de configuración relevantes

```yaml
# application-local.yml
spring:
  cloud:
    aws:
      sqs:
        enabled: true
        endpoint: http://localhost:4566
      region:
        static: us-east-1
      credentials:
        access-key: test
        secret-key: test

app:
  sqs:
    queue:
      ruta-cerrada: ruta-cerrada-queue
      contrato-creado: contrato-creado-queue
      novedad-estado-paquete: novedad-estado-paquete-queue
```

---

## 4. Orden de Arranque

> **Regla de oro:** La infraestructura siempre antes que la aplicación.

```
Paso 1 → PostgreSQL        (la app necesita la BD para persistir)
Paso 2 → LocalStack        (la app necesita las colas SQS para los listeners)
Paso 3 → Spring Boot       (consume PostgreSQL + LocalStack)
```

El `depends_on` con `condition: service_healthy` en `docker-compose.yml` garantiza
este orden automáticamente cuando se usa `docker compose up`.

---

## 5. Levantar LocalStack con Docker Compose

### Opción A — Levantar todo el stack completo

```bash
# Desde el directorio raíz del proyecto (donde está docker-compose.yml)
docker compose up --build
```

Levanta: **PostgreSQL + pgAdmin + LocalStack + Spring Boot App**.

### Opción B — Solo la infraestructura (recomendado en desarrollo)

Cuando desarrollas y corres Spring Boot desde el IDE, solo necesitas los servicios de soporte:

```bash
docker compose up db localstack
```

### Opción C — En segundo plano (detached)

```bash
docker compose up db localstack -d
```

### Verificar que los contenedores arrancaron

```bash
docker compose ps
```

Salida esperada:

```
NAME          IMAGE                          STATUS
postgres-db   postgres:15                    Up (healthy)
localstack    localstack/localstack:3.3.0    Up (healthy)
```

El estado `(healthy)` es clave — significa que el healthcheck pasó correctamente.

---

## 6. Verificar el Contenedor LocalStack

### Ver logs del contenedor

```bash
docker compose logs localstack
```

Busca estas líneas que confirman que LocalStack arrancó y las colas se crearon:

```
localstack  | Ready.
localstack  | ==> Creando colas SQS en LocalStack...
localstack  | ==> Colas SQS creadas exitosamente:
localstack  | {
localstack  |     "QueueUrls": [
localstack  |         "http://sqs.us-east-1.localhost.localstack.cloud:4566/.../ruta-cerrada-queue",
localstack  |         "http://sqs.us-east-1.localhost.localstack.cloud:4566/.../contrato-creado-queue",
localstack  |         "http://sqs.us-east-1.localhost.localstack.cloud:4566/.../novedad-estado-paquete-queue"
localstack  |     ]
localstack  | }
```

### Verificar conectividad al endpoint

```bash
curl http://localhost:4566/_localstack/health
```

Respuesta esperada:

```json
{
  "services": {
    "sqs": "running"
  },
  "status": "running"
}
```

---

## 7. Verificar y Crear Colas SQS

### Listar colas existentes — desde dentro del contenedor (más simple)

```bash
docker exec localstack awslocal sqs list-queues
```

### Listar colas existentes — desde la terminal con AWS CLI

```bash
aws sqs list-queues \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

Respuesta esperada:

```json
{
    "QueueUrls": [
        "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/ruta-cerrada-queue",
        "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/contrato-creado-queue",
        "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/novedad-estado-paquete-queue"
    ]
}
```

### Crear colas manualmente (si no existen)

```bash
# Desde dentro del contenedor (recomendado)
docker exec localstack awslocal sqs create-queue --queue-name ruta-cerrada-queue
docker exec localstack awslocal sqs create-queue --queue-name contrato-creado-queue
docker exec localstack awslocal sqs create-queue --queue-name novedad-estado-paquete-queue

# Alternativa: desde fuera con AWS CLI
aws sqs create-queue \
  --queue-name ruta-cerrada-queue \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

### Obtener la URL de una cola específica

```bash
aws sqs get-queue-url \
  --queue-name ruta-cerrada-queue \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

---

## 8. Iniciar Spring Boot con el Perfil Correcto

### Perfil `local` (desarrollo en tu máquina)

Apunta a LocalStack en `localhost:4566` y a PostgreSQL en `localhost:5432`.

**Desde IntelliJ IDEA:**
```
Run/Debug Configuration → Environment → Active profiles: local
```

**Desde la terminal con Gradle:**

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

**Verificar qué perfil está activo** — busca en los logs de arranque:

```
The following 1 profile is active: "local"
```

### Perfil `docker` (dentro de Docker Compose)

Se activa automáticamente a través de la variable de entorno definida en `docker-compose.yml`:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
```

---

## 9. Verificar que los Listeners SQS Están Activos

Al arrancar con `spring.cloud.aws.sqs.enabled=true`, Spring Cloud AWS registra los listeners
automáticamente. Busca estos logs en la consola de Spring Boot:

```
INFO  SqsClientConfig                    - Iniciando SqsAsyncClient — región: 'us-east-1' | endpoint: http://localhost:4566
INFO  SqsMessageListenerContainerFactory - Creating SqsMessageListenerContainer
INFO  SqsMessageListenerContainer        - Starting container for queues: [ruta-cerrada-queue]
INFO  SqsMessageListenerContainer        - Starting container for queues: [contrato-creado-queue]
INFO  SqsMessageListenerContainer        - Starting container for queues: [novedad-estado-paquete-queue]
```

Si **no** aparecen estas líneas, verifica que la propiedad está configurada:

```yaml
# application-local.yml
spring:
  cloud:
    aws:
      sqs:
        enabled: true  # ← debe estar en true
```

---

## 10. Enviar Mensajes a Cada Cola

### Estructura general del comando

```bash
aws sqs send-message \
  --queue-url <URL_DE_LA_COLA> \
  --message-body '<JSON_DEL_EVENTO>' \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

Los payloads completos de cada evento están en la sección siguiente.

---

## 11. Payloads JSON Completos por Evento

### Variables de entorno útiles para los comandos

```bash
EP="--endpoint-url http://localhost:4566 --region us-east-1 --no-sign-request"
BASE="http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000"
```

---

### 11.1 Evento: Ruta Cerrada

**Cola:** `ruta-cerrada-queue`

```bash
aws sqs send-message \
  --queue-url "$BASE/ruta-cerrada-queue" \
  $EP \
  --message-body '{
    "tipo_evento": "RUTA_CERRADA",
    "ruta_id": "770e8400-e29b-41d4-a716-446655440001",
    "fecha_hora_inicio_transito": "2026-05-19T08:00:00",
    "fecha_hora_cierre": "2026-05-19T14:30:00",
    "conductor": {
      "conductor_id": "550e8400-e29b-41d4-a716-446655440010",
      "nombre": "Carlos Perez",
      "modelo_contrato": "Por Parada Realizada"
    },
    "vehiculo": {
      "vehiculo_id": "660e8400-e29b-41d4-a716-446655440020",
      "tipo": "MOTO"
    },
    "paradas": [
      {
        "parada_id": "aa0e8400-e29b-41d4-a716-446655440030",
        "paquete_id": "bb0e8400-e29b-41d4-a716-446655440040",
        "estado": "EXITOSA",
        "motivo_no_entrega": null,
        "fecha_hora_gestion": "2026-05-19T09:15:00"
      },
      {
        "parada_id": "cc0e8400-e29b-41d4-a716-446655440050",
        "paquete_id": "dd0e8400-e29b-41d4-a716-446655440060",
        "estado": "FALLIDA",
        "motivo_no_entrega": "CLIENTE_AUSENTE",
        "fecha_hora_gestion": "2026-05-19T10:45:00"
      },
      {
        "parada_id": "ee0e8400-e29b-41d4-a716-446655440070",
        "paquete_id": "ff0e8400-e29b-41d4-a716-446655440080",
        "estado": "EXITOSA",
        "motivo_no_entrega": null,
        "fecha_hora_gestion": "2026-05-19T12:00:00"
      }
    ]
  }'
```

**Valores válidos para `estado` en paradas:**

| Valor | Significado |
|---|---|
| `EXITOSA` | Entrega realizada correctamente |
| `FALLIDA` | No se pudo entregar (requiere `motivo_no_entrega`) |
| `NOVEDAD` | Ocurrió un imprevisto |
| `SIN_GESTION_CONDUCTOR` | El conductor no registró gestión |

**Valores válidos para `motivo_no_entrega`** (solo cuando `estado = FALLIDA`):

`CLIENTE_AUSENTE` · `DIRECCION_INCORRECTA` · `RECHAZADO_POR_CLIENTE` ·
`ZONA_DIFICIL_ACCESO` · `DAÑADO_EN_RUTA` · `EXTRAVIADO` · `DEVOLUCION`

**Valores válidos para `tipo` en vehículo:** `MOTO` · `VAN` · `NHR` · `TURBO`

---

### 11.2 Evento: Estado de Paquete

**Cola:** `novedad-estado-paquete-queue`

```bash
aws sqs send-message \
  --queue-url "$BASE/novedad-estado-paquete-queue" \
  $EP \
  --message-body '{
    "id_paquete": "bb0e8400-e29b-41d4-a716-446655440040",
    "id_ruta": "770e8400-e29b-41d4-a716-446655440001",
    "estado": "ENTREGADO"
  }'
```

**Valores válidos para `estado`:**

| Valor | Descripción |
|---|---|
| `ENTREGADO` | Paquete entregado al destinatario |
| `EN_TRANSITO` | Paquete en camino |
| `DEVUELTO` | Paquete devuelto al origen |
| `PERDIDO` | Paquete extraviado |

> Nota: Los valores exactos dependen del enum `NovedadEstadoPaqueteEstadoPaquete`
> definido en `domain/novedadEstadoPaquete/enums/`.

---

### 11.3 Evento: Contrato Creado

**Cola:** `contrato-creado-queue`

```bash
aws sqs send-message \
  --queue-url "$BASE/contrato-creado-queue" \
  $EP \
  --message-body '{
    "tipo_evento": "CONTRATO_CREADO",
    "id_contrato": "CTR-2026-001",
    "tipo_contrato": "TERCERIZADO",
    "transportista": {
      "transportista_id": "550e8400-e29b-41d4-a716-446655440010",
      "nombre": "Carlos Perez"
    },
    "tipo_vehiculo": "MOTO",
    "es_por_parada": true,
    "precio_paradas": 3500.00,
    "precio": 0.00,
    "fecha_inicio": "2026-05-01T00:00:00",
    "fecha_final": "2026-12-31T23:59:59",
    "seguro": {
      "numero_poliza": "POL-2026-9988",
      "estado": "ACTIVO"
    }
  }'
```

> **Nota:** El campo `seguro` es opcional. Para un contrato sin seguro enviar `"seguro": null`.

**Valores válidos para `tipo_vehiculo`:** `MOTO` · `VAN` · `NHR` · `TURBO`

---

## 12. Logs Esperados si Todo Funciona

### Ruta Cerrada

```
INFO  RutaCerradaConsumer          - Evento RUTA_CERRADA recibido para ruta_id: 770e8400-e29b-41d4-a716-446655440001
INFO  ProcesarRutaCerradaUseCase   - Iniciando proceso de RUTA_CERRADA para ruta_id: 770e8400-e29b-41d4-a716-446655440001
INFO  ProcesarRutaCerradaUseCase   - RutaCerrada 770e8400... procesada en 42 ms
```

### Estado de Paquete

```
INFO  EstadoPaqueteConsumer              - Evento ESTADO_PAQUETE recibido para id_paquete: bb0e8400-e29b-41d4-a716-446655440040
INFO  ProcesarNovedadEstadoPaqueteUseCase - Procesando evento NOVEDAD_ESTADO_PAQUETE para paquete: bb0e8400-e29b-41d4-a716-446655440040
INFO  ProcesarNovedadEstadoPaqueteUseCase - Paquete bb0e8400-... actualizado a estado ENTREGADO desde evento SQS
```

### Contrato Creado

```
INFO  ContratoCreadoConsumer        - Evento CONTRATO_CREADO recibido para id_contrato: CTR-2026-001
INFO  ProcesarContratoCreadoUseCase - Procesando evento CONTRATO_CREADO para contrato: CTR-2026-001
INFO  ProcesarContratoCreadoUseCase - Contrato CTR-2026-001 persistido localmente desde evento
```

---

## 13. Validar que el Use Case se Ejecutó

### Opción A — Consultar la base de datos directamente

```bash
# Conectar al contenedor de PostgreSQL
docker exec -it postgres-db psql -U luis -d logistica
```

```sql
-- Verificar ruta cerrada procesada
SELECT ruta_id, estado_procesamiento, fecha_cierre
FROM rutas_cerradas
WHERE ruta_id = '770e8400-e29b-41d4-a716-446655440001';

-- Verificar estado de paquete actualizado
SELECT id_paquete, estado_actual, updated_at
FROM paquetes
WHERE id_paquete = 'bb0e8400-e29b-41d4-a716-446655440040';

-- Verificar historial de estados del paquete
SELECT id_paquete, estado, fecha
FROM historial_estados
WHERE id_paquete = 'bb0e8400-e29b-41d4-a716-446655440040'
ORDER BY fecha DESC;

-- Verificar contrato guardado
SELECT id_contrato, tipo_contrato, tipo_vehiculo, fecha_inicio
FROM contratos
WHERE id_contrato = 'CTR-2026-001';
```

### Opción B — Usar el endpoint REST de la aplicación

```bash
# Consultar contrato procesado
curl -s http://localhost:8080/api/contratos/CTR-2026-001 \
  -H "Authorization: Bearer <token>" | python -m json.tool
```

### Opción C — Verificar que la cola quedó vacía (ACK exitoso)

Si el consumer procesó correctamente el mensaje, la cola debe estar vacía:

```bash
aws sqs get-queue-attributes \
  --queue-url "$BASE/ruta-cerrada-queue" \
  --attribute-names ApproximateNumberOfMessages \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

Respuesta esperada:

```json
{
    "Attributes": {
        "ApproximateNumberOfMessages": "0"
    }
}
```

Si el número es mayor a 0, el consumer no procesó el mensaje o falló y no hizo ACK.

---

## 14. Errores Comunes y Cómo Resolverlos

### Error 1 — Cola inexistente

**Síntoma:**
```
ERROR SqsMessageListenerContainer - Queue ruta-cerrada-queue does not exist
```

**Causa:** Las colas no se crearon al iniciar LocalStack.

**Solución:**
```bash
# Reiniciar LocalStack (el script init/01-create-queues.sh las crea automáticamente)
docker compose restart localstack

# O crearlas manualmente
docker exec localstack awslocal sqs create-queue --queue-name ruta-cerrada-queue
docker exec localstack awslocal sqs create-queue --queue-name contrato-creado-queue
docker exec localstack awslocal sqs create-queue --queue-name novedad-estado-paquete-queue
```

---

### Error 2 — Endpoint incorrecto / Conexión rechazada

**Síntoma:**
```
ERROR - Connection refused: localhost/127.0.0.1:4566
software.amazon.awssdk.core.exception.SdkClientException: Unable to execute HTTP request
```

**Causa:** LocalStack no está corriendo, o `spring.cloud.aws.sqs.endpoint` no apunta a `http://localhost:4566`.

**Solución:**
```bash
# 1. Verificar que LocalStack está corriendo
docker compose ps localstack

# 2. Verificar la propiedad en application-local.yml
#    spring.cloud.aws.sqs.endpoint: http://localhost:4566
```

---

### Error 3 — Problema de deserialización JSON

**Síntoma:**
```
com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "idPaquete"
```

**Causa:** El JSON enviado usa `camelCase` pero los DTOs esperan `snake_case`.

**Diagnóstico:**
```bash
# Ver el mensaje exacto que está en la cola
aws sqs receive-message \
  --queue-url "$BASE/novedad-estado-paquete-queue" \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

**Solución:** Usar `snake_case` en el JSON:

```json
✅ Correcto:   { "id_paquete": "...", "id_ruta": "...", "estado": "..." }
❌ Incorrecto: { "idPaquete":  "...", "idRuta":  "...", "estado": "..." }
```

---

### Error 4 — Credenciales inválidas o vacías

**Síntoma:**
```
SqsException: The security token included in the request is invalid
```

**Causa:** Las credenciales están vacías. LocalStack no las valida, pero el SDK de AWS sí
requiere que existan con cualquier valor.

**Solución:**
```yaml
# application-local.yml
spring:
  cloud:
    aws:
      credentials:
        access-key: test    # Cualquier valor no vacío
        secret-key: test    # Cualquier valor no vacío
```

---

### Error 5 — Listeners no registrados (SQS deshabilitado)

**Síntoma:** Spring Boot arranca sin errores, pero los mensajes se acumulan en la cola
y nunca se procesan. No hay logs de los consumers.

**Causa:** `spring.cloud.aws.sqs.enabled` no está en `true`.

**Diagnóstico:**
```bash
# Buscar si los listeners se registraron en los logs de arranque
docker compose logs app | grep -i "SqsMessageListenerContainer"
```

**Solución:**
```yaml
spring:
  cloud:
    aws:
      sqs:
        enabled: true
```

---

### Error 6 — Bean SqsAsyncClient no encontrado

**Síntoma:**
```
NoSuchBeanDefinitionException: No qualifying bean of type 'SqsAsyncClient'
```

**Causa:** `SqsClientConfig` tiene `@ConditionalOnProperty(sqs.enabled=true)` pero la
propiedad no está definida en el perfil activo.

**Solución:** Agregar `spring.cloud.aws.sqs.enabled: true` al perfil que estás usando.

---

### Error 7 — Mensaje se reprocesa infinitamente

**Síntoma:** El consumer loguea el evento, aparece el error, y el mismo mensaje
vuelve a procesarse segundos después (en bucle).

**Causa:** El use case lanzó una excepción. El consumer re-lanza → SQS no recibe el ACK
→ el mensaje vuelve a estar visible → se reprocesa.

**Diagnóstico:**
```bash
# Ver cuántos mensajes están "en vuelo" (procesándose sin ACK)
aws sqs get-queue-attributes \
  --queue-url "$BASE/ruta-cerrada-queue" \
  --attribute-names ApproximateNumberOfMessagesNotVisible \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

**Solución:** Identificar y corregir la excepción en el use case. Revisar logs de ERROR.

---

### Error 8 — Evento duplicado ignorado (comportamiento esperado)

**Síntoma:** Envías un mensaje y en los logs aparece:
```
INFO ProcesarRutaCerradaUseCase - Evento duplicado ignorado para ruta_id 770e8400...
```

**Causa:** El sistema ya procesó ese `ruta_id` antes. Es **comportamiento esperado** —
los use cases implementan idempotencia para evitar doble procesamiento.

**Solución:** Usar un `ruta_id` / `id_contrato` diferente en cada prueba.

---

## 15. Depurar Problemas de Comunicación

### Habilitar logs detallados temporalmente

Agregar a `application-local.yml` durante el debug:

```yaml
logging:
  level:
    io.awspring.cloud.sqs: DEBUG
    software.amazon.awssdk.core.interceptor: DEBUG
    com.logistica.infrastructure.messaging: DEBUG
```

Esto mostrará cada poll a SQS, cada mensaje recibido y los detalles de serialización.
**Recordar quitarlo** cuando termines de depurar.

### Ver el contenido exacto de un mensaje en la cola

```bash
aws sqs receive-message \
  --queue-url "$BASE/ruta-cerrada-queue" \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request \
  --max-number-of-messages 1 \
  --visibility-timeout 30
```

### Purgar una cola (eliminar todos los mensajes atascados)

```bash
aws sqs purge-queue \
  --queue-url "$BASE/ruta-cerrada-queue" \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

### Ver todos los atributos de una cola

```bash
aws sqs get-queue-attributes \
  --queue-url "$BASE/ruta-cerrada-queue" \
  --attribute-names All \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

| Atributo | Significado |
|---|---|
| `ApproximateNumberOfMessages` | Mensajes esperando ser consumidos |
| `ApproximateNumberOfMessagesNotVisible` | En proceso (sin ACK aún) |
| `ApproximateNumberOfMessagesDelayed` | Con delay programado |
| `VisibilityTimeout` | Segundos que un mensaje queda invisible mientras se procesa |

---

## 16. Buenas Prácticas para Pruebas de Integración

### 1. Usa IDs únicos en cada prueba

Evita reutilizar el mismo UUID para no chocar con la lógica de idempotencia:

```bash
# Genera un UUID nuevo en bash (Linux/Mac)
RUTA_ID=$(python3 -c "import uuid; print(uuid.uuid4())")
echo "Usando ruta_id: $RUTA_ID"

# PowerShell (Windows)
$RUTA_ID = [guid]::NewGuid().ToString()
```

### 2. Referencia: patrón del IT Test existente

El proyecto ya tiene `RutaCerradaConsumerIT` que muestra el patrón correcto para
tests de integración con SQS:

```java
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class RutaCerradaConsumerIT {

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.3.0")
    ).withServices(SQS).withStartupTimeout(Duration.ofMinutes(2));

    @TestConfiguration
    static class SqsTestConfig {
        @Bean
        @Primary                           // ← gana sobre el bean productivo
        public SqsAsyncClient sqsAsyncClient() {
            return SqsAsyncClient.builder()
                    .endpointOverride(localStack.getEndpoint())
                    .region(Region.of(localStack.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    localStack.getAccessKey(),
                                    localStack.getSecretKey())))
                    .build();
        }
    }

    @MockBean
    private ProcesarRutaCerradaUseCase procesarRutaCerradaUseCase;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.sqs.enabled", () -> "true");
        registry.add("spring.cloud.aws.sqs.endpoint",
                () -> localStack.getEndpoint().toString());
        // ... más propiedades
    }

    @Test
    void debeConsumirMensajeDeSqsYEjecutarUseCase() throws Exception {
        // Enviar mensaje → verificar que el use case se llamó
        verify(procesarRutaCerradaUseCase,
               timeout(Duration.ofSeconds(10).toMillis()))
               .ejecutar(any(RutaCerradaEventDTO.class));
    }
}
```

### 3. Nunca uses `Thread.sleep()` para esperar eventos asíncronos

```java
// ❌ Frágil: puede ser demasiado corto o desperdiciar tiempo
Thread.sleep(5000);
verify(useCase).ejecutar(any());

// ✅ Correcto: Mockito espera hasta 10s, falla en cuanto ocurre
verify(useCase, timeout(10_000)).ejecutar(any());
```

### 4. Verifica el ACK además de la ejecución

```java
// Verificar que el mensaje fue eliminado de la cola (ACK recibido)
String count = sqsAsyncClient
        .getQueueAttributes(r -> r.queueUrl(queueUrl)
                .attributeNamesWithStrings("ApproximateNumberOfMessages"))
        .get()
        .attributesAsStrings()
        .get("ApproximateNumberOfMessages");

assertThat(count).isEqualTo("0");
```

### 5. Cada dominio debería tener su propio IT Test

```
src/test/java/com/logistica/infrastructure/messaging/consumers/
├── RutaCerradaConsumerIT.java        ← ya existe
├── EstadoPaqueteConsumerIT.java      ← por crear
└── ContratoCreadoConsumerIT.java     ← por crear
```

### 6. Usa `awslocal` dentro del contenedor (más simple)

```bash
# ✅ Dentro del contenedor: no necesita flags adicionales
docker exec localstack awslocal sqs list-queues

# También válido pero más verboso
aws sqs list-queues \
  --endpoint-url http://localhost:4566 \
  --region us-east-1 \
  --no-sign-request
```

### 7. Script de smoke test para los tres eventos

Guardar como `scripts/smoke-test-sqs.sh` y ejecutar después de levantar el stack:

```bash
#!/bin/bash
set -e

EP="--endpoint-url http://localhost:4566 --region us-east-1 --no-sign-request"
BASE="http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000"
RUTA_ID=$(python3 -c "import uuid; print(uuid.uuid4())")
PAQUETE_ID=$(python3 -c "import uuid; print(uuid.uuid4())")
CONTRATO_ID="CTR-SMOKE-$(date +%s)"

echo "=================================================="
echo "  Smoke Test SQS — Logistics Invoice Backend"
echo "=================================================="

echo ""
echo "[1/3] Enviando RUTA_CERRADA (ruta_id: $RUTA_ID)..."
aws sqs send-message \
  --queue-url "$BASE/ruta-cerrada-queue" $EP \
  --message-body "{
    \"tipo_evento\": \"RUTA_CERRADA\",
    \"ruta_id\": \"$RUTA_ID\",
    \"fecha_hora_inicio_transito\": \"2026-05-19T08:00:00\",
    \"fecha_hora_cierre\": \"2026-05-19T14:00:00\",
    \"conductor\": {
      \"conductor_id\": \"550e8400-e29b-41d4-a716-000000000001\",
      \"nombre\": \"Smoke Test Conductor\",
      \"modelo_contrato\": \"Por Parada Realizada\"
    },
    \"vehiculo\": {\"vehiculo_id\": \"660e8400-e29b-41d4-a716-000000000002\", \"tipo\": \"MOTO\"},
    \"paradas\": [{
      \"parada_id\": \"770e8400-e29b-41d4-a716-000000000003\",
      \"paquete_id\": \"$PAQUETE_ID\",
      \"estado\": \"EXITOSA\",
      \"motivo_no_entrega\": null,
      \"fecha_hora_gestion\": \"2026-05-19T09:00:00\"
    }]
  }"

echo "[2/3] Enviando ESTADO_PAQUETE (paquete_id: $PAQUETE_ID)..."
aws sqs send-message \
  --queue-url "$BASE/novedad-estado-paquete-queue" $EP \
  --message-body "{
    \"id_paquete\": \"$PAQUETE_ID\",
    \"id_ruta\": \"$RUTA_ID\",
    \"estado\": \"ENTREGADO\"
  }"

echo "[3/3] Enviando CONTRATO_CREADO (id_contrato: $CONTRATO_ID)..."
aws sqs send-message \
  --queue-url "$BASE/contrato-creado-queue" $EP \
  --message-body "{
    \"tipo_evento\": \"CONTRATO_CREADO\",
    \"id_contrato\": \"$CONTRATO_ID\",
    \"tipo_contrato\": \"TERCERIZADO\",
    \"transportista\": {
      \"transportista_id\": \"550e8400-e29b-41d4-a716-000000000001\",
      \"nombre\": \"Smoke Test Conductor\"
    },
    \"tipo_vehiculo\": \"MOTO\",
    \"es_por_parada\": true,
    \"precio_paradas\": 3500.00,
    \"precio\": 0.00,
    \"fecha_inicio\": \"2026-01-01T00:00:00\",
    \"fecha_final\": \"2026-12-31T23:59:59\",
    \"seguro\": null
  }"

echo ""
echo "=================================================="
echo "  Mensajes enviados. Revisa los logs de Spring Boot."
echo "  Espera ~3 segundos y verifica las colas:"
echo ""
echo "  docker exec localstack awslocal sqs list-queues"
echo "=================================================="
```

---

## 17. Referencia Rápida — Cheat Sheet

```bash
# ─── VARIABLES ────────────────────────────────────────────────────────────
EP="--endpoint-url http://localhost:4566 --region us-east-1 --no-sign-request"
BASE="http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000"

# ─── DOCKER COMPOSE ───────────────────────────────────────────────────────
docker compose up db localstack -d          # Levantar solo la infra (dev)
docker compose up --build                   # Levantar todo el stack
docker compose ps                           # Estado de todos los servicios
docker compose logs localstack -f           # Logs de LocalStack en tiempo real
docker compose logs app -f                  # Logs de Spring Boot en tiempo real
docker compose restart localstack           # Reiniciar LocalStack (recrea colas)
docker compose down                         # Bajar todo

# ─── LOCALSTACK (dentro del contenedor — más simple) ─────────────────────
docker exec localstack awslocal sqs list-queues
docker exec localstack awslocal sqs create-queue --queue-name <nombre>
docker exec localstack awslocal sqs purge-queue --queue-url <url>

# ─── AWS CLI (desde la terminal — más verboso) ────────────────────────────
aws sqs list-queues $EP
aws sqs create-queue --queue-name <nombre> $EP
aws sqs send-message --queue-url $BASE/<cola> --message-body '<json>' $EP
aws sqs get-queue-attributes --queue-url $BASE/<cola> --attribute-names All $EP
aws sqs purge-queue --queue-url $BASE/<cola> $EP

# ─── COLAS DEL PROYECTO ───────────────────────────────────────────────────
# ruta-cerrada-queue          →  RutaCerradaConsumer
# novedad-estado-paquete-queue →  EstadoPaqueteConsumer
# contrato-creado-queue       →  ContratoCreadoConsumer

# ─── SPRING BOOT ──────────────────────────────────────────────────────────
./gradlew bootRun --args='--spring.profiles.active=local'

# ─── POSTGRESQL ───────────────────────────────────────────────────────────
docker exec -it postgres-db psql -U luis -d logistica
```

---

*Guía generada para el equipo de desarrollo de 20261 Logistics Invoice.*
*Última actualización: 2026-05-19*

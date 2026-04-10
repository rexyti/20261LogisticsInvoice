# Guía de Tecnologías — Módulo 3: Facturación y Liquidación

**Proyecto**: Sistema de Gestión Logística  
**Módulo**: Módulo 3 — Facturación y Liquidación  
**Fecha**: Abril 2026

---

## ¿Para qué sirve esta guía?

Esta guía explica cada tecnología que usamos en el módulo, por qué la elegimos, para qué la usamos concretamente dentro del proyecto y en qué features aparece. Está pensada para que cualquier miembro del equipo, sin importar su experiencia, entienda el stack completo antes de empezar a escribir código.

---

## Visión general del stack

```
┌─────────────────────────────────────────────┐
│              FRONTEND                        │
│         React 18 + Vite + Axios             │
└─────────────────┬───────────────────────────┘
                  │ HTTP / REST
┌─────────────────▼───────────────────────────┐
│               BACKEND                        │
│            Java 21 + Spring Boot            │
│   Spring Security · Spring Data JPA         │
│   Resilience4j · Flyway · Bean Validation   │
└────────┬────────────────────┬───────────────┘
         │                    │
┌────────▼──────┐    ┌────────▼──────────────┐
│  PostgreSQL   │    │  AWS SQS / Kafka       │
│  Base de      │    │  Cola de mensajería    │
│  datos        │    │  asíncrona             │
└───────────────┘    └───────────────────────┘
```

---

## Tecnologías del Backend

### Java 21

**¿Qué es?** El lenguaje de programación principal del backend.

**¿Por qué Java 21?** Es la versión LTS (Long Term Support) más reciente, lo que significa que tiene soporte garantizado por años. Trae mejoras de rendimiento y características modernas del lenguaje como Records y Pattern Matching que hacen el código más limpio y menos verboso que versiones anteriores.

**¿Dónde lo usamos?** En absolutamente todo el backend: servicios, controladores, entidades, validadores y tests.

---

### Spring Boot

**¿Qué es?** El framework principal sobre el que construimos el backend. Spring Boot es una capa sobre Spring que elimina la configuración manual y permite levantar un servidor web funcional con muy poco código inicial.

**¿Por qué Spring Boot?** Es el estándar de la industria para backends empresariales en Java. Tiene un ecosistema enorme de librerías oficiales (seguridad, base de datos, validación, mensajería) que se integran entre sí sin fricción. Permite enfocarse en la lógica de negocio en lugar de en la configuración de infraestructura.

**¿Dónde lo usamos?** En todos los features del módulo. Es la base sobre la que corren los controladores REST, los servicios de negocio y la conexión a base de datos.

**Módulos de Spring Boot que usamos:**

---

#### Spring Web (parte de Spring Boot)

**¿Qué es?** El módulo que permite crear endpoints REST (las URLs que el frontend llama para obtener o enviar datos).

**¿Para qué lo usamos?** Para exponer los endpoints de cada feature:
- `POST /api/contratos` → Registrar modelo de contratación
- `GET /api/contratos/{id}` → Consultar contrato
- `POST /api/eventos/cierre-ruta` → Recibir cierre de ruta del Módulo de Rutas
- `PUT /api/liquidaciones/{id}/recalcular` → Recalcular liquidación
- `GET /api/liquidaciones` → Visualizar liquidaciones
- `GET /api/rutas/{id}` → Consultar ruta procesada

---

#### Spring Data JPA (parte de Spring Boot)

**¿Qué es?** El módulo que conecta el código Java con la base de datos PostgreSQL. JPA significa Java Persistence API y permite trabajar con tablas de base de datos como si fueran objetos Java normales, sin escribir SQL manualmente en la mayoría de los casos.

**¿Por qué JPA?** Elimina la necesidad de escribir queries SQL repetitivas para operaciones básicas (guardar, buscar, eliminar). Spring Data JPA además genera automáticamente las consultas a partir del nombre de los métodos, por ejemplo `existsByRutaId(UUID id)` genera el SQL correspondiente sin que el desarrollador lo escriba.

**¿Dónde lo usamos?** En todos los features para persistir y consultar datos. Las entidades principales del módulo son: `Contrato`, `Ruta`, `Parada`, `Transportista`, `Liquidacion`, `Ajuste`, `AuditoriaLiquidacion`, `Paquete`, `HistorialEstado`, `LogSincronizacion`, `Pago` y `EstadoPago`.

**Concepto clave — `@Transactional`:** Esta anotación de Spring garantiza que un conjunto de operaciones de base de datos ocurra de forma atómica. Si cualquier parte falla, todo se revierte. La usamos en operaciones críticas como calcular la liquidación (guardar la liquidación + guardar la auditoría en una sola operación) y registrar un contrato (crear el contrato + asociarlo con vehículo y usuario).

---

#### Spring Security (parte de Spring Boot)

**¿Qué es?** El módulo que controla quién puede acceder a qué dentro del sistema.

**¿Por qué lo usamos?** El módulo financiero maneja datos sensibles de pagos y contratos. No puede ser que cualquier usuario acceda a cualquier endpoint. Spring Security permite definir roles y restringir el acceso por rol.

**¿Cómo funciona en el proyecto?** Usamos JWT (JSON Web Tokens), que son credenciales digitales que el usuario recibe al iniciar sesión y envía en cada petición para demostrar quién es. Spring Security valida ese token en cada llamada.

**Roles definidos en el módulo:**
| Rol | Descripción | Qué puede hacer |
|:---|:---|:---|
| `ROLE_GESTOR_TARIFAS` | Gestiona tarifas y contratos | Registrar y consultar contratos |
| `ROLE_ADMIN` | Administrador del módulo | Aceptar solicitudes de recálculo, ingresar ajustes |
| `ROLE_FINANCIERO` | Equipo financiero | Ver liquidaciones, ver estado de pagos |
| `ROLE_TRANSPORTISTA` | Conductor | Ver su propio estado de pago y liquidación |

---

#### Spring Boot Validation / Bean Validation

**¿Qué es?** Un sistema de validación de datos que permite anotar los campos de un DTO (objeto de transferencia de datos) con reglas, y Spring las valida automáticamente antes de que el código de negocio se ejecute.

**¿Por qué lo usamos?** El FR-002 de Registrar Contrato exige que todos los campos obligatorios estén presentes y que los datos sean coherentes. En lugar de escribir `if (campo == null) throw new Exception(...)` en cada servicio, Bean Validation permite declarar las reglas directamente en el DTO con anotaciones como `@NotNull`, `@NotBlank` o `@FutureOrPresent`.

**Custom Validators:** Para reglas que las anotaciones estándar no pueden expresar (como "si el tipo de contrato es Por Parada, el campo `precioParadas` es obligatorio; si no, el campo `precio` es obligatorio"), implementamos validadores personalizados: `FechasContratoValidator` y `PrecioCondicionalValidator`.

**¿Dónde lo usamos?** Principalmente en Registrar Modelo de Contratación, pero el patrón se aplica en todos los features que reciben datos del usuario.

---

### Flyway

**¿Qué es?** Una herramienta de migración de base de datos. Permite gestionar los cambios al esquema de la base de datos (crear tablas, agregar columnas, añadir restricciones) mediante scripts SQL versionados.

**¿Por qué Flyway?** Sin Flyway, cada desarrollador tendría que aplicar cambios al esquema manualmente en su base de datos local, lo que genera inconsistencias entre entornos. Flyway garantiza que la base de datos de desarrollo, la de pruebas y la de producción siempre tengan exactamente el mismo esquema.

**¿Cómo funciona?** Se crean archivos SQL con nombres como `V1__init_schema.sql`, `V2__add_auditoria_table.sql`. Flyway los ejecuta en orden automáticamente al iniciar la aplicación.

**¿Dónde lo usamos?** En todos los features. Es aquí donde se definen las restricciones críticas de la base de datos como `UNIQUE(ruta_id)` en la tabla de liquidaciones (para evitar duplicados) y `UNIQUE(id_ruta)` en la tabla de rutas (para idempotencia del cierre de ruta).

---

### Resilience4j

**¿Qué es?** Una librería de resiliencia para Java que implementa patrones de tolerancia a fallos en comunicaciones entre servicios.

**¿Por qué la usamos?** En el feature de Novedad Estado del Paquete, el Módulo Financiero debe consultar sincónicamente al Módulo de Gestión de Paquetes. Ese módulo externo puede estar lento o caído. Sin Resilience4j, un timeout o fallo del módulo externo derribaría todo el proceso de liquidación.

**Patrones que usamos:**

| Patrón | Anotación | ¿Para qué? |
|:---|:---|:---|
| **Timeout** | `@TimeLimiter` | Aborta la consulta si tarda más de 2 segundos (exigido por el spec) |
| **Retry** | `@Retry` | Reintenta la consulta hasta 3 veces antes de rendirse |
| **Circuit Breaker** | `@CircuitBreaker` | Si el módulo externo falla repetidamente, deja de llamarlo temporalmente para no saturarlo |

**Orden obligatorio de las anotaciones:** `@CircuitBreaker` → `@Retry` → `@TimeLimiter`. Este orden es crítico porque determina qué patrón envuelve a cuál. Invertirlo produce comportamiento impredecible.

**¿Dónde lo usamos?** Exclusivamente en Novedad Estado del Paquete, en el cliente HTTP `PackageApiClient`.

---

### WireMock (solo en tests)

**¿Qué es?** Una herramienta que simula servidores HTTP externos durante las pruebas. Permite definir qué respuesta debe dar un servidor ficticio ante una petición específica.

**¿Por qué lo usamos?** El Módulo Financiero depende del Módulo de Gestión de Paquetes para consultar estados. No podemos depender de que ese módulo esté disponible para ejecutar nuestros tests. WireMock nos permite simular respuestas exitosas, errores 404, errores 500 y timeouts sin necesidad del servicio real.

**¿Dónde lo usamos?** En los tests de Novedad Estado del Paquete. Solo existe en el scope de test, nunca en producción.

---

### Spring Cloud AWS / Spring Kafka

**¿Qué es?** Librerías que permiten a Spring Boot conectarse a sistemas de mensajería asíncrona. AWS SQS es el servicio de colas de Amazon; Kafka es una alternativa open source muy usada en arquitecturas de microservicios.

**¿Por qué mensajería asíncrona?** El Módulo de Rutas y Flotas notifica al Módulo Financiero cuando una ruta se cierra. Esta comunicación no puede ser sincrónica (es decir, el Módulo de Rutas no puede quedarse esperando que el Módulo Financiero responda). La cola de mensajería actúa como intermediario: el Módulo de Rutas deposita el evento en la cola y sigue su camino; el Módulo Financiero lo consume cuando está listo.

**Dead Letter Queue (DLQ):** Es una cola especial donde van los mensajes que no pudieron procesarse después de varios intentos. Permite que el equipo técnico revise manualmente qué salió mal sin perder el mensaje original.

**¿Dónde lo usamos?** En Cierre de Ruta, donde el `RutaCerradaConsumer` escucha la cola y procesa los eventos `RUTA_CERRADA`.

---

### Slf4j + CloudWatch

**¿Qué es?** Slf4j es la librería de logging estándar en Java. CloudWatch es el servicio de monitoreo de logs de AWS.

**¿Por qué lo usamos?** Los edge cases de Cierre de Ruta (contrato nulo, vehículo sin tarifa) no alteran la estructura de la base de datos — se manejan mediante logs de advertencia (`WARN`) y notificaciones al equipo financiero. Slf4j permite registrar esos eventos con niveles (`INFO`, `WARN`, `ERROR`) y CloudWatch los centraliza en AWS para que el equipo pueda consultarlos y configurar alertas.

**¿Dónde lo usamos?** En todos los features del backend, pero especialmente en Cierre de Ruta y Calcular Liquidación para registrar eventos de auditoría y anomalías.

---

### Swagger / OpenAPI

**¿Qué es?** Una herramienta que genera automáticamente documentación interactiva de los endpoints REST a partir del código.

**¿Por qué lo usamos?** El módulo financiero se integra con otros módulos (Gestión de Paquetes, Rutas y Flotas) y con el frontend. Swagger genera una página web donde cualquier desarrollador puede ver todos los endpoints, sus parámetros y probarlos directamente desde el navegador, sin necesidad de leer el código fuente.

**¿Dónde lo usamos?** Como tarea de polish en todos los features que exponen endpoints REST públicos.

---

## Tecnologías del Frontend

### React 18

**¿Qué es?** La librería de JavaScript más usada para construir interfaces de usuario. Permite construir la UI como un conjunto de componentes reutilizables que se actualizan automáticamente cuando los datos cambian.

**¿Por qué React?** Es el estándar de la industria para frontends modernos, tiene un ecosistema enorme y permite construir interfaces reactivas (que responden a cambios en tiempo real) de forma organizada. La versión 18 trae mejoras de rendimiento con renderizado concurrente.

**¿Dónde lo usamos?** En todos los features que tienen interfaz de usuario: formulario de registro de contratos, visualización de liquidaciones, estado de pagos, dashboard de rutas y panel de recálculo del administrador.

---

### Vite

**¿Qué es?** La herramienta que compila y sirve el proyecto React durante el desarrollo y para producción.

**¿Por qué Vite?** Es significativamente más rápido que Create React App (la alternativa anterior). El servidor de desarrollo inicia en menos de un segundo y los cambios en el código se reflejan en el navegador casi instantáneamente, lo que hace el desarrollo mucho más ágil.

**¿Dónde lo usamos?** Es la base de configuración del proyecto frontend. El equipo lo usa durante todo el desarrollo pero es invisible en producción.

---

### Axios

**¿Qué es?** Una librería de JavaScript para hacer peticiones HTTP desde el frontend hacia el backend.

**¿Por qué Axios?** Simplifica las llamadas HTTP con una API más limpia que el `fetch` nativo del navegador, permite configurar interceptores globales (para manejar errores HTTP de forma centralizada) y maneja automáticamente la serialización y deserialización de JSON.

**Interceptores:** Configuramos interceptores de Axios para capturar errores globales como HTTP 503 (base de datos no disponible) y mostrar mensajes amigables al usuario sin necesidad de manejar ese error en cada componente por separado.

**¿Dónde lo usamos?** En todos los features del frontend para comunicarse con los endpoints del backend.

---

### React Hook Form / Formik + Yup/Zod

**¿Qué es?** Librerías para manejar el estado y la validación de formularios en React.

**¿Por qué las usamos?** Los formularios con múltiples campos y reglas de validación (como el formulario de registro de contratos) son complejos de manejar manualmente en React. Estas librerías simplifican el manejo del estado de cada campo, los mensajes de error y el envío del formulario.

**Yup/Zod** son librerías de validación de esquemas que permiten definir las reglas del formulario de forma declarativa (similar a Bean Validation en el backend), mostrando errores en tiempo real sin necesidad de esperar la respuesta del servidor.

**¿Dónde lo usamos?** Principalmente en el formulario de Registrar Modelo de Contratación, que tiene campos condicionales (el precio cambia según el tipo de contrato) y múltiples validaciones.

---

## Tecnologías de Base de Datos

### PostgreSQL 15

**¿Qué es?** El sistema de base de datos relacional que usamos para persistir toda la información del módulo.

**¿Por qué PostgreSQL?** Es la base de datos relacional open source más avanzada del mercado. Soporta transacciones ACID (garantía de que los datos nunca quedan en estado inconsistente), tipos de datos avanzados como UUID, y tiene excelente rendimiento para las consultas financieras que hacemos.

**ACID** significa:
- **Atomicidad**: Una operación ocurre completa o no ocurre (lo que garantiza `@Transactional`).
- **Consistencia**: Los datos siempre cumplen las reglas definidas (restricciones UNIQUE, foreign keys).
- **Aislamiento**: Operaciones concurrentes no se interfieren entre sí.
- **Durabilidad**: Una vez guardado, el dato no se pierde aunque el sistema falle.

**Restricciones clave que usamos:**
- `UNIQUE(ruta_id)` en la tabla `liquidaciones` → Evita liquidaciones duplicadas.
- `UNIQUE(id_ruta)` en la tabla `rutas` → Garantiza idempotencia del evento de cierre de ruta.
- `UNIQUE(id_contrato)` en la tabla `contratos` → Evita contratos duplicados.

**¿Dónde lo usamos?** Es la única base de datos del módulo. Todos los features persisten sus datos aquí.

---

## Tecnologías de Testing

### JUnit 5

**¿Qué es?** El framework estándar de pruebas unitarias en Java.

**¿Para qué lo usamos?** Para escribir tests que verifican que cada pieza de lógica de negocio funciona correctamente de forma aislada: el motor de cálculo de liquidación, los validadores de fechas y precios, el mapeo de estados de paquetes a porcentajes de pago, etc.

**¿Dónde lo usamos?** En todos los features del backend.

---

### Mockito

**¿Qué es?** Una librería que permite crear "dobles" de objetos reales para aislar la lógica que se está probando.

**¿Por qué lo usamos?** Cuando probamos un servicio que depende de un repositorio de base de datos, no queremos que el test realmente consulte la base de datos. Mockito permite simular el comportamiento del repositorio ("cuando se llame a `findById(1)`, retorna este objeto") para probar solo la lógica del servicio.

**¿Dónde lo usamos?** En los tests unitarios de todos los features del backend junto con JUnit 5.

---

### Testcontainers

**¿Qué es?** Una librería que levanta contenedores Docker reales (como una base de datos PostgreSQL o una cola de mensajería) durante la ejecución de los tests de integración.

**¿Por qué lo usamos?** Los tests de integración necesitan una base de datos o una cola real para verificar que el sistema completo funciona. Testcontainers levanta esos servicios automáticamente al iniciar el test y los destruye al terminar, sin necesidad de tenerlos instalados manualmente.

**¿Dónde lo usamos?** En Cierre de Ruta para simular la cola de mensajería (SQS/Kafka) en los tests de integración del consumer.

---

### WireMock (repetido aquí por completitud)

Ya explicado en la sección de Backend. Se usa exclusivamente en los tests de Novedad Estado del Paquete para simular el Módulo de Gestión de Paquetes.

---

### Jest + React Testing Library

**¿Qué es?** Jest es el framework de pruebas de JavaScript. React Testing Library es una librería que permite probar componentes de React simulando cómo los usa un usuario real (haciendo clic en botones, llenando formularios, leyendo textos en pantalla).

**¿Por qué React Testing Library?** A diferencia de otras librerías de testing de React, no prueba detalles internos del componente sino su comportamiento visible. Esto hace que los tests sean más robustos ante refactorizaciones del código interno.

**¿Dónde lo usamos?** En los features que tienen formularios o lógica de UI compleja: formulario de contratos, panel de recálculo del administrador, visualización de estados de pago.

---

## Tecnologías de Infraestructura

### Docker + Docker Compose

**¿Qué es?** Docker permite empaquetar la aplicación y todas sus dependencias en un contenedor que funciona igual en cualquier máquina. Docker Compose permite orquestar múltiples contenedores (la aplicación Spring Boot + PostgreSQL + la cola de mensajería) con un solo comando.

**¿Por qué lo usamos?** Elimina el problema de "en mi máquina funciona". Con Docker Compose, cualquier desarrollador puede levantar el entorno completo de desarrollo con un solo comando sin instalar Java, PostgreSQL ni la cola de mensajería manualmente.

**¿Dónde lo usamos?** Cada feature tiene su `Dockerfile` para el backend y el frontend, y el `docker-compose.yml` del proyecto levanta el entorno completo localmente.

---

### AWS

**¿Qué es?** Amazon Web Services es la plataforma de nube donde se despliega el sistema en producción.

**Servicios de AWS que usamos en este módulo:**

| Servicio | ¿Para qué? |
|:---|:---|
| **EC2 / ECS** | Servidores donde corre la aplicación Spring Boot |
| **RDS (PostgreSQL)** | Base de datos administrada en la nube |
| **SQS** | Cola de mensajería para recibir eventos de cierre de ruta |
| **CloudWatch** | Monitoreo de logs y alertas en producción |

**Variables de entorno:** Todas las credenciales (contraseña de base de datos, URL de la cola, claves de AWS) se configuran como variables de entorno en AWS, nunca hardcodeadas en el código. Esto es una práctica de seguridad fundamental.

---

## Resumen rápido por feature

| Feature | Tecnologías clave |
|:---|:---|
| **Calcular Liquidación** | Spring Boot · JPA · `@Transactional` · Patrón Strategy · PostgreSQL · Flyway |
| **Cierre de Ruta** | Spring Boot · SQS/Kafka · Consumer/Listener · JPA · `@Transactional` · Slf4j · Testcontainers |
| **Novedad Estado del Paquete** | Spring Boot · OpenFeign/WebClient · Resilience4j · WireMock · JPA · `@Transactional` |
| **Registrar Modelo de Contratación** | Spring Boot · Bean Validation · Custom Validators · Spring Security · JPA · React Hook Form |
| **Visualizar Liquidación** | Spring Boot · Spring Security · JPA (paginación) · React · Axios |
| **Registrar Estado del Pago** | Spring Boot · SQS/Kafka · Idempotencia · JPA · `@Transactional` |
| **Visualizar Estado del Pago** | Spring Boot · Spring Security · JPA · React · Axios |
# HelpDesk Flow

Aplicación de consola para la gestión de incidencias de mesa de ayuda (helpdesk), desarrollada como
trabajo práctico de Xanpan, XP, Kanban e IA. Permite registrar incidencias, calcular su prioridad
automáticamente, gestionar su flujo de estados, consultarlas/filtrarlas, ver métricas básicas del
equipo y marcar incidencias críticas como EXPEDITE.

## Integrantes

- Ivannia Porras Miranda
- Anyelina Chacón Mora

## Requisitos de ejecución

- Java 17 o superior
- Maven 3.8+

## Cómo compilar

```
mvn compile
```

## Cómo ejecutar las pruebas

```
mvn test
```

## Cómo ejecutar la aplicación

```
mvn package -DskipTests
java -cp target/classes com.utn.helpdesk.app.App
```

Se abre un menú interactivo por consola para registrar incidencias, cambiar su estado, registrar
soluciones, marcar EXPEDITE, consultar/filtrar y ver métricas.

## Tablero Kanban

[https://github.com/users/Anyelina13/projects/2](https://github.com/users/Anyelina13/projects/2)

## Decisiones principales de diseño

- **Cálculo de prioridad separado (`CalculadorPrioridad`):** la regla de negocio que traduce
  impacto + urgencia en prioridad vive en su propia clase de servicio, en vez de estar mezclada
  con `Incidencia` o `GestorIncidencias`. Se calcula una única vez, al crear la incidencia, y
  queda fijo en el campo `prioridad`.
- **`Incidencia` guarda su propio estado y solución:** en un principio el estado y la solución de
  una incidencia vivían en `Map`s internos de `GestorIncidencias`, separados del resto de sus
  datos. Se refactorizó para que `Incidencia` sea dueña de esos datos (estado, solución, fecha de
  cierre), y `GestorIncidencias` pasó a ser responsable solo de **validar** las reglas de negocio
  (transiciones válidas, exigir solución antes de finalizar) antes de aplicarlas sobre el objeto.
- **EXPEDITE como servicio que envuelve a `GestorIncidencias` (`ServicioExpedite`):** en vez de
  modificar `GestorIncidencias` para soportar la regla de "una sola incidencia EXPEDITE activa a
  la vez", se creó una clase nueva que delega en `GestorIncidencias` para todo lo que ya
  funcionaba, y solo agrega la validación adicional de EXPEDITE por encima. Esto evitó tocar
  código ya probado por HU-01 a HU-05.
- **Transiciones de estado centralizadas en `ValidadorTransicionEstado`:** el mapa de transiciones
  válidas está en un único lugar, separado de `GestorIncidencias`, para que agregar o quitar una
  transición no implique tocar la lógica de negocio (exigencia de solución, etc.).

## Estado de la integración continua

El workflow de GitHub Actions (`.github/workflows/ci.yml`) compila el proyecto y ejecuta las
pruebas con Maven en cada `push` y `pull request` sobre `main`. Ver la pestaña
[Actions](https://github.com/Anyelina13/helpdesk-flow/actions) del repositorio para el estado
actual.

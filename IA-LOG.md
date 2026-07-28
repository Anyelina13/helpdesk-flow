# Bitácora de uso de IA

Registro de las interacciones relevantes con Claude Code (Anthropic) durante el desarrollo de
HelpDesk Flow.

| Fecha | Herramienta | Objetivo | Resultado usado | Verificación | Cambios humanos |
|---|---|---|---|---|---|
| 2026-07-26 | Claude Code | Auditar el repositorio contra el checklist de la consigna (`CHECKLIST_VALIDACION_TAREA.md`) para saber qué faltaba de HU-01 a HU-05, EXPEDITE, Kanban, CI, XP e IA. | Tabla de estado por sección con lo cumplido/parcial/faltante y una lista priorizada de pendientes. | Se contrastó cada punto contra el código real (lectura de `Incidencia`, `GestorIncidencias`, tests existentes) y contra el historial de commits (`git log`), no se aceptó el diagnóstico sin cruzarlo con el repo. | Se decidió sacar el checklist del repositorio (no debía versionarse) y moverlo a una carpeta fuera del proyecto. |
| 2026-07-26 | Claude Code | Completar el modelo `Incidencia` (categoría, prioridad calculada, fecha de creación) y refactorizar el estado/solución fuera de los `Map` internos de `GestorIncidencias`, siguiendo TDD (prueba que falla → implementación). | Código de `Incidencia.java`, `GestorIncidencias.java` y sus tests correspondientes. | Se corrió `mvn test` después de cada paso (antes solo se había verificado compilando con `javac`, sin Maven disponible en el entorno). Los 19-24 tests pasaron en cada etapa, incluyendo los tests previos al refactor, sin modificarlos. | Se pidió explícitamente agrupar los commits (no un commit por cada test individual) antes de confirmar cualquier commit. |
| 2026-07-26 | Claude Code | Implementar HU-04 (consulta y filtrado), HU-05 (métricas con throughput y lead time) y el cambio de requerimiento EXPEDITE como clase de servicio separada. | `GestorIncidencias` extendido con métodos de consulta, `MetricasIncidencias` y `ServicioExpedite` como clases nuevas, con sus tests. | Se corrió `mvn test` real (ya con Maven disponible) después de cada feature, confirmando 24 → 29 → 34 tests en verde sin romper los anteriores. | Ninguno sobre el código en sí; se aprobaron los diseños propuestos (separar `ServicioExpedite` para no tocar `GestorIncidencias`) tal como se presentaron. |
| 2026-07-26 | Claude Code | Construir un `App.java` funcional (menú de consola) en lugar del "Hello World" por defecto de Maven. | Menú interactivo que usa `GestorIncidencias`, `ServicioExpedite` y `MetricasIncidencias`. | Se probó en vivo con una sesión real por consola (registrar incidencia, cambiar estado, marcar EXPEDITE, ver métricas), no solo se asumió que compilaba. Un primer intento de prueba automatizada vía PowerShell falló por un problema de encoding del pipe (no del programa); se repitió con Bash y funcionó correctamente. | — |
| 2026-07-26 | Claude Code | Redactar el workflow de CI (`.github/workflows/ci.yml`). | Pipeline de GitHub Actions que compila y testea con Maven en cada `push`/`pull_request`. | No se pudo ejecutar localmente (corre en la infraestructura de GitHub); queda pendiente confirmar en la pestaña Actions del repo que corrió en verde tras el primer push. | — |

## Sugerencias rechazadas

- No hubo sugerencias rechazadas durante el desarrollo.

## Notas

- Todas las respuestas de la IA usadas en código pasaron por `mvn test` antes de darse por buenas;
  ninguna se aceptó "a ciegas".
- La IA no tomó decisiones de negocio por su cuenta (por ejemplo, la fecha del Kanban, quién hace
  qué): esas siguieron siendo decisiones del equipo.

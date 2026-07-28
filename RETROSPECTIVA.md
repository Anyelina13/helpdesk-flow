# Retrospectiva

## 1. ¿Qué aportó Kanban al trabajo de la pareja?

El tablero nos obligó a poner en columnas explícitas algo que veníamos manejando de forma informal:
qué estaba realmente terminado y qué no. Al ordenar el trabajo por historia de usuario (HU-01 a
HU-05, EXPEDITE, y las tareas técnicas como el refactor o el CI) en tarjetas separadas, fue más
fácil ver de un vistazo cuánto quedaba pendiente en vez de pensar en "el proyecto" como un bloque
único. También ayudó a separar trabajo de código de trabajo de documentación (README, IA-LOG,
retrospectiva), que si no quedan como tarjetas propias tienden a postergarse hasta el final.

## 2. ¿Qué dificultad generó el límite WIP?

El mayor problema no fue respetar el límite en sí, sino que el tablero se armó después de haber
avanzado bastante en el código, así que varias tarjetas nacieron directamente en "Hecho" en vez de
recorrer las columnas intermedias. Eso hace difícil mostrar el límite de "En desarrollo" (1 por
pareja) como una restricción que realmente haya frenado a alguien, más allá de que en la práctica
se trabajó de a una historia por vez.

## 3. ¿Qué errores fueron detectados mediante TDD?

El caso más claro fue durante el refactor de estado y solución: al mover esos datos desde `Map`
internos de `GestorIncidencias` hacia el propio objeto `Incidencia`, los tests existentes de
transiciones inválidas y de "no se puede finalizar sin solución" se corrieron sin modificarlos y
siguieron pasando, confirmando que el cambio interno no alteró el comportamiento externo. También
ayudó a detectar que la prioridad no estaba conectada a la incidencia real (se calculaba aislada,
pero nunca se guardaba), algo que un test específico dejó en evidencia antes de tocar producción.

## 4. ¿Qué parte del código fue refactorizada?

El estado (`estado`), la solución aplicada (`solucion`) y la fecha de cierre (`fechaCierre`) de una
incidencia, que originalmente vivían en dos `Map<String, ...>` separados dentro de
`GestorIncidencias`, se movieron a ser parte del propio objeto `Incidencia`. `GestorIncidencias`
pasó a tener una sola responsabilidad clara: validar las reglas de negocio (transición válida,
solución obligatoria antes de finalizar) antes de aplicar el cambio sobre la incidencia.

## 5. ¿Cómo afectó el cambio de requerimiento (EXPEDITE)?

Se resolvió sin modificar ni una línea de `GestorIncidencias`. En vez de meter la regla de "una sola
incidencia EXPEDITE activa en desarrollo o validación" dentro de la clase ya probada por HU-01 a
HU-05, se creó `ServicioExpedite`, que envuelve a `GestorIncidencias` y agrega la validación
adicional por encima. Esto redujo el riesgo de romper funcionalidad existente al incorporar el
cambio de requerimiento, que es justamente lo que se espera poder demostrar en este punto.

## 6. ¿En qué ayudó la IA?

Aceleró la implementación siguiendo el mismo patrón de TDD que ya traía el proyecto (prueba que
falla, luego implementación), mantuvo consistencia con el estilo de código existente, y corrió
`mvn test` después de cada cambio para verificar que no se rompiera nada, en vez de asumir que el
código nuevo funcionaba. También fue útil para auditar el repositorio completo contra el checklist
de la consigna y priorizar qué faltaba.

## 7. ¿En qué se equivocó o fue insuficiente la IA?

No puede tomar decisiones que dependen del equipo o de herramientas externas: no pudo crear el
tablero de Kanban (requiere ser dueño/colaborador del repositorio), no conoce los nombres reales de
las integrantes, y no puede verificar si el CI corrió en verde en GitHub (corre fuera del entorno
local). Tampoco puede escribir de forma genuina esta retrospectiva: arma un borrador con la
información técnica real, pero la reflexión sobre cómo trabajó la pareja la tiene que confirmar o
corregir el equipo.

## 8. ¿Qué cambiarían en una siguiente versión?

Armar el tablero de Kanban desde el primer día, antes de escribir código, para que el movimiento de
tarjetas refleje el avance real en vez de tener que reconstruirlo después. También repartir el
trabajo de forma más pareja entre ambas integrantes desde el inicio, en vez de concentrarlo, para
que el historial de commits muestre participación distribuida de las dos personas a lo largo de todo
el proyecto y no solo hacia el final.

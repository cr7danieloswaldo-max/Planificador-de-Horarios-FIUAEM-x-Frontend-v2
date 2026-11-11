# Planificador de Horarios — Prototipo 1 (JSP + MVC según el diagrama de frontend)

Asistente de 3 pasos para armar el horario de reinscripción (UAEMéx · Facultad de Ingeniería · ICO):
**1 Tu avance → 2 Materias y preferencias → 3 Resultados**. Propone hasta 4 horarios sin traslapes y con
las seriaciones respetadas. No requiere login ni número de cuenta.

## Cómo ejecutarlo

Requisitos: **JDK 17+**, **Maven 3.9+** y **Tomcat 10.1** (Jakarta EE 10; no sirve Tomcat 9).

```
mvn clean package          # compila, corre las pruebas y genera target/planificador.war
```
Despliega el `.war` en Tomcat 10.1 (o "Run" desde NetBeans/IntelliJ con Tomcat 10.1) y abre
`http://localhost:8080/planificador`. El `context.xml` publica la app en la raíz (`path=""`).

## Estructura (cada clase corresponde a una caja del diagrama de clases)

| Paquete `mx.uaemex.planificador.…` | Clases |
|---|---|
| `web` | `PlanificadorServlet` — adaptador HTTP delgado (GET muestra un paso, POST aplica el formulario y redirige) |
| `controlador` | `PlanificadorController`, `SesionPlanificador`, `Paso` |
| `vm` | `MateriaVM`, `BloqueVM`, `OpcionHorarioVM`, `ResultadoVM`, `OpcionHorarioMapper` |
| `vista` | `Vista` (interfaz), `PantallaInicio`, `PantallaAvance`, `PantallaMateriasPreferencias`, `PantallaResultados`, `PantallaSinResultados`, `BarraPasos` y los componentes `SelectorSemestre`, `ChecklistMaterias`, `PanelPreferencias`, `SelectorOpcion`, `CalendarioSemanal`, `PanelCarga` |
| `logica` | `FiltroMateriasAprobadas`, `ValidadorSeriaciones`, `MotorCombinatorio` |
| `datos` | `OfertaReader` (interfaz), `CsvOfertaReader`, `ParserException` |
| `dominio` | `Materia`, `Grupo`, `Sesion`, `OfertaAcademica`, `OpcionHorario`, `Alumno`, `Turno`, `DiaSemana` |

Plantillas JSP en `WEB-INF/vistas/` (una por pantalla) y un *tag file* por componente en `WEB-INF/tags/`
(`checklist`, `calendario`, `panelCarga`, …). Estilos en `css/estilos.css` (sin cambios de diseño).

### Flujo de una petición
`Servlet` → `PlanificadorController` (cambia la `SesionPlanificador` y devuelve un `Paso`) →
`controller.vistaPara(paso)` crea la `Pantalla*` → `pantalla.mostrar(sesion)` prepara sus componentes →
el servlet reenvía a `pantalla.getPlantilla()` (JSP), que solo pinta (`${pantalla.calendario}`, etc.).

### URLs
* `planificador` o `?paso=0` inicio · `?paso=1` avance · `?paso=2` materias y preferencias · `?paso=3` resultados
* `?paso=3&opcion=K` selecciona la pestaña K (base 0) · `?paso=3&opcion=K&exportar=pdf` abre el diálogo de impresión
* `?reiniciar=1` borra lo capturado y empieza de nuevo

## Datos (CSV en `src/main/resources/datos/`)
* `materias.csv` (clave, nombre, semestre, etiqueta corta) · `seriaciones.csv` (clave, prerrequisito) ·
  `grupos.csv` (una fila por sesión semanal: clave, gpo, profesor, día 0-5, ini, fin, aula).
* Para cambiar la oferta basta con reemplazar los archivos. Una clave inexistente o una hora mal escrita
  se reporta con **archivo y línea exacta** (`ParserException`); la pantalla de inicio muestra el mensaje
  y se reintenta al recargar, en lugar de tumbar la aplicación.
* `_advertencias.txt` lista los grupos excluidos por horarios dudosos en las plantillas originales.

## Reglas que implementa
* Se listan como cursables las materias del semestre elegido y del siguiente, salvo las aprobadas.
* Una materia con seriación pendiente aparece deshabilitada con el motivo («requiere Cálculo II (seriación)»).
* Turno matutino = todas las sesiones inician antes de las 14:00; vespertino = a partir de las 14:00.
* Mínimo 2 materias para generar horarios. El motor devuelve hasta 4 opciones y, si no hay ninguna,
  explica el motivo (grupo sin cupo en el turno, par de materias que siempre se traslapan, etc.) y sugiere qué cambiar.
* Si cambia el avance, el semestre, las materias o las preferencias, los resultados anteriores se descartan.

## Pruebas
`mvn test` — JUnit 5: reproduce el recorrido de las capturas (4 opciones con Cálculo II en los grupos 2, 3, 4 y 5,
rango de horas 7–18 y 7–20, caso «sin horarios viables», seriaciones, avisos) y prueba el servlet con petición simulada.

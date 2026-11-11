package mx.uaemex.planificador.controlador;

import mx.uaemex.planificador.datos.CsvOfertaReader;
import mx.uaemex.planificador.dominio.DiaSemana;
import mx.uaemex.planificador.dominio.Turno;
import mx.uaemex.planificador.vista.*;
import mx.uaemex.planificador.vm.OpcionHorarioVM;
import mx.uaemex.planificador.vm.ResultadoVM;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Reproduce el recorrido de las capturas del Prototipo 1 (semestre 2, 7 materias de 2.º semestre). */
class PlanificadorControllerTest {
    // Epistemología, Ec. diferenciales, Álgebra lineal, Programación II, Química, Cálculo II, Inglés 5
    private static final List<String> SEGUNDO = List.of("LINC10", "LINC07", "LINC01", "LINC35", "LINC29", "LINC04", "LMU209");

    private static PlanificadorController nuevo() { return new PlanificadorController(new CsvOfertaReader()); }

    private static PlanificadorController conSegundoSemestre(Turno turno) {
        PlanificadorController c = nuevo();
        for (String k : SEGUNDO) c.marcarDeseada(k, true);
        c.definirTurno(turno);
        return c;
    }

    @Test void iniciaEnInicioConSemestre2YPrimerSemestreAprobado() {
        PlanificadorController c = nuevo();
        assertEquals(Paso.INICIO, c.getSesion().getPasoActual());
        assertEquals(2, c.getSesion().getSemestre());
        assertEquals(5, c.getSesion().getAprobadas().size());
        assertNull(c.getAviso());
    }

    @Test void pantallaMateriasMuestraSeriacionesComoEnLaCaptura() {
        PlanificadorController c = nuevo();
        var lista = c.getMateriasCursables();
        assertEquals(14, lista.size());                       // 7 de 2.º + 7 de 3.º
        var calcIII = lista.stream().filter(m -> m.getClave().equals("LINC05")).findFirst().orElseThrow();
        assertFalse(calcIII.isHabilitada());
        assertEquals("No disponible: requiere Cálculo II (seriación).", calcIII.getMotivoBloqueo());
    }

    @Test void noPermiteElegirMateriaConSeriacionPendiente() {
        PlanificadorController c = nuevo();
        c.marcarDeseada("LINC05", true);                      // Cálculo III requiere Cálculo II
        assertTrue(c.getSesion().getDeseadas().isEmpty());
        assertNotNull(c.getAviso());
    }

    @Test void generaCuatroOpcionesSinPreferenciaDeTurno() {
        PlanificadorController c = conSegundoSemestre(Turno.SIN_PREFERENCIA);
        assertEquals(Paso.RESULTADOS, c.generarHorarios());
        ResultadoVM r = c.getSesion().getResultado();
        assertTrue(r.isHayViables());
        assertEquals(4, r.getOpciones().size());
        assertEquals("Opción 1", r.getOpciones().get(0).getEtiqueta());
        // En las capturas, Cálculo II cambia de grupo en cada opción: 2, 3, 4 y 5
        for (int i = 0; i < 4; i++) {
            String gpo = r.getOpciones().get(i).getBloques().stream()
                    .filter(b -> b.getMateria().equals("Cálculo II")).findFirst().orElseThrow().getGrupo();
            assertEquals(String.valueOf(i + 2), gpo);
        }
        assertEquals("Sin cruces · Seriaciones válidas · Sin preferencia de turno", r.getOpciones().get(0).getResumen());
        assertInstanceOf(PantallaResultados.class, c.vistaPara(Paso.RESULTADOS));
    }

    @Test void calendarioAjustaElRangoDeHoras() {
        PlanificadorController c = conSegundoSemestre(Turno.SIN_PREFERENCIA);
        c.generarHorarios();
        ResultadoVM r = c.getSesion().getResultado();
        CalendarioSemanal cal = new CalendarioSemanal();
        cal.render(r.getOpciones().get(0));
        assertEquals(LocalTime.of(7, 0), cal.getHoraInicio());
        assertEquals(LocalTime.of(18, 0), cal.getHoraFin());   // Química 17-19 h ocupa la fila de las 18
        assertFalse(cal.bloquesEn(DiaSemana.LUN, 7).isEmpty()); // Programación II, lunes 7:00
        cal.render(r.getOpciones().get(2));
        assertEquals(LocalTime.of(20, 0), cal.getHoraFin());   // Cálculo II Gpo 4 hasta las 21:00
    }

    @Test void panelCargaTieneUnRenglonPorMateria() {
        PlanificadorController c = conSegundoSemestre(Turno.SIN_PREFERENCIA);
        c.generarHorarios();
        OpcionHorarioVM op = c.getSesion().getResultado().getOpciones().get(0);
        PanelCarga panel = new PanelCarga();
        panel.render(op);
        assertEquals(7, panel.getItems().size());
        assertTrue(op.getBloques().size() > 7);                // varias materias tienen 2 sesiones
    }

    @Test void conMenosDeDosMateriasSeQuedaEnElPasoDosConAviso() {
        PlanificadorController c = nuevo();
        c.marcarDeseada("LINC10", true);
        assertEquals(Paso.MATERIAS_PREFERENCIAS, c.generarHorarios());
        assertEquals("Selecciona al menos 2 materias para generar horarios.", c.getAviso());
        assertNull(c.getAviso());                              // el aviso se muestra una sola vez
        assertEquals(Paso.MATERIAS_PREFERENCIAS, c.irAPaso(Paso.RESULTADOS)); // sin resultados no se puede entrar
    }

    @Test void sinHorariosViablesExplicaElMotivoYSugiere() {
        PlanificadorController c = nuevo();
        c.marcarDeseada("LINC10", true);                       // Epistemología: solo grupos matutinos
        c.marcarDeseada("LINC01", true);
        c.definirTurno(Turno.TARDE);
        c.definirDiaLibre(DiaSemana.VIE);
        assertEquals(Paso.RESULTADOS, c.generarHorarios());
        ResultadoVM r = c.getSesion().getResultado();
        assertFalse(r.isHayViables());
        assertTrue(r.getMensajeSinResultados().startsWith("«Epistemología» no tiene grupos con tus preferencias"));
        assertEquals(List.of("Cambiar el turno a «Sin preferencia».", "Quitar el viernes como día libre.",
                "Quitar una de las materias seleccionadas."), r.getSugerencias());
        assertInstanceOf(PantallaSinResultados.class, c.vistaPara(Paso.RESULTADOS));
    }

    @Test void cambiarElAvanceInvalidaLoElegidoYLosResultados() {
        PlanificadorController c = conSegundoSemestre(Turno.SIN_PREFERENCIA);
        c.generarHorarios();
        c.marcarAprobada("LINC03", false);                     // quita Cálculo I: Cálculo II y Ec. diferenciales ya no cumplen
        assertFalse(c.getSesion().getDeseadas().contains("LINC04"));
        assertFalse(c.getSesion().getDeseadas().contains("LINC07"));
        assertNull(c.getSesion().getResultado());
    }

    @Test void cambiarDeSemestreLimpiaLaSeleccion() {
        PlanificadorController c = conSegundoSemestre(Turno.MANANA);
        c.seleccionarSemestre(3);
        assertTrue(c.getSesion().getDeseadas().isEmpty());
        c.seleccionarSemestre(9);                              // inválido: se ignora con aviso
        assertEquals(3, c.getSesion().getSemestre());
        assertNotNull(c.getAviso());
    }

    @Test void exportarSoloConResultadosYFormatoValido() {
        PlanificadorController c = conSegundoSemestre(Turno.SIN_PREFERENCIA);
        c.exportar(0, "pdf");
        assertFalse(c.consumirExportacion());                  // todavía no hay horarios
        c.getAviso();
        c.generarHorarios();
        c.exportar(2, "pdf");
        assertEquals(2, c.getSesion().getOpcionSeleccionada());
        assertTrue(c.consumirExportacion());
        assertFalse(c.consumirExportacion());
        c.exportar(0, "docx");
        assertNotNull(c.getAviso());
    }

    @Test void barraDePasosResaltaElPasoActual() {
        PlanificadorController c = nuevo();
        Vista v = c.vistaPara(Paso.MATERIAS_PREFERENCIAS);
        v.mostrar(c.getSesion());
        var etapas = v.getBarraPasos().getEtapas();
        assertFalse(etapas.get(0).isActiva());
        assertTrue(etapas.get(1).isActiva());
        assertFalse(etapas.get(2).isActiva());
    }
}

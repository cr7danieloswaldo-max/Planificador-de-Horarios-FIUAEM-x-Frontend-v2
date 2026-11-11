package mx.uaemex.planificador.controlador;

import mx.uaemex.planificador.datos.OfertaReader;
import mx.uaemex.planificador.datos.ParserException;
import mx.uaemex.planificador.dominio.*;
import mx.uaemex.planificador.logica.FiltroMateriasAprobadas;
import mx.uaemex.planificador.logica.MotorCombinatorio;
import mx.uaemex.planificador.logica.ValidadorSeriaciones;
import mx.uaemex.planificador.vista.*;
import mx.uaemex.planificador.vm.MateriaVM;
import mx.uaemex.planificador.vm.OpcionHorarioMapper;
import mx.uaemex.planificador.vm.ResultadoVM;

import java.util.*;

/**
 * Orquesta el asistente: recibe las acciones del usuario, actualiza la SesionPlanificador y devuelve el Paso
 * que debe mostrarse. Hay uno por visitante (se guarda en su HttpSession). No conoce el API de servlets.
 */
public final class PlanificadorController {
    public static final int MIN_MATERIAS = 2;

    private final SesionPlanificador sesion = new SesionPlanificador();
    private final OfertaReader reader;
    private final FiltroMateriasAprobadas filtro = new FiltroMateriasAprobadas();
    private final ValidadorSeriaciones validador = new ValidadorSeriaciones();
    private final MotorCombinatorio motor = new MotorCombinatorio();
    private OpcionHorarioMapper mapper;
    private OfertaAcademica oferta;       // null si el catálogo no se pudo cargar
    private String aviso;                 // mensaje de un solo uso para la siguiente pantalla
    private boolean exportacionPendiente;

    public PlanificadorController(OfertaReader reader) {
        this.reader = reader;
        iniciar();
    }

    // ------------------------------------------------------------------ flujo

    /** Reinicia el asistente y carga el catálogo. Si el catálogo falla se informa en la pantalla de inicio. */
    public Paso iniciar() {
        sesion.reiniciar();
        aviso = null;
        exportacionPendiente = false;
        try {
            oferta = reader.leer();
            mapper = new OpcionHorarioMapper(oferta);
            // Ejemplo del prototipo: todo lo anterior al semestre inicial viene marcado como aprobado.
            for (Materia m : filtro.anteriores(oferta, sesion.getSemestre())) sesion.getAprobadas().add(m.getClave());
        } catch (ParserException ex) {            // "captura ParserException"
            oferta = null;
            mapper = null;
            aviso = "No se pudo cargar el catálogo de materias. " + ex.getMessage();
        }
        sesion.setPasoActual(Paso.INICIO);
        return Paso.INICIO;
    }

    /** Navega a un paso; si no se puede (sin catálogo, o sin resultados generados) devuelve el paso válido más cercano. */
    public Paso irAPaso(Paso paso) {
        if (oferta == null) return iniciar(); // el catálogo falló antes: reintenta y, si sigue mal, vuelve a avisar en INICIO
        Paso destino = paso;
        if (paso == Paso.RESULTADOS && sesion.getResultado() == null) destino = Paso.MATERIAS_PREFERENCIAS;
        sesion.setPasoActual(destino);
        return destino;
    }

    // ------------------------------------------------------------------ paso 1: avance

    public Paso seleccionarSemestre(int semestre) {
        if (oferta == null) return Paso.INICIO;
        if (!SelectorSemestre.SEMESTRES_DISPONIBLES.contains(semestre)) {
            aviso = "Semestre no válido.";
            return Paso.AVANCE;
        }
        if (semestre != sesion.getSemestre()) {
            sesion.setSemestre(semestre);
            sesion.getDeseadas().clear();
            sesion.getAprobadas().removeIf(c -> oferta.materia(c).getSemestre() >= semestre);
            invalidarResultado();
        }
        return Paso.AVANCE;
    }

    /** Solo se pueden declarar aprobadas materias de semestres anteriores al que se cursa. */
    public Paso marcarAprobada(String clave, boolean marcada) {
        if (oferta == null) return Paso.INICIO;
        Materia m = oferta.materia(clave);
        if (m == null || m.getSemestre() >= sesion.getSemestre()) return Paso.AVANCE;
        boolean cambio = marcada ? sesion.getAprobadas().add(clave) : sesion.getAprobadas().remove(clave);
        if (cambio) {
            // Lo ya elegido debe seguir cumpliendo seriación y no estar aprobado
            sesion.getDeseadas().removeIf(c -> sesion.getAprobadas().contains(c)
                    || !validador.cumple(oferta, oferta.materia(c), sesion.getAprobadas()));
            invalidarResultado();
        }
        return Paso.AVANCE;
    }

    // ------------------------------------------------------------------ paso 2: materias y preferencias

    /** Marcar respeta la seriación y que la materia sea cursable; desmarcar siempre se permite. */
    public Paso marcarDeseada(String clave, boolean marcada) {
        if (oferta == null) return Paso.INICIO;
        Materia m = oferta.materia(clave);
        if (m == null) return Paso.MATERIAS_PREFERENCIAS;
        boolean cambio;
        if (marcada) {
            boolean cursable = filtro.cursables(oferta, sesion.getSemestre(), sesion.getAprobadas()).contains(m);
            List<Materia> falta = validador.faltantes(oferta, m, sesion.getAprobadas());
            if (!cursable) { aviso = "«" + m.getNombre() + "» no se puede cursar en este semestre."; return Paso.MATERIAS_PREFERENCIAS; }
            if (!falta.isEmpty()) { aviso = "«" + m.getNombre() + "» no está disponible: " + motivoSeriacion(falta); return Paso.MATERIAS_PREFERENCIAS; }
            cambio = sesion.getDeseadas().add(clave);
            if (cambio) reordenarDeseadas();
        } else {
            cambio = sesion.getDeseadas().remove(clave);
        }
        if (cambio) invalidarResultado();
        return Paso.MATERIAS_PREFERENCIAS;
    }

    public Paso definirTurno(Turno turno) {
        Turno nuevo = turno == null ? Turno.MANANA : turno;
        if (nuevo != sesion.getTurno()) { sesion.setTurno(nuevo); invalidarResultado(); }
        return Paso.MATERIAS_PREFERENCIAS;
    }

    /** null = ninguno. */
    public Paso definirDiaLibre(DiaSemana dia) {
        if (dia != sesion.getDiaLibre()) { sesion.setDiaLibre(dia); invalidarResultado(); }
        return Paso.MATERIAS_PREFERENCIAS;
    }

    /** Corre el motor. Si hay menos de 2 materias se queda en el paso 2 con un aviso. */
    public Paso generarHorarios() {
        if (oferta == null) return Paso.INICIO;
        if (sesion.getDeseadas().size() < MIN_MATERIAS) {
            aviso = "Selecciona al menos " + MIN_MATERIAS + " materias para generar horarios.";
            return irAPaso(Paso.MATERIAS_PREFERENCIAS);
        }
        MotorCombinatorio.Resultado r = motor.generar(sesion.toAlumno(), oferta);
        ResultadoVM vm = mapper.aResultadoVM(r.getOpciones(), sesion, r.getMotivo());
        sesion.setResultado(vm);
        sesion.setOpcionSeleccionada(0);
        return irAPaso(Paso.RESULTADOS);
    }

    // ------------------------------------------------------------------ paso 3: resultados

    public Paso seleccionarOpcion(int indice) {
        ResultadoVM r = sesion.getResultado();
        if (r == null) return irAPaso(Paso.RESULTADOS);
        if (r.isHayViables()) sesion.setOpcionSeleccionada(Math.max(0, Math.min(indice, r.getOpciones().size() - 1)));
        return irAPaso(Paso.RESULTADOS);
    }

    /**
     * Pide imprimir / guardar como PDF la opción dada. La impresión la hace el navegador (window.print),
     * así que aquí solo se selecciona la opción y se marca la exportación para que la pantalla la dispare.
     */
    public void exportar(int indice, String formato) {
        ResultadoVM r = sesion.getResultado();
        if (r == null || !r.isHayViables()) { aviso = "Primero genera tus horarios."; return; }
        if (!"pdf".equalsIgnoreCase(formato) && !"imprimir".equalsIgnoreCase(formato)) {
            aviso = "Formato de exportación no admitido: " + formato;
            return;
        }
        seleccionarOpcion(indice);
        exportacionPendiente = true;
    }

    // ------------------------------------------------------------------ vistas y datos para las pantallas

    /** Elige y construye la pantalla del paso (la "Vista" que el controlador selecciona). */
    public Vista vistaPara(Paso paso) {
        if (oferta == null) return new PantallaInicio();
        return switch (paso) {
            case INICIO -> new PantallaInicio();
            case AVANCE -> new PantallaAvance(getMateriasAnteriores());
            case MATERIAS_PREFERENCIAS -> new PantallaMateriasPreferencias(getMateriasCursables());
            case RESULTADOS -> {
                ResultadoVM r = sesion.getResultado();
                if (r == null) yield new PantallaMateriasPreferencias(getMateriasCursables());
                yield r.isHayViables() ? new PantallaResultados(r) : new PantallaSinResultados(r);
            }
        };
    }

    /** Materias que pueden declararse aprobadas (todas habilitadas). */
    public List<MateriaVM> getMateriasAnteriores() {
        if (oferta == null) return List.of();
        return filtro.anteriores(oferta, sesion.getSemestre()).stream()
                .map(m -> new MateriaVM(m.getClave(), m.getNombre(), true, null)).toList();
    }

    /** Materias cursables; las que no cumplen seriación salen deshabilitadas con su motivo. */
    public List<MateriaVM> getMateriasCursables() {
        if (oferta == null) return List.of();
        List<MateriaVM> l = new ArrayList<>();
        for (Materia m : filtro.cursables(oferta, sesion.getSemestre(), sesion.getAprobadas())) {
            List<Materia> falta = validador.faltantes(oferta, m, sesion.getAprobadas());
            l.add(new MateriaVM(m.getClave(), m.getNombre(), falta.isEmpty(),
                    falta.isEmpty() ? null : "No disponible: " + motivoSeriacion(falta)));
        }
        return l;
    }

    public SesionPlanificador getSesion() { return sesion; }

    /** Devuelve el aviso pendiente y lo borra (se muestra una sola vez). */
    public String getAviso() { String a = aviso; aviso = null; return a; }

    /** true una sola vez después de exportar(): la pantalla debe abrir el diálogo de impresión. */
    public boolean consumirExportacion() { boolean e = exportacionPendiente; exportacionPendiente = false; return e; }

    // ------------------------------------------------------------------ privados

    private static String motivoSeriacion(List<Materia> falta) {
        return "requiere " + String.join(" y ", falta.stream().map(Materia::getNombre).toList()) + " (seriación).";
    }

    private void invalidarResultado() { sesion.setResultado(null); sesion.setOpcionSeleccionada(0); }

    /** Mantiene las deseadas en el mismo orden en que se listan las materias. */
    private void reordenarDeseadas() {
        Set<String> ordenadas = new LinkedHashSet<>();
        for (Materia m : filtro.cursables(oferta, sesion.getSemestre(), sesion.getAprobadas()))
            if (sesion.getDeseadas().contains(m.getClave())) ordenadas.add(m.getClave());
        sesion.getDeseadas().clear();
        sesion.getDeseadas().addAll(ordenadas);
    }
}

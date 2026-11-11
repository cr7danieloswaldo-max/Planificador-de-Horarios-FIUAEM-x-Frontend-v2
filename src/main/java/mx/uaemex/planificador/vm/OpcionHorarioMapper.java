package mx.uaemex.planificador.vm;

import mx.uaemex.planificador.controlador.SesionPlanificador;
import mx.uaemex.planificador.dominio.*;

import java.time.LocalTime;
import java.util.*;

/** Convierte el resultado del motor (dominio) en ViewModels que las pantallas pueden pintar sin lógica. */
public class OpcionHorarioMapper {
    private static final int COLORES = 6; // c0..c5 en estilos.css
    private final OfertaAcademica oferta;

    public OpcionHorarioMapper(OfertaAcademica oferta) { this.oferta = oferta; }

    public OpcionHorarioVM aVM(OpcionHorario opcion) {
        return aVM(opcion, "Sin cruces · Seriaciones válidas");
    }

    private OpcionHorarioVM aVM(OpcionHorario opcion, String resumen) {
        Map<String, Integer> colores = new HashMap<>(); // clave de materia -> índice de color
        List<BloqueVM> bloques = new ArrayList<>();
        for (Grupo g : opcion.getGrupos()) {
            String clave = g.getMateria().getClave();
            if (!colores.containsKey(clave)) colores.put(clave, colores.size());
            String color = "c" + (colores.get(clave) % COLORES);
            Materia m = oferta.materia(clave) != null ? oferta.materia(clave) : g.getMateria();
            for (Sesion s : g.getSesiones())
                bloques.add(new BloqueVM(m.getNombre(), g.getNumero(), g.getProfesor(),
                        DiaSemana.deIndice(s.getDia()), hora(s.getInicio()), hora(s.getFin()),
                        color, m.getEtiqueta() + " · Gpo " + g.getNumero()));
        }
        return new OpcionHorarioVM("Opción " + opcion.getNumero(), bloques, resumen);
    }

    public ResultadoVM aResultadoVM(List<OpcionHorario> opciones, SesionPlanificador sesion) {
        return aResultadoVM(opciones, sesion, null);
    }

    /** "motivo" es la explicación del motor cuando no hubo opciones (puede ser null). */
    public ResultadoVM aResultadoVM(List<OpcionHorario> opciones, SesionPlanificador sesion, String motivo) {
        String resumen = "Sin cruces · Seriaciones válidas · " + sesion.getTurno().getResumen();
        List<OpcionHorarioVM> vms = opciones.stream().map(o -> aVM(o, resumen)).toList();
        if (vms.isEmpty())
            return new ResultadoVM(vms,
                    motivo != null ? motivo : "Las materias elegidas no pueden combinarse sin cruces.",
                    null, sugerencias(sesion));
        String unica = vms.size() == 1
                ? "Solo se encontró un horario viable con tu selección y tus preferencias." : null;
        return new ResultadoVM(vms, null, unica, List.of());
    }

    private static List<String> sugerencias(SesionPlanificador sesion) {
        List<String> s = new ArrayList<>();
        if (sesion.getTurno() != Turno.SIN_PREFERENCIA) s.add("Cambiar el turno a «Sin preferencia».");
        if (sesion.getDiaLibre() != null)
            s.add("Quitar el " + sesion.getDiaLibre().getNombre().toLowerCase(Locale.ROOT) + " como día libre.");
        s.add("Quitar una de las materias seleccionadas.");
        return s;
    }

    private static LocalTime hora(int minutos) { return LocalTime.of(minutos / 60, minutos % 60); }
}

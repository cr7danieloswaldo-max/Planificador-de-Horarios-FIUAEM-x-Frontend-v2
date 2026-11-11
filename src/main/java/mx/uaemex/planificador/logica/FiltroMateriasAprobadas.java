package mx.uaemex.planificador.logica;

import mx.uaemex.planificador.dominio.Materia;
import mx.uaemex.planificador.dominio.OfertaAcademica;

import java.util.List;
import java.util.Set;

/** Decide qué materias se listan como "ya aprobadas" y cuáles como "cursables" según el semestre. Sin estado. */
public class FiltroMateriasAprobadas {

    /** Materias que pueden declararse aprobadas al reinscribirse a "semestre": las de semestres anteriores. */
    public List<Materia> anteriores(OfertaAcademica oferta, int semestre) {
        return oferta.materias().stream().filter(m -> m.getSemestre() < semestre).toList();
    }

    /** Materias que se ofrecen como cursables: semestre actual y siguiente, menos las ya aprobadas. */
    public List<Materia> cursables(OfertaAcademica oferta, int semestre, Set<String> aprobadas) {
        return oferta.materias().stream()
                .filter(m -> m.getSemestre() == semestre || m.getSemestre() == semestre + 1)
                .filter(m -> !aprobadas.contains(m.getClave()))
                .toList();
    }
}

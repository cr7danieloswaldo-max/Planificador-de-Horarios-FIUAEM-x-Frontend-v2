package mx.uaemex.planificador.logica;

import mx.uaemex.planificador.dominio.Materia;
import mx.uaemex.planificador.dominio.OfertaAcademica;

import java.util.List;
import java.util.Set;

/** Verifica las seriaciones (prerrequisitos) de una materia contra lo que el alumno declaró aprobado. Sin estado. */
public class ValidadorSeriaciones {

    /** Prerrequisitos que todavía no se cumplen (vacío = la materia está habilitada). */
    public List<Materia> faltantes(OfertaAcademica oferta, Materia materia, Set<String> aprobadas) {
        return materia.getSeriacion().stream()
                .filter(clave -> !aprobadas.contains(clave))
                .map(oferta::materia)
                .toList();
    }

    public boolean cumple(OfertaAcademica oferta, Materia materia, Set<String> aprobadas) {
        return faltantes(oferta, materia, aprobadas).isEmpty();
    }
}

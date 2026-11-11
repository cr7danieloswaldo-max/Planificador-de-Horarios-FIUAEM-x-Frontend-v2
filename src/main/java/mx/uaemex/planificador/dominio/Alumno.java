package mx.uaemex.planificador.dominio;

import java.util.*;

/** Lo que el motor necesita saber del alumno (sin login ni número de cuenta). Inmutable. */
public final class Alumno {
    private final int semestre;
    private final Set<String> aprobadas, deseadas; // claves de materia
    private final Turno turno;
    private final DiaSemana diaLibre;               // null = ninguno

    public Alumno(int semestre, Set<String> aprobadas, Set<String> deseadas, Turno turno, DiaSemana diaLibre) {
        this.semestre = semestre;
        this.aprobadas = Collections.unmodifiableSet(new LinkedHashSet<>(aprobadas));
        this.deseadas = Collections.unmodifiableSet(new LinkedHashSet<>(deseadas)); // conserva el orden elegido
        this.turno = turno;
        this.diaLibre = diaLibre;
    }
    public int getSemestre() { return semestre; }
    public Set<String> getAprobadas() { return aprobadas; }
    public Set<String> getDeseadas() { return deseadas; }
    public Turno getTurno() { return turno; }
    public DiaSemana getDiaLibre() { return diaLibre; }
}

package mx.uaemex.planificador.dominio;

import java.util.*;

/** Oferta del periodo: materias (con sus seriaciones) y los grupos abiertos de cada una. Inmutable. */
public final class OfertaAcademica {
    private final Map<String, Materia> materias;
    private final Map<String, List<Grupo>> grupos;

    public OfertaAcademica(Map<String, Materia> materias, Map<String, List<Grupo>> grupos) {
        this.materias = Collections.unmodifiableMap(new LinkedHashMap<>(materias));
        this.grupos = Collections.unmodifiableMap(new HashMap<>(grupos));
    }
    /** null si la clave no existe. */
    public Materia materia(String clave) { return materias.get(clave); }
    public Collection<Materia> materias() { return materias.values(); }
    public List<Grupo> grupos(String clave) { return grupos.getOrDefault(clave, List.of()); }
    /** Nombre legible; si la clave no existe devuelve la propia clave. */
    public String nombre(String clave) { Materia m = materias.get(clave); return m == null ? clave : m.getNombre(); }
}

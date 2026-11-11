package mx.uaemex.planificador.vm;

/** Materia lista para pintarse en un checklist. Si no está habilitada, motivoBloqueo dice por qué. */
public class MateriaVM {
    private final String clave, nombre, motivoBloqueo;
    private final boolean habilitada;

    public MateriaVM(String clave, String nombre, boolean habilitada, String motivoBloqueo) {
        this.clave = clave; this.nombre = nombre; this.habilitada = habilitada; this.motivoBloqueo = motivoBloqueo;
    }
    public String getClave() { return clave; }
    public String getNombre() { return nombre; }
    public boolean isHabilitada() { return habilitada; }
    /** null si está habilitada. Ej.: "No disponible: requiere Cálculo II (seriación)." */
    public String getMotivoBloqueo() { return motivoBloqueo; }
}

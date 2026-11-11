package mx.uaemex.planificador.vm;

import java.util.List;

/** Un horario completo listo para mostrarse (una pestaña "Opción n"). */
public class OpcionHorarioVM {
    private final String etiqueta, resumen;
    private final List<BloqueVM> bloques;

    public OpcionHorarioVM(String etiqueta, List<BloqueVM> bloques, String resumen) {
        this.etiqueta = etiqueta; this.bloques = List.copyOf(bloques); this.resumen = resumen;
    }
    /** "Opción 1". */
    public String getEtiqueta() { return etiqueta; }
    public List<BloqueVM> getBloques() { return bloques; }
    /** "Sin cruces · Seriaciones válidas · Sin preferencia de turno". */
    public String getResumen() { return resumen; }
}

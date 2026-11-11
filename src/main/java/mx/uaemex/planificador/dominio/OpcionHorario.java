package mx.uaemex.planificador.dominio;

import java.util.List;

/** Una combinación de grupos (uno por materia deseada) sin traslapes. */
public final class OpcionHorario {
    private final int numero; // 1, 2, 3...
    private final List<Grupo> grupos;

    public OpcionHorario(int numero, List<Grupo> grupos) { this.numero = numero; this.grupos = List.copyOf(grupos); }
    public int getNumero() { return numero; }
    public List<Grupo> getGrupos() { return grupos; }
}

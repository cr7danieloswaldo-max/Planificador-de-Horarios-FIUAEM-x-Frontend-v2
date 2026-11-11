package mx.uaemex.planificador.vm;

import java.util.List;

/** Todo lo que necesitan las pantallas de resultados: opciones, o el motivo y las sugerencias si no hay. */
public class ResultadoVM {
    private final List<OpcionHorarioVM> opciones;
    private final String mensajeSinResultados, mensajeOpcionUnica;
    private final List<String> sugerencias;

    public ResultadoVM(List<OpcionHorarioVM> opciones, String mensajeSinResultados,
                       String mensajeOpcionUnica, List<String> sugerencias) {
        this.opciones = List.copyOf(opciones);
        this.mensajeSinResultados = mensajeSinResultados;
        this.mensajeOpcionUnica = mensajeOpcionUnica;
        this.sugerencias = List.copyOf(sugerencias);
    }
    public List<OpcionHorarioVM> getOpciones() { return opciones; }
    public boolean isHayViables() { return !opciones.isEmpty(); }
    /** null si hay opciones. */
    public String getMensajeSinResultados() { return mensajeSinResultados; }
    /** null salvo cuando solo existe una opción. */
    public String getMensajeOpcionUnica() { return mensajeOpcionUnica; }
    public List<String> getSugerencias() { return sugerencias; }
}

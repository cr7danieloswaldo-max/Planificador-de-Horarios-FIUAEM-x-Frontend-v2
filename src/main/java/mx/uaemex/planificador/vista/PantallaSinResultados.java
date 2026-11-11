package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.controlador.Paso;
import mx.uaemex.planificador.controlador.SesionPlanificador;
import mx.uaemex.planificador.vm.ResultadoVM;

import java.util.List;

/** Paso 3 cuando no hay horarios viables: explica el motivo y sugiere qué cambiar. */
public class PantallaSinResultados implements Vista {
    private final BarraPasos barraPasos = new BarraPasos();
    private final ResultadoVM resultado;
    private String motivo = "";
    private List<String> sugerencias = List.of();

    public PantallaSinResultados(ResultadoVM resultado) { this.resultado = resultado; }

    @Override public void mostrar(SesionPlanificador sesion) {
        barraPasos.resaltar(Paso.RESULTADOS);
        mostrarMotivo(resultado.getMensajeSinResultados());
        mostrarSugerencias(resultado.getSugerencias());
    }
    public void mostrarMotivo(String msg) { this.motivo = msg; }
    public void mostrarSugerencias(List<String> s) { this.sugerencias = List.copyOf(s); }

    @Override public String getPlantilla() { return "/WEB-INF/vistas/sinResultados.jsp"; }
    @Override public BarraPasos getBarraPasos() { return barraPasos; }

    public String getMotivo() { return motivo; }
    public List<String> getSugerencias() { return sugerencias; }
}

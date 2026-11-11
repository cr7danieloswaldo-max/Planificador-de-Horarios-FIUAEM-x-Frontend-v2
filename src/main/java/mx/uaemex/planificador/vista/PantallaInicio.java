package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.controlador.Paso;
import mx.uaemex.planificador.controlador.SesionPlanificador;

/** Paso 0: bienvenida con el botón "Comenzar". */
public class PantallaInicio implements Vista {
    private final BarraPasos barraPasos = new BarraPasos();

    @Override public void mostrar(SesionPlanificador sesion) { barraPasos.resaltar(Paso.INICIO); }
    @Override public String getPlantilla() { return "/WEB-INF/vistas/inicio.jsp"; }
    @Override public BarraPasos getBarraPasos() { return barraPasos; }
}

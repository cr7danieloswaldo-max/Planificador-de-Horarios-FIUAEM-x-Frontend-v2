package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.controlador.SesionPlanificador;

/**
 * Una pantalla del asistente. El servlet llama a mostrar() para que la pantalla prepare sus componentes
 * a partir de la sesión, y luego reenvía a la plantilla JSP que indica getPlantilla().
 */
public interface Vista {
    void mostrar(SesionPlanificador sesion);
    /** Ruta de la JSP, ej. "/WEB-INF/vistas/avance.jsp". */
    String getPlantilla();
    BarraPasos getBarraPasos();
}

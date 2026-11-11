package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.controlador.Paso;
import mx.uaemex.planificador.controlador.SesionPlanificador;
import mx.uaemex.planificador.vm.OpcionHorarioVM;
import mx.uaemex.planificador.vm.ResultadoVM;

/** Paso 3 «Resultados» con horarios viables: pestañas de opción, calendario y "Tu carga". */
public class PantallaResultados implements Vista {
    private final BarraPasos barraPasos = new BarraPasos();
    private final SelectorOpcion selectorOpcion = new SelectorOpcion();
    private final CalendarioSemanal calendario = new CalendarioSemanal();
    private final PanelCarga panelCarga = new PanelCarga();
    private final ResultadoVM resultado;
    private String resumen = "";
    private String avisoOpcionUnica;

    public PantallaResultados(ResultadoVM resultado) { this.resultado = resultado; }

    @Override public void mostrar(SesionPlanificador sesion) {
        barraPasos.resaltar(Paso.RESULTADOS);
        int ultima = resultado.getOpciones().size() - 1;
        int indice = Math.max(0, Math.min(sesion.getOpcionSeleccionada(), ultima));
        OpcionHorarioVM opcion = resultado.getOpciones().get(indice);
        selectorOpcion.render(resultado.getOpciones(), indice);
        calendario.render(opcion);
        panelCarga.render(opcion);
        resumen = opcion.getResumen();
        if (resultado.getMensajeOpcionUnica() != null) mostrarAvisoOpcionUnica(resultado.getMensajeOpcionUnica());
    }
    public void mostrarAvisoOpcionUnica(String msg) { this.avisoOpcionUnica = msg; }

    @Override public String getPlantilla() { return "/WEB-INF/vistas/resultados.jsp"; }
    @Override public BarraPasos getBarraPasos() { return barraPasos; }

    public SelectorOpcion getSelectorOpcion() { return selectorOpcion; }
    public CalendarioSemanal getCalendario() { return calendario; }
    public PanelCarga getPanelCarga() { return panelCarga; }
    public String getResumen() { return resumen; }
    public String getAvisoOpcionUnica() { return avisoOpcionUnica; }
}

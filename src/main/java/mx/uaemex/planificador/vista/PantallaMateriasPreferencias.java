package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.controlador.Paso;
import mx.uaemex.planificador.controlador.SesionPlanificador;
import mx.uaemex.planificador.vm.MateriaVM;

import java.util.List;

/** Paso 2 «Materias y preferencias»: qué cursar, turno y día libre. */
public class PantallaMateriasPreferencias implements Vista {
    private final BarraPasos barraPasos = new BarraPasos();
    private final ChecklistMaterias listaDeseadas = new ChecklistMaterias("sel");
    private final PanelPreferencias panelPreferencias = new PanelPreferencias();
    private final List<MateriaVM> cursables;
    private int contador;

    public PantallaMateriasPreferencias(List<MateriaVM> materiasCursables) { this.cursables = materiasCursables; }

    @Override public void mostrar(SesionPlanificador sesion) {
        barraPasos.resaltar(Paso.MATERIAS_PREFERENCIAS);
        listaDeseadas.render(cursables);
        listaDeseadas.setMarcadas(sesion.getDeseadas());
        panelPreferencias.render(sesion.getTurno(), sesion.getDiaLibre());
        actualizarContador(sesion.getDeseadas().size());
    }
    /** Valor inicial de "N materias seleccionadas" (en el navegador se actualiza al marcar casillas). */
    public void actualizarContador(int n) { this.contador = n; }

    @Override public String getPlantilla() { return "/WEB-INF/vistas/prefs.jsp"; }
    @Override public BarraPasos getBarraPasos() { return barraPasos; }

    public ChecklistMaterias getListaDeseadas() { return listaDeseadas; }
    public PanelPreferencias getPanelPreferencias() { return panelPreferencias; }
    public int getContador() { return contador; }
}

package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.controlador.Paso;
import mx.uaemex.planificador.controlador.SesionPlanificador;
import mx.uaemex.planificador.vm.MateriaVM;

import java.util.List;

/** Paso 1 «Tu avance»: semestre al que se reinscribe y materias ya aprobadas. */
public class PantallaAvance implements Vista {
    private final BarraPasos barraPasos = new BarraPasos();
    private final SelectorSemestre selectorSemestre = new SelectorSemestre();
    private final ChecklistMaterias listaAprobadas = new ChecklistMaterias("apr");
    private final List<MateriaVM> anteriores;

    public PantallaAvance(List<MateriaVM> materiasAnteriores) { this.anteriores = materiasAnteriores; }

    @Override public void mostrar(SesionPlanificador sesion) {
        barraPasos.resaltar(Paso.AVANCE);
        selectorSemestre.render(sesion.getSemestre());
        listaAprobadas.render(anteriores);
        listaAprobadas.setMarcadas(sesion.getAprobadas());
    }
    @Override public String getPlantilla() { return "/WEB-INF/vistas/avance.jsp"; }
    @Override public BarraPasos getBarraPasos() { return barraPasos; }

    public SelectorSemestre getSelectorSemestre() { return selectorSemestre; }
    public ChecklistMaterias getListaAprobadas() { return listaAprobadas; }
}

package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.vm.MateriaVM;

import java.util.List;
import java.util.Set;

/** Lista de casillas de materias. Las no habilitadas salen en gris con el motivo (seriación). */
public class ChecklistMaterias {
    private final String campo; // nombre del parámetro del formulario ("apr" o "sel")
    private List<MateriaVM> materias = List.of();
    private Set<String> marcadas = Set.of();

    public ChecklistMaterias(String campo) { this.campo = campo; }

    public void render(List<MateriaVM> materias) { this.materias = List.copyOf(materias); }
    public void setMarcadas(Set<String> marcadas) { this.marcadas = Set.copyOf(marcadas); }

    public String getCampo() { return campo; }
    public List<MateriaVM> getMaterias() { return materias; }
    public boolean estaMarcada(String clave) { return marcadas.contains(clave); }
}

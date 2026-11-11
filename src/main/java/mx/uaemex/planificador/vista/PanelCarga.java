package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.vm.BloqueVM;
import mx.uaemex.planificador.vm.OpcionHorarioVM;

import java.util.*;

/** Tarjeta "Tu carga": una línea por materia con su grupo y docente. */
public class PanelCarga {
    public static class Item {
        private final String materia, grupo, docente, color;
        Item(String materia, String grupo, String docente, String color) { this.materia = materia; this.grupo = grupo; this.docente = docente; this.color = color; }
        public String getMateria() { return materia; }
        public String getGrupo() { return grupo; }
        public String getDocente() { return docente; }
        public String getColor() { return color; }
    }

    private List<Item> items = List.of();

    public void render(OpcionHorarioVM opcion) {
        Map<String, Item> porMateria = new LinkedHashMap<>(); // un renglón por materia aunque tenga 2-3 sesiones
        for (BloqueVM b : opcion.getBloques())
            porMateria.putIfAbsent(b.getMateria() + "|" + b.getGrupo(),
                    new Item(b.getMateria(), b.getGrupo(), b.getDocente(), b.getColor()));
        items = List.copyOf(porMateria.values());
    }
    public List<Item> getItems() { return items; }
}

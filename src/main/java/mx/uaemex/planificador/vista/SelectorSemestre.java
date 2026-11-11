package mx.uaemex.planificador.vista;

import java.util.ArrayList;
import java.util.List;

/** Radios "¿A qué semestre te reinscribes?". */
public class SelectorSemestre {
    /** Semestres a los que el prototipo permite reinscribirse. */
    public static final List<Integer> SEMESTRES_DISPONIBLES = List.of(2, 3, 4);

    public static class Opcion {
        private final int valor;
        private final String etiqueta;
        private final boolean seleccionada;
        Opcion(int valor, String etiqueta, boolean seleccionada) { this.valor = valor; this.etiqueta = etiqueta; this.seleccionada = seleccionada; }
        public int getValor() { return valor; }
        public String getEtiqueta() { return etiqueta; }
        public boolean isSeleccionada() { return seleccionada; }
    }

    private List<Opcion> opciones = List.of();

    public void render(int semestreSeleccionado) {
        List<Opcion> l = new ArrayList<>();
        for (int n : SEMESTRES_DISPONIBLES)
            l.add(new Opcion(n, n + (n == 3 ? ".er" : ".º") + " semestre", n == semestreSeleccionado));
        opciones = l;
    }
    public List<Opcion> getOpciones() { return opciones; }
}

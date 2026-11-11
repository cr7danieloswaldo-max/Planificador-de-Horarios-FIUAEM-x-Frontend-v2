package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.vm.OpcionHorarioVM;

import java.util.ArrayList;
import java.util.List;

/** Pestañas "Opción 1 · Opción 2 · …". */
public class SelectorOpcion {
    public static class Pestana {
        private final int indice;
        private final String etiqueta;
        private final boolean activa;
        Pestana(int indice, String etiqueta, boolean activa) { this.indice = indice; this.etiqueta = etiqueta; this.activa = activa; }
        /** Base 0: es el valor de ?opcion= en la URL. */
        public int getIndice() { return indice; }
        public String getEtiqueta() { return etiqueta; }
        public boolean isActiva() { return activa; }
    }

    private List<Pestana> pestanas = List.of();

    public void render(List<OpcionHorarioVM> opciones, int seleccionada) {
        List<Pestana> l = new ArrayList<>();
        for (int i = 0; i < opciones.size(); i++) l.add(new Pestana(i, opciones.get(i).getEtiqueta(), i == seleccionada));
        pestanas = l;
    }
    public List<Pestana> getPestanas() { return pestanas; }
}

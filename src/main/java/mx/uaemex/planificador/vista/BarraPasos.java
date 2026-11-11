package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.controlador.Paso;

import java.util.List;

/** Las tres "pastillas" del encabezado: 1 Tu avance · 2 Materias y preferencias · 3 Resultados. */
public class BarraPasos {

    public static class Etapa {
        private final int numero;
        private final String etiqueta;
        private final Paso paso;
        private boolean activa;

        Etapa(int numero, String etiqueta, Paso paso) { this.numero = numero; this.etiqueta = etiqueta; this.paso = paso; }
        public int getNumero() { return numero; }
        public String getEtiqueta() { return etiqueta; }
        public boolean isActiva() { return activa; }
    }

    private final List<Etapa> etapas = List.of(
            new Etapa(1, "Tu avance", Paso.AVANCE),
            new Etapa(2, "Materias y preferencias", Paso.MATERIAS_PREFERENCIAS),
            new Etapa(3, "Resultados", Paso.RESULTADOS));

    /** Marca como activa la pastilla del paso dado (en INICIO ninguna). */
    public void resaltar(Paso paso) {
        for (Etapa e : etapas) e.activa = e.paso == paso;
    }

    public List<Etapa> getEtapas() { return etapas; }
}

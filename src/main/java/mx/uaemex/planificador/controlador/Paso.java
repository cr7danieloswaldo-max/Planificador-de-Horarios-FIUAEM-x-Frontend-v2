package mx.uaemex.planificador.controlador;

/** Pasos del asistente. El número va en la URL (planificador?paso=N). */
public enum Paso {
    INICIO, AVANCE, MATERIAS_PREFERENCIAS, RESULTADOS;

    public int getIndice() { return ordinal(); }

    public static Paso deIndice(int i, Paso porDefecto) {
        return i >= 0 && i < values().length ? values()[i] : porDefecto;
    }
}

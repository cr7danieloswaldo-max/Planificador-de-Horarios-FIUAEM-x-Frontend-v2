package mx.uaemex.planificador.dominio;

/** Preferencia de turno. Matutino = clases que inician antes de las 14:00. */
public enum Turno {
    MANANA("mat", "Matutino", "Turno matutino"),
    TARDE("ves", "Vespertino", "Turno vespertino"),
    SIN_PREFERENCIA("sin", "Sin preferencia", "Sin preferencia de turno");

    /** Minuto del día a partir del cual una clase se considera vespertina (14:00). */
    public static final int CORTE_MINUTOS = 14 * 60;

    private final String codigo, etiqueta, resumen;

    Turno(String codigo, String etiqueta, String resumen) { this.codigo = codigo; this.etiqueta = etiqueta; this.resumen = resumen; }

    public String getCodigo() { return codigo; }
    public String getEtiqueta() { return etiqueta; }
    /** Texto para la línea "Sin cruces · Seriaciones válidas · …". */
    public String getResumen() { return resumen; }

    /** ¿Una sesión que inicia en este minuto del día respeta el turno? */
    public boolean admite(int inicioMinutos) {
        return switch (this) {
            case MANANA -> inicioMinutos < CORTE_MINUTOS;
            case TARDE -> inicioMinutos >= CORTE_MINUTOS;
            case SIN_PREFERENCIA -> true;
        };
    }

    public static Turno deCodigo(String codigo, Turno porDefecto) {
        if (codigo != null) for (Turno t : values()) if (t.codigo.equals(codigo)) return t;
        return porDefecto;
    }
}

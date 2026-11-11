package mx.uaemex.planificador.dominio;

/** Día de la semana en que puede haber clase (Lun-Sáb). El índice coincide con la columna "dia" de grupos.csv. */
public enum DiaSemana {
    LUN("Lun", "Lunes"), MAR("Mar", "Martes"), MIE("Mié", "Miércoles"),
    JUE("Jue", "Jueves"), VIE("Vie", "Viernes"), SAB("Sáb", "Sábado");

    private final String etiqueta, nombre;

    DiaSemana(String etiqueta, String nombre) { this.etiqueta = etiqueta; this.nombre = nombre; }

    /** Encabezado corto del calendario ("Lun"). */
    public String getEtiqueta() { return etiqueta; }
    /** Nombre completo ("Lunes"). */
    public String getNombre() { return nombre; }
    /** Valor que viaja en los formularios ("VIE"). */
    public String getCodigo() { return name(); }
    /** 0 = Lun ... 5 = Sáb. */
    public int getIndice() { return ordinal(); }

    public static DiaSemana deIndice(int i) {
        if (i < 0 || i >= values().length) throw new IllegalArgumentException("Día fuera de rango: " + i);
        return values()[i];
    }

    /** Devuelve null si el código es nulo, vacío o desconocido (= "sin día libre"). */
    public static DiaSemana deCodigo(String codigo) {
        if (codigo == null) return null;
        for (DiaSemana d : values()) if (d.name().equalsIgnoreCase(codigo.trim())) return d;
        return null;
    }
}

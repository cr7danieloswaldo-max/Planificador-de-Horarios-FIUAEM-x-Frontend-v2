package mx.uaemex.planificador.datos;

/** Error al leer o interpretar un archivo de datos; indica archivo y, si aplica, la línea exacta. */
public class ParserException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String archivo;
    private final int linea; // 0 = no aplica

    public ParserException(String archivo, int linea, String detalle) {
        super(archivo + (linea > 0 ? " línea " + linea : "") + ": " + detalle);
        this.archivo = archivo;
        this.linea = linea;
    }
    public ParserException(String archivo, String detalle) { this(archivo, 0, detalle); }

    public String getArchivo() { return archivo; }
    public int getLinea() { return linea; }
}

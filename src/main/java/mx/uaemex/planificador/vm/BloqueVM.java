package mx.uaemex.planificador.vm;

import mx.uaemex.planificador.dominio.DiaSemana;

import java.time.LocalTime;

/** Una sesión de clase lista para pintarse en el calendario. */
public class BloqueVM {
    private final String materia, grupo, docente, color, etiqueta;
    private final DiaSemana dia;
    private final LocalTime inicio, fin;

    public BloqueVM(String materia, String grupo, String docente, DiaSemana dia,
                    LocalTime inicio, LocalTime fin, String color, String etiqueta) {
        this.materia = materia; this.grupo = grupo; this.docente = docente; this.dia = dia;
        this.inicio = inicio; this.fin = fin; this.color = color; this.etiqueta = etiqueta;
    }
    /** Nombre completo de la materia. */
    public String getMateria() { return materia; }
    public String getGrupo() { return grupo; }
    public String getDocente() { return docente; }
    public DiaSemana getDia() { return dia; }
    public LocalTime getInicio() { return inicio; }
    public LocalTime getFin() { return fin; }
    /** Clase CSS de color (c0...c5); la misma materia conserva el mismo color en todo el horario. */
    public String getColor() { return color; }
    /** Texto del bloque, ej.: "Ec. diferenciales · Gpo 3". */
    public String getEtiqueta() { return etiqueta; }
}

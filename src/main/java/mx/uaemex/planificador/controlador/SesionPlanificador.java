package mx.uaemex.planificador.controlador;

import mx.uaemex.planificador.dominio.Alumno;
import mx.uaemex.planificador.dominio.DiaSemana;
import mx.uaemex.planificador.dominio.Turno;
import mx.uaemex.planificador.vm.ResultadoVM;

import java.util.LinkedHashSet;
import java.util.Set;

/** Estado del asistente para un visitante (vive en su sesión HTTP; sin login ni número de cuenta). Todo por CLAVE de materia. */
public class SesionPlanificador {
    public static final int SEMESTRE_INICIAL = 2;

    private int semestre = SEMESTRE_INICIAL;
    private final Set<String> aprobadas = new LinkedHashSet<>();
    private final Set<String> deseadas = new LinkedHashSet<>();
    private Turno turno = Turno.MANANA;
    private DiaSemana diaLibre;              // null = ninguno
    private ResultadoVM resultado;           // null = aún no se generan horarios
    private Paso pasoActual = Paso.INICIO;
    private int opcionSeleccionada;

    /** Convierte el estado de pantalla en la entrada que entiende el motor. */
    public Alumno toAlumno() { return new Alumno(semestre, aprobadas, deseadas, turno, diaLibre); }

    /** Vuelve a los valores iniciales. */
    public void reiniciar() {
        semestre = SEMESTRE_INICIAL;
        aprobadas.clear();
        deseadas.clear();
        turno = Turno.MANANA;
        diaLibre = null;
        resultado = null;
        pasoActual = Paso.INICIO;
        opcionSeleccionada = 0;
    }

    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    /** Conjunto vivo: el controlador lo modifica directamente. */
    public Set<String> getAprobadas() { return aprobadas; }
    /** Conjunto vivo, en el orden en que se listan las materias. */
    public Set<String> getDeseadas() { return deseadas; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public DiaSemana getDiaLibre() { return diaLibre; }
    public void setDiaLibre(DiaSemana diaLibre) { this.diaLibre = diaLibre; }
    public ResultadoVM getResultado() { return resultado; }
    public void setResultado(ResultadoVM resultado) { this.resultado = resultado; }
    public Paso getPasoActual() { return pasoActual; }
    public void setPasoActual(Paso pasoActual) { this.pasoActual = pasoActual; }
    public int getOpcionSeleccionada() { return opcionSeleccionada; }
    public void setOpcionSeleccionada(int opcionSeleccionada) { this.opcionSeleccionada = opcionSeleccionada; }
}

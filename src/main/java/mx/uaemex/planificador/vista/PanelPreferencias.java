package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.dominio.DiaSemana;
import mx.uaemex.planificador.dominio.Turno;

import java.util.List;

/** Tarjeta "Preferencias de horario": turno (radios) y día libre (casilla). */
public class PanelPreferencias {
    /** Días que se pueden pedir libres (el wireframe solo ofrece el viernes). */
    public static final List<DiaSemana> DIAS_LIBRES = List.of(DiaSemana.VIE);

    private Turno turno = Turno.MANANA;
    private DiaSemana diaLibre;

    public void render(Turno turno, DiaSemana diaLibre) { this.turno = turno; this.diaLibre = diaLibre; }

    public List<Turno> getTurnos() { return List.of(Turno.values()); }
    public List<DiaSemana> getDiasLibres() { return DIAS_LIBRES; }
    public boolean turnoSeleccionado(Turno t) { return t == turno; }
    public boolean diaLibreSeleccionado(DiaSemana d) { return d == diaLibre; }
}

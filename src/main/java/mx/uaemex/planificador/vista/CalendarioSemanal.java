package mx.uaemex.planificador.vista;

import mx.uaemex.planificador.dominio.DiaSemana;
import mx.uaemex.planificador.vm.BloqueVM;
import mx.uaemex.planificador.vm.OpcionHorarioVM;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Rejilla Lun-Sáb × horas. Cada bloque se ubica en la celda de su hora de inicio y se desplaza/estira en
 * píxeles para reflejar minutos reales (1.5 h, 2.5 h, 07:30...). El rango de horas se ajusta al horario.
 */
public class CalendarioSemanal {
    /** Píxeles por hora; debe coincidir con la altura de ".cal td" en estilos.css. */
    public static final int ALTO_FILA = 32;
    private static final int HORA_INICIO_MINIMA = 7, HORA_FIN_MINIMA = 17; // rango mínimo visible (como en el boceto)

    /** Bloque ya posicionado dentro de su celda. */
    public static class BloqueUbicado {
        private final BloqueVM bloque;
        private final int top, alto;
        BloqueUbicado(BloqueVM bloque, int top, int alto) { this.bloque = bloque; this.top = top; this.alto = alto; }
        public BloqueVM getBloque() { return bloque; }
        public int getTop() { return top; }
        public int getAlto() { return alto; }
    }

    private LocalTime horaInicio = LocalTime.of(HORA_INICIO_MINIMA, 0);
    private LocalTime horaFin = LocalTime.of(HORA_FIN_MINIMA, 0); // inicio de la última fila visible
    private Map<String, List<BloqueUbicado>> celdas = Map.of();

    public void render(OpcionHorarioVM opcion) {
        int hi = HORA_INICIO_MINIMA, hf = HORA_FIN_MINIMA;
        Map<String, List<BloqueUbicado>> mapa = new HashMap<>();
        for (BloqueVM b : opcion.getBloques()) {
            int ini = b.getInicio().getHour() * 60 + b.getInicio().getMinute();
            int fin = b.getFin().getHour() * 60 + b.getFin().getMinute();
            int hora = ini / 60;
            int top = 1 + (ini % 60) * ALTO_FILA / 60;
            int alto = (fin - ini) * ALTO_FILA / 60 - 2;
            mapa.computeIfAbsent(clave(b.getDia(), hora), k -> new ArrayList<>()).add(new BloqueUbicado(b, top, alto));
            hi = Math.min(hi, hora);
            hf = Math.max(hf, (fin - 1) / 60); // última fila que ocupa el bloque
        }
        celdas = mapa;
        horaInicio = LocalTime.of(hi, 0);
        horaFin = LocalTime.of(hf, 0);
    }

    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    /** Filas visibles (7, 8, ... 18). */
    public List<Integer> getHoras() { return IntStream.rangeClosed(horaInicio.getHour(), horaFin.getHour()).boxed().toList(); }
    public List<DiaSemana> getDias() { return List.of(DiaSemana.values()); }
    /** Bloques que INICIAN en esa celda (día, hora). */
    public List<BloqueUbicado> bloquesEn(DiaSemana dia, int hora) { return celdas.getOrDefault(clave(dia, hora), List.of()); }

    private static String clave(DiaSemana dia, int hora) { return dia.getIndice() + "-" + hora; }
}

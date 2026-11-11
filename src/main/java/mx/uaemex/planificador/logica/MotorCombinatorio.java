package mx.uaemex.planificador.logica;

import mx.uaemex.planificador.dominio.*;

import java.util.*;

/**
 * Busca combinaciones de grupos (uno por materia deseada) sin traslapes, respetando turno y día libre.
 * Busca primero por las materias con menos grupos (poda más rápido) y tiene un tope de nodos de seguridad.
 */
public class MotorCombinatorio {
    public static final int MAX_OPCIONES = 4;
    private static final int MAX_NODOS = 2_000_000;

    /** Opciones encontradas o, si no hay ninguna, el motivo en lenguaje natural. */
    public static final class Resultado {
        private final List<OpcionHorario> opciones;
        private final String motivo;
        Resultado(List<OpcionHorario> opciones, String motivo) { this.opciones = opciones; this.motivo = motivo; }
        public List<OpcionHorario> getOpciones() { return opciones; }
        /** null cuando sí hay opciones. */
        public String getMotivo() { return motivo; }
        public boolean isVacio() { return opciones.isEmpty(); }
    }

    public Resultado generar(Alumno alumno, OfertaAcademica oferta) {
        List<String> orden = new ArrayList<>(alumno.getDeseadas());
        Map<String, List<Grupo>> candidatos = new LinkedHashMap<>();
        for (String clave : orden) {
            List<Grupo> l = oferta.grupos(clave).stream().filter(g -> permitido(g, alumno)).toList();
            if (l.isEmpty())
                return sin("«" + oferta.nombre(clave) + "» no tiene grupos con tus preferencias de horario.");
            candidatos.put(clave, l);
        }
        List<String> materias = new ArrayList<>(orden);
        materias.sort(Comparator.comparingInt(c -> candidatos.get(c).size()));

        List<List<Grupo>> encontradas = new ArrayList<>();
        int[] nodos = {0};
        buscar(0, materias, candidatos, new ArrayList<>(), encontradas, nodos);

        if (!encontradas.isEmpty()) {
            List<OpcionHorario> opciones = new ArrayList<>();
            for (List<Grupo> g : encontradas) {
                g.sort(Comparator.comparingInt(x -> orden.indexOf(x.getMateria().getClave())));
                opciones.add(new OpcionHorario(opciones.size() + 1, g));
            }
            return new Resultado(opciones, null);
        }
        if (nodos[0] >= MAX_NODOS)
            return sin("La combinación es demasiado grande para revisarla completa. Prueba con menos materias o con otro turno.");
        return sin(explicarCruce(orden, candidatos, oferta, alumno.getTurno()));
    }

    private static Resultado sin(String motivo) { return new Resultado(List.of(), motivo); }

    private static boolean permitido(Grupo g, Alumno a) {
        if (a.getDiaLibre() != null && g.usaDia(a.getDiaLibre())) return false;
        return g.getSesiones().stream().allMatch(s -> a.getTurno().admite(s.getInicio()));
    }

    /** Busca un par de materias que choquen en todos sus grupos para dar un motivo concreto. */
    private static String explicarCruce(List<String> orden, Map<String, List<Grupo>> cand, OfertaAcademica oferta, Turno turno) {
        String complemento = switch (turno) {
            case MANANA -> " del turno matutino";
            case TARDE -> " del turno vespertino";
            case SIN_PREFERENCIA -> "";
        };
        for (int i = 0; i < orden.size(); i++)
            for (int j = i + 1; j < orden.size(); j++) {
                boolean posible = false;
                for (Grupo a : cand.get(orden.get(i))) for (Grupo b : cand.get(orden.get(j))) if (!a.choca(b)) posible = true;
                if (!posible)
                    return oferta.nombre(orden.get(i)) + " y " + oferta.nombre(orden.get(j))
                         + " coinciden en todos los grupos" + complemento + ". El sistema no fuerza combinaciones con cruces.";
            }
        return "Las materias elegidas no pueden combinarse sin cruces.";
    }

    private static void buscar(int i, List<String> mats, Map<String, List<Grupo>> cand, List<Grupo> cur,
                               List<List<Grupo>> out, int[] nodos) {
        if (out.size() >= MAX_OPCIONES || nodos[0] >= MAX_NODOS) return;
        if (i == mats.size()) { out.add(new ArrayList<>(cur)); return; }
        for (Grupo g : cand.get(mats.get(i))) {
            nodos[0]++;
            if (cur.stream().noneMatch(c -> c.choca(g))) {
                cur.add(g); buscar(i + 1, mats, cand, cur, out, nodos); cur.remove(cur.size() - 1);
            }
        }
    }
}

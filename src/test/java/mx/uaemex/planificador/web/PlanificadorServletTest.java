package mx.uaemex.planificador.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.uaemex.planificador.vista.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Prueba el servlet sin contenedor: petición/respuesta/sesión simuladas con proxies. */
class PlanificadorServletTest {

    /** Navegador mínimo: conserva la sesión entre peticiones. */
    private static class Navegador {
        final PlanificadorServlet servlet = new PlanificadorServlet();
        final Map<String, Object> sesion = new HashMap<>();
        String redireccion, plantilla;
        Map<String, Object> atributos;

        HttpSession sesionProxy() {
            return (HttpSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{HttpSession.class}, (p, m, a) -> switch (m.getName()) {
                case "getId" -> "sesion-prueba";
                case "getAttribute" -> sesion.get((String) a[0]);
                case "setAttribute" -> sesion.put((String) a[0], a[1]);
                default -> null;
            });
        }

        void peticion(boolean post, Map<String, String[]> params) throws Exception {
            redireccion = null; plantilla = null; atributos = new HashMap<>();
            HttpServletResponse resp = (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class}, (p, m, a) -> { if (m.getName().equals("sendRedirect")) redireccion = (String) a[0]; return null; });
            HttpServletRequest req = (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class}, (p, m, a) -> switch (m.getName()) {
                        case "getParameter" -> params.containsKey(a[0]) ? params.get(a[0])[0] : null;
                        case "getParameterValues" -> params.get(a[0]);
                        case "getSession" -> sesionProxy();
                        case "getContextPath" -> "";
                        case "setAttribute" -> { atributos.put((String) a[0], a[1]); yield null; }
                        case "getRequestDispatcher" -> {
                            plantilla = (String) a[0];
                            yield Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{RequestDispatcher.class}, (p2, m2, a2) -> null);
                        }
                        default -> null;
                    });
            if (post) servlet.doPost(req, resp); else servlet.doGet(req, resp);
        }
        void get(String... kv) throws Exception { peticion(false, mapa(kv)); }
        void post(String... kv) throws Exception { peticion(true, mapa(kv)); }

        static Map<String, String[]> mapa(String... kv) {
            Map<String, List<String>> tmp = new LinkedHashMap<>();
            for (int i = 0; i < kv.length; i += 2) tmp.computeIfAbsent(kv[i], k -> new ArrayList<>()).add(kv[i + 1]);
            Map<String, String[]> m = new HashMap<>();
            tmp.forEach((k, v) -> m.put(k, v.toArray(new String[0])));
            return m;
        }
    }

    @Test void recorridoCompletoComoEnLasCapturas() throws Exception {
        Navegador n = new Navegador();

        n.get();                                                    // /planificador
        assertEquals("/WEB-INF/vistas/inicio.jsp", n.plantilla);
        assertInstanceOf(PantallaInicio.class, n.atributos.get("pantalla"));

        n.get("paso", "1");                                         // Comenzar
        assertEquals("/WEB-INF/vistas/avance.jsp", n.plantilla);

        n.post("accion", "avance", "semestre", "2",                 // Siguiente
               "apr", "LINC02", "apr", "LINC12", "apr", "LINC03", "apr", "LINC15", "apr", "LINC11");
        assertEquals("/planificador?paso=2", n.redireccion);        // POST/Redirect/GET
        n.get("paso", "2");
        assertEquals("/WEB-INF/vistas/prefs.jsp", n.plantilla);

        n.post("accion", "generar", "turno", "sin",
               "sel", "LINC10", "sel", "LINC07", "sel", "LINC01", "sel", "LINC35", "sel", "LINC29", "sel", "LINC04", "sel", "LMU209");
        assertEquals("/planificador?paso=3", n.redireccion);
        n.get("paso", "3");
        assertEquals("/WEB-INF/vistas/resultados.jsp", n.plantilla);
        PantallaResultados pr = (PantallaResultados) n.atributos.get("pantalla");
        assertEquals(4, pr.getSelectorOpcion().getPestanas().size());
        assertTrue(pr.getSelectorOpcion().getPestanas().get(0).isActiva());

        n.get("paso", "3", "opcion", "2");                          // pestaña "Opción 3"
        pr = (PantallaResultados) n.atributos.get("pantalla");
        assertTrue(pr.getSelectorOpcion().getPestanas().get(2).isActiva());

        n.get("paso", "3", "opcion", "2", "exportar", "pdf");       // abre el diálogo de impresión
        assertEquals(Boolean.TRUE, n.atributos.get("autoImprimir"));
        n.get("paso", "3", "opcion", "2");
        assertEquals(Boolean.FALSE, n.atributos.get("autoImprimir"));
    }

    @Test void pocasMateriasRegresanAlPasoDosConAvisoUnaSolaVez() throws Exception {
        Navegador n = new Navegador();
        n.post("accion", "generar", "turno", "ves", "sel", "LINC10");
        assertEquals("/planificador?paso=2", n.redireccion);
        n.get("paso", "2");
        assertEquals("Selecciona al menos 2 materias para generar horarios.", n.atributos.get("aviso"));
        n.get("paso", "2");
        assertNull(n.atributos.get("aviso"));
    }

    @Test void sinHorariosUsaLaPantallaDeSinResultados() throws Exception {
        Navegador n = new Navegador();
        n.post("accion", "generar", "turno", "ves", "sel", "LINC10", "sel", "LINC01");
        n.get("paso", "3");
        assertEquals("/WEB-INF/vistas/sinResultados.jsp", n.plantilla);
        assertInstanceOf(PantallaSinResultados.class, n.atributos.get("pantalla"));
    }

    @Test void pasoTresSinResultadosVuelveAlPasoDos() throws Exception {
        Navegador n = new Navegador();
        n.get("paso", "3");
        assertEquals("/WEB-INF/vistas/prefs.jsp", n.plantilla);
    }

    @Test void clavesInventadasEnElFormularioSeIgnoran() throws Exception {
        Navegador n = new Navegador();
        n.post("accion", "generar", "turno", "sin", "sel", "NOEXISTE", "sel", "LINC05", "sel", "LINC10", "sel", "LINC01");
        n.get("paso", "3");
        PantallaResultados pr = (PantallaResultados) n.atributos.get("pantalla"); // LINC05 (Cálculo III) se rechaza por seriación
        assertEquals(2, pr.getPanelCarga().getItems().size());
    }

    @Test void radioDeSemestreVuelveAlPasoUno() throws Exception {
        Navegador n = new Navegador();
        n.post("accion", "semestre", "semestre", "3");
        assertEquals("/planificador?paso=1", n.redireccion);
        n.get("paso", "1");
        PantallaAvance pa = (PantallaAvance) n.atributos.get("pantalla");
        assertTrue(pa.getSelectorSemestre().getOpciones().get(1).isSeleccionada());
        assertEquals(12, pa.getListaAprobadas().getMaterias().size()); // 5 de 1.º + 7 de 2.º
    }
}

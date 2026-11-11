package mx.uaemex.planificador.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mx.uaemex.planificador.controlador.Paso;
import mx.uaemex.planificador.controlador.PlanificadorController;
import mx.uaemex.planificador.datos.CsvOfertaReader;
import mx.uaemex.planificador.datos.OfertaReader;
import mx.uaemex.planificador.dominio.DiaSemana;
import mx.uaemex.planificador.dominio.Turno;
import mx.uaemex.planificador.vista.Vista;
import mx.uaemex.planificador.vm.MateriaVM;

import java.io.IOException;
import java.util.*;

/**
 * Adaptador delgado entre HTTP y el PlanificadorController (un controlador por sesión HTTP).
 *   GET  /planificador?paso=N[&opcion=K][&exportar=pdf]  -> muestra un paso
 *   POST /planificador  (accion = semestre | avance | generar)  -> aplica el formulario y REDIRIGE al paso resultante
 * Se redirige después de cada POST (patrón Post/Redirect/Get) para que recargar la página no reenvíe el formulario.
 */
@WebServlet("/planificador")
public class PlanificadorServlet extends HttpServlet {
    private static final String ATR_CONTROLADOR = "planificadorController";
    private final OfertaReader reader = new CsvOfertaReader(); // lee los CSV una sola vez para toda la aplicación

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PlanificadorController c = controlador(req);
        synchronized (c) {
            if (req.getParameter("reiniciar") != null) c.iniciar();
            Paso paso = c.irAPaso(Paso.deIndice(entero(req.getParameter("paso"), 0), Paso.INICIO));
            boolean imprimir = false;
            if (paso == Paso.RESULTADOS) {
                int opcion = entero(req.getParameter("opcion"), c.getSesion().getOpcionSeleccionada());
                paso = c.seleccionarOpcion(opcion);
                String formato = req.getParameter("exportar");
                if (formato != null) { c.exportar(opcion, formato); imprimir = c.consumirExportacion(); }
            }
            mostrar(req, resp, c, paso, imprimir);
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        PlanificadorController c = controlador(req);
        synchronized (c) {
            String accion = Optional.ofNullable(req.getParameter("accion")).orElse("");
            Paso destino;
            switch (accion) {
                case "semestre" -> { aplicarAvance(req, c); destino = Paso.AVANCE; }   // el radio de semestre se envía solo
                case "avance" -> { aplicarAvance(req, c); destino = c.irAPaso(Paso.MATERIAS_PREFERENCIAS); }
                case "generar" -> { aplicarPreferencias(req, c); destino = c.generarHorarios(); }
                default -> destino = c.irAPaso(Paso.INICIO);
            }
            resp.sendRedirect(req.getContextPath() + "/planificador?paso=" + destino.getIndice());
        }
    }

    // El formulario envía CLAVES de materia (no nombres); se recorre la lista conocida, así que valores inventados se ignoran.
    private void aplicarAvance(HttpServletRequest req, PlanificadorController c) {
        c.seleccionarSemestre(entero(req.getParameter("semestre"), c.getSesion().getSemestre()));
        Set<String> marcadas = valores(req, "apr");
        for (MateriaVM m : c.getMateriasAnteriores()) c.marcarAprobada(m.getClave(), marcadas.contains(m.getClave()));
    }

    private void aplicarPreferencias(HttpServletRequest req, PlanificadorController c) {
        Set<String> marcadas = valores(req, "sel");
        for (MateriaVM m : c.getMateriasCursables()) c.marcarDeseada(m.getClave(), marcadas.contains(m.getClave()));
        c.definirTurno(Turno.deCodigo(req.getParameter("turno"), Turno.MANANA));
        c.definirDiaLibre(DiaSemana.deCodigo(req.getParameter("diaLibre")));
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse resp, PlanificadorController c,
                         Paso paso, boolean imprimir) throws ServletException, IOException {
        Vista vista = c.vistaPara(paso);
        vista.mostrar(c.getSesion());
        req.setAttribute("pantalla", vista);
        req.setAttribute("barraPasos", vista.getBarraPasos());
        req.setAttribute("aviso", c.getAviso());
        req.setAttribute("autoImprimir", imprimir);
        req.getRequestDispatcher(vista.getPlantilla()).forward(req, resp);
    }

    private PlanificadorController controlador(HttpServletRequest req) {
        HttpSession s = req.getSession();
        synchronized (s.getId().intern()) {
            PlanificadorController c = (PlanificadorController) s.getAttribute(ATR_CONTROLADOR);
            if (c == null) { c = new PlanificadorController(reader); s.setAttribute(ATR_CONTROLADOR, c); }
            return c;
        }
    }

    private static int entero(String s, int def) { try { return Integer.parseInt(s); } catch (Exception ex) { return def; } }

    private static Set<String> valores(HttpServletRequest req, String nombre) {
        String[] v = req.getParameterValues(nombre);
        return v == null ? Set.of() : new HashSet<>(Arrays.asList(v));
    }
}

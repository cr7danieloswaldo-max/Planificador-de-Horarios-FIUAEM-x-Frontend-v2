package mx.uaemex.planificador.datos;

import mx.uaemex.planificador.dominio.Grupo;
import mx.uaemex.planificador.dominio.OfertaAcademica;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvOfertaReaderTest {
    @Test void cargaLosTresCsv() throws ParserException {
        OfertaAcademica o = new CsvOfertaReader().leer();
        assertEquals("Cálculo II", o.materia("LINC04").getNombre());
        assertEquals(2, o.materia("LINC04").getSeriacion().size());   // Cálculo I y Geometría analítica
        assertFalse(o.grupos("LINC04").isEmpty());
        Grupo g = o.grupos("LINC11").get(0);                          // Física CO01: dos sesiones
        assertEquals(2, g.getSesiones().size());
        assertEquals(9 * 60, g.getSesiones().get(0).getInicio());
    }

    @Test void leeUnaSolaVez() throws ParserException {
        CsvOfertaReader r = new CsvOfertaReader();
        assertSame(r.leer(), r.leer());
    }

    @Test void elErrorIndicaArchivoYLinea() {
        ParserException e = new ParserException("grupos.csv", 12, "hora inválida");
        assertEquals("grupos.csv línea 12: hora inválida", e.getMessage());
        assertEquals(12, e.getLinea());
    }
}

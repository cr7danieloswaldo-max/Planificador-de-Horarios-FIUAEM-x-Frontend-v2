package mx.uaemex.planificador.datos;

import mx.uaemex.planificador.dominio.OfertaAcademica;

/** Origen de la oferta académica. Hoy son CSV; mañana podría ser una base de datos o un servicio. */
public interface OfertaReader {
    OfertaAcademica leer() throws ParserException;
}

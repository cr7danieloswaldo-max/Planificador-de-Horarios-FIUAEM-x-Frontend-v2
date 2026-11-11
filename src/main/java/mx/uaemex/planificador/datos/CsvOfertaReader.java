package mx.uaemex.planificador.datos;

import mx.uaemex.planificador.dominio.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Lee la oferta desde tres CSV (UTF-8) de src/main/resources/datos/:
 *   materias.csv     clave,nombre,semestre,etiqueta
 *   seriaciones.csv  clave,prerrequisito
 *   grupos.csv       clave,gpo,profesor,dia,ini,fin,aula   (una fila por sesión semanal; dia 0=Lun ... 5=Sáb)
 * Para cambiar la oferta basta con reemplazar esos archivos; no hay que tocar código.
 * Cualquier error se reporta como ParserException con archivo y línea. La oferta se lee una sola vez.
 */
public class CsvOfertaReader implements OfertaReader {
    private static final String RUTA = "/datos/";
    private OfertaAcademica cache;

    @Override public synchronized OfertaAcademica leer() throws ParserException {
        if (cache == null) cache = cargar(); // si falla no se cachea: el siguiente intento vuelve a leer
        return cache;
    }

    private OfertaAcademica cargar() throws ParserException {
        List<Fila> seriaciones = leerCsv("seriaciones.csv", 2);
        Map<String, List<String>> prerreq = new HashMap<>();
        for (Fila f : seriaciones) prerreq.computeIfAbsent(f.c(0), k -> new ArrayList<>()).add(f.c(1));

        Map<String, Materia> materias = new LinkedHashMap<>();
        for (Fila f : leerCsv("materias.csv", 4)) {
            if (materias.containsKey(f.c(0))) throw f.error("clave repetida " + f.c(0));
            materias.put(f.c(0), new Materia(f.c(0), f.c(1), entero(f, 2), f.c(3),
                    List.copyOf(prerreq.getOrDefault(f.c(0), List.of()))));
        }
        for (Fila f : seriaciones) {
            if (!materias.containsKey(f.c(0))) throw f.error("clave desconocida " + f.c(0));
            if (!materias.containsKey(f.c(1))) throw f.error("prerrequisito desconocido " + f.c(1));
        }

        Map<String, List<Sesion>> sesiones = new LinkedHashMap<>(); // "clave|gpo" -> sesiones
        Map<String, String[]> cabecera = new HashMap<>();           // "clave|gpo" -> {clave, gpo, profesor}
        for (Fila f : leerCsv("grupos.csv", 7)) {
            if (!materias.containsKey(f.c(0))) throw f.error("clave desconocida " + f.c(0));
            int dia = entero(f, 3);
            if (dia < 0 || dia > 5) throw f.error("día fuera de rango (0-5): " + dia);
            int ini = minutos(f, 4), fin = minutos(f, 5);
            if (fin <= ini) throw f.error("la hora de fin debe ser posterior a la de inicio");
            String k = f.c(0) + "|" + f.c(1);
            cabecera.putIfAbsent(k, new String[]{f.c(0), f.c(1), f.c(2)});
            sesiones.computeIfAbsent(k, x -> new ArrayList<>()).add(new Sesion(dia, ini, fin));
        }
        Map<String, List<Grupo>> grupos = new HashMap<>();
        for (var e : sesiones.entrySet()) {
            String[] c = cabecera.get(e.getKey());
            grupos.computeIfAbsent(c[0], k -> new ArrayList<>())
                  .add(new Grupo(materias.get(c[0]), c[1], c[2], List.copyOf(e.getValue())));
        }
        return new OfertaAcademica(materias, grupos);
    }

    // ---------- utilidades de lectura ----------

    private record Fila(String archivo, int linea, String[] campos) {
        String c(int i) { return campos[i]; }
        ParserException error(String detalle) { return new ParserException(archivo, linea, detalle); }
    }

    private static int entero(Fila f, int i) throws ParserException {
        try { return Integer.parseInt(f.c(i)); }
        catch (NumberFormatException ex) { throw f.error("se esperaba un número entero y dice \"" + f.c(i) + "\""); }
    }

    /** "07:30" -> 450. */
    private static int minutos(Fila f, int i) throws ParserException {
        String[] p = f.c(i).split(":");
        try {
            if (p.length != 2) throw new NumberFormatException();
            int h = Integer.parseInt(p[0].trim()), m = Integer.parseInt(p[1].trim());
            if (h < 0 || h > 23 || m < 0 || m > 59) throw new NumberFormatException();
            return h * 60 + m;
        } catch (NumberFormatException ex) {
            throw f.error("hora inválida \"" + f.c(i) + "\" (formato HH:MM)");
        }
    }

    /** Lee un CSV del classpath; omite vacías, comentarios (#) y el encabezado. */
    private static List<Fila> leerCsv(String archivo, int columnas) throws ParserException {
        List<Fila> filas = new ArrayList<>();
        try (InputStream in = CsvOfertaReader.class.getResourceAsStream(RUTA + archivo)) {
            if (in == null) throw new ParserException(archivo, "no se encontró " + RUTA + archivo + " en el classpath");
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String linea; int n = 0; boolean encabezado = true;
            while ((linea = br.readLine()) != null) {
                n++;
                if (n == 1 && linea.startsWith("\uFEFF")) linea = linea.substring(1);
                if (linea.isBlank() || linea.startsWith("#")) continue;
                if (encabezado) { encabezado = false; continue; }
                String[] f = linea.split(",", -1);
                if (f.length != columnas)
                    throw new ParserException(archivo, n, "se esperaban " + columnas + " columnas y hay " + f.length);
                for (int j = 0; j < f.length; j++) f[j] = f[j].trim();
                filas.add(new Fila(archivo, n, f));
            }
        } catch (IOException ex) {
            throw new ParserException(archivo, "no se pudo leer: " + ex.getMessage());
        }
        return filas;
    }
}

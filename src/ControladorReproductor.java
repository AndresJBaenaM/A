import java.util.ArrayList;
import java.util.List;

/**
 * Controlador: contiene TODA la lógica de negocio del reproductor
 * (qué estructura de datos usar según el modo, cómo agregar/eliminar/editar
 * canciones, cómo avanzar o retroceder).
 *
 * La GUI (ReproductorGUI) solo se encarga de pintar cosas en pantalla y
 * llamar a estos métodos; no conoce nada sobre ListaCircularDoble,
 * ColaSimple ni ArbolBinarioBusqueda. Esta separación es justo lo que
 * pide el enunciado: "separación entre lógica y presentación".
 */
public class ControladorReproductor {

    public static final int MODO_ALEATORIO = 0;
    public static final int MODO_COLA = 1;
    public static final int MODO_ALFABETICO = 2;

    // Fuente única de verdad con TODAS las canciones que existen,
    // independientemente de la estructura que se esté usando para recorrerlas.
    private final List<Cancion> bibliotecaMaestra = new ArrayList<>();

    private ModoReproduccion modoActivo;
    private int modoActual;

    public ControladorReproductor() {
        cambiarModo(MODO_ALEATORIO);
    }

    /**
     * Reconstruye la estructura de datos activa desde cero a partir de la
     * biblioteca maestra. Esto es justo lo que pide el enunciado: cambiar
     * de modo cambia la estructura de datos usada internamente.
     */
    public void cambiarModo(int nuevoModo) {
        if (nuevoModo == MODO_ALEATORIO) {
            modoActivo = new ListaCircularDoble();
        } else if (nuevoModo == MODO_COLA) {
            modoActivo = new ColaSimple();
        } else if (nuevoModo == MODO_ALFABETICO) {
            modoActivo = new ArbolBinarioBusqueda();
        } else {
            throw new IllegalArgumentException("Modo inválido: " + nuevoModo);
        }

        this.modoActual = nuevoModo;
        for (Cancion c : bibliotecaMaestra) {
            modoActivo.agregarCancion(c);
        }
    }

    public int getModoActual() {
        return modoActual;
    }

    public boolean permiteAnterior() {
        return modoActual != MODO_COLA;
    }

    public void agregarCancion(Cancion cancion) {
        bibliotecaMaestra.add(cancion);
        modoActivo.agregarCancion(cancion);
    }

    public void eliminarCancion(Cancion cancion) {
        bibliotecaMaestra.remove(cancion);
        modoActivo.eliminarCancion(cancion);
    }

    /**
     * Edita una canción existente EN SITIO (misma referencia de objeto),
     * así los demás modos no necesitan enterarse del cambio.
     *
     * La única excepción es el Árbol Binario de Búsqueda: si el nombre
     * cambia, la posición de la canción dentro del árbol ya no sería
     * válida (el árbol está ordenado por nombre), así que en ese caso
     * puntual hay que sacarla del árbol y volver a insertarla.
     */
    public void editarCancion(Cancion cancion, String nombre, String artista, String album,
                               int duracionSegundos, String genero, int anio, int calificacion,
                               String rutaImagen) {
        boolean nombreCambio = !cancion.getNombre().equalsIgnoreCase(nombre);
        boolean modoEsArbol = modoActivo instanceof ArbolBinarioBusqueda;

        if (nombreCambio && modoEsArbol) {
            modoActivo.eliminarCancion(cancion);
        }

        cancion.setNombre(nombre);
        cancion.setArtista(artista);
        cancion.setAlbum(album);
        cancion.setDuracionSegundos(duracionSegundos);
        cancion.setGenero(genero);
        cancion.setAnioLanzamiento(anio);
        cancion.setCalificacion(calificacion);
        cancion.setRutaImagen(rutaImagen);

        if (nombreCambio && modoEsArbol) {
            modoActivo.agregarCancion(cancion);
        }
    }

    public Cancion siguiente() {
        return modoActivo.siguiente();
    }

    public Cancion anterior() {
        return modoActivo.anterior();
    }

    public Cancion obtenerActual() {
        return modoActivo.obtenerActual();
    }

    public List<Cancion> obtenerTodas() {
        return modoActivo.obtenerTodas();
    }

    public int cantidadCanciones() {
        return modoActivo.cantidadCanciones();
    }

    /**
     * Busca la primera canción (dentro del modo activo) cuyo nombre
     * contenga la consulta, sin importar mayúsculas/minúsculas.
     */
    public Cancion buscarPorNombre(String consulta) {
        String q = consulta.toLowerCase();
        for (Cancion c : modoActivo.obtenerTodas()) {
            if (c.getNombre().toLowerCase().contains(q)) {
                return c;
            }
        }
        return null;
    }
}

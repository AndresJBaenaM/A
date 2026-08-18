import java.util.List;

/**
 * Interfaz que define el contrato que debe cumplir cualquier modo de
 * reproducción (Aleatorio, Orden de llegada, Alfabético).
 */
public interface ModoReproduccion {

    void agregarCancion(Cancion cancion);

    boolean eliminarCancion(Cancion cancion);

    Cancion siguiente();

    Cancion anterior(); // en modos donde no aplica (ej. Cola), puede lanzar UnsupportedOperationException

    Cancion obtenerActual();

    List<Cancion> obtenerTodas();

    int cantidadCanciones();
}

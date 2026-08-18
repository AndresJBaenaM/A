import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de datos para el Modo 2: Reproducción por orden de llegada.
 *
 * Es una Cola Simple que respeta FIFO (First In, First Out):
 * la primera canción agregada es la primera en reproducirse,
 * y una vez reproducida, desaparece de la cola para siempre.
 */
public class ColaSimple implements ModoReproduccion {

    // "frente" es la canción que se está reproduciendo actualmente
    // (la próxima en salir de la cola).
    private NodoSimple frente;

    // "fin" es el último nodo, lo guardamos aparte para poder
    // agregar canciones nuevas en O(1) sin recorrer toda la cola.
    private NodoSimple fin;

    private int tamanio;

    public ColaSimple() {
        this.frente = null;
        this.fin = null;
        this.tamanio = 0;
    }

    /**
     * Encolar: la nueva canción siempre se agrega al final.
     */
    @Override
    public void agregarCancion(Cancion cancion) {
        NodoSimple nuevoNodo = new NodoSimple(cancion);

        if (frente == null) {
            // Cola vacía: el nuevo nodo es a la vez frente y fin.
            frente = nuevoNodo;
            fin = nuevoNodo;
        } else {
            // Enganchamos el nuevo nodo después del actual "fin"
            // y lo convertimos en el nuevo "fin".
            fin.setSiguiente(nuevoNodo);
            fin = nuevoNodo;
        }
        tamanio++;
    }

    /**
     * Elimina una canción específica de la cola (no necesariamente el frente).
     * Útil para el botón "Eliminar" de la GUI: el usuario puede querer
     * quitar una canción que todavía no le ha tocado sonar.
     */
    @Override
    public boolean eliminarCancion(Cancion cancion) {
        NodoSimple actualNodo = frente;
        NodoSimple anteriorNodo = null;

        while (actualNodo != null) {
            if (actualNodo.getCancion().equals(cancion)) {
                if (anteriorNodo == null) {
                    // Estamos eliminando el frente de la cola.
                    frente = actualNodo.getSiguiente();
                } else {
                    // "Saltamos" el nodo, igual que en la lista del Modo 1,
                    // pero aquí solo hay un puntero que reconectar (siguiente).
                    anteriorNodo.setSiguiente(actualNodo.getSiguiente());
                }
                if (actualNodo == fin) {
                    fin = anteriorNodo; // si era el último, el anterior pasa a ser el nuevo fin
                }
                tamanio--;
                return true;
            }
            anteriorNodo = actualNodo;
            actualNodo = actualNodo.getSiguiente();
        }
        return false; // no se encontró
    }

    /**
     * Avanza a la siguiente canción.
     *
     * A diferencia del Modo 1, aquí "avanzar" significa que la
     * canción que estaba sonando (el frente actual) YA SE REPRODUJO
     * y sale de la cola para siempre.
     */
    @Override
    public Cancion siguiente() {
        if (frente == null) return null;

        frente = frente.getSiguiente(); // el nodo viejo queda sin referencias -> se descarta
        if (frente == null) {
            fin = null; // la cola quedó vacía
        }
        tamanio--;

        return (frente == null) ? null : frente.getCancion();
    }

    /**
     * Lanzamos una excepción en vez de simplemente no hacer
     * nada, para que la GUI (o quien use esta clase) tenga que manejar
     * ese caso explícitamente y no se le olvide la restricción.
     */
    @Override
    public Cancion anterior() {
        throw new UnsupportedOperationException(
                "El modo Cola (FIFO) no permite regresar a canciones anteriores.");
    }

    @Override
    public Cancion obtenerActual() {
        return (frente == null) ? null : frente.getCancion();
    }

    /**
     * Recorre la cola de frente a fin. A diferencia del Modo 1, aquí
     * SÍ hay un final real (frente == null), no necesitamos "do-while".
     */
    @Override
    public List<Cancion> obtenerTodas() {
        List<Cancion> resultado = new ArrayList<>();
        NodoSimple nodoActual = frente;
        while (nodoActual != null) {
            resultado.add(nodoActual.getCancion());
            nodoActual = nodoActual.getSiguiente();
        }
        return resultado;
    }

    @Override
    public int cantidadCanciones() {
        return tamanio;
    }
}

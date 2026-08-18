import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de datos para el Modo 1: Reproducción Aleatoria.
 */
public class ListaCircularDoble implements ModoReproduccion {

    // "cabeza" es simplemente el primer nodo insertado, nos sirve como
    // punto de referencia fijo para saber cuándo dimos una vuelta completa.
    private NodoCancion cabeza;

    // "actual" es el nodo que se está reproduciendo en este momento.
    private NodoCancion actual;

    private int tamanio; // cantidad de canciones en la lista

    public ListaCircularDoble() {
        this.cabeza = null;
        this.actual = null;
        this.tamanio = 0;
    }

    /**
     * Agrega una canción al final de la lista, manteniendo la circularidad.
     */
    @Override
    public void agregarCancion(Cancion cancion) {
        NodoCancion nuevoNodo = new NodoCancion(cancion);

        if (cabeza == null) {
            // Caso 1: la lista está vacía.
            // El nuevo nodo se convierte en cabeza y se apunta a sí mismo
            // en ambas direcciones (una lista circular de un solo elemento
            // sigue siendo circular: el nodo es su propio vecino).
            cabeza = nuevoNodo;
            cabeza.setSiguiente(cabeza);
            cabeza.setAnterior(cabeza);
            actual = cabeza; // empezamos a reproducir desde aquí
        } else {
            // Caso 2: ya hay elementos.
            // Como es circular, "el último nodo" es simplemente
            // cabeza.getAnterior() — no necesitamos recorrer toda la lista.
            NodoCancion ultimo = cabeza.getAnterior();

            // Insertamos el nuevo nodo entre "ultimo" y "cabeza":
            ultimo.setSiguiente(nuevoNodo);
            nuevoNodo.setAnterior(ultimo);
            nuevoNodo.setSiguiente(cabeza);
            cabeza.setAnterior(nuevoNodo);
        }

        tamanio++;
    }

    /**
     * Elimina la primera canción encontrada que coincida (por referencia,
     * usando equals que por defecto compara identidad del objeto).
     */
    @Override
    public boolean eliminarCancion(Cancion cancion) {
        if (cabeza == null) return false;

        NodoCancion nodoActual = cabeza;
        boolean encontrado = false;

        // Recorremos la lista una sola vuelta completa.
        // Usamos "do-while" porque cabeza SIEMPRE debe evaluarse al menos una vez,
        // y un "while" normal nunca entraría (cabeza.getSiguiente() != cabeza
        // sería falso de entrada si solo hay un elemento).
        do {
            if (nodoActual.getCancion().equals(cancion)) {
                encontrado = true;
                break;
            }
            nodoActual = nodoActual.getSiguiente();
        } while (nodoActual != cabeza);

        if (!encontrado) return false;

        if (tamanio == 1) {
            // Era el único nodo: la lista queda vacía.
            cabeza = null;
            actual = null;
        } else {
            NodoCancion anterior = nodoActual.getAnterior();
            NodoCancion siguiente = nodoActual.getSiguiente();

            // "Saltamos" el nodo a eliminar reconectando sus vecinos entre sí.
            anterior.setSiguiente(siguiente);
            siguiente.setAnterior(anterior);

            if (nodoActual == cabeza) {
                cabeza = siguiente; // si eliminamos la cabeza, hay que reasignarla
            }
            if (nodoActual == actual) {
                actual = siguiente; // si eliminamos la canción actual, avanzamos
            }
        }

        tamanio--;
        return true;
    }

    /**
     * Avanza a la siguiente canción. Como la lista es circular,
     * esto NUNCA lanza un error de "fin de lista": si estamos en el
     * último nodo, siguiente() nos regresa automáticamente al primero.
     */
    @Override
    public Cancion siguiente() {
        if (actual == null) return null;
        actual = actual.getSiguiente();
        return actual.getCancion();
    }

    /**
     * Retrocede a la canción anterior. Igual de "infinito" que siguiente(),
     * pero en la otra dirección.
     */
    @Override
    public Cancion anterior() {
        if (actual == null) return null;
        actual = actual.getAnterior();
        return actual.getCancion();
    }

    @Override
    public Cancion obtenerActual() {
        return (actual == null) ? null : actual.getCancion();
    }

    /**
     * Recorre la lista UNA vuelta y arma una lista normal de Java,
     * útil para mostrar la biblioteca completa en la GUI.
     */
    @Override
    public List<Cancion> obtenerTodas() {
        List<Cancion> resultado = new ArrayList<>();
        if (cabeza == null) return resultado;

        NodoCancion nodoActual = cabeza;
        do {
            resultado.add(nodoActual.getCancion());
            nodoActual = nodoActual.getSiguiente();
        } while (nodoActual != cabeza);

        return resultado;
    }

    @Override
    public int cantidadCanciones() {
        return tamanio;
    }
}

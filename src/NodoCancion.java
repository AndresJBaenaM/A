/**
 * Nodo de una lista ligada doble.
 * Cada nodo "sabe" quién es su vecino anterior y su vecino siguiente,
 * eso es lo que permite navegar en ambas direcciones.
 */
public class NodoCancion {

    private Cancion cancion;
    private NodoCancion anterior;
    private NodoCancion siguiente;

    public NodoCancion(Cancion cancion) {
        this.cancion = cancion;
        this.anterior = null;
        this.siguiente = null;
    }

    public Cancion getCancion() { return cancion; }

    public NodoCancion getAnterior() { return anterior; }
    public void setAnterior(NodoCancion anterior) { this.anterior = anterior; }

    public NodoCancion getSiguiente() { return siguiente; }
    public void setSiguiente(NodoCancion siguiente) { this.siguiente = siguiente; }
}

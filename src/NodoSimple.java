/**
 * Nodo para una lista ligada SIMPLE (a diferencia de NodoCancion del Modo 1,
 * este solo apunta hacia adelante — no necesitamos "anterior" porque en una
 * cola FIFO nunca vamos a retroceder).
 */
public class NodoSimple {

    private Cancion cancion;
    private NodoSimple siguiente;

    public NodoSimple(Cancion cancion) {
        this.cancion = cancion;
        this.siguiente = null;
    }

    public Cancion getCancion() { return cancion; }

    public NodoSimple getSiguiente() { return siguiente; }
    public void setSiguiente(NodoSimple siguiente) { this.siguiente = siguiente; }
}

public class NodoArbol {
    private Cancion cancion;
    private NodoArbol izquierda;
    private NodoArbol derecha;

    public NodoArbol(Cancion cancion) {
        this.cancion = cancion;
    }

    public Cancion getCancion() { return cancion; }
    public NodoArbol getIzquierda() { return izquierda; }
    public NodoArbol getDerecha() { return derecha; }
    public void setIzquierda(NodoArbol izquierda) { this.izquierda = izquierda; }
    public void setDerecha(NodoArbol derecha) { this.derecha = derecha; }
}

import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de datos para el Modo 3: Reproducción alfabética.
 *
 * Las canciones se ordenan por nombre (ignorando mayúsculas/minúsculas).
 * "Avanzar" y "Retroceder" simulan un recorrido inorden: por eso, en vez
 * de recorrer el árbol nodo por nodo cada vez, mantenemos una lista con
 * el recorrido inorden ya calculado y un índice que se mueve sobre ella.
 * Es más simple y evita tener que guardar punteros "padre" en cada nodo.
 */
public class ArbolBinarioBusqueda implements ModoReproduccion {

    private NodoArbol raiz;
    private List<Cancion> recorridoInorden;
    private int indiceActual;

    public ArbolBinarioBusqueda() {
        raiz = null;
        recorridoInorden = new ArrayList<>();
        indiceActual = -1;
    }

    @Override
    public void agregarCancion(Cancion cancion) {
        raiz = insertar(raiz, cancion);
        reconstruirRecorrido();
    }

    private NodoArbol insertar(NodoArbol nodo, Cancion cancion) {
        if (nodo == null) return new NodoArbol(cancion);
        if (cancion.getNombre().compareToIgnoreCase(nodo.getCancion().getNombre()) < 0) {
            nodo.setIzquierda(insertar(nodo.getIzquierda(), cancion));
        } else {
            nodo.setDerecha(insertar(nodo.getDerecha(), cancion));
        }
        return nodo;
    }

    @Override
    public boolean eliminarCancion(Cancion cancion) {
        if (!contiene(raiz, cancion)) return false;
        raiz = eliminar(raiz, cancion);
        reconstruirRecorrido();
        return true;
    }

    private boolean contiene(NodoArbol nodo, Cancion cancion) {
        if (nodo == null) return false;
        if (nodo.getCancion().equals(cancion)) return true;
        int cmp = cancion.getNombre().compareToIgnoreCase(nodo.getCancion().getNombre());
        if (cmp < 0) return contiene(nodo.getIzquierda(), cancion);
        return contiene(nodo.getDerecha(), cancion);
    }

    /**
     * Elimina un nodo del árbol manteniendo la propiedad de orden.
     *
     * OJO con el caso de dos hijos: hay que reemplazar la canción del nodo
     * por la del mínimo del subárbol derecho (su "sucesor inorden"), y
     * conservar TANTO el subárbol izquierdo original COMO el subárbol
     * derecho (ya sin el mínimo que acabamos de mover). Perder cualquiera
     * de los dos aquí borra canciones enteras del árbol sin querer.
     */
    private NodoArbol eliminar(NodoArbol nodo, Cancion cancion) {
        if (nodo == null) return null;
        int cmp = cancion.getNombre().compareToIgnoreCase(nodo.getCancion().getNombre());
        if (cmp < 0) {
            nodo.setIzquierda(eliminar(nodo.getIzquierda(), cancion));
        } else if (cmp > 0) {
            nodo.setDerecha(eliminar(nodo.getDerecha(), cancion));
        } else if (!nodo.getCancion().equals(cancion)) {
            // Mismo nombre pero canción distinta (puede pasar si dos
            // canciones comparten nombre): seguimos buscando por el
            // subárbol derecho, que es donde insertamos los empates.
            nodo.setDerecha(eliminar(nodo.getDerecha(), cancion));
        } else {
            // Encontramos el nodo exacto a eliminar.
            if (nodo.getIzquierda() == null) return nodo.getDerecha();
            if (nodo.getDerecha() == null) return nodo.getIzquierda();

            // Caso con dos hijos: buscamos el mínimo del subárbol derecho.
            NodoArbol minimo = nodo.getDerecha();
            while (minimo.getIzquierda() != null) {
                minimo = minimo.getIzquierda();
            }

            NodoArbol nodoReemplazo = new NodoArbol(minimo.getCancion());
            nodoReemplazo.setIzquierda(nodo.getIzquierda());               // se conserva el subárbol izquierdo original
            nodoReemplazo.setDerecha(eliminar(nodo.getDerecha(), minimo.getCancion())); // se quita el mínimo duplicado
            return nodoReemplazo;
        }
        return nodo;
    }

    @Override
    public Cancion siguiente() {
        if (recorridoInorden.isEmpty()) return null;
        if (indiceActual < recorridoInorden.size() - 1) indiceActual++;
        return recorridoInorden.get(indiceActual);
    }

    @Override
    public Cancion anterior() {
        if (recorridoInorden.isEmpty()) return null;
        if (indiceActual > 0) indiceActual--;
        return recorridoInorden.get(indiceActual);
    }

    @Override
    public Cancion obtenerActual() {
        if (indiceActual == -1 || recorridoInorden.isEmpty()) return null;
        return recorridoInorden.get(indiceActual);
    }

    @Override
    public List<Cancion> obtenerTodas() {
        return new ArrayList<>(recorridoInorden);
    }

    @Override
    public int cantidadCanciones() {
        return recorridoInorden.size();
    }

    private void reconstruirRecorrido() {
        Cancion actualAntes = obtenerActual();
        recorridoInorden.clear();
        inorden(raiz);

        if (actualAntes != null && recorridoInorden.contains(actualAntes)) {
            // Si la canción que estaba sonando sigue existiendo, no perdemos
            // el lugar solo porque se agregó o se borró otra canción distinta.
            indiceActual = recorridoInorden.indexOf(actualAntes);
        } else {
            indiceActual = recorridoInorden.isEmpty() ? -1 : 0;
        }
    }

    private void inorden(NodoArbol nodo) {
        if (nodo == null) return;
        inorden(nodo.getIzquierda());
        recorridoInorden.add(nodo.getCancion());
        inorden(nodo.getDerecha());
    }
}

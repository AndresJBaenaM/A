/**
 * Clase modelo que representa una canción.
 */
public class Cancion {

    private String nombre;
    private String artista;
    private String album;
    private int duracionSegundos; // duración en segundos, más fácil de manipular que un String
    private String genero;
    private int anioLanzamiento;
    private int calificacion; // 0 a 100

    // Ruta a una imagen elegida por el usuario para la portada (opcional).
    // Si es null o no existe en disco, la GUI dibuja una portada genérica.
    private String rutaImagen;

    public Cancion(String nombre, String artista, String album,
                   int duracionSegundos, String genero,
                   int anioLanzamiento, int calificacion) {
        this(nombre, artista, album, duracionSegundos, genero, anioLanzamiento, calificacion, null);
    }

    public Cancion(String nombre, String artista, String album,
                   int duracionSegundos, String genero,
                   int anioLanzamiento, int calificacion, String rutaImagen) {
        this.nombre = nombre;
        this.artista = artista;
        this.album = album;
        this.duracionSegundos = duracionSegundos;
        this.genero = genero;
        this.anioLanzamiento = anioLanzamiento;
        setCalificacion(calificacion); // usamos el setter para validar el rango
        this.rutaImagen = rutaImagen;
    }

    // ---------- Getters ----------
    public String getNombre() { return nombre; }
    public String getArtista() { return artista; }
    public String getAlbum() { return album; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public String getGenero() { return genero; }
    public int getAnioLanzamiento() { return anioLanzamiento; }
    public int getCalificacion() { return calificacion; }
    public String getRutaImagen() { return rutaImagen; }

    // ---------- Setters ----------
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setArtista(String artista) { this.artista = artista; }
    public void setAlbum(String album) { this.album = album; }
    public void setDuracionSegundos(int duracionSegundos) { this.duracionSegundos = duracionSegundos; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setAnioLanzamiento(int anioLanzamiento) { this.anioLanzamiento = anioLanzamiento; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }

    public void setCalificacion(int calificacion) {
        if (calificacion < 0 || calificacion > 100) {
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 100");
        }
        this.calificacion = calificacion;
    }

    /**
     * Formatea la duración como mm:ss para mostrarla en la GUI.
     */
    public String getDuracionFormateada() {
        int minutos = duracionSegundos / 60;
        int segundos = duracionSegundos % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    @Override
    public String toString() {
        return nombre + " - " + artista + " (" + getDuracionFormateada() + ")";
    }
}

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta de colores centralizada para los dos temas de la aplicación.
 *
 * Mantener los colores en un solo lugar es lo que permite que el botón
 * "cambiar tema" simplemente reconstruya esta paleta y repinte todo,
 * sin tener que ir componente por componente cambiando colores sueltos.
 *
 * Se usa el mismo azul de acento en ambos temas (para el oscuro, uno
 * vibrante que resalta sobre negro; en el claro, el mismo tono pero con
 * un "hover" más oscuro, porque sobre blanco un azul más claro pierde
 * contraste). Esto mantiene una identidad visual consistente entre modos.
 */
public class Tema {

    public final Color fondoPrincipal;
    public final Color fondoSuperficie;
    public final Color fondoSuperficieAlterna;
    public final Color textoPrimario;
    public final Color textoSecundario;
    public final Color acento;
    public final Color acentoHover;
    public final Color borde;
    public final Color exito;
    public final Color peligro;
    public final boolean esOscuro;

    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FUENTE_NOMBRE_CANCION = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FUENTE_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FUENTE_PEQUENA = new Font("SansSerif", Font.PLAIN, 11);

    private Tema(Color fondoPrincipal, Color fondoSuperficie, Color fondoSuperficieAlterna,
                 Color textoPrimario, Color textoSecundario, Color acento, Color acentoHover,
                 Color borde, Color exito, Color peligro, boolean esOscuro) {
        this.fondoPrincipal = fondoPrincipal;
        this.fondoSuperficie = fondoSuperficie;
        this.fondoSuperficieAlterna = fondoSuperficieAlterna;
        this.textoPrimario = textoPrimario;
        this.textoSecundario = textoSecundario;
        this.acento = acento;
        this.acentoHover = acentoHover;
        this.borde = borde;
        this.exito = exito;
        this.peligro = peligro;
        this.esOscuro = esOscuro;
    }

    public static Tema oscuro() {
        return new Tema(
                new Color(18, 18, 18),      // fondo principal
                new Color(24, 24, 24),      // superficie (paneles/tarjetas)
                new Color(34, 34, 34),      // superficie alterna (hover de lista)
                Color.WHITE,
                new Color(179, 179, 179),
                new Color(61, 139, 255),    // azul de acento
                new Color(97, 161, 255),    // azul más claro al pasar el mouse
                new Color(45, 45, 45),
                new Color(46, 204, 113),
                new Color(235, 87, 87),
                true
        );
    }

    public static Tema claro() {
        return new Tema(
                new Color(247, 247, 249),
                Color.WHITE,
                new Color(238, 240, 245),
                new Color(24, 24, 24),
                new Color(96, 96, 102),
                new Color(61, 139, 255),    // mismo azul de acento del tema oscuro
                new Color(37, 105, 209),    // más oscuro al pasar el mouse (contraste sobre blanco)
                new Color(224, 224, 228),
                new Color(39, 174, 96),
                new Color(214, 69, 69),
                false
        );
    }
}

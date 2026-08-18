import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

/**
 * Panel que dibuja la "portada" del álbum de la canción actual.
 *
 * Si la canción tiene una ruta de imagen asignada por el usuario (botón
 * Agregar/Editar), se carga esa imagen y se recorta a un cuadrado con
 * esquinas redondeadas. Si no, se genera un color a partir del nombre de
 * la canción (siempre el mismo color para la misma canción) con una nota
 * musical encima, para que cada canción sin portada personalizada igual
 * se vea distinta de las demás.
 */
public class PortadaCancion extends JPanel {

    private Image imagen;
    private Color colorFondoGenerado = new Color(80, 80, 80);
    private static final String SIMBOLO = "\u266A"; // nota musical ♪

    public PortadaCancion() {
        setOpaque(false);
        setPreferredSize(new Dimension(210, 210));
    }

    public void actualizar(Cancion cancion) {
        imagen = null;
        if (cancion == null) {
            colorFondoGenerado = new Color(80, 80, 80);
            repaint();
            return;
        }

        String ruta = cancion.getRutaImagen();
        if (ruta != null && !ruta.isBlank()) {
            File archivo = new File(ruta);
            if (archivo.exists() && archivo.isFile()) {
                imagen = new ImageIcon(ruta).getImage();
            }
        }

        colorFondoGenerado = colorDesdeTexto(cancion.getNombre());
        repaint();
    }

    private Color colorDesdeTexto(String texto) {
        int hash = Math.abs(texto.hashCode());
        float tono = (hash % 360) / 360f;
        return Color.getHSBColor(tono, 0.55f, 0.55f);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int tam = Math.min(getWidth(), getHeight());
        int x = (getWidth() - tam) / 2;
        int y = 0;

        RoundRectangle2D redondeado = new RoundRectangle2D.Float(x, y, tam, tam, 24, 24);
        g2.clip(redondeado);

        if (imagen != null) {
            g2.drawImage(imagen, x, y, tam, tam, this);
        } else {
            GradientPaint degradado = new GradientPaint(
                    x, y, colorFondoGenerado.brighter(),
                    x + tam, y + tam, colorFondoGenerado.darker());
            g2.setPaint(degradado);
            g2.fillRect(x, y, tam, tam);

            g2.setColor(new Color(255, 255, 255, 210));
            g2.setFont(new Font("SansSerif", Font.PLAIN, tam / 3));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (tam - fm.stringWidth(SIMBOLO)) / 2;
            int ty = y + (tam + fm.getAscent()) / 2 - (fm.getDescent() / 2);
            g2.drawString(SIMBOLO, tx, ty);
        }
        g2.dispose();
    }
}

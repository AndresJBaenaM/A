import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Botón circular para los controles de transporte (anterior, play/pausa,
 * siguiente), igual que en cualquier reproductor de música conocido.
 *
 * "relleno" = true dibuja un círculo sólido de color (se usa para el botón
 * grande de play/pausa); relleno = false deja el círculo transparente,
 * solo con el símbolo encima (anterior/siguiente, más discretos).
 */
public class BotonCircular extends JButton {

    private Color colorFondo;
    private final boolean relleno;

    public BotonCircular(String texto, boolean relleno) {
        super(texto);
        this.relleno = relleno;
        this.colorFondo = new Color(61, 139, 255);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("SansSerif", Font.PLAIN, relleno ? 20 : 16));
    }

    public void setColorFondo(Color color) {
        this.colorFondo = color;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int tam = relleno ? 54 : 40;
        return new Dimension(tam, tam);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (relleno) {
            g2.setColor(isEnabled() ? colorFondo : new Color(90, 90, 90));
            g2.fillOval(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
        super.paintComponent(g);
    }
}

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botón con esquinas totalmente redondeadas (forma de "píldora"), al
 * estilo de los botones de Spotify. Extiende JButton y sobrescribe
 * paintComponent para dibujar el fondo nosotros mismos, en vez de usar
 * el rectángulo cuadrado por defecto de Swing.
 */
public class BotonPill extends JButton {

    private Color colorFondo = new Color(61, 139, 255);
    private Color colorFondoHover = new Color(97, 161, 255);
    private boolean mouseEncima = false;

    public BotonPill(String texto) {
        super(texto);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(Tema.FUENTE_BOLD);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseEncima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseEncima = false;
                repaint();
            }
        });
    }

    public void setColores(Color normal, Color hover) {
        this.colorFondo = normal;
        this.colorFondoHover = hover;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fondo;
        if (!isEnabled()) {
            fondo = new Color(90, 90, 90);
        } else if (mouseEncima) {
            fondo = colorFondoHover;
        } else {
            fondo = colorFondo;
        }

        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}

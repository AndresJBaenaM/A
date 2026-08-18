import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

/**
 * GUI del reproductor.
 *
 * Esta clase SOLO se encarga de la presentación: construir los paneles,
 * pintarlos según el tema activo y reaccionar a los eventos del usuario.
 * Toda la lógica real (qué estructura de datos usar, cómo agregar/editar/
 * eliminar canciones, cómo avanzar o retroceder) vive en
 * {@link ControladorReproductor}, que es lo único que esta clase conoce
 * de la capa de datos.
 */
public class ReproductorGUI extends JFrame {

    private final ControladorReproductor controlador;
    private Tema temaActual;
    private boolean modoOscuro;

    private Timer timerProgreso;
    private int progresoMilisegundos;

    // ---- Paneles principales (necesitan repintarse al cambiar de tema) ----
    private JPanel panelSuperior;
    private JPanel panelSidebar;
    private JPanel panelCentral;

    // ---- Barra superior ----
    private JLabel lblTitulo;
    private JLabel lblModoLabel;
    private JComboBox<String> comboModos;
    private BotonPill btnTema;

    // ---- Sidebar / biblioteca ----
    private JTextField campoBusqueda;
    private BotonPill btnBuscar;
    private BotonPill btnAgregar;
    private BotonPill btnEditar;
    private BotonPill btnEliminar;
    private DefaultListModel<Cancion> modeloLista;
    private JList<Cancion> jListBiblioteca;
    private JScrollPane scrollBiblioteca;

    // ---- Panel "ahora reproduciendo" ----
    private PortadaCancion portada;
    private JLabel lblNombreCancion;
    private JLabel lblArtistaAlbum;
    private JLabel lblGeneroAnioDuracion;
    private JLabel lblCalificacion;
    private JLabel lblTiempoActual;
    private JLabel lblTiempoTotal;
    private JProgressBar barraProgreso;
    private BotonCircular btnAnterior;
    private BotonCircular btnReproducir;
    private BotonCircular btnPausar;
    private BotonCircular btnSiguiente;

    /**
     * Contenedor simple con los datos capturados en el formulario de
     * Agregar/Editar canción, ya validados.
     */
    private record DatosCancion(String nombre, String artista, String album, int duracionSegundos,
                                 String genero, int anio, int calificacion, String rutaImagen) {
    }

    public ReproductorGUI() {
        this.controlador = new ControladorReproductor();
        this.modoOscuro = true;

        construirInterfaz();
        cargarCancionesDeEjemplo();
        cambiarAModo(ControladorReproductor.MODO_ALEATORIO);
        aplicarTema();
    }

    // ==================== CONSTRUCCIÓN DE LA INTERFAZ ====================

    private void construirInterfaz() {
        setTitle("EIA Player - Universidad EIA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(construirPanelSidebar(), BorderLayout.WEST);
        add(construirPanelCentral(), BorderLayout.CENTER);

        configurarAtajosTeclado();
    }

    private JPanel construirPanelSuperior() {
        panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        lblTitulo = new JLabel("\u266B EIA Player");
        lblTitulo.setFont(Tema.FUENTE_TITULO);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panelDerecho.setOpaque(false);

        lblModoLabel = new JLabel("Modo de reproducción:");
        lblModoLabel.setFont(Tema.FUENTE_NORMAL);

        comboModos = new JComboBox<>(new String[]{
                "Aleatorio · Lista Circular Doble",
                "Orden de llegada · Cola FIFO",
                "Alfabético · Árbol Binario de Búsqueda"
        });
        comboModos.addActionListener(this::onCambiarModoDesdeCombo);

        btnTema = new BotonPill("\u2600 Tema claro");
        btnTema.addActionListener(e -> onCambiarTema());

        panelDerecho.add(lblModoLabel);
        panelDerecho.add(comboModos);
        panelDerecho.add(btnTema);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);

        return panelSuperior;
    }

    private JPanel construirPanelSidebar() {
        panelSidebar = new JPanel(new BorderLayout(0, 12));
        panelSidebar.setPreferredSize(new Dimension(300, 0));
        panelSidebar.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 10));

        JPanel panelBusqueda = new JPanel(new BorderLayout(8, 0));
        panelBusqueda.setOpaque(false);
        campoBusqueda = new JTextField();
        campoBusqueda.addActionListener(this::onBuscar);
        btnBuscar = new BotonPill("Buscar");
        btnBuscar.addActionListener(this::onBuscar);
        panelBusqueda.add(campoBusqueda, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);

        modeloLista = new DefaultListModel<>();
        jListBiblioteca = new JList<>(modeloLista);
        jListBiblioteca.setCellRenderer(new CeldaCancionRenderer());
        jListBiblioteca.setFixedCellHeight(52);
        scrollBiblioteca = new JScrollPane(jListBiblioteca);
        scrollBiblioteca.setBorder(BorderFactory.createTitledBorder("Biblioteca"));
        scrollBiblioteca.getVerticalScrollBar().setUnitIncrement(16);

        JPanel panelAcciones = new JPanel(new GridLayout(1, 3, 8, 0));
        panelAcciones.setOpaque(false);
        btnAgregar = new BotonPill("+ Agregar");
        btnEditar = new BotonPill("\u270E Editar");
        btnEliminar = new BotonPill("\u2715 Eliminar");
        btnAgregar.addActionListener(this::onAgregar);
        btnEditar.addActionListener(this::onEditar);
        btnEliminar.addActionListener(this::onEliminar);
        panelAcciones.add(btnAgregar);
        panelAcciones.add(btnEditar);
        panelAcciones.add(btnEliminar);

        panelSidebar.add(panelBusqueda, BorderLayout.NORTH);
        panelSidebar.add(scrollBiblioteca, BorderLayout.CENTER);
        panelSidebar.add(panelAcciones, BorderLayout.SOUTH);

        return panelSidebar;
    }

    private JPanel construirPanelCentral() {
        panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 40));

        portada = new PortadaCancion();
        portada.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNombreCancion = new JLabel("Sin canciones", SwingConstants.CENTER);
        lblNombreCancion.setFont(Tema.FUENTE_NOMBRE_CANCION);
        lblNombreCancion.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblArtistaAlbum = new JLabel("—", SwingConstants.CENTER);
        lblArtistaAlbum.setFont(Tema.FUENTE_SUBTITULO);
        lblArtistaAlbum.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblGeneroAnioDuracion = new JLabel("—", SwingConstants.CENTER);
        lblGeneroAnioDuracion.setFont(Tema.FUENTE_NORMAL);
        lblGeneroAnioDuracion.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblCalificacion = new JLabel("—", SwingConstants.CENTER);
        lblCalificacion.setFont(Tema.FUENTE_BOLD);
        lblCalificacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelProgreso = new JPanel(new BorderLayout(10, 0));
        panelProgreso.setOpaque(false);
        panelProgreso.setMaximumSize(new Dimension(460, 26));
        panelProgreso.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTiempoActual = new JLabel("0:00");
        lblTiempoActual.setFont(Tema.FUENTE_PEQUENA);
        lblTiempoTotal = new JLabel("0:00");
        lblTiempoTotal.setFont(Tema.FUENTE_PEQUENA);
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setStringPainted(false);
        panelProgreso.add(lblTiempoActual, BorderLayout.WEST);
        panelProgreso.add(barraProgreso, BorderLayout.CENTER);
        panelProgreso.add(lblTiempoTotal, BorderLayout.EAST);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        panelControles.setOpaque(false);
        panelControles.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnAnterior = new BotonCircular("\u23EE", false);
        btnReproducir = new BotonCircular("\u25B6", true);
        btnPausar = new BotonCircular("\u23F8", true);
        btnSiguiente = new BotonCircular("\u23ED", false);

        btnAnterior.addActionListener(this::onAnterior);
        btnReproducir.addActionListener(this::onReproducir);
        btnPausar.addActionListener(this::onPausar);
        btnSiguiente.addActionListener(this::onSiguiente);

        panelControles.add(btnAnterior);
        panelControles.add(btnReproducir);
        panelControles.add(btnPausar);
        panelControles.add(btnSiguiente);

        panelCentral.add(Box.createVerticalGlue());
        panelCentral.add(portada);
        panelCentral.add(Box.createVerticalStrut(22));
        panelCentral.add(lblNombreCancion);
        panelCentral.add(Box.createVerticalStrut(6));
        panelCentral.add(lblArtistaAlbum);
        panelCentral.add(Box.createVerticalStrut(4));
        panelCentral.add(lblGeneroAnioDuracion);
        panelCentral.add(Box.createVerticalStrut(4));
        panelCentral.add(lblCalificacion);
        panelCentral.add(Box.createVerticalStrut(20));
        panelCentral.add(panelProgreso);
        panelCentral.add(Box.createVerticalStrut(20));
        panelCentral.add(panelControles);
        panelCentral.add(Box.createVerticalGlue());

        return panelCentral;
    }

    /**
     * Atajos de teclado (funcionalidad de bonificación mencionada en el
     * enunciado): espacio para reproducir/pausar, flechas para avanzar
     * y retroceder.
     */
    private void configurarAtajosTeclado() {
        JRootPane raiz = getRootPane();
        InputMap mapaEntrada = raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap mapaAccion = raiz.getActionMap();

        mapaEntrada.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "alternarReproduccion");
        mapaAccion.put("alternarReproduccion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timerProgreso != null && timerProgreso.isRunning()) {
                    onPausar(e);
                } else {
                    onReproducir(e);
                }
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "siguienteAtajo");
        mapaAccion.put("siguienteAtajo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnSiguiente.isEnabled()) onSiguiente(e);
            }
        });

        mapaEntrada.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "anteriorAtajo");
        mapaAccion.put("anteriorAtajo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnAnterior.isEnabled()) onAnterior(e);
            }
        });
    }

    private void cargarCancionesDeEjemplo() {
        controlador.agregarCancion(new Cancion("Bohemian Rhapsody", "Queen", "A Night at the Opera", 355, "Rock", 1975, 95));
        controlador.agregarCancion(new Cancion("Blinding Lights", "The Weeknd", "After Hours", 200, "Synthpop", 2019, 90));
        controlador.agregarCancion(new Cancion("Hotel California", "Eagles", "Hotel California", 391, "Rock", 1976, 92));
        controlador.agregarCancion(new Cancion("La Vida es un Carnaval", "Celia Cruz", "Mi Vida es Cantar", 240, "Salsa", 1998, 88));
        controlador.agregarCancion(new Cancion("Don't Stop Believin'", "Journey", "Escape", 251, "Rock", 1981, 85));
    }

    // ==================== CAMBIO DE MODO ====================

    private void onCambiarModoDesdeCombo(ActionEvent e) {
        cambiarAModo(comboModos.getSelectedIndex());
    }

    private void cambiarAModo(int indiceModo) {
        detenerTimer();
        controlador.cambiarModo(indiceModo);
        actualizarListaBiblioteca();
        actualizarCancionActual();
        actualizarEstadoBotones();
    }

    // ==================== TEMA ====================

    private void onCambiarTema() {
        modoOscuro = !modoOscuro;
        aplicarTema();
    }

    private void aplicarTema() {
        temaActual = modoOscuro ? Tema.oscuro() : Tema.claro();

        getContentPane().setBackground(temaActual.fondoPrincipal);
        panelSuperior.setBackground(temaActual.fondoPrincipal);
        panelSidebar.setBackground(temaActual.fondoPrincipal);
        panelCentral.setBackground(temaActual.fondoPrincipal);

        lblTitulo.setForeground(temaActual.textoPrimario);
        lblModoLabel.setForeground(temaActual.textoSecundario);
        lblNombreCancion.setForeground(temaActual.textoPrimario);
        lblArtistaAlbum.setForeground(temaActual.textoSecundario);
        lblGeneroAnioDuracion.setForeground(temaActual.textoSecundario);
        lblCalificacion.setForeground(temaActual.acento);
        lblTiempoActual.setForeground(temaActual.textoSecundario);
        lblTiempoTotal.setForeground(temaActual.textoSecundario);

        comboModos.setBackground(temaActual.fondoSuperficie);
        comboModos.setForeground(temaActual.textoPrimario);

        campoBusqueda.setBackground(temaActual.fondoSuperficie);
        campoBusqueda.setForeground(temaActual.textoPrimario);
        campoBusqueda.setCaretColor(temaActual.textoPrimario);
        campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(temaActual.borde),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        jListBiblioteca.setBackground(temaActual.fondoSuperficie);
        scrollBiblioteca.getViewport().setBackground(temaActual.fondoSuperficie);
        TitledBorder tituloBiblioteca = BorderFactory.createTitledBorder("Biblioteca");
        tituloBiblioteca.setTitleColor(temaActual.textoSecundario);
        scrollBiblioteca.setBorder(tituloBiblioteca);

        barraProgreso.setForeground(temaActual.acento);
        barraProgreso.setBackground(temaActual.fondoSuperficieAlterna);

        for (BotonPill boton : new BotonPill[]{btnAgregar, btnEditar, btnEliminar, btnBuscar}) {
            boton.setColores(temaActual.acento, temaActual.acentoHover);
        }

        btnTema.setColores(temaActual.fondoSuperficieAlterna, temaActual.borde);
        btnTema.setForeground(temaActual.textoPrimario);
        btnTema.setText(modoOscuro ? "\u2600 Tema claro" : "\u263E Tema oscuro");

        for (BotonCircular boton : new BotonCircular[]{btnAnterior, btnSiguiente}) {
            boton.setColorFondo(temaActual.fondoSuperficieAlterna);
            boton.setForeground(temaActual.textoPrimario);
        }
        btnReproducir.setColorFondo(temaActual.acento);
        btnReproducir.setForeground(Color.WHITE);
        btnPausar.setColorFondo(temaActual.acento);
        btnPausar.setForeground(Color.WHITE);

        jListBiblioteca.repaint();
        repaint();
    }

    // ==================== REPRODUCCIÓN ====================

    private void onSiguiente(ActionEvent e) {
        controlador.siguiente();
        actualizarListaBiblioteca(); // en el modo Cola, la canción reproducida desaparece de la lista
        actualizarCancionActual();
    }

    private void onAnterior(ActionEvent e) {
        try {
            controlador.anterior();
            actualizarCancionActual();
        } catch (UnsupportedOperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void onReproducir(ActionEvent e) {
        Cancion actual = controlador.obtenerActual();
        if (actual == null) return;
        if (timerProgreso != null && timerProgreso.isRunning()) return;

        final int duracion = Math.max(1, actual.getDuracionSegundos());
        if (timerProgreso != null) {
            timerProgreso.stop();
        }

        timerProgreso = new Timer(200, evt -> {
            progresoMilisegundos += 200;
            int porcentaje = Math.min(100, (int) (progresoMilisegundos / 1000.0 / duracion * 100));
            barraProgreso.setValue(porcentaje);
            lblTiempoActual.setText(formatearTiempo(Math.min(duracion, progresoMilisegundos / 1000)));
            if (porcentaje >= 100) {
                timerProgreso.stop();
                actualizarEstadoBotonesReproduccion(false);
            }
        });
        timerProgreso.start();
        actualizarEstadoBotonesReproduccion(true);
    }

    private void onPausar(ActionEvent e) {
        detenerTimer();
        actualizarEstadoBotonesReproduccion(false);
    }

    private void detenerTimer() {
        if (timerProgreso != null) {
            timerProgreso.stop();
        }
    }

    private String formatearTiempo(int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    // ==================== BIBLIOTECA (AGREGAR / EDITAR / ELIMINAR / BUSCAR) ====================

    private void onAgregar(ActionEvent e) {
        DatosCancion datos = mostrarDialogoCancion("Agregar canción", null);
        if (datos == null) return;

        Cancion nueva = new Cancion(datos.nombre(), datos.artista(), datos.album(),
                datos.duracionSegundos(), datos.genero(), datos.anio(), datos.calificacion(),
                datos.rutaImagen());
        controlador.agregarCancion(nueva);
        actualizarListaBiblioteca();
        if (controlador.cantidadCanciones() == 1) actualizarCancionActual();
        actualizarEstadoBotones();
    }

    private void onEditar(ActionEvent e) {
        Cancion seleccionada = jListBiblioteca.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una canción de la biblioteca primero.");
            return;
        }

        DatosCancion datos = mostrarDialogoCancion("Editar canción", seleccionada);
        if (datos == null) return;

        controlador.editarCancion(seleccionada, datos.nombre(), datos.artista(), datos.album(),
                datos.duracionSegundos(), datos.genero(), datos.anio(), datos.calificacion(),
                datos.rutaImagen());
        actualizarListaBiblioteca();
        actualizarCancionActual();
    }

    private void onEliminar(ActionEvent e) {
        Cancion seleccionada = jListBiblioteca.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una canción de la biblioteca primero.");
            return;
        }

        controlador.eliminarCancion(seleccionada);
        actualizarListaBiblioteca();
        actualizarCancionActual();
        actualizarEstadoBotones();
    }

    private void onBuscar(ActionEvent e) {
        String consulta = campoBusqueda.getText().trim();
        if (consulta.isEmpty()) return;

        Cancion encontrada = controlador.buscarPorNombre(consulta);
        if (encontrada == null) {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna canción con ese nombre.");
            return;
        }
        jListBiblioteca.setSelectedValue(encontrada, true);
    }

    /**
     * Muestra el formulario de Agregar/Editar (según si "existente" es null
     * o no), valida los datos y los devuelve. Retorna null si el usuario
     * cancela o si hay un error de validación.
     */
    private DatosCancion mostrarDialogoCancion(String tituloDialogo, Cancion existente) {
        JTextField campoNombre = new JTextField(existente != null ? existente.getNombre() : "");
        JTextField campoArtista = new JTextField(existente != null ? existente.getArtista() : "");
        JTextField campoAlbum = new JTextField(existente != null ? existente.getAlbum() : "");
        JTextField campoDuracion = new JTextField(String.valueOf(existente != null ? existente.getDuracionSegundos() : 180));
        JTextField campoGenero = new JTextField(existente != null ? existente.getGenero() : "");
        JTextField campoAnio = new JTextField(String.valueOf(existente != null ? existente.getAnioLanzamiento() : 2024));
        JTextField campoCalificacion = new JTextField(String.valueOf(existente != null ? existente.getCalificacion() : 80));

        final String[] rutaImagenSeleccionada = {existente != null ? existente.getRutaImagen() : null};
        JLabel lblImagen = new JLabel(rutaImagenSeleccionada[0] != null
                ? new File(rutaImagenSeleccionada[0]).getName()
                : "Ninguna imagen seleccionada");
        JButton btnElegirImagen = new JButton("Elegir imagen de portada...");
        btnElegirImagen.addActionListener(ev -> {
            JFileChooser selector = new JFileChooser();
            selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imágenes", "jpg", "jpeg", "png", "gif"));
            int resultadoSelector = selector.showOpenDialog(this);
            if (resultadoSelector == JFileChooser.APPROVE_OPTION) {
                rutaImagenSeleccionada[0] = selector.getSelectedFile().getAbsolutePath();
                lblImagen.setText(selector.getSelectedFile().getName());
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Nombre:"));
        panel.add(campoNombre);
        panel.add(new JLabel("Artista:"));
        panel.add(campoArtista);
        panel.add(new JLabel("Álbum:"));
        panel.add(campoAlbum);
        panel.add(new JLabel("Duración (segundos):"));
        panel.add(campoDuracion);
        panel.add(new JLabel("Género:"));
        panel.add(campoGenero);
        panel.add(new JLabel("Año:"));
        panel.add(campoAnio);
        panel.add(new JLabel("Calificación (0-100):"));
        panel.add(campoCalificacion);
        panel.add(btnElegirImagen);
        panel.add(lblImagen);

        int resultado = JOptionPane.showConfirmDialog(this, panel, tituloDialogo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) return null;

        if (campoNombre.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "El nombre de la canción no puede estar vacío.");
            return null;
        }

        try {
            int duracion = Integer.parseInt(campoDuracion.getText().trim());
            int anio = Integer.parseInt(campoAnio.getText().trim());
            int calificacion = Integer.parseInt(campoCalificacion.getText().trim());

            if (calificacion < 0 || calificacion > 100) {
                JOptionPane.showMessageDialog(this, "La calificación debe estar entre 0 y 100.");
                return null;
            }
            if (duracion <= 0) {
                JOptionPane.showMessageDialog(this, "La duración debe ser un número positivo.");
                return null;
            }

            return new DatosCancion(campoNombre.getText().trim(), campoArtista.getText().trim(),
                    campoAlbum.getText().trim(), duracion, campoGenero.getText().trim(), anio,
                    calificacion, rutaImagenSeleccionada[0]);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Duración, año y calificación deben ser números.");
            return null;
        }
    }

    // ==================== ACTUALIZACIÓN DE LA VISTA ====================

    private void actualizarListaBiblioteca() {
        Cancion seleccionActual = controlador.obtenerActual();
        modeloLista.clear();
        List<Cancion> todas = controlador.obtenerTodas();
        for (Cancion c : todas) {
            modeloLista.addElement(c);
        }
        if (seleccionActual != null) {
            jListBiblioteca.setSelectedValue(seleccionActual, true);
        }
    }

    private void actualizarCancionActual() {
        detenerTimer();
        progresoMilisegundos = 0;

        Cancion actual = controlador.obtenerActual();
        portada.actualizar(actual);

        if (actual == null) {
            lblNombreCancion.setText("Sin canciones");
            lblArtistaAlbum.setText("—");
            lblGeneroAnioDuracion.setText("—");
            lblCalificacion.setText("—");
            barraProgreso.setValue(0);
            lblTiempoActual.setText("0:00");
            lblTiempoTotal.setText("0:00");
            actualizarEstadoBotonesReproduccion(false);
            return;
        }

        lblNombreCancion.setText(actual.getNombre());
        lblArtistaAlbum.setText(actual.getArtista() + "  ·  " + actual.getAlbum());
        lblGeneroAnioDuracion.setText(actual.getGenero() + "  ·  " + actual.getAnioLanzamiento());
        lblCalificacion.setText("\u2605 " + actual.getCalificacion() + "/100");
        barraProgreso.setValue(0);
        lblTiempoActual.setText("0:00");
        lblTiempoTotal.setText(actual.getDuracionFormateada());
        actualizarEstadoBotonesReproduccion(false);

        jListBiblioteca.setSelectedValue(actual, true);
    }

    private void actualizarEstadoBotones() {
        boolean hayCanciones = controlador.cantidadCanciones() > 0;
        btnEditar.setEnabled(hayCanciones);
        btnEliminar.setEnabled(hayCanciones);
        btnBuscar.setEnabled(hayCanciones);
        btnSiguiente.setEnabled(hayCanciones);
        btnAnterior.setEnabled(hayCanciones && controlador.permiteAnterior());
    }

    private void actualizarEstadoBotonesReproduccion(boolean reproduciendo) {
        boolean haySeleccion = controlador.obtenerActual() != null;
        btnReproducir.setEnabled(haySeleccion && !reproduciendo);
        btnPausar.setEnabled(haySeleccion && reproduciendo);
    }

    /**
     * Renderiza cada fila de la biblioteca como nombre (en negrita) +
     * artista (en gris), en vez del texto plano de toString().
     */
    private class CeldaCancionRenderer extends JPanel implements ListCellRenderer<Cancion> {

        private final JLabel lblNombre = new JLabel();
        private final JLabel lblDetalle = new JLabel();

        CeldaCancionRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            lblNombre.setFont(Tema.FUENTE_BOLD);
            lblDetalle.setFont(Tema.FUENTE_PEQUENA);
            lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblDetalle.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(lblNombre);
            add(lblDetalle);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Cancion> list, Cancion cancion,
                                                        int index, boolean isSelected, boolean cellHasFocus) {
            lblNombre.setText(cancion.getNombre());
            lblDetalle.setText(cancion.getArtista());

            if (isSelected) {
                setBackground(temaActual.acento);
                lblNombre.setForeground(Color.WHITE);
                lblDetalle.setForeground(new Color(230, 230, 230));
            } else {
                setBackground(temaActual.fondoSuperficie);
                lblNombre.setForeground(temaActual.textoPrimario);
                lblDetalle.setForeground(temaActual.textoSecundario);
            }
            setOpaque(true);
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReproductorGUI().setVisible(true));
    }
}

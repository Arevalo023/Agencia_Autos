package view;

import controller.AuthController;
import controller.NuevaOrdenController;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class NuevaOrdenView extends JFrame {

    // Paleta de colores
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color SUCCESS_HOVER = new Color(22, 163, 74);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color DANGER_HOVER = new Color(220, 38, 38);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);

    // Controller 
    private final NuevaOrdenController controller = new NuevaOrdenController();

    // Componentes
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Vehiculo> cbVehiculo;
    private JComboBox<Tecnico> cbTecnico;
    private JComboBox<TipoServicio> cbTipoServicio;
    private JTextField txtManoObra;
    private JTextArea txtNotas;
    private JTextField txtProximoServicio;
    private JButton btnGuardar;
    private JButton btnCerrar;
    private JButton btnVerOrdenes;

    private String ultimoTicketTexto = null;

    public NuevaOrdenView() {
        setTitle("Nueva Orden de Servicio");
        setSize(700, 680);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Header fijo arriba
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // === Formulario con Scroll ===
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botones al final
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarClientes();
        cargarTecnicos();
        cargarTiposServicio();

        // Listeners
        cbCliente.addActionListener(e -> cargarVehiculosDelClienteSeleccionado());
        initActions();
        aplicarPermisosSegunRol();
    }

    // ====================== HEADER ======================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Nueva Orden de Servicio");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Complete los datos para registrar una nueva orden");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(BG_COLOR);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    // ====================== FORMULARIO ======================
    private JPanel createFormPanel() {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(229, 231, 235), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // --- Sección Cliente ---
        cardPanel.add(createSectionLabel("Información del Cliente"));
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(createFieldRow("Cliente:", cbCliente = createStyledComboBox()));
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(createFieldRow("Vehículo (Placa):", cbVehiculo = createStyledComboBox()));
        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(createSeparator());
        cardPanel.add(Box.createVerticalStrut(20));

        // --- Sección Servicio ---
        cardPanel.add(createSectionLabel("Detalles del Servicio"));
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(createFieldRow("Técnico Asignado:", cbTecnico = createStyledComboBox()));
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(createFieldRow("Tipo de Servicio:", cbTipoServicio = createStyledComboBox()));
        cardPanel.add(Box.createVerticalStrut(12));

        txtManoObra = createStyledTextField();
        cardPanel.add(createFieldRow("Mano de Obra ($):", txtManoObra));
        cardPanel.add(Box.createVerticalStrut(12));

     // ===== Fecha del servicio con selector =====
        txtProximoServicio = createStyledTextField();
        JButton btnPickFecha = createDatePickerButton();

        JPanel fechaRow = new JPanel(new BorderLayout(8, 0));
        fechaRow.setBackground(CARD_BG);
        fechaRow.add(txtProximoServicio, BorderLayout.CENTER);
        fechaRow.add(btnPickFecha, BorderLayout.EAST);

        cardPanel.add(createFieldRow("Fecha del servicio (yyyy-MM-dd):", fechaRow));

        cardPanel.add(Box.createVerticalStrut(20));
        cardPanel.add(createSeparator());
        cardPanel.add(Box.createVerticalStrut(20));

        // --- Sección Notas ---
        cardPanel.add(createSectionLabel("Notas Iniciales"));
        cardPanel.add(Box.createVerticalStrut(15));

        txtNotas = new JTextArea(5, 20);
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNotas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(209, 213, 219), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JScrollPane scrollNotas = new JScrollPane(txtNotas);
        scrollNotas.setBorder(BorderFactory.createLineBorder(new java.awt.Color(209, 213, 219), 1));
        scrollNotas.setPreferredSize(new Dimension(600, 100));
        scrollNotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        cardPanel.add(scrollNotas);

        return cardPanel;
    }

    // ====================== BOTONES ======================
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnVerOrdenes = createStyledButton("Ver Órdenes", PRIMARY_COLOR, PRIMARY_HOVER);
        btnGuardar = createStyledButton("Guardar Orden", SUCCESS_COLOR, SUCCESS_HOVER);
        btnCerrar = createStyledButton("Cerrar", DANGER_COLOR, DANGER_HOVER);

        panel.add(btnVerOrdenes);
        panel.add(btnGuardar);
        panel.add(btnCerrar);

        return panel;
    }

    // ====================== COMPONENTES ======================
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new java.awt.Color(229, 231, 235));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JPanel createFieldRow(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(CARD_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(200, 30));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(209, 213, 219), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return combo;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(209, 213, 219), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // ====================== CARGA DE DATOS ======================
    private void cargarClientes() {
        cbCliente.removeAllItems();
        for (Cliente c : controller.getClientesActivos()) {
            cbCliente.addItem(c);
        }
        cargarVehiculosDelClienteSeleccionado();
    }

    private void cargarVehiculosDelClienteSeleccionado() {
        cbVehiculo.removeAllItems();
        Cliente sel = (Cliente) cbCliente.getSelectedItem();
        if (sel == null) return;
        for (Vehiculo v : controller.getVehiculosActivosPorCliente(sel.getIdCliente())) {
            cbVehiculo.addItem(v);
        }
    }

    private void cargarTecnicos() {
        cbTecnico.removeAllItems();
        for (Tecnico t : controller.getTecnicosActivos()) {
            cbTecnico.addItem(t);
        }
    }

    private void cargarTiposServicio() {
        cbTipoServicio.removeAllItems();
        for (TipoServicio ts : controller.getTiposServicioActivos()) {
            cbTipoServicio.addItem(ts);
        }
    }

    // ====================== ACCIONES ======================
    private void initActions() {
        btnCerrar.addActionListener(e -> dispose());

        btnVerOrdenes.addActionListener(e -> new OrdenesView().setVisible(true));

        btnGuardar.addActionListener(e -> {
            Cliente cli = (Cliente) cbCliente.getSelectedItem();
            Vehiculo veh = (Vehiculo) cbVehiculo.getSelectedItem();
            Tecnico tec = (Tecnico) cbTecnico.getSelectedItem();
            TipoServicio tipo = (TipoServicio) cbTipoServicio.getSelectedItem();

            if (cli == null || veh == null || tec == null || tipo == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Todos los campos son obligatorios.",
                        "Faltan datos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            double manoObra;
            try {
                manoObra = Double.parseDouble(txtManoObra.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Mano de obra inválida.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            LocalDate prox = null;
            String proxTxt = txtProximoServicio.getText().trim();
            if (!proxTxt.isEmpty()) {
                try {
                    prox = LocalDate.parse(proxTxt, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Fecha inválida. Usa yyyy-MM-dd.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }

            String notas = txtNotas.getText().trim();

            // crear orden vía controller
            OrdenServicio nueva = controller.crearNuevaOrden(
                    cli,
                    veh,
                    tec,
                    tipo,
                    manoObra,
                    notas,
                    prox
            );

            if (nueva == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar la orden (sesión inválida o error).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            limpiarFormulario();
            mostrarComprobante(nueva);
        });
    }

    // ====================== UTILIDADES ======================
    private void limpiarFormulario() {
        txtManoObra.setText("");
        txtNotas.setText("");
        txtProximoServicio.setText("");

        if (cbCliente.getItemCount() > 0) cbCliente.setSelectedIndex(0);
        cargarVehiculosDelClienteSeleccionado();

        if (cbTecnico.getItemCount() > 0) cbTecnico.setSelectedIndex(0);
        if (cbTipoServicio.getItemCount() > 0) cbTipoServicio.setSelectedIndex(0);
    }

    private void mostrarComprobante(OrdenServicio o) {
        // ticket desde el controller
        ultimoTicketTexto = controller.buildTicketText(o);

        JTextArea area = new JTextArea(ultimoTicketTexto);
        area.setEditable(false);
        area.setFont(new Font("monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 320));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Comprobante / Resumen",
                JOptionPane.INFORMATION_MESSAGE
        );

        int opc = JOptionPane.showConfirmDialog(
                this,
                "¿Quieres guardar este comprobante como imagen (PNG)?",
                "Exportar ticket",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opc == JOptionPane.YES_OPTION) {
            guardarTicketComoPNG(ultimoTicketTexto);
        }
    }

    private void guardarTicketComoPNG(String ticketText) {
        if (ticketText == null || ticketText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay ticket para guardar.",
                    "Nada que exportar",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Font font = new Font("monospaced", Font.PLAIN, 14);

        String[] lineas = ticketText.split("\n");

        BufferedImage tmpImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2tmp = tmpImg.createGraphics();
        g2tmp.setFont(font);
        FontMetrics fm = g2tmp.getFontMetrics();

        int maxWidth = 0;
        for (String linea : lineas) {
            int w = fm.stringWidth(linea);
            if (w > maxWidth) maxWidth = w;
        }
        int lineHeight = fm.getHeight();
        int padding = 20;

        int imgWidth = maxWidth + padding * 2;
        int imgHeight = lineHeight * lineas.length + padding * 2;

        g2tmp.dispose();

        BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, imgWidth, imgHeight);

        g2.setColor(Color.BLACK);
        g2.setFont(font);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int x = padding;
        int y = padding + fm.getAscent();
        for (String linea : lineas) {
            g2.drawString(linea, x, y);
            y += lineHeight;
        }
        g2.dispose();

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar ticket como imagen");
        chooser.setSelectedFile(new File(
                "ticket_" +
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                                .format(java.time.LocalDateTime.now()) +
                        ".png"
        ));

        int userChoice = chooser.showSaveDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File destino = chooser.getSelectedFile();

            String path = destino.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".png")) {
                destino = new File(path + ".png");
            }

            try {
                ImageIO.write(img, "png", destino);
                JOptionPane.showMessageDialog(
                        this,
                        "Ticket guardado en:\n" + destino.getAbsolutePath(),
                        "PNG generado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar la imagen:\n" + ex.getMessage(),
                        "Error al guardar",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void aplicarPermisosSegunRol() {
        Usuario u = AuthController.getUsuarioActual();
        if (u == null || !"ADMIN".equalsIgnoreCase(u.getRol())) {
            bloquearEdicion();
        }
    }
    
    private JButton createDatePickerButton() {
        JButton btn = new JButton("📅");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(209, 213, 219), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Acción: abrir selector de fecha
        btn.addActionListener(e -> {
            // Spinner de fecha
            JSpinner spinnerFecha = new JSpinner(
                    new SpinnerDateModel(
                            new java.util.Date(), // valor inicial = hoy
                            null,                 // mínimo
                            null,                 // máximo
                            java.util.Calendar.DAY_OF_MONTH
                    )
            );
            spinnerFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Formato visual amigable dentro del spinner
            JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerFecha, "yyyy-MM-dd");
            spinnerFecha.setEditor(editor);

            // Mostramos diálogo emergente
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    spinnerFecha,
                    "Seleccionar fecha del servicio",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion == JOptionPane.OK_OPTION) {
                java.util.Date seleccion = (java.util.Date) spinnerFecha.getValue();
                // Lo convertimos a LocalDate y lo mandamos al textfield

                LocalDate ld = seleccion.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();

                txtProximoServicio.setText(ld.toString()); // yyyy-MM-dd
            }
        });

        return btn;
    }


    private void bloquearEdicion() {
        btnGuardar.setEnabled(false);
        cbCliente.setEnabled(false);
        cbVehiculo.setEnabled(false);
        cbTecnico.setEnabled(false);
        cbTipoServicio.setEnabled(false);
        txtManoObra.setEditable(false);
        txtNotas.setEditable(false);
        txtProximoServicio.setEditable(false);
    }
}

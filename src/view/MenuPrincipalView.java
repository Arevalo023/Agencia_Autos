package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.AuthController;
import controller.MenuPrincipalController;


/**
 * MenuPrincipalView
 *
 * Ventana principal después de iniciar sesión.
 * Desde aquí se navega a todo el sistema.
 *
 * Ya sin lógica de sesión directa: eso vive en MenuPrincipalController.
 */
public class MenuPrincipalView extends JFrame {

    private static final Color COLOR_PRIMARY = new Color(37, 99, 235);      // Azul moderno
    private static final Color COLOR_SECONDARY = new Color(99, 102, 241);   // Índigo
    private static final Color COLOR_SUCCESS = new Color(34, 197, 94);      // Verde
    private static final Color COLOR_WARNING = new Color(251, 146, 60);     // Naranja
    private static final Color COLOR_INFO = new Color(14, 165, 233);        // Cyan
    private static final Color COLOR_DANGER = new Color(239, 68, 68);       // Rojo
    private static final Color COLOR_BACKGROUND = new Color(248, 250, 252); // Gris muy claro
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color COLOR_TEXT_SECONDARY = new Color(100, 116, 139);

    private final MenuPrincipalController controller;

    private JLabel lblSesionUsuario;
    private JButton btnCatalogos;
    private JButton btnClienteVehiculo;
    private JButton btnServicios;
    private JButton btnProximos;
    private JButton btnDashboard;
    private JButton btnCerrarSesion;

    // Constructor público para usar en runtime normal
    public MenuPrincipalView(AuthController authController) {
        this(new MenuPrincipalController(authController));
    }

    // Constructor interno para inyectar directamente el controller (útil en tests)
    public MenuPrincipalView(MenuPrincipalController controller) {
        this.controller = controller;

        setTitle("Agencia de Autos - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setLayout(new BorderLayout(0, 0));
        getContentPane().add(mainPanel);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // acciones botones / listeners
        initActions();

        // mostrar info de quién inició sesión
        cargarInfoSesion();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_CARD);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(20, 30, 20, 30)
        ));

        // Título
        JLabel lblTitulo = new JLabel("Panel Principal");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_TEXT_PRIMARY);
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        // Info de sesión
        lblSesionUsuario = new JLabel("Sesión: ----");
        lblSesionUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSesionUsuario.setForeground(COLOR_TEXT_SECONDARY);
        lblSesionUsuario.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(lblSesionUsuario, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(COLOR_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Fila 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        btnCatalogos = createMenuButton("Catálogos", "Gestionar marcas, modelos y más", COLOR_SECONDARY);
        contentPanel.add(btnCatalogos, gbc);

        gbc.gridx = 1;
        btnClienteVehiculo = createMenuButton("Clientes / Vehículos", "Administrar clientes y sus vehículos", COLOR_INFO);
        contentPanel.add(btnClienteVehiculo, gbc);

        // Fila 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        btnServicios = createMenuButton("Servicios", "Órdenes de servicio y mantenimiento", COLOR_SUCCESS);
        contentPanel.add(btnServicios, gbc);

        gbc.gridx = 1;
        btnProximos = createMenuButton("Próximos Servicios", "Ver servicios programados", COLOR_WARNING);
        contentPanel.add(btnProximos, gbc);

        // Fila 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        btnDashboard = createMenuButton("Dashboard", "Estadísticas y reportes", COLOR_PRIMARY);
        contentPanel.add(btnDashboard, gbc);

        gbc.gridx = 1;
        btnCerrarSesion = createMenuButton("Cerrar Sesión", "Salir del sistema", COLOR_DANGER);
        contentPanel.add(btnCerrarSesion, gbc);

        return contentPanel;
    }

    private JButton createMenuButton(String title, String description, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 5));
        button.setBackground(COLOR_CARD);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 8));
        contentPanel.setOpaque(false);

        JPanel colorIndicator = new JPanel();
        colorIndicator.setBackground(color);
        colorIndicator.setPreferredSize(new Dimension(4, 40));
        contentPanel.add(colorIndicator, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDescription = new JLabel(description);
        lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescription.setForeground(COLOR_TEXT_SECONDARY);
        lblDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(lblDescription);

        contentPanel.add(textPanel, BorderLayout.CENTER);
        button.add(contentPanel);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(249, 250, 251));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 2),
                        new EmptyBorder(19, 19, 19, 19)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(COLOR_CARD);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                        new EmptyBorder(20, 20, 20, 20)
                ));
            }
        });

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(COLOR_CARD);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                new EmptyBorder(15, 30, 15, 30)
        ));

        JLabel lblFooter = new JLabel("Agencia de Automóviles - Sistema de Mantenimiento");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(COLOR_TEXT_SECONDARY);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);

        footerPanel.add(lblFooter);

        return footerPanel;
    }

    /**
     * Pide al controller el texto de sesión.
     */
    private void cargarInfoSesion() {
        lblSesionUsuario.setText(controller.getTextoSesionHeader());
    }

    /**
     * Conectar listeners a cada botón.
     * Nota: ya casi nada de lógica vive aquí,
     * salvo mostrar JOptionPane y abrir ventanas,
     * que sí es responsabilidad de la vista.
     */
    private void initActions() {

        // Catálogos
        btnCatalogos.addActionListener(e -> {
            CatalogosView catView = new CatalogosView(controller.getAuthController());
            catView.setVisible(true);
        });

        // Clientes / Vehículos
        btnClienteVehiculo.addActionListener(e -> {
            ClientesVehiculosView cv = new ClientesVehiculosView(controller.getAuthController());
            cv.setVisible(true);
        });

        // Servicios (Orden de Servicio) - SOLO ADMIN
        btnServicios.addActionListener(e -> {
            String restriccion = controller.validarAccesoServicios();

            if (restriccion != null) {
                JOptionPane.showMessageDialog(
                        MenuPrincipalView.this,
                        restriccion,
                        "Permiso denegado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            NuevaOrdenView vista = new NuevaOrdenView();
            vista.setVisible(true);
        });

     // Próximos servicios
        btnProximos.addActionListener(e -> {
            ProximosServiciosView ps = new ProximosServiciosView();
            ps.setVisible(true);
        });

        // Dashboard (gráfica de estatus)
        btnDashboard.addActionListener(e -> {

         
            DashboardView dash = new DashboardView(controller);
            dash.setVisible(true);
        });

        // Cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(
                    MenuPrincipalView.this,
                    "¿Seguro que deseas cerrar sesión?",
                    "Cerrar sesión",
                    JOptionPane.YES_NO_OPTION
            );

            if (opt == JOptionPane.YES_OPTION) {
                // 1. cerrar sesión en controller
                controller.cerrarSesion();

                // 2. cerrar esta ventana
                dispose();

                // 3. volver al login
                LoginView login = new LoginView();
                login.setVisible(true);
            }
        });
    }
}

package view;
 
import controller.AuthController;
import controller.LoginScreenController;
import controller.LoginScreenController.ResultadoLogin;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
 
public class LoginView extends JFrame {
 
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
 
    // Ahora la vista ya no conoce directamente AuthController,
    // solo trata con el controlador de pantalla:
    private final LoginScreenController loginController;
 
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_PRIMARIO_HOVER = new Color(29, 78, 216);
    private static final Color COLOR_FONDO = new Color(249, 250, 251);
    private static final Color COLOR_PANEL = Color.WHITE;
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(107, 114, 128);
 
    // --- Constructores ---
    public LoginView() {
        this(new LoginScreenController(new AuthController()));
    }
 
    public LoginView(LoginScreenController loginController) {
        this.loginController = loginController;
 
        setTitle("Agencia de Automóviles - Módulo de Mantenimiento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setResizable(false);
 
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));
        getContentPane().add(panelPrincipal);
 
        JPanel panelFormulario = crearPanelFormulario();
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
    }
 
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(30, 30, 30, 30)
        ));
 
        JLabel lblTitulo = new JLabel("Iniciar sesión");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel lblSubtitulo = new JLabel("Sistema de gestión de servicios y mantenimiento vehicular");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(lblSubtitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
 
        txtUsuario = crearCampoTexto();
        panel.add(crearCampoConLabel("Usuario", txtUsuario));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
 
        txtPassword = new JPasswordField();
        configurarEstiloCampo(txtPassword);
        panel.add(crearCampoConLabel("Contraseña", txtPassword));
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
 
        btnIngresar = crearBotonIngresar();
        btnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnIngresar);
 
        agregarListenerEnter();
 
        return panel;
    }
 
    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        configurarEstiloCampo(campo);
        return campo;
    }
 
    private void configurarEstiloCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campo.setPreferredSize(new Dimension(400, 40));
        campo.setMaximumSize(new Dimension(400, 40));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }
 
    private JPanel crearCampoConLabel(String textoLabel, JTextField campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel label = new JLabel(textoLabel);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(COLOR_TEXTO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(campo);
 
        return panel;
    }
 
    private JButton crearBotonIngresar() {
        JButton boton = new JButton("Ingresar");
        boton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        boton.setBackground(COLOR_PRIMARIO);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(400, 44));
        boton.setMaximumSize(new Dimension(400, 44));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(COLOR_PRIMARIO_HOVER);
            }
 
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(COLOR_PRIMARIO);
            }
        });
 
        boton.addActionListener(e -> intentarLogin());
 
        return boton;
    }
 
    private void agregarListenerEnter() {
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        };
 
        txtUsuario.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }
 
    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
 
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarMensaje(
                    "Por favor ingresa usuario y contraseña.",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
 
        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");
 
        ResultadoLogin resultado = loginController.intentarLogin(usuario, password);
 
        if (resultado.exito) {
            mostrarMensaje(
                    resultado.mensaje,
                    "Acceso concedido",
                    JOptionPane.INFORMATION_MESSAGE
            );
            abrirMenuPrincipal();
            dispose();
        } else {
            mostrarMensaje(
                    resultado.mensaje,
                    "Acceso denegado",
                    JOptionPane.ERROR_MESSAGE
            );
 
            btnIngresar.setEnabled(true);
            btnIngresar.setText("Ingresar");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
 
    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
 
    private void abrirMenuPrincipal() {
        // Le pasamos el AuthController real para que el menú sepa quién inició sesión
        MenuPrincipalView menu = new MenuPrincipalView(
                loginController.getAuthController()
        );
        menu.setVisible(true);
    }
 
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        SwingUtilities.invokeLater(() -> {
            LoginView frame = new LoginView(); // usa el constructor default
            frame.setVisible(true);
        });
    }
}
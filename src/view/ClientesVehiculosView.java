package view;

import controller.AuthController;
import model.*;
import model.dao.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * ClientesVehiculosView
 *
 * Ventana separada para gestionar:
 *  - Clientes (CRUD)
 *  - Vehículos (CRUD, ligado a cliente + modelo + año)
 *
 * Esta vista vive aparte del módulo Catálogos.
 * Esto alimenta el botón "Clientes / Vehículos" del menú principal.
 */
public class ClientesVehiculosView extends JFrame {

    // ===== colores / estilo reutilizado (mismos que CatalogosView bonito) =====
    private static final Color BG_COLOR        = new Color(248, 250, 252); // gris clarito fondo
    private static final Color CARD_COLOR      = Color.WHITE;              // paneles blancos
    private static final Color BORDER_COLOR    = new Color(226, 232, 240); // gris borde
    private static final Color TEXT_PRIMARY    = new Color(15, 23, 42);    // casi negro
    private static final Color TEXT_SECONDARY  = new Color(100, 116, 139); // gris texto
    private static final Color PRIMARY_COLOR   = new Color(37, 99, 235);   // azul
    private static final Color PRIMARY_HOVER   = new Color(29, 78, 216);
    private static final Color SUCCESS_COLOR   = new Color(34, 197, 94);   // verde
    private static final Color SUCCESS_HOVER   = new Color(22, 163, 74);
    private static final Color WARNING_COLOR   = new Color(251, 146, 60);  // naranja
    private static final Color WARNING_HOVER   = new Color(234, 88, 12);
    private static final Color DANGER_COLOR    = new Color(239, 68, 68);   // rojo
    private static final Color DANGER_HOVER    = new Color(220, 38, 38);

    // ===== DAOs =====
    private final ClienteDAO clienteDAO      = new ClienteDAO();
    private final VehiculoDAO vehiculoDAO    = new VehiculoDAO();
    private final MarcaDAO marcaDAO          = new MarcaDAO();
    private final ModeloAutoDAO modeloDAO    = new ModeloAutoDAO();
    private final AnioModeloDAO anioDAO      = new AnioModeloDAO();

    // ===== COMPONENTES CLIENTE =====
    private DefaultTableModel tmCli;
    private JTable tblCli;
    private JTextField txtCliId;
    private JTextField txtCliNom;
    private JTextField txtCliApe;
    private JTextField txtCliTel;
    private JTextField txtCliMail;

    // ===== COMPONENTES VEHICULO =====
    private DefaultTableModel tmVeh;
    private JTable tblVeh;
    private JComboBox<Cliente> cbCli;
    private JComboBox<ModeloAuto> cbVehModelo;
    private JComboBox<AnioModelo> cbVehAnio;
    private JTextField txtVehId;
    private JTextField txtVehPlaca;
    private JTextField txtVehVin;
    private JTextField txtVehColor;
    private JTextField txtVehKm;
    
 // ===== BOTONES CLIENTE (para permisos) =====
    private JButton btnCliAdd;
    private JButton btnCliUpd;
    private JButton btnCliDel;
    private JButton btnCliRea;
    private JButton btnCliClean;
    private JButton btnCliHardDelete;   // <-- NUEVO

    // ===== BOTONES VEHICULO (para permisos) =====
    private JButton btnVehAdd;
    private JButton btnVehUpd;
    private JButton btnVehDel;
    private JButton btnVehRea;
    private JButton btnVehClean;
    private JButton btnVehHardDelete;   // <-- NUEVO



    public ClientesVehiculosView(AuthController authController) {
        setTitle("Clientes y Vehículos");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        //setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setBackground(BG_COLOR);
        getContentPane().setLayout(new BorderLayout());

        // Header bonito arriba
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel titulo = new JLabel("Gestión de Clientes y Vehículos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT_PRIMARY);
        header.add(titulo, BorderLayout.WEST);

        JLabel usuarioLbl = new JLabel();
        usuarioLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usuarioLbl.setForeground(TEXT_SECONDARY);
        Usuario actual = AuthController.getUsuarioActual();
        if (actual != null) {
            usuarioLbl.setText(actual.getNombreCompleto() + " • " + actual.getRol());
        } else {
            usuarioLbl.setText("Sesión desconocida");
        }
        header.add(usuarioLbl, BorderLayout.EAST);

        getContentPane().add(header, BorderLayout.NORTH);

        // Tabs centro
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_COLOR);
        tabs.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabs.addTab("Clientes", crearPanelClientes());
        tabs.addTab("Vehículos", crearPanelVehiculos());

        getContentPane().add(tabs, BorderLayout.CENTER);
        aplicarPermisosSegunRol();

     // Footer con botón Cerrar
        JPanel footer = createFooterPanel();
        getContentPane().add(footer, BorderLayout.SOUTH);

    }

    // ==========================================================
    // HELPERS DE ESTILO (copiados de tu CatalogosView estilizado)
    // ==========================================================
    private void styleTable(JTable tbl) {
        tbl.setRowHeight(24);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setGridColor(BORDER_COLOR);
        tbl.setSelectionBackground(new Color(219, 234, 254));
        tbl.setSelectionForeground(TEXT_PRIMARY);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl.getTableHeader().setBackground(new Color(241, 245, 249));
        tbl.getTableHeader().setForeground(TEXT_PRIMARY);
        tbl.setFillsViewportHeight(true);
    }

    private JTextField createStyledTextField(boolean editable) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setForeground(TEXT_PRIMARY);
        txt.setBackground(Color.WHITE);
        txt.setCaretColor(TEXT_PRIMARY);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        txt.setEditable(editable);
        return txt;
    }

    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    private JButton createStyledButton(String text, Color base, Color hover) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.black);
        btn.setBackground(base);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(base);  }
        });

        return btn;
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD_COLOR);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        return p;
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new GridLayout(2, 3, 10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        return p;
    }

    private JComboBox<?> createStyledComboBox() {
        JComboBox<Object> cb = new JComboBox<>();
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT_PRIMARY);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return cb;
    }

    // ==========================================================
    //  TAB CLIENTES
    // ==========================================================
    private JPanel crearPanelClientes() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // tabla clientes
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        tmCli = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Apellidos", "Teléfono", "Email", "Activo"}, 0
        );
        tblCli = new JTable(tmCli);
        styleTable(tblCli);

        
     // ===== barra de búsqueda clientes (con altura segura) =====
        JPanel searchCliWrapper = new JPanel();
        searchCliWrapper.setLayout(new BorderLayout());
        searchCliWrapper.setBackground(CARD_COLOR);

        // fila interna con FlowLayout (los controles uno tras otro)
        JPanel searchCliPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchCliPanel.setOpaque(false); // hereda color del wrapper

        JLabel lblBuscarCli = new JLabel("Buscar cliente:");
        lblBuscarCli.setForeground(TEXT_SECONDARY);
        lblBuscarCli.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTextField txtBuscarCli = new JTextField(15);
        txtBuscarCli.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarCli.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        JButton btnBuscarCli = createStyledButton("Buscar", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton btnResetCli  = createStyledButton("Mostrar todo", BG_COLOR, BORDER_COLOR);
        btnResetCli.setForeground(TEXT_PRIMARY);

        // agregar controles a la fila
        searchCliPanel.add(lblBuscarCli);
        searchCliPanel.add(txtBuscarCli);
        searchCliPanel.add(btnBuscarCli);
        searchCliPanel.add(btnResetCli);

        // damos margen abajo para que no quede pegado a la tabla
        searchCliWrapper.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        searchCliWrapper.add(searchCliPanel, BorderLayout.WEST);

        // ahora sí lo metemos ARRIBA de la tabla
        tablePanel.add(searchCliWrapper, BorderLayout.NORTH);



        JScrollPane sc = new JScrollPane(tblCli);
        sc.setBorder(null);
        tablePanel.add(sc, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        p.add(tablePanel, gbc);

        // form cliente
        JPanel formPanel = createFormPanel();

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        p.add(formPanel, gbc);

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = GridBagConstraints.REMAINDER;
        formGbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        JLabel titleForm = new JLabel("Información de Cliente");
        titleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleForm.setForeground(TEXT_PRIMARY);
        formGbc.gridy = row++;
        formPanel.add(titleForm, formGbc);

        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("ID:"), formGbc);
        formGbc.gridy = row++;
        txtCliId = createStyledTextField(false);
        formPanel.add(txtCliId, formGbc);

        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Nombre:"), formGbc);
        formGbc.gridy = row++;
        txtCliNom = createStyledTextField(true);
        formPanel.add(txtCliNom, formGbc);

        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Apellidos:"), formGbc);
        formGbc.gridy = row++;
        txtCliApe = createStyledTextField(true);
        formPanel.add(txtCliApe, formGbc);

        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Teléfono:"), formGbc);
        formGbc.gridy = row++;
        txtCliTel = createStyledTextField(true);
        formPanel.add(txtCliTel, formGbc);

        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Email:"), formGbc);
        formGbc.gridy = row++;
        txtCliMail = createStyledTextField(true);
        formPanel.add(txtCliMail, formGbc);

        // botones cliente
        btnCliAdd   = createStyledButton("Agregar",        SUCCESS_COLOR,  SUCCESS_HOVER);
        btnCliUpd   = createStyledButton("Actualizar",     PRIMARY_COLOR,  PRIMARY_HOVER);
        btnCliDel   = createStyledButton("Desactivar",     DANGER_COLOR,   DANGER_HOVER);
        btnCliRea   = createStyledButton("Reactivar",      WARNING_COLOR,  WARNING_HOVER);
        btnCliClean = createStyledButton("Limpiar Campos", BG_COLOR,       BORDER_COLOR);
        btnCliClean.setForeground(TEXT_PRIMARY);
        btnCliHardDelete = createStyledButton("Eliminar DEFINITIVO", Color.BLACK, Color.DARK_GRAY);
        btnCliHardDelete.setForeground(Color.RED);


        JPanel buttonPanel = createButtonPanel();
        buttonPanel.add(btnCliAdd);
        buttonPanel.add(btnCliUpd);
        buttonPanel.add(btnCliDel);
        buttonPanel.add(btnCliRea);
        buttonPanel.add(btnCliClean);
        buttonPanel.add(btnCliHardDelete);

        formGbc.gridy = row++;
        formGbc.weighty = 1.0;
        formGbc.anchor = GridBagConstraints.SOUTH;
        formGbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonPanel, formGbc);

        // data-binding
        cargarClientes();
        
     // Buscar por nombre / apellido
        btnBuscarCli.addActionListener(e -> {
            String texto = txtBuscarCli.getText().trim();
            if (texto.isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Escribe el nombre EXACTO del cliente.",
                        "Búsqueda",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            
            cargarClientesFiltrados(texto);
        });

        // Reset tabla
        btnResetCli.addActionListener(e -> {
            cargarClientes();
            txtBuscarCli.setText("");
        });


        tblCli.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tblCli.getSelectedRow();
                if (f != -1) {
                    txtCliId.setText(tmCli.getValueAt(f, 0).toString());
                    txtCliNom.setText(tmCli.getValueAt(f, 1).toString());
                    txtCliApe.setText(tmCli.getValueAt(f, 2).toString());
                    txtCliTel.setText(tmCli.getValueAt(f, 3).toString());
                    txtCliMail.setText(tmCli.getValueAt(f, 4).toString());
                }
            }
        });

        btnCliAdd.addActionListener(e -> {
            if (txtCliNom.getText().trim().isEmpty()
             || txtCliApe.getText().trim().isEmpty()
             || txtCliTel.getText().trim().isEmpty()
             || txtCliMail.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Complete todos los campos del cliente",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            clienteDAO.insertar(
                    txtCliNom.getText(),
                    txtCliApe.getText(),
                    txtCliTel.getText(),
                    txtCliMail.getText()
            );
            cargarClientes();
            limpiarCamposCli();
        });

        btnCliUpd.addActionListener(e -> {
            if (txtCliId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Seleccione un cliente de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            if (txtCliTel.getText().trim().isEmpty()
             || txtCliMail.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Complete Teléfono y Email",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            clienteDAO.actualizar(
                    Integer.parseInt(txtCliId.getText()),
                    txtCliTel.getText(),
                    txtCliMail.getText()
            );
            cargarClientes();
            limpiarCamposCli();
        });

        btnCliDel.addActionListener(e -> {
            if (txtCliId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Seleccione un cliente de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            clienteDAO.desactivar(Integer.parseInt(txtCliId.getText()));
            cargarClientes();
            limpiarCamposCli();
        });
        btnCliHardDelete.addActionListener(e -> {
            if (txtCliId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Seleccione un cliente de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int idCliente = Integer.parseInt(txtCliId.getText());

            // 1. validar si tiene vehículos
            int cuantosVehiculos = vehiculoDAO.contarVehiculosPorCliente(idCliente);
            if (cuantosVehiculos > 0) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Este cliente tiene " + cuantosVehiculos + " vehículo(s) asociado(s).\n" +
                        "No se puede eliminar definitivamente.",
                        "Acción no permitida",
                        JOptionPane.ERROR_MESSAGE
                );
                return; // abortamos aquí, ni preguntamos confirmación
            }

            // 2. si no tiene vehículos, entonces sí preguntamos confirmación final
            int opt = JOptionPane.showConfirmDialog(
                    ClientesVehiculosView.this,
                    "Esto BORRA al cliente de la base de datos.\n" +
                    "Esta acción NO se puede deshacer.\n\n" +
                    "¿Seguro que quieres ELIMINAR DEFINITIVO?",
                    "Confirmar eliminación permanente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opt != JOptionPane.YES_OPTION) {
                return;
            }

            // 3. ejecutar el borrado real
            try {
                clienteDAO.eliminarDefinitivo(idCliente);
                cargarClientes();
                limpiarCamposCli();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "No se pudo eliminar el cliente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnCliRea.addActionListener(e -> {
            if (txtCliId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Seleccione un cliente de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            clienteDAO.reactivar(Integer.parseInt(txtCliId.getText()));
            cargarClientes();
            limpiarCamposCli();
        });

        btnCliClean.addActionListener(e -> limpiarCamposCli());

        return p;
        
        
        
    }

    private void limpiarCamposCli() {
        txtCliId.setText("");
        txtCliNom.setText("");
        txtCliApe.setText("");
        txtCliTel.setText("");
        txtCliMail.setText("");
    }

    private void cargarClientes() {
        tmCli.setRowCount(0);
        for (Cliente c : clienteDAO.listar()) {
            tmCli.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getApellidos(),
                    c.getTelefono(),
                    c.getEmail(),
                    c.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void cargarClientesFiltrados(String nombreExacto) {
        tmCli.setRowCount(0);
        for (Cliente c : clienteDAO.buscarPorNombreExacto(nombreExacto)) {
            tmCli.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getApellidos(),
                    c.getTelefono(),
                    c.getEmail(),
                    c.isActivo() ? "Sí" : "No"
            });
        }
    }



    // ==========================================================
    //  TAB VEHÍCULOS
    // ==========================================================
    private JPanel crearPanelVehiculos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

     // =============== COLUMNA IZQUIERDA (tabla vehículos) ===============
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;          // igual que Clientes
        gbc.weighty = 1.0;
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.insets  = new Insets(10, 10, 10, 5); // <- margen de la derecha pequeño (5px)

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

     // --- barra de búsqueda vehículos ---
        JPanel searchVehPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchVehPanel.setBackground(CARD_COLOR);

        JLabel lblBuscarVeh = new JLabel("Buscar por placa:");
        lblBuscarVeh.setForeground(TEXT_SECONDARY);
        lblBuscarVeh.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTextField txtBuscarVeh = new JTextField(12);
        txtBuscarVeh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarVeh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        JButton btnBuscarVeh = createStyledButton("Buscar", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton btnResetVeh  = createStyledButton("Mostrar todo", BG_COLOR, BORDER_COLOR);
        btnResetVeh.setForeground(TEXT_PRIMARY);

        // NUEVO botón refrescar catálogo
        JButton btnRefrescarCatalogo = createStyledButton("Refrescar catálogo", PRIMARY_COLOR, PRIMARY_HOVER);

        // agregamos en orden visual bonito:
        searchVehPanel.add(lblBuscarVeh);
        searchVehPanel.add(txtBuscarVeh);
        searchVehPanel.add(btnBuscarVeh);
        searchVehPanel.add(btnResetVeh);
        searchVehPanel.add(btnRefrescarCatalogo);

        tablePanel.add(searchVehPanel, BorderLayout.NORTH);


        // tabla + scroll
        tmVeh = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Modelo", "Año", "Placa", "VIN", "Color", "KM", "Activo"}, 0
        );
        tblVeh = new JTable(tmVeh);
        styleTable(tblVeh);

        JScrollPane scTabla = new JScrollPane(tblVeh);
        scTabla.setBorder(null);
        tablePanel.add(scTabla, BorderLayout.CENTER);

        // añadimos la columna izquierda al grid
        p.add(tablePanel, gbc);

        // =============== COLUMNA DERECHA (formulario vehículo) ===============
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill    = GridBagConstraints.BOTH;
        // OJO: margen izquierdo grande para separar de la tabla
        gbc.insets  = new Insets(10, 5, 10, 10); // <- margen izq 5, der 10

        // el form real
        JPanel formPanel = createFormPanel();

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = GridBagConstraints.REMAINDER;
        formGbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        JLabel titleForm = new JLabel("Información de Vehículo");
        titleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleForm.setForeground(TEXT_PRIMARY);
        formGbc.gridy = row++;
        formPanel.add(titleForm, formGbc);

        // Cliente
        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Cliente:"), formGbc);
        formGbc.gridy = row++;
        cbCli = (JComboBox<Cliente>) createStyledComboBox();
        formPanel.add(cbCli, formGbc);

     // Modelo
        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Modelo:"), formGbc);

        // fila con combo Modelo + botón refrescar catálogo
        formGbc.gridy = row++;
        {
            JPanel modeloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            modeloRow.setOpaque(false);

            cbVehModelo = (JComboBox<ModeloAuto>) createStyledComboBox();
            modeloRow.add(cbVehModelo);

            formPanel.add(modeloRow, formGbc);

        }

        // listener: cuando cambie el modelo seleccionado, recargamos años válidos
        cbVehModelo.addActionListener(e -> {
            recargarAniosParaModeloSeleccionado();
        });

        // Año
        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Año:"), formGbc);

        formGbc.gridy = row++;
        cbVehAnio = (JComboBox<AnioModelo>) createStyledComboBox();
        formPanel.add(cbVehAnio, formGbc);


        // ID
        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("ID:"), formGbc);
        formGbc.gridy = row++;
        txtVehId = createStyledTextField(false);
        formPanel.add(txtVehId, formGbc);

        // Placa
        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Placa:"), formGbc);
        formGbc.gridy = row++;
        txtVehPlaca = createStyledTextField(true);
        formPanel.add(txtVehPlaca, formGbc);

        // VIN
        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("VIN:"), formGbc);
        formGbc.gridy = row++;
        txtVehVin = createStyledTextField(true);
        formPanel.add(txtVehVin, formGbc);

        // Color
        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Color:"), formGbc);
        formGbc.gridy = row++;
        txtVehColor = createStyledTextField(true);
        formPanel.add(txtVehColor, formGbc);

        // KM
        formGbc.gridy = row++;
        formPanel.add(createStyledLabel("Kilometraje:"), formGbc);
        formGbc.gridy = row++;
        txtVehKm = createStyledTextField(true);
        formPanel.add(txtVehKm, formGbc);

        // botones
        btnVehAdd   = createStyledButton("Agregar",        SUCCESS_COLOR,  SUCCESS_HOVER);
        btnVehUpd   = createStyledButton("Actualizar",     PRIMARY_COLOR,  PRIMARY_HOVER);
        btnVehDel   = createStyledButton("Desactivar",     DANGER_COLOR,   DANGER_HOVER);
        btnVehRea   = createStyledButton("Reactivar",      WARNING_COLOR,  WARNING_HOVER);
        btnVehClean = createStyledButton("Limpiar Campos", BG_COLOR,       BORDER_COLOR);
        btnVehClean.setForeground(TEXT_PRIMARY);
        btnVehHardDelete = createStyledButton("Eliminar DEFINITIVO", Color.BLACK, Color.DARK_GRAY);
        btnVehHardDelete.setForeground(Color.RED);

        JPanel buttonPanel1 = createButtonPanel();
        buttonPanel1.add(btnVehAdd);
        buttonPanel1.add(btnVehUpd);
        buttonPanel1.add(btnVehDel);
        buttonPanel1.add(btnVehRea);
        buttonPanel1.add(btnVehClean);
        buttonPanel1.add(btnVehHardDelete);

        formGbc.gridy = row++;
        formGbc.weighty = 1.0;
        formGbc.anchor = GridBagConstraints.NORTH;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel1, formGbc);

        // ahora, para permitir scroll en formulario alto,
        // igual que ya hacías, pero con padding alrededor:
        JScrollPane scForm = new JScrollPane(formPanel);
        scForm.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scForm.getVerticalScrollBar().setUnitIncrement(16);

        // wrapper para que no quede pegado directo
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.setBorder(new EmptyBorder(0, 0, 0, 0)); // si quieres más aire interno, pon (0,10,0,0)
        rightWrapper.add(scForm, BorderLayout.CENTER);

        // añadimos la columna derecha al grid
        p.add(rightWrapper, gbc);


        // ================= DATA / EVENTOS =================

        cargarCombosVehiculos();
        cargarVehiculos();
        
        btnBuscarVeh.addActionListener(e -> {
            String placa = txtBuscarVeh.getText().trim();
            if (placa.isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Escribe una placa para buscar.",
                        "Búsqueda",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            cargarVehiculosFiltradosPorPlaca(placa);
        });

        btnResetVeh.addActionListener(e -> {
            cargarVehiculos();
            txtBuscarVeh.setText("");
        });


        tblVeh.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tblVeh.getSelectedRow();
                if (f == -1) return;

                txtVehId.setText(tmVeh.getValueAt(f, 0).toString());
                txtVehPlaca.setText(tmVeh.getValueAt(f, 4).toString());
                txtVehVin.setText(tmVeh.getValueAt(f, 5).toString());
                txtVehColor.setText(tmVeh.getValueAt(f, 6).toString());
                txtVehKm.setText(tmVeh.getValueAt(f, 7).toString());

                String clienteTabla = tmVeh.getValueAt(f, 1).toString();
                String modeloTabla  = tmVeh.getValueAt(f, 2).toString();
                String anioTabla    = tmVeh.getValueAt(f, 3).toString();

                // seleccionar cliente
                for (int i = 0; i < cbCli.getItemCount(); i++) {
                    Cliente c = cbCli.getItemAt(i);
                    String nombreCompleto = c.getNombre() + " " + c.getApellidos();
                    if (nombreCompleto.equalsIgnoreCase(clienteTabla)) {
                        cbCli.setSelectedIndex(i);
                        break;
                    }
                }
                // seleccionar modelo
                for (int i = 0; i < cbVehModelo.getItemCount(); i++) {
                    ModeloAuto m = cbVehModelo.getItemAt(i);
                    if (m.getNombre().equalsIgnoreCase(modeloTabla)) {
                        cbVehModelo.setSelectedIndex(i);
                        break;
                    }
                }
                // seleccionar año
                for (int i = 0; i < cbVehAnio.getItemCount(); i++) {
                    AnioModelo a = cbVehAnio.getItemAt(i);
                    if (String.valueOf(a.getAnio()).equals(anioTabla)) {
                        cbVehAnio.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        btnVehAdd.addActionListener(e -> {
            Cliente cli = (Cliente) cbCli.getSelectedItem();
            ModeloAuto mo = (ModeloAuto) cbVehModelo.getSelectedItem();
            AnioModelo an = (AnioModelo) cbVehAnio.getSelectedItem();

            if (cli == null || mo == null || an == null
                    || txtVehPlaca.getText().trim().isEmpty()
                    || txtVehVin.getText().trim().isEmpty()
                    || txtVehColor.getText().trim().isEmpty()
                    || txtVehKm.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Complete todos los campos del vehículo",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                vehiculoDAO.insertar(
                        cli.getIdCliente(),
                        mo.getIdModelo(),
                        an.getIdAnioModelo(),
                        txtVehPlaca.getText(),
                        txtVehVin.getText(),
                        txtVehColor.getText(),
                        Integer.parseInt(txtVehKm.getText())
                );
                cargarVehiculos();
                limpiarCamposVeh();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Kilometraje inválido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        btnRefrescarCatalogo.addActionListener(e -> {
            cargarCombosVehiculos();
        });

        btnVehUpd.addActionListener(e -> {
            if (txtVehId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Seleccione un vehículo de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (txtVehColor.getText().trim().isEmpty()
             || txtVehKm.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Complete Color y Kilometraje",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                vehiculoDAO.actualizar(
                        Integer.parseInt(txtVehId.getText()),
                        txtVehColor.getText(),
                        Integer.parseInt(txtVehKm.getText())
                );
                cargarVehiculos();
                limpiarCamposVeh();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Kilometraje inválido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnVehDel.addActionListener(e -> {
            if (txtVehId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Seleccione un vehículo de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            vehiculoDAO.desactivar(Integer.parseInt(txtVehId.getText()));
            cargarVehiculos();
            limpiarCamposVeh();
        });

        btnVehHardDelete.addActionListener(e -> {
            if (txtVehId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Seleccione un vehículo de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int opt = JOptionPane.showConfirmDialog(
                    ClientesVehiculosView.this,
                    "Esto BORRA el vehículo de la base de datos.\n" +
                    "Esta acción NO se puede deshacer.\n\n" +
                    "¿Seguro que quieres ELIMINAR DEFINITIVO?",
                    "Confirmar eliminación permanente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opt != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                int id = Integer.parseInt(txtVehId.getText());
                vehiculoDAO.eliminarDefinitivo(id);
                cargarVehiculos();
                limpiarCamposVeh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "No se pudo eliminar el vehículo.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnVehRea.addActionListener(e -> {
            if (txtVehId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        ClientesVehiculosView.this,
                        "Seleccione un vehículo de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            vehiculoDAO.reactivar(Integer.parseInt(txtVehId.getText()));
            cargarVehiculos();
            limpiarCamposVeh();
        });

        btnVehClean.addActionListener(e -> limpiarCamposVeh());

        return p;
    }

    private void limpiarCamposVeh() {
        txtVehId.setText("");
        txtVehPlaca.setText("");
        txtVehVin.setText("");
        txtVehColor.setText("");
        txtVehKm.setText("");
        if (cbCli.getItemCount() > 0) cbCli.setSelectedIndex(-1);
        if (cbVehModelo.getItemCount() > 0) cbVehModelo.setSelectedIndex(-1);
        if (cbVehAnio.getItemCount() > 0) cbVehAnio.setSelectedIndex(-1);
    }

    private void cargarCombosVehiculos() {
        // === Clientes activos ===
        if (cbCli != null) {
            cbCli.removeAllItems();
            for (Cliente c : clienteDAO.listar()) {
                if (c.isActivo()) {
                    cbCli.addItem(c);
                }
            }
        }

        // === Modelos activos de marcas activas ===
        if (cbVehModelo != null) {
            cbVehModelo.removeAllItems();

            // solo marcas activas
            for (Marca m : marcaDAO.listarMarcas()) {
                if (!m.isActivo()) {
                    continue; // saltar marcas apagadas
                }

                // sólo modelos activos de esa marca activa
                for (ModeloAuto mo : modeloDAO.listarModelosPorMarca(m.getIdMarca())) {
                    if (mo.isActivo()) {
                        cbVehModelo.addItem(mo);
                    }
                }
            }
        }

        // === Años: depende del modelo seleccionado ===
        recargarAniosParaModeloSeleccionado();
    }


    private void cargarVehiculos() {
        tmVeh.setRowCount(0);

        List<Cliente> listaClientes = clienteDAO.listar();
        List<Marca> marcas = marcaDAO.listarMarcas();

        java.util.List<ModeloAuto> listaModelos = new java.util.ArrayList<>();
        for (Marca m : marcas) {
            listaModelos.addAll(modeloDAO.listarModelosPorMarca(m.getIdMarca()));
        }

        java.util.List<AnioModelo> listaAnios = new java.util.ArrayList<>();
        for (ModeloAuto mo : listaModelos) {
            listaAnios.addAll(anioDAO.listarPorModelo(mo.getIdModelo()));
        }

        for (Vehiculo v : vehiculoDAO.listar()) {

            // Cliente legible
            String clienteTxt = "";
            if (v.getCliente() != null) {
                int idCli = v.getCliente().getIdCliente();
                Cliente cliFound = listaClientes.stream()
                        .filter(c -> c.getIdCliente() == idCli)
                        .findFirst()
                        .orElse(null);
                clienteTxt = (cliFound != null)
                        ? (cliFound.getNombre() + " " + cliFound.getApellidos())
                        : ("ID " + idCli);
            }

            // Modelo legible
            String modeloTxt = "";
            if (v.getModelo() != null) {
                int idMod = v.getModelo().getIdModelo();
                ModeloAuto moFound = listaModelos.stream()
                        .filter(mo -> mo.getIdModelo() == idMod)
                        .findFirst()
                        .orElse(null);
                modeloTxt = (moFound != null)
                        ? moFound.getNombre()
                        : ("Modelo " + idMod);
            }

            // Año legible
            String anioTxt = "";
            if (v.getAnioModelo() != null) {
                int idAn = v.getAnioModelo().getIdAnioModelo();
                AnioModelo anFound = listaAnios.stream()
                        .filter(a -> a.getIdAnioModelo() == idAn)
                        .findFirst()
                        .orElse(null);
                anioTxt = (anFound != null)
                        ? String.valueOf(anFound.getAnio())
                        : ("Año " + idAn);
            }

            tmVeh.addRow(new Object[]{
                    v.getIdVehiculo(),
                    clienteTxt,
                    modeloTxt,
                    anioTxt,
                    v.getPlaca(),
                    v.getVin(),
                    v.getColor(),
                    v.getKilometraje(),
                    v.isActivo() ? "Sí" : "No"
            });
        }
    }
    
    private void cargarVehiculosFiltradosPorPlaca(String placaFiltro) {
        tmVeh.setRowCount(0);

        // cache igual que en cargarVehiculos()
        List<Cliente> listaClientes = clienteDAO.listar();
        List<Marca> marcas = marcaDAO.listarMarcas();

        java.util.List<ModeloAuto> listaModelos = new java.util.ArrayList<>();
        for (Marca m : marcas) {
            listaModelos.addAll(modeloDAO.listarModelosPorMarca(m.getIdMarca()));
        }

        java.util.List<AnioModelo> listaAnios = new java.util.ArrayList<>();
        for (ModeloAuto mo : listaModelos) {
            listaAnios.addAll(anioDAO.listarPorModelo(mo.getIdModelo()));
        }

        for (Vehiculo v : vehiculoDAO.buscarPorPlaca(placaFiltro)) {

            // mismo formateo que cargarVehiculos()

            String clienteTxt = "";
            if (v.getCliente() != null) {
                int idCli = v.getCliente().getIdCliente();
                Cliente cliFound = listaClientes.stream()
                        .filter(c -> c.getIdCliente() == idCli)
                        .findFirst()
                        .orElse(null);
                clienteTxt = (cliFound != null)
                        ? (cliFound.getNombre() + " " + cliFound.getApellidos())
                        : ("ID " + idCli);
            }

            String modeloTxt = "";
            if (v.getModelo() != null) {
                int idMod = v.getModelo().getIdModelo();
                ModeloAuto moFound = listaModelos.stream()
                        .filter(mo -> mo.getIdModelo() == idMod)
                        .findFirst()
                        .orElse(null);
                modeloTxt = (moFound != null)
                        ? moFound.getNombre()
                        : ("Modelo " + idMod);
            }

            String anioTxt = "";
            if (v.getAnioModelo() != null) {
                int idAn = v.getAnioModelo().getIdAnioModelo();
                AnioModelo anFound = listaAnios.stream()
                        .filter(a -> a.getIdAnioModelo() == idAn)
                        .findFirst()
                        .orElse(null);
                anioTxt = (anFound != null)
                        ? String.valueOf(anFound.getAnio())
                        : ("Año " + idAn);
            }

            tmVeh.addRow(new Object[]{
                    v.getIdVehiculo(),
                    clienteTxt,
                    modeloTxt,
                    anioTxt,
                    v.getPlaca(),
                    v.getVin(),
                    v.getColor(),
                    v.getKilometraje(),
                    v.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void bloquearEdicionClientesYVehiculos() {

        // ---- CLIENTES ----
        if (btnCliAdd         != null) btnCliAdd.setEnabled(false);
        if (btnCliUpd         != null) btnCliUpd.setEnabled(false);
        if (btnCliDel         != null) btnCliDel.setEnabled(false);
        if (btnCliRea         != null) btnCliRea.setEnabled(false);
        if (btnCliHardDelete  != null) btnCliHardDelete.setEnabled(false); // NUEVO
        // btnCliClean enabled

        if (txtCliNom   != null) txtCliNom.setEditable(false);
        if (txtCliApe   != null) txtCliApe.setEditable(false);
        if (txtCliTel   != null) txtCliTel.setEditable(false);
        if (txtCliMail  != null) txtCliMail.setEditable(false);

        // ---- VEHÍCULOS ----
        if (btnVehAdd        != null) btnVehAdd.setEnabled(false);
        if (btnVehUpd        != null) btnVehUpd.setEnabled(false);
        if (btnVehDel        != null) btnVehDel.setEnabled(false);
        if (btnVehRea        != null) btnVehRea.setEnabled(false);
        if (btnVehHardDelete != null) btnVehHardDelete.setEnabled(false); // NUEVO
        // btnVehClean enabled

        if (cbCli       != null) cbCli.setEnabled(false);
        if (cbVehModelo != null) cbVehModelo.setEnabled(false);
        if (cbVehAnio   != null) cbVehAnio.setEnabled(false);

        if (txtVehPlaca != null) txtVehPlaca.setEditable(false);
        if (txtVehVin   != null) txtVehVin.setEditable(false);
        if (txtVehColor != null) txtVehColor.setEditable(false);
        if (txtVehKm    != null) txtVehKm.setEditable(false);
    }


    private void aplicarPermisosSegunRol() {
        Usuario actual = AuthController.getUsuarioActual();
        if (actual == null) {
            // por seguridad extrema: si no hay sesión registrada, bloqueamos todo
            bloquearEdicionClientesYVehiculos();
            return;
        }

        String rol = actual.getRol(); // "ADMIN", "TECNICO", "CONSULTA", etc.

        if (!"ADMIN".equalsIgnoreCase(rol)) {
            // si NO es admin -> solo lectura
            bloquearEdicionClientesYVehiculos();
        }
    }
    
 // Llama esto cada vez que cambie cbVehModelo
    private void recargarAniosParaModeloSeleccionado() {
        cbVehAnio.removeAllItems();

        ModeloAuto modeloSel = (ModeloAuto) cbVehModelo.getSelectedItem();
        if (modeloSel == null) {
            return;
        }

        int idModelo = modeloSel.getIdModelo();

        // pedirle al DAO sólo los años de ese modelo
        for (AnioModelo a : anioDAO.listarPorModelo(idModelo)) {
            if (a.isActivo()) {
                cbVehAnio.addItem(a);
            }
        }

        // opcional: dejar nada seleccionado al inicio
        if (cbVehAnio.getItemCount() > 0) {
            cbVehAnio.setSelectedIndex(0);
        }
    }

    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setPreferredSize(new Dimension(0, 60)); // altura fija como en CatalogosView
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR)); // línea arriba

        // texto izq (mismo texto que ya tenías)
        JLabel lblFoot = new JLabel("Agencia de Automóviles • Mantenimiento");
        lblFoot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFoot.setForeground(TEXT_SECONDARY);
        lblFoot.setBorder(new EmptyBorder(0, 20, 0, 0)); // padding a la izquierda

        // contenedor para el botón a la derecha
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightGroup.setOpaque(false);

        JButton btnCerrar = createStyledButton("Cerrar", DANGER_COLOR, DANGER_HOVER);
        btnCerrar.addActionListener(e -> dispose());
        rightGroup.add(btnCerrar);

        panel.add(lblFoot, BorderLayout.WEST);
        panel.add(rightGroup, BorderLayout.EAST);

        return panel;
    }


}

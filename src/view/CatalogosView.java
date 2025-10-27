package view;

import controller.AuthController;
import model.*;
import model.dao.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;

/**
 * CatalogosView - Interfaz Mejorada
 *
 * Contiene C(CREATE)RUD completos para:
 *   Marcas
 *   Modelos
 *   Años de Modelo
 *   Tipos de Servicio
 *   Refacciones
 *   Técnicos

 */
public class CatalogosView extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);      // Azul moderno
    private static final Color PRIMARY_HOVER = new Color(29, 78, 216);      // Azul hover
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);      // Verde
    private static final Color SUCCESS_HOVER = new Color(22, 163, 74);      // Verde hover
    private static final Color WARNING_COLOR = new Color(251, 146, 60);     // Naranja
    private static final Color WARNING_HOVER = new Color(249, 115, 22);     // Naranja hover
    private static final Color DANGER_COLOR = new Color(239, 68, 68);       // Rojo
    private static final Color DANGER_HOVER = new Color(220, 38, 38);       // Rojo hover
    private static final Color BG_COLOR = new Color(248, 250, 252);         // Fondo claro
    private static final Color CARD_COLOR = Color.WHITE;                    // Blanco para cards
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);        // Texto oscuro
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);   // Texto gris
    private static final Color BORDER_COLOR = new Color(226, 232, 240);     // Bordes suaves

    private final AuthController authController;

    // DAOs
    private final MarcaDAO marcaDAO = new MarcaDAO();
    private final ModeloAutoDAO modeloDAO = new ModeloAutoDAO();
    private final AnioModeloDAO anioDAO = new AnioModeloDAO();
    private final TipoServicioDAO tipoDAO = new TipoServicioDAO();
    private final RefaccionDAO refaccionDAO = new RefaccionDAO();
    private final TecnicoDAO tecnicoDAO = new TecnicoDAO();
   
    private JTabbedPane tabs;

    // Marcas
    private DefaultTableModel tmMarcas;
    private JTable tblMarcas;
    private JTextField txtMarcaId, txtMarcaNombre;

    // Modelos
    private DefaultTableModel tmModelos;
    private JTable tblModelos;
    private JComboBox<Marca> cbMarca;
    private JTextField txtModeloId, txtModeloNombre;

    // Años
    private DefaultTableModel tmAnios;
    private JTable tblAnios;
    private JComboBox<ModeloAuto> cbModelo;
    private JTextField txtAnioId, txtAnioValor;

    // Tipos de servicio
    private DefaultTableModel tmTipos;
    private JTable tblTipos;
    private JTextField txtTipoId, txtTipoNombre, txtTipoDesc;

    // Refacciones
    private DefaultTableModel tmRef;
    private JTable tblRef;
    private JTextField txtRefId, txtRefClave, txtRefDesc, txtRefPrecio;

    // Técnicos
    private DefaultTableModel tmTec;
    private JTable tblTec;
    private JTextField txtTecId, txtTecNo, txtTecNom, txtTecTel, txtTecMail;

    // Clientes
    private DefaultTableModel tmCli;
    private JTable tblCli;
    private JTextField txtCliId, txtCliNom, txtCliApe, txtCliTel, txtCliMail;

    // Vehículos
    private DefaultTableModel tmVeh;
    private JTable tblVeh;
    private JComboBox<Cliente> cbCli;
    private JComboBox<ModeloAuto> cbVehModelo;
    private JComboBox<AnioModelo> cbVehAnio;
    private JTextField txtVehId, txtVehPlaca, txtVehVin, txtVehColor, txtVehKm;

    public CatalogosView(AuthController authController) {
        this.authController = authController;
        setTitle("Gestión de Catálogos - Sistema de Agencia");
        setSize(1200, 750); // Tamaño inicial más grande para mejor visualización
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        getContentPane().setBackground(BG_COLOR);
        getContentPane().setLayout(new BorderLayout()); // Usamos BorderLayout para el header, tabs y footer

        // Header
        JPanel headerPanel = createHeaderPanel();
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Tabs con estilo mejorado
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBackground(CARD_COLOR);
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Espaciado alrededor de las pestañas

        tabs.addTab("Marcas", crearPanelMarcas());
        tabs.addTab("Modelos", crearPanelModelos());
        tabs.addTab("Años", crearPanelAnios());
        tabs.addTab("Tipos Servicio", crearPanelTipos());
        tabs.addTab("Refacciones", crearPanelRefacciones());
        tabs.addTab("Tecnicos", crearPanelTecnicos());
   
        getContentPane().add(tabs, BorderLayout.CENTER);

        // Footer con botón cerrar
        JPanel footerPanel = createFooterPanel();
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        aplicarPermisosSegunRol();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setPreferredSize(new Dimension(0, 70)); // Ancho se ajustará, alto fijo
        panel.setLayout(new BorderLayout(15, 0)); // Agrega un BorderLayout con espacio
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false); // Para que el color de fondo del padre se vea
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS)); // Apila etiquetas verticalmente
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 0)); // Padding a la izquierda

        JLabel titleLabel = new JLabel("Gestión de Catálogos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinea el texto a la izquierda
        textPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Administra todos los catálogos del sistema");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinea el texto a la izquierda
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }


    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setPreferredSize(new Dimension(0, 60)); // Altura fija
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 15)); // Alinea a la derecha
        panel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, BORDER_COLOR));

        JButton btnCerrar = createStyledButton("Cerrar", DANGER_COLOR, DANGER_HOVER);
        btnCerrar.addActionListener(e -> dispose());
        panel.add(btnCerrar);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.BLACK);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JTextField createStyledTextField(boolean editable) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setEditable(editable);
        if (!editable) {
            field.setBackground(new Color(241, 245, 249));
        }
        return field;
    }

    private JComboBox<?> createStyledComboBox() {
        JComboBox<?> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8) // Ajusta el padding interno
        ));
        ((JLabel)comboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER); // Centra texto
        return comboBox;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); // Usar GridBagLayout para control detallado
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 filas, 2 columnas, espaciado de 10
        panel.setOpaque(false); // Fondo transparente
        return panel;
    }

    // =====================================================================================
    // 1️ MARCAS
    // =====================================================================================
    private JPanel crearPanelMarcas() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Panel de tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tmMarcas = new DefaultTableModel(new Object[]{"ID", "Nombre", "Activo"}, 0);
        tblMarcas = new JTable(tmMarcas);
        styleTable(tblMarcas);
        JScrollPane sc = new JScrollPane(tblMarcas);
        sc.setBorder(null);
        tablePanel.add(sc, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7; // La tabla ocupa más espacio horizontal
        gbc.weighty = 1.0;
        p.add(tablePanel, gbc);

        // Panel de formulario
        JPanel formPanel = createFormPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // El formulario ocupa menos espacio horizontal
        gbc.weighty = 1.0;
        p.add(formPanel, gbc);

        // Contenido del formulario (usando GridBagLayout internamente)
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = GridBagConstraints.REMAINDER; // Ocupa todo el ancho

        JLabel titleForm = new JLabel("Información de Marca");
        titleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleForm.setForeground(TEXT_PRIMARY);
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleForm, formGbc);

        formGbc.gridy++;
        formGbc.weighty = 0;
        formPanel.add(createStyledLabel("ID:"), formGbc);
        formGbc.gridy++;
        txtMarcaId = createStyledTextField(false);
        formPanel.add(txtMarcaId, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Nombre de la Marca:"), formGbc);
        formGbc.gridy++;
        txtMarcaNombre = createStyledTextField(true);
        formPanel.add(txtMarcaNombre, formGbc);

        // Panel para botones
        JPanel buttonPanel = createButtonPanel();
        JButton bAdd = createStyledButton("Agregar", SUCCESS_COLOR, SUCCESS_HOVER);
        JButton bUpd = createStyledButton("Actualizar", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton bDel = createStyledButton("Desactivar", DANGER_COLOR, DANGER_HOVER);
        JButton bRea = createStyledButton("Reactivar", WARNING_COLOR, WARNING_HOVER);
        JButton bClean = createStyledButton("Limpiar Campos", BG_COLOR, BORDER_COLOR);
        bClean.setForeground(TEXT_PRIMARY); // Color de texto para el botón de limpiar

        buttonPanel.add(bAdd);
        buttonPanel.add(bUpd);
        buttonPanel.add(bDel);
        buttonPanel.add(bRea);
        buttonPanel.add(bClean);

        formGbc.gridy++;
        formGbc.weighty = 1.0; // Empuja los botones hacia abajo si hay espacio
        formGbc.anchor = GridBagConstraints.SOUTH; // Alinea los botones al final
        formGbc.fill = GridBagConstraints.HORIZONTAL; // Asegura que el panel de botones se expanda horizontalmente
        formPanel.add(buttonPanel, formGbc);


        cargarMarcas();

        tblMarcas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tblMarcas.getSelectedRow();
                if (f != -1) {
                    txtMarcaId.setText(tmMarcas.getValueAt(f, 0).toString());
                    txtMarcaNombre.setText(tmMarcas.getValueAt(f, 1).toString());
                }
            }
        });

        bAdd.addActionListener(e -> {
            if (txtMarcaNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            marcaDAO.insertarMarca(txtMarcaNombre.getText());
            cargarMarcas();
            limpiarCamposMarca();
        });

        bUpd.addActionListener(e -> {
            if (txtMarcaId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una marca de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            marcaDAO.actualizarMarca(Integer.parseInt(txtMarcaId.getText()), txtMarcaNombre.getText());
            cargarMarcas();
            limpiarCamposMarca();
        });

        bDel.addActionListener(e -> {
            if (txtMarcaId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una marca de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            marcaDAO.desactivarMarca(Integer.parseInt(txtMarcaId.getText()));
            cargarMarcas();
            limpiarCamposMarca();
        });

        bRea.addActionListener(e -> {
            if (txtMarcaId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una marca de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            marcaDAO.reactivarMarca(Integer.parseInt(txtMarcaId.getText()));
            cargarMarcas();
            limpiarCamposMarca();
        });
        
        bClean.addActionListener(e -> limpiarCamposMarca());

        return p;
    }

    private void limpiarCamposMarca() {
        txtMarcaId.setText("");
        txtMarcaNombre.setText("");
    }

    private void cargarMarcas() {
        tmMarcas.setRowCount(0);
        for (Marca m : marcaDAO.listarMarcas()) {
            tmMarcas.addRow(new Object[]{m.getIdMarca(), m.getNombre(), m.isActivo() ? " Sí" : " No"});
        }
    }

    // =====================================================================================
    // 2️ MODELOS
    // =====================================================================================
    private JPanel crearPanelModelos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Panel de controles superiores
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setOpaque(false);

        // etiqueta
        controlPanel.add(createStyledLabel("Seleccionar Marca:"));

        // combo marcas
        cbMarca = (JComboBox<Marca>) createStyledComboBox();
        cbMarca.setPreferredSize(new Dimension(200, 35));
        controlPanel.add(cbMarca);

        // botón cargar modelos (tabla)
        JButton bLoad = createStyledButton("Cargar Modelos", PRIMARY_COLOR, PRIMARY_HOVER);
        controlPanel.add(bLoad);

        // NUEVO: botón refrescar catálogo
        JButton btnRefrescarCatModelos = createStyledButton("Refrescar catálogo", PRIMARY_COLOR, PRIMARY_HOVER);
        controlPanel.add(btnRefrescarCatModelos);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1; // Ocupa una columna
        gbc.weightx = 1.0;
        gbc.weighty = 0; // No se expande verticalmente
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(controlPanel, gbc);


        // Panel de tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tmModelos = new DefaultTableModel(new Object[]{"ID", "Nombre", "Activo"}, 0);
        tblModelos = new JTable(tmModelos);
        styleTable(tblModelos);
        JScrollPane sc = new JScrollPane(tblModelos);
        sc.setBorder(null);
        tablePanel.add(sc, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        p.add(tablePanel, gbc);

        // Panel de formulario
        JPanel formPanel = createFormPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2; // Ocupa dos filas
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        p.add(formPanel, gbc);

        // Contenido del formulario
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel titleForm = new JLabel("Información de Modelo");
        titleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleForm.setForeground(TEXT_PRIMARY);
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleForm, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("ID:"), formGbc);
        formGbc.gridy++;
        txtModeloId = createStyledTextField(false);
        formPanel.add(txtModeloId, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Nombre del Modelo:"), formGbc);
        formGbc.gridy++;
        txtModeloNombre = createStyledTextField(true);
        formPanel.add(txtModeloNombre, formGbc);

        // Panel para botones
        JPanel buttonPanel = createButtonPanel();
        JButton bAddForm = createStyledButton("Agregar", SUCCESS_COLOR, SUCCESS_HOVER);
        JButton bUpdForm = createStyledButton("Actualizar", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton bDelForm = createStyledButton("Desactivar", DANGER_COLOR, DANGER_HOVER);
        JButton bReaForm = createStyledButton("Reactivar", WARNING_COLOR, WARNING_HOVER);
        JButton bClean = createStyledButton("Limpiar Campos", BG_COLOR, BORDER_COLOR);
        bClean.setForeground(TEXT_PRIMARY);

        buttonPanel.add(bAddForm);
        buttonPanel.add(bUpdForm);
        buttonPanel.add(bDelForm);
        buttonPanel.add(bReaForm);
        buttonPanel.add(bClean);

        formGbc.gridy++;
        formGbc.weighty = 1.0;
        formGbc.anchor = GridBagConstraints.SOUTH;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, formGbc);


     // ya estaba
        cargarComboMarcas();
        bLoad.addActionListener(e -> cargarModelos());

        // NUEVO: refrescar catálogo de marcas/modelos
        btnRefrescarCatModelos.addActionListener(e -> {
            // recarga las marcas activas en el combo
            cargarComboMarcas();
            // intenta volver a cargar la tabla modelos con la marca que haya ahora seleccionada
            cargarModelos();
        });

        
        tblModelos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tblModelos.getSelectedRow();
                if (f != -1) {
                    txtModeloId.setText(tmModelos.getValueAt(f, 0).toString());
                    txtModeloNombre.setText(tmModelos.getValueAt(f, 1).toString());
                }
            }
        });

        bAddForm.addActionListener(e -> {
            Marca sel = (Marca) cbMarca.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una marca", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtModeloNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un nombre", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            modeloDAO.insertarModelo(sel.getIdMarca(), txtModeloNombre.getText());
            cargarModelos();
            limpiarCamposModelo();
        });

        bUpdForm.addActionListener(e -> {
            if (txtModeloId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un modelo", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            modeloDAO.actualizarModelo(Integer.parseInt(txtModeloId.getText()), txtModeloNombre.getText());
            cargarModelos();
            limpiarCamposModelo();
        });

        bDelForm.addActionListener(e -> {
            if (txtModeloId.getText().isEmpty()) return;
            modeloDAO.desactivarModelo(Integer.parseInt(txtModeloId.getText()));
            cargarModelos();
            limpiarCamposModelo();
        });

        bReaForm.addActionListener(e -> {
            if (txtModeloId.getText().isEmpty()) return;
            modeloDAO.reactivarModelo(Integer.parseInt(txtModeloId.getText()));
            cargarModelos();
            limpiarCamposModelo();
        });

        bClean.addActionListener(e -> limpiarCamposModelo());

        return p;
    }

    private void limpiarCamposModelo() {
        txtModeloId.setText("");
        txtModeloNombre.setText("");
    }

    private void cargarComboMarcas() {
        cbMarca.removeAllItems();
        for (Marca m : marcaDAO.listarMarcas()) if (m.isActivo()) cbMarca.addItem(m);
    }

    private void cargarModelos() {
        tmModelos.setRowCount(0);
        Marca sel = (Marca) cbMarca.getSelectedItem();
        if (sel == null) return;
        for (ModeloAuto mo : modeloDAO.listarModelosPorMarca(sel.getIdMarca())) {
            tmModelos.addRow(new Object[]{mo.getIdModelo(), mo.getNombre(), mo.isActivo() ? "Sí" : "No"});
        }
    }
    // =====================================================================================
    // 3️ AÑOS
    // =====================================================================================
    private JPanel crearPanelAnios() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Panel de controles superiores
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setOpaque(false);

        // etiqueta
        controlPanel.add(createStyledLabel("Seleccionar Modelo:"));

        // combo modelos
        cbModelo = (JComboBox<ModeloAuto>) createStyledComboBox();
        cbModelo.setPreferredSize(new Dimension(200, 35));
        controlPanel.add(cbModelo);

        // botón cargar años (tabla)
        JButton bLoad = createStyledButton("Cargar Años", PRIMARY_COLOR, PRIMARY_HOVER);
        controlPanel.add(bLoad);

        // NUEVO: botón refrescar catálogo
        JButton btnRefrescarCatAnios = createStyledButton("Refrescar catálogo", PRIMARY_COLOR, PRIMARY_HOVER);
        controlPanel.add(btnRefrescarCatAnios);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(controlPanel, gbc);

        // Panel de tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tmAnios = new DefaultTableModel(new Object[]{"ID", "Año", "Activo"}, 0);
        tblAnios = new JTable(tmAnios);
        styleTable(tblAnios);
        JScrollPane sc = new JScrollPane(tblAnios);
        sc.setBorder(null);
        tablePanel.add(sc, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        p.add(tablePanel, gbc);

        // Panel de formulario
        JPanel formPanel = createFormPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        p.add(formPanel, gbc);

        // Contenido del formulario
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel titleForm = new JLabel("Información de Año");
        titleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleForm.setForeground(TEXT_PRIMARY);
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleForm, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("ID:"), formGbc);
        formGbc.gridy++;
        txtAnioId = createStyledTextField(false);
        formPanel.add(txtAnioId, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Año:"), formGbc);
        formGbc.gridy++;
        txtAnioValor = createStyledTextField(true);
        formPanel.add(txtAnioValor, formGbc);

        // Panel para botones
        JPanel buttonPanel = createButtonPanel();
        JButton bAddForm = createStyledButton("Agregar", SUCCESS_COLOR, SUCCESS_HOVER);
        JButton bUpdForm = createStyledButton("Actualizar", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton bDelForm = createStyledButton("Desactivar", DANGER_COLOR, DANGER_HOVER);
        JButton bReaForm = createStyledButton("Reactivar", WARNING_COLOR, WARNING_HOVER);
        JButton bClean = createStyledButton("Limpiar Campos", BG_COLOR, BORDER_COLOR);
        bClean.setForeground(TEXT_PRIMARY);

        buttonPanel.add(bAddForm);
        buttonPanel.add(bUpdForm);
        buttonPanel.add(bDelForm);
        buttonPanel.add(bReaForm);
        buttonPanel.add(bClean);

        formGbc.gridy++;
        formGbc.weighty = 1.0;
        formGbc.gridy++;
        formGbc.weighty = 1.0;
        formGbc.anchor = GridBagConstraints.SOUTH;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, formGbc);

        cargarComboModelos();
        bLoad.addActionListener(e -> cargarAnios());
        
     // refrescar catálogo de modelos/años
        btnRefrescarCatAnios.addActionListener(e -> {
            // recargar el combo cbModelo sólo con modelos activos de marcas activas
            cargarComboModelos();
            // volver a cargar la tabla de años con el modelo actualmente seleccionado (si hay)
            cargarAnios();
        });

        
        tblAnios.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tblAnios.getSelectedRow();
                if (f != -1) {
                    txtAnioId.setText(tmAnios.getValueAt(f, 0).toString());
                    txtAnioValor.setText(tmAnios.getValueAt(f, 1).toString());
                }
            }
        });

        bAddForm.addActionListener(e -> {
            ModeloAuto sel = (ModeloAuto) cbModelo.getSelectedItem();
            if (sel == null) {
                 JOptionPane.showMessageDialog(this, "Seleccione un modelo", "Validación", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            try {
                if (txtAnioValor.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor ingrese un año", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                anioDAO.insertar(sel.getIdModelo(), Integer.parseInt(txtAnioValor.getText()));
                cargarAnios();
                limpiarCamposAnio();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un año válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bUpdForm.addActionListener(e -> {
            if (txtAnioId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un año de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (txtAnioValor.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor ingrese un año", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                anioDAO.actualizar(Integer.parseInt(txtAnioId.getText()), Integer.parseInt(txtAnioValor.getText()));
                cargarAnios();
                limpiarCamposAnio();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un año válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bDelForm.addActionListener(e -> {
            if (txtAnioId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un año de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            anioDAO.desactivar(Integer.parseInt(txtAnioId.getText()));
            cargarAnios();
            limpiarCamposAnio();
        });

        bReaForm.addActionListener(e -> {
            if (txtAnioId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un año de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            anioDAO.reactivar(Integer.parseInt(txtAnioId.getText()));
            cargarAnios();
            limpiarCamposAnio();
        });

        bClean.addActionListener(e -> limpiarCamposAnio());

        return p;
    }

    private void limpiarCamposAnio() {
        txtAnioId.setText("");
        txtAnioValor.setText("");
    }

    private void cargarComboModelos() {
        cbModelo.removeAllItems();
        // Cargar modelos de todas las marcas activas
        for (Marca m : marcaDAO.listarMarcas()) {
            if (m.isActivo()) {
                for (ModeloAuto mo : modeloDAO.listarModelosPorMarca(m.getIdMarca())) {
                    if (mo.isActivo()) {
                        cbModelo.addItem(mo);
                    }
                }
            }
        }
    }

    private void cargarAnios() {
        tmAnios.setRowCount(0);
        ModeloAuto sel = (ModeloAuto) cbModelo.getSelectedItem();
        if (sel == null) return;
        for (AnioModelo a : anioDAO.listarPorModelo(sel.getIdModelo())) {
            tmAnios.addRow(new Object[]{a.getIdAnioModelo(), a.getAnio(), a.isActivo() ? "Sí" : "No"});
        }
    }

    // =====================================================================================
    // 4️TIPOS SERVICIO
    // =====================================================================================
    private JPanel crearPanelTipos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Panel de tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tmTipos = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción", "Activo"}, 0);
        tblTipos = new JTable(tmTipos);
        styleTable(tblTipos);
        JScrollPane sc = new JScrollPane(tblTipos);
        sc.setBorder(null);
        tablePanel.add(sc, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        p.add(tablePanel, gbc);

        // Panel de formulario
        JPanel formPanel = createFormPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        p.add(formPanel, gbc);

        // Contenido del formulario
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel titleForm = new JLabel("Tipo de Servicio");
        titleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleForm.setForeground(TEXT_PRIMARY);
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleForm, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("ID:"), formGbc);
        formGbc.gridy++;
        txtTipoId = createStyledTextField(false);
        formPanel.add(txtTipoId, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Nombre:"), formGbc);
        formGbc.gridy++;
        txtTipoNombre = createStyledTextField(true);
        formPanel.add(txtTipoNombre, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Descripción:"), formGbc);
        formGbc.gridy++;
        txtTipoDesc = createStyledTextField(true);
        formPanel.add(txtTipoDesc, formGbc);

        // Panel para botones
        JPanel buttonPanel = createButtonPanel();
        JButton bAdd = createStyledButton("Agregar", SUCCESS_COLOR, SUCCESS_HOVER);
        JButton bUpd = createStyledButton("Actualizar", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton bDel = createStyledButton("Desactivar", DANGER_COLOR, DANGER_HOVER);
        JButton bRea = createStyledButton("Reactivar", WARNING_COLOR, WARNING_HOVER);
        JButton bClean = createStyledButton("Limpiar Campos", BG_COLOR, BORDER_COLOR);
        bClean.setForeground(TEXT_PRIMARY);

        buttonPanel.add(bAdd);
        buttonPanel.add(bUpd);
        buttonPanel.add(bDel);
        buttonPanel.add(bRea);
        buttonPanel.add(bClean);

        formGbc.gridy++;
        formGbc.weighty = 1.0;
        formGbc.anchor = GridBagConstraints.SOUTH;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, formGbc);

        cargarTipos();
        
        tblTipos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tblTipos.getSelectedRow();
                if (f != -1) {
                    txtTipoId.setText(tmTipos.getValueAt(f, 0).toString());
                    txtTipoNombre.setText(tmTipos.getValueAt(f, 1).toString());
                    txtTipoDesc.setText(tmTipos.getValueAt(f, 2).toString());
                }
            }
        });

        bAdd.addActionListener(e -> {
            if (txtTipoNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            tipoDAO.insertar(txtTipoNombre.getText(), txtTipoDesc.getText());
            cargarTipos();
            limpiarCamposTipo();
        });

        bUpd.addActionListener(e -> {
            if (txtTipoId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo de servicio de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtTipoNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            tipoDAO.actualizar(Integer.parseInt(txtTipoId.getText()), txtTipoNombre.getText(), txtTipoDesc.getText());
            cargarTipos();
            limpiarCamposTipo();
        });

        bDel.addActionListener(e -> {
            if (txtTipoId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo de servicio de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            tipoDAO.desactivar(Integer.parseInt(txtTipoId.getText()));
            cargarTipos();
            limpiarCamposTipo();
        });

        bRea.addActionListener(e -> {
            if (txtTipoId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo de servicio de la tabla", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            tipoDAO.reactivar(Integer.parseInt(txtTipoId.getText()));
            cargarTipos();
            limpiarCamposTipo();
        });

        bClean.addActionListener(e -> limpiarCamposTipo());

        return p;
    }

    private void limpiarCamposTipo() {
        txtTipoId.setText("");
        txtTipoNombre.setText("");
        txtTipoDesc.setText("");
    }

    private void cargarTipos() {
        tmTipos.setRowCount(0);
        for (TipoServicio t : tipoDAO.listar()) {
            tmTipos.addRow(new Object[]{t.getIdTipoServicio(), t.getNombre(), t.getDescripcion(), t.isActivo() ? "Sí" : "No"});
        }
    }

    private JPanel crearPanelRefacciones() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
     
        // Panel de tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // ====== barra de búsqueda refacciones ======
        JPanel searchRefPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchRefPanel.setBackground(CARD_COLOR);

        JLabel lblBuscarRef = new JLabel("Buscar refacción:");
        lblBuscarRef.setForeground(TEXT_SECONDARY);
        lblBuscarRef.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTextField txtBuscarRef = new JTextField(15);
        txtBuscarRef.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarRef.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        JButton btnBuscarRef = createStyledButton("Buscar", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton btnResetRef  = createStyledButton("Mostrar todo", BG_COLOR, BORDER_COLOR);
        btnResetRef.setForeground(TEXT_PRIMARY);

        searchRefPanel.add(lblBuscarRef);
        searchRefPanel.add(txtBuscarRef);
        searchRefPanel.add(btnBuscarRef);
        searchRefPanel.add(btnResetRef);

        // metemos la barra arriba
        tablePanel.add(searchRefPanel, BorderLayout.NORTH);

        // ====== tabla refacciones ======
        tmRef = new DefaultTableModel(new Object[]{"ID", "Clave", "Descripción", "Precio", "Activo"}, 0);
        tblRef = new JTable(tmRef);
        styleTable(tblRef);

        JScrollPane sc = new JScrollPane(tblRef);
        sc.setBorder(null);
        tablePanel.add(sc, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        p.add(tablePanel, gbc);

        // Panel de formulario
        JPanel formPanel = createFormPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        p.add(formPanel, gbc);

        // Contenido del formulario
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel titleForm = new JLabel("Información de Refacción");
        titleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleForm.setForeground(TEXT_PRIMARY);
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleForm, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("ID:"), formGbc);
        formGbc.gridy++;
        txtRefId = createStyledTextField(false);
        formPanel.add(txtRefId, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Clave:"), formGbc);
        formGbc.gridy++;
        txtRefClave = createStyledTextField(true);
        formPanel.add(txtRefClave, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Descripción:"), formGbc);
        formGbc.gridy++;
        txtRefDesc = createStyledTextField(true);
        formPanel.add(txtRefDesc, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Precio:"), formGbc);
        formGbc.gridy++;
        txtRefPrecio = createStyledTextField(true);
        formPanel.add(txtRefPrecio, formGbc);

        // ==== Botones ====
        JPanel buttonPanel = createButtonPanel();
        JButton bAdd   = createStyledButton("Agregar",        SUCCESS_COLOR,  SUCCESS_HOVER);
        JButton bUpd   = createStyledButton("Actualizar",     PRIMARY_COLOR,  PRIMARY_HOVER);
        JButton bDel   = createStyledButton("Desactivar",     DANGER_COLOR,   DANGER_HOVER);
        JButton bRea   = createStyledButton("Reactivar",      WARNING_COLOR,  WARNING_HOVER);
        JButton bClean = createStyledButton("Limpiar Campos", BG_COLOR,       BORDER_COLOR);
        bClean.setForeground(TEXT_PRIMARY);

        // botón especial solo para ADMIN
        JButton bDelDef = null;
        if (AuthController.getUsuarioActual() != null &&
            "ADMIN".equalsIgnoreCase(AuthController.getUsuarioActual().getRol())) {

            bDelDef = createStyledButton("Eliminar DEFINITIVO", Color.BLACK, Color.DARK_GRAY);
            bDelDef.setForeground(Color.RED);
        }

        // armamos el grid de botones
        // OJO: createButtonPanel() es GridLayout(3,2) en tu código,
        // aquí vamos a meter 6 o 5 botones, está bien.
        buttonPanel.add(bAdd);
        buttonPanel.add(bUpd);
        buttonPanel.add(bDel);
        buttonPanel.add(bRea);
        buttonPanel.add(bClean);
        if (bDelDef != null) {
            buttonPanel.add(bDelDef);
        }

        formGbc.gridy++;
        formGbc.weighty = 1.0;
        formGbc.anchor = GridBagConstraints.SOUTH;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, formGbc);

        // ====== Data-bind y listeners ======
        cargarRef();

        tblRef.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tblRef.getSelectedRow();
                if (f != -1) {
                    txtRefId.setText(tmRef.getValueAt(f, 0).toString());
                    txtRefClave.setText(tmRef.getValueAt(f, 1).toString());
                    txtRefDesc.setText(tmRef.getValueAt(f, 2).toString());
                    txtRefPrecio.setText(
                            tmRef.getValueAt(f, 3).toString().replace("$", "")
                    );
                }
            }
        });

        bAdd.addActionListener(e -> {
            if (txtRefClave.getText().trim().isEmpty()
             || txtRefDesc.getText().trim().isEmpty()
             || txtRefPrecio.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Complete todos los campos de refacción",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            try {
                refaccionDAO.insertar(
                        txtRefClave.getText(),
                        txtRefDesc.getText(),
                        Double.parseDouble(txtRefPrecio.getText())
                );
                cargarRef();
                limpiarCamposRef();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Precio inválido, ingrese un valor numérico.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        bUpd.addActionListener(e -> {
            if (txtRefId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione una refacción de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            if (txtRefDesc.getText().trim().isEmpty()
             || txtRefPrecio.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Complete la descripción y el precio",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            try {
                refaccionDAO.actualizar(
                        Integer.parseInt(txtRefId.getText()),
                        txtRefDesc.getText(),
                        Double.parseDouble(txtRefPrecio.getText())
                );
                cargarRef();
                limpiarCamposRef();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Precio inválido, ingrese un valor numérico.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        bDel.addActionListener(e -> {
            if (txtRefId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione una refacción de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            refaccionDAO.desactivar(Integer.parseInt(txtRefId.getText()));
            cargarRef();
            limpiarCamposRef();
        });

        bRea.addActionListener(e -> {
            if (txtRefId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione una refacción de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            refaccionDAO.reactivar(Integer.parseInt(txtRefId.getText()));
            cargarRef();
            limpiarCamposRef();
        });

        // BUSCAR
        btnBuscarRef.addActionListener(e -> {
            String q = txtBuscarRef.getText().trim();
            if (q.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Escribe clave o texto de descripción para buscar.",
                        "Búsqueda",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            cargarRefFiltradas(q);
        });

        // MOSTRAR TODO
        btnResetRef.addActionListener(e -> {
            txtBuscarRef.setText("");
            cargarRef();
        });

        // ===== Eliminar DEFINITIVO =====
        if (bDelDef != null) {
            JButton finalBDelDef = bDelDef;
            finalBDelDef.addActionListener(e -> {
                if (txtRefId.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Seleccione una refacción de la tabla",
                            "Validación",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Esto BORRARÁ la refacción de la base de datos.\n" +
                        "Esta acción NO se puede deshacer.\n\n" +
                        "¿Seguro que quieres ELIMINAR DEFINITIVO?",
                        "Confirmar eliminación permanente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                boolean ok = refaccionDAO.eliminarDefinitivo(
                        Integer.parseInt(txtRefId.getText())
                );

                if (ok) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Refacción eliminada permanentemente.",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    cargarRef();
                    limpiarCamposRef();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se pudo eliminar la refacción.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
        }

        bClean.addActionListener(e -> limpiarCamposRef());

        return p;
    }

    private void limpiarCamposRef() {
        txtRefId.setText("");
        txtRefClave.setText("");
        txtRefDesc.setText("");
        txtRefPrecio.setText("");
    }

    private void cargarRef() {
        tmRef.setRowCount(0);
        for (Refaccion r : refaccionDAO.listar()) {
            tmRef.addRow(new Object[]{r.getIdRefaccion(), r.getClave(), r.getDescripcion(), String.format("$%.2f", r.getPrecioUnitario()), r.isActivo() ? "Si" : "No"});
        }
    }
    private void cargarRefFiltradas(String filtro) {
        tmRef.setRowCount(0);
        for (Refaccion r : refaccionDAO.buscarPorTexto(filtro)) {
            tmRef.addRow(new Object[]{
                    r.getIdRefaccion(),
                    r.getClave(),
                    r.getDescripcion(),
                    String.format("$%.2f", r.getPrecioUnitario()),
                    r.isActivo() ? "Si" : "No"
            });
        }
    }

//////////////////////////////////////////////////////TECNICOS
    private JPanel crearPanelTecnicos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Panel de tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // ====== barra de búsqueda técnicos ======
        JPanel searchTecPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchTecPanel.setBackground(CARD_COLOR);

        JLabel lblBuscarTec = new JLabel("Buscar técnico:");
        lblBuscarTec.setForeground(TEXT_SECONDARY);
        lblBuscarTec.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTextField txtBuscarTec = new JTextField(15);
        txtBuscarTec.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarTec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        JButton btnBuscarTec = createStyledButton("Buscar", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton btnResetTec  = createStyledButton("Mostrar todo", BG_COLOR, BORDER_COLOR);
        btnResetTec.setForeground(TEXT_PRIMARY);

        searchTecPanel.add(lblBuscarTec);
        searchTecPanel.add(txtBuscarTec);
        searchTecPanel.add(btnBuscarTec);
        searchTecPanel.add(btnResetTec);

        // ponemos la barra arriba de la tabla
        tablePanel.add(searchTecPanel, BorderLayout.NORTH);

        // ====== tabla técnicos ======
        tmTec = new DefaultTableModel(
                new Object[]{"ID", "No.Empleado", "Nombre", "Teléfono", "Email", "Activo"}, 0
        );
        tblTec = new JTable(tmTec);
        styleTable(tblTec);

        JScrollPane sc = new JScrollPane(tblTec);
        sc.setBorder(null);
        tablePanel.add(sc, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        p.add(tablePanel, gbc);

        // Panel de formulario
        JPanel formPanel = createFormPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        p.add(formPanel, gbc);

        // Contenido del formulario
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel titleForm = new JLabel("Información de Técnico");
        titleForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleForm.setForeground(TEXT_PRIMARY);
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleForm, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("ID:"), formGbc);
        formGbc.gridy++;
        txtTecId = createStyledTextField(false);
        formPanel.add(txtTecId, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("No. Empleado:"), formGbc);
        formGbc.gridy++;
        txtTecNo = createStyledTextField(true);
        formPanel.add(txtTecNo, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Nombre:"), formGbc);
        formGbc.gridy++;
        txtTecNom = createStyledTextField(true);
        formPanel.add(txtTecNom, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Teléfono:"), formGbc);
        formGbc.gridy++;
        txtTecTel = createStyledTextField(true);
        formPanel.add(txtTecTel, formGbc);

        formGbc.gridy++;
        formPanel.add(createStyledLabel("Email:"), formGbc);
        formGbc.gridy++;
        txtTecMail = createStyledTextField(true);
        formPanel.add(txtTecMail, formGbc);

        // ==== Botones ====
        JButton bAdd   = createStyledButton("Agregar",        SUCCESS_COLOR,  SUCCESS_HOVER);
        JButton bUpd   = createStyledButton("Actualizar",     PRIMARY_COLOR,  PRIMARY_HOVER);
        JButton bDel   = createStyledButton("Desactivar",     DANGER_COLOR,   DANGER_HOVER);
        JButton bRea   = createStyledButton("Reactivar",      WARNING_COLOR,  WARNING_HOVER);
        JButton bClean = createStyledButton("Limpiar Campos", BG_COLOR,       BORDER_COLOR);
        bClean.setForeground(TEXT_PRIMARY);

        // solo admin puede ver "Eliminar DEFINITIVO"
        JButton bDelDef = null;
        if (AuthController.getUsuarioActual() != null &&
            "ADMIN".equalsIgnoreCase(AuthController.getUsuarioActual().getRol())) {

            bDelDef = createStyledButton("Eliminar DEFINITIVO", Color.BLACK, Color.DARK_GRAY);
            bDelDef.setForeground(Color.RED);
        }

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(bAdd);
        buttonPanel.add(bUpd);
        buttonPanel.add(bDel);
        buttonPanel.add(bRea);
        buttonPanel.add(bClean);
        if (bDelDef != null) {
            buttonPanel.add(bDelDef);
        }

        formGbc.gridy++;
        formGbc.weighty = 1.0;
        formGbc.anchor = GridBagConstraints.SOUTH;
        formGbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonPanel, formGbc);

        cargarTec();

        // --- listeners tabla ---
        tblTec.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tblTec.getSelectedRow();
                if (f != -1) {
                    txtTecId.setText(tmTec.getValueAt(f, 0).toString());
                    txtTecNo.setText(tmTec.getValueAt(f, 1).toString());
                    txtTecNom.setText(tmTec.getValueAt(f, 2).toString());
                    txtTecTel.setText(tmTec.getValueAt(f, 3).toString());
                    txtTecMail.setText(tmTec.getValueAt(f, 4).toString());
                }
            }
        });

        // BUSCAR
        btnBuscarTec.addActionListener(e -> {
            String q = txtBuscarTec.getText().trim();
            if (q.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Escribe nombre, no.empleado, tel o email para buscar.",
                        "Búsqueda",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            cargarTecFiltrados(q);
        });

        // MOSTRAR TODO
        btnResetTec.addActionListener(e -> {
            txtBuscarTec.setText("");
            cargarTec();
        });

        bAdd.addActionListener(e -> {
            if (txtTecNo.getText().trim().isEmpty()
             || txtTecNom.getText().trim().isEmpty()
             || txtTecTel.getText().trim().isEmpty()
             || txtTecMail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Complete todos los campos de técnico",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            tecnicoDAO.insertar(
                    txtTecNo.getText(),
                    txtTecNom.getText(),
                    txtTecTel.getText(),
                    txtTecMail.getText()
            );
            cargarTec();
            limpiarCamposTec();
        });

        bUpd.addActionListener(e -> {
            if (txtTecId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un técnico de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            if (txtTecTel.getText().trim().isEmpty()
             || txtTecMail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Complete el teléfono y el email",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            tecnicoDAO.actualizar(
                    Integer.parseInt(txtTecId.getText()),
                    txtTecTel.getText(),
                    txtTecMail.getText()
            );
            cargarTec();
            limpiarCamposTec();
        });

        bDel.addActionListener(e -> {
            if (txtTecId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un técnico de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            tecnicoDAO.desactivar(Integer.parseInt(txtTecId.getText()));
            cargarTec();
            limpiarCamposTec();
        });

        bRea.addActionListener(e -> {
            if (txtTecId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un técnico de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            tecnicoDAO.reactivar(Integer.parseInt(txtTecId.getText()));
            cargarTec();
            limpiarCamposTec();
        });

        if (bDelDef != null) {
            JButton finalBDelDef = bDelDef;
            finalBDelDef.addActionListener(e -> {
                if (txtTecId.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Seleccione un técnico de la tabla",
                            "Validación",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Esto BORRARÁ el técnico de la base de datos.\n" +
                        "Esta acción NO se puede deshacer.\n\n" +
                        "¿Seguro que quieres ELIMINAR DEFINITIVO?",
                        "Confirmar eliminación permanente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                boolean ok = tecnicoDAO.eliminarDefinitivo(
                        Integer.parseInt(txtTecId.getText())
                );

                if (ok) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Técnico eliminado permanentemente.",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    cargarTec();
                    limpiarCamposTec();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se pudo eliminar el técnico.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
        }

        bClean.addActionListener(e -> limpiarCamposTec());

        return p;
    }


    private void limpiarCamposTec() {
        txtTecId.setText("");
        txtTecNo.setText("");
        txtTecNom.setText("");
        txtTecTel.setText("");
        txtTecMail.setText("");
    }

    private void cargarTec() {
        tmTec.setRowCount(0);
        for (Tecnico t : tecnicoDAO.listar()) {
            tmTec.addRow(new Object[]{
                t.getIdTecnico(),
                t.getNoEmpleado(),
                t.getNombre(),
                t.getTelefono(),
                t.getEmail(),
                t.isActivo() ? "Sí" : "No"
            });
        }
    }
    
    private void cargarTecFiltrados(String filtro) {
        tmTec.setRowCount(0);
        for (Tecnico t : tecnicoDAO.buscarPorTexto(filtro)) {
            tmTec.addRow(new Object[]{
                    t.getIdTecnico(),
                    t.getNoEmpleado(),
                    t.getNombre(),
                    t.getTelefono(),
                    t.getEmail(),
                    t.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void aplicarPermisosSegunRol() {
        // Implementar lógica de permisos según el rol del usuario
        // Este método se mantiene para compatibilidad con el código original
    }
}

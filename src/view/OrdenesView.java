package view;

import controller.AuthController;
import model.*;
import model.dao.OrdenServicioDAO;
import model.dao.RefaccionDAO;
import model.dao.TecnicoDAO;
import model.dao.TipoServicioDAO;
import model.state.ControlEstadoOrden;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * OrdenesView
 * Buscar, ver, modificar y eliminar órdenes de servicio
 */
public class OrdenesView extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);      // Azul
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);      // Verde
    private static final Color WARNING_COLOR = new Color(251, 191, 36);     // Amarillo
    private static final Color DANGER_COLOR = new Color(239, 68, 68);       // Rojo
    private static final Color SECONDARY_COLOR = new Color(107, 114, 128);  // Gris
    private static final Color BG_COLOR = new Color(249, 250, 251);         // Fondo claro
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(31, 41, 55);

    // DAOs
    private final OrdenServicioDAO ordenDAO = new OrdenServicioDAO();
    private final RefaccionDAO refaccionDAO = new RefaccionDAO();
    private final TecnicoDAO tecnicoDAO = new TecnicoDAO();
    private final TipoServicioDAO tipoServicioDAO = new TipoServicioDAO();

    // Búsqueda
    private JTextField txtBuscarFolio;
    private JTextField txtBuscarNombre;
    private JButton btnBuscarFolio;
    private JButton btnBuscarNombre;
    private JTable tblResultados;
    private DefaultTableModel tmResultados;

    // Detalle de la orden
    private JTextField txtFolio;
    private JTextField txtCliente;
    private JTextField txtEntregadoPor;   // entregado por 
    private JTextField txtVehiculo;
    private JComboBox<Tecnico> cbTecnicoEdit;
    private JComboBox<TipoServicio> cbTipoServicioEdit;
    private JTextField txtManoObra;
    private JTextField txtProximoServicio;
    private JTextArea txtNotas;
    private JTextField txtTotalRef;
    private JLabel lblEstatusActual;
    private JButton btnGuardarProximo;

    // Estatus 
    private JButton btnMoverProceso;
    private JButton btnMoverFinalizado;

    // Refacciones usadas
    private JTable tblRefacciones;
    private DefaultTableModel tmRefacciones;
    private JComboBox<Refaccion> cbRefaccion;
    private JTextField txtCantidad;
    private JTextField txtPrecioUnitario;
    private JButton btnAgregarRefaccion;
    private JButton btnQuitarRefaccion;

    // Acciones globales
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    // Estado actual
    private OrdenServicio ordenActual;
    private ControlEstadoOrden controlEstado;
    private boolean modoEdicion = false;

    public OrdenesView() {
        setTitle("Gestión de Órdenes de Servicio");
        setSize(1200, 750);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelRoot = new JPanel(null);
        panelRoot.setBackground(BG_COLOR);
        panelRoot.setPreferredSize(new Dimension(1200, 900));

        JScrollPane scrollPane = new JScrollPane(panelRoot);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        setContentPane(scrollPane);

        JLabel lblTitulo = new JLabel("Órdenes de Servicio");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(TEXT_COLOR);
        lblTitulo.setBounds(30, 20, 400, 35);
        panelRoot.add(lblTitulo);

        JPanel panelBuscar = crearPanelCard("Búsqueda de Órdenes");
        panelBuscar.setBounds(30, 70, 550, 250);
        panelRoot.add(panelBuscar);

        JLabel lblBF = new JLabel("Folio:");
        lblBF.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBF.setBounds(20, 40, 80, 25);
        panelBuscar.add(lblBF);

        txtBuscarFolio = crearTextField();
        txtBuscarFolio.setBounds(100, 40, 150, 30);
        panelBuscar.add(txtBuscarFolio);

        btnBuscarFolio = crearBoton("Buscar por Folio", PRIMARY_COLOR);
        btnBuscarFolio.setBounds(270, 40, 250, 30);
        panelBuscar.add(btnBuscarFolio);

        JLabel lblBN = new JLabel("Cliente:");
        lblBN.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBN.setBounds(20, 85, 80, 25);
        panelBuscar.add(lblBN);

        txtBuscarNombre = crearTextField();
        txtBuscarNombre.setBounds(100, 85, 150, 30);
        panelBuscar.add(txtBuscarNombre);

        btnBuscarNombre = crearBoton("Buscar por Cliente", PRIMARY_COLOR);
        btnBuscarNombre.setBounds(270, 85, 250, 30);
        panelBuscar.add(btnBuscarNombre);

        tmResultados = new DefaultTableModel(
                new Object[]{"ID", "Folio", "Cliente", "Placa", "Estatus"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblResultados = new JTable(tmResultados);
        estilizarTabla(tblResultados);
        JScrollPane scRes = new JScrollPane(tblResultados);
        scRes.setBounds(20, 130, 510, 90);
        scRes.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        panelBuscar.add(scRes);

        JPanel panelDetalle = crearPanelCard("Detalle de la Orden");
        panelDetalle.setBounds(30, 340, 550, 600);
        panelRoot.add(panelDetalle);

        int y = 40;

        // Folio
        agregarLabel(panelDetalle, "Folio:", 20, y);
        txtFolio = crearTextField();
        txtFolio.setEditable(false);
        txtFolio.setBounds(180, y, 150, 30);
        panelDetalle.add(txtFolio);

        // Cliente
        y += 45;
        agregarLabel(panelDetalle, "Cliente:", 20, y);
        txtCliente = crearTextField();
        txtCliente.setEditable(false);
        txtCliente.setBounds(180, y, 350, 30);
        panelDetalle.add(txtCliente);

        // NUEVO: Entregado por
        y += 45;
        agregarLabel(panelDetalle, "Entregado por:", 20, y);
        txtEntregadoPor = crearTextField();
        txtEntregadoPor.setEditable(false);
        txtEntregadoPor.setBounds(180, y, 350, 30);
        panelDetalle.add(txtEntregadoPor);

        // Vehículo
        y += 45;
        agregarLabel(panelDetalle, "Vehículo (Placa):", 20, y);
        txtVehiculo = crearTextField();
        txtVehiculo.setEditable(false);
        txtVehiculo.setBounds(180, y, 150, 30);
        panelDetalle.add(txtVehiculo);

        // Técnico
        y += 45;
        agregarLabel(panelDetalle, "Técnico:", 20, y);
        cbTecnicoEdit = new JComboBox<>();
        cbTecnicoEdit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTecnicoEdit.setBounds(180, y, 350, 30);
        cbTecnicoEdit.setEnabled(false);
        panelDetalle.add(cbTecnicoEdit);

        // Tipo Servicio
        y += 45;
        agregarLabel(panelDetalle, "Tipo Servicio:", 20, y);
        cbTipoServicioEdit = new JComboBox<>();
        cbTipoServicioEdit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTipoServicioEdit.setBounds(180, y, 350, 30);
        cbTipoServicioEdit.setEnabled(false);
        panelDetalle.add(cbTipoServicioEdit);

        // Mano Obra y Total Refacciones
        y += 45;
        agregarLabel(panelDetalle, "Mano de Obra:", 20, y);
        txtManoObra = crearTextField();
        txtManoObra.setEditable(false);
        txtManoObra.setBounds(180, y, 120, 30);
        panelDetalle.add(txtManoObra);

        agregarLabel(panelDetalle, "Total Refacciones:", 320, y);
        txtTotalRef = crearTextField();
        txtTotalRef.setEditable(false);
        txtTotalRef.setBounds(450, y, 80, 30);
        panelDetalle.add(txtTotalRef);

        // Próximo Servicio
        y += 45;
        agregarLabel(panelDetalle, "Fecha del Servicio:", 20, y);
        txtProximoServicio = crearTextField();
        txtProximoServicio.setEditable(false);
        txtProximoServicio.setBounds(180, y, 150, 30);
        panelDetalle.add(txtProximoServicio);

        btnGuardarProximo = crearBoton("Guardar", SUCCESS_COLOR);
        btnGuardarProximo.setBounds(350, y, 180, 30);
        panelDetalle.add(btnGuardarProximo);

        // Notas
        y += 45;
        agregarLabel(panelDetalle, "Notas:", 20, y);
        txtNotas = new JTextArea();
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        txtNotas.setEditable(false);
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNotas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        JScrollPane scNotas = new JScrollPane(txtNotas);
        scNotas.setBounds(180, y, 350, 80);
        scNotas.setBorder(null);
        panelDetalle.add(scNotas);

        // Estatus
        y += 95;
        agregarLabel(panelDetalle, "Estatus Actual:", 20, y);
        lblEstatusActual = new JLabel("------", SwingConstants.CENTER);
        lblEstatusActual.setOpaque(true);
        lblEstatusActual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstatusActual.setBackground(SECONDARY_COLOR);
        lblEstatusActual.setForeground(Color.WHITE);
        lblEstatusActual.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        lblEstatusActual.setBounds(180, y, 150, 35);
        panelDetalle.add(lblEstatusActual);

        btnMoverProceso = crearBoton("→ EN PROCESO", PRIMARY_COLOR);
        btnMoverProceso.setBounds(350, y, 180, 32);
        panelDetalle.add(btnMoverProceso);

        btnMoverFinalizado = crearBoton("→ FINALIZADO", SUCCESS_COLOR);
        btnMoverFinalizado.setBounds(350, y + 40, 180, 32);
        panelDetalle.add(btnMoverFinalizado);

        JPanel panelRef = crearPanelCard("Refacciones Usadas");
        panelRef.setBounds(600, 70, 570, 650);
        panelRoot.add(panelRef);

        // Tabla de refacciones
        tmRefacciones = new DefaultTableModel(
                new Object[]{"Refacción", "Cant", "P.Unit", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblRefacciones = new JTable(tmRefacciones);
        estilizarTabla(tblRefacciones);
        JScrollPane scRef = new JScrollPane(tblRefacciones);
        scRef.setBounds(20, 40, 530, 280);
        scRef.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        panelRef.add(scRef);

        // Formulario agregar refacción
        int yRef = 340;
        agregarLabel(panelRef, "Refacción:", 20, yRef);
        cbRefaccion = new JComboBox<>();
        cbRefaccion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRefaccion.setBounds(120, yRef, 430, 30);
        panelRef.add(cbRefaccion);

        yRef += 45;
        agregarLabel(panelRef, "Cantidad:", 20, yRef);
        txtCantidad = crearTextField();
        txtCantidad.setBounds(120, yRef, 100, 30);
        panelRef.add(txtCantidad);

        agregarLabel(panelRef, "Precio Unit:", 250, yRef);
        txtPrecioUnitario = crearTextField();
        txtPrecioUnitario.setBounds(350, yRef, 100, 30);
        panelRef.add(txtPrecioUnitario);

        yRef += 50;
        btnAgregarRefaccion = crearBoton("Agregar Refacción", SUCCESS_COLOR);
        btnAgregarRefaccion.setBounds(20, yRef, 250, 35);
        panelRef.add(btnAgregarRefaccion);

        btnQuitarRefaccion = crearBoton("Quitar Refacción", DANGER_COLOR);
        btnQuitarRefaccion.setBounds(290, yRef, 250, 35);
        panelRef.add(btnQuitarRefaccion);

        JPanel panelAcciones = crearPanelCard("Acciones");
        panelAcciones.setBounds(600, 740, 570, 120);
        panelRoot.add(panelAcciones);

        btnModificar = crearBoton("Modificar / Guardar", PRIMARY_COLOR);
        btnModificar.setBounds(20, 40, 200, 40);
        panelAcciones.add(btnModificar);

        btnEliminar = crearBoton("Eliminar Orden", DANGER_COLOR);
        btnEliminar.setBounds(240, 40, 180, 40);
        panelAcciones.add(btnEliminar);

        btnCerrar = crearBoton("Cerrar", SECONDARY_COLOR);
        btnCerrar.setBounds(440, 40, 110, 40);
        panelAcciones.add(btnCerrar);

        // Cargar combos
        cargarComboTecnicos();
        cargarComboTiposServicio();
        cargarComboRefacciones();

        // Listeners
        initActions();

        // Permisos
        aplicarPermisosSegunRol();
    }

    private JPanel crearPanelCard(String titulo) {
        JPanel panel = new JPanel(null);
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(TEXT_COLOR);
        lblTitulo.setBounds(10, 5, 400, 25);
        panel.add(lblTitulo);

        return panel;
    }

    private JTextField crearTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void agregarLabel(JPanel panel, String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(TEXT_COLOR);
        lbl.setBounds(x, y, 150, 25);
        panel.add(lbl);
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(TEXT_COLOR);
        tabla.setGridColor(new Color(229, 231, 235));

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void cargarComboTecnicos() {
        cbTecnicoEdit.removeAllItems();
        for (Tecnico t : tecnicoDAO.listar()) {
            if (t.isActivo()) {
                cbTecnicoEdit.addItem(t);
            }
        }
    }

    private void cargarComboTiposServicio() {
        cbTipoServicioEdit.removeAllItems();
        for (TipoServicio ts : tipoServicioDAO.listar()) {
            if (ts.isActivo()) {
                cbTipoServicioEdit.addItem(ts);
            }
        }
    }

    private void cargarComboRefacciones() {
        cbRefaccion.removeAllItems();
        for (Refaccion r : refaccionDAO.listar()) {
            if (r.isActivo()) {
                cbRefaccion.addItem(r);
            }
        }

        cbRefaccion.addActionListener(e -> {
            Refaccion refSel = (Refaccion) cbRefaccion.getSelectedItem();
            if (refSel != null) {
                txtPrecioUnitario.setText(String.valueOf(refSel.getPrecioUnitario()));
            }
        });
    }

    private void refrescarTablaResultados(List<OrdenServicio> lista) {
        tmResultados.setRowCount(0);
        for (OrdenServicio o : lista) {
            String clienteTxt = o.getCliente().getNombre() + " " + o.getCliente().getApellidos();
            String placaTxt   = (o.getVehiculo() != null) ? o.getVehiculo().getPlaca() : "";
            tmResultados.addRow(new Object[]{
                    o.getIdOrden(),
                    o.getFolio(),
                    clienteTxt,
                    placaTxt,
                    o.getEstatus()
            });
        }
    }

    private void refrescarTablaRefacciones(OrdenServicio orden) {
        tmRefacciones.setRowCount(0);
        if (orden == null) return;
        if (orden.getRefacciones() == null) return;

        for (OrdenRefaccion det : orden.getRefacciones()) {
            Refaccion ref = det.getRefaccion();
            String refTxt = ref.getClave() + " - " + ref.getDescripcion();
            double subtotal = det.getSubtotal();
            tmRefacciones.addRow(new Object[]{
                    refTxt,
                    det.getCantidad(),
                    det.getPrecioUnitario(),
                    subtotal
            });
        }
    }

    private void mostrarOrdenEnPantalla(OrdenServicio o) {
        if (o == null) return;
        ordenActual = o;
        controlEstado = new ControlEstadoOrden(ordenActual, ordenDAO);

        txtFolio.setText(o.getFolio());

        String clienteTxt = o.getCliente().getNombre() + " " + o.getCliente().getApellidos();
        txtCliente.setText(clienteTxt);

        String entregado = o.getEntregadoPor();
        txtEntregadoPor.setText(entregado != null ? entregado : "");
        // Si no coincide con el cliente, solo destacamos visualmente
        if (entregado != null && !entregado.isBlank()
                && !entregado.equalsIgnoreCase(clienteTxt)) {
            txtEntregadoPor.setForeground(Color.RED);
            txtEntregadoPor.setToolTipText("El vehículo fue entregado por otra persona. Verificar INE del propietario.");
        } else {
            txtEntregadoPor.setForeground(TEXT_COLOR);
            txtEntregadoPor.setToolTipText(null);
        }

        String placaTxt = (o.getVehiculo() != null) ? o.getVehiculo().getPlaca() : "";
        txtVehiculo.setText(placaTxt);

        seleccionarTecnicoEnCombo(o.getTecnico());
        seleccionarTipoServicioEnCombo(o.getTipoServicio());

        txtManoObra.setText(String.valueOf(o.getManoObra()));
        txtTotalRef.setText(String.valueOf(o.getTotalRefacciones()));

        if (o.getProximoServicio() != null) {
            txtProximoServicio.setText(o.getProximoServicio().toString());
        } else {
            txtProximoServicio.setText("");
        }

        txtNotas.setText(o.getNotas() != null ? o.getNotas() : "");

        lblEstatusActual.setText(String.valueOf(o.getEstatus()));
        pintarEstatus(o.getEstatus());

        refrescarTablaRefacciones(o);

        setModoEdicion(false);
    }

    private void seleccionarTecnicoEnCombo(Tecnico tecnicoActual) {
        if (tecnicoActual == null) {
            cbTecnicoEdit.setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < cbTecnicoEdit.getItemCount(); i++) {
            Tecnico t = cbTecnicoEdit.getItemAt(i);
            if (t.getIdTecnico() == tecnicoActual.getIdTecnico()) {
                cbTecnicoEdit.setSelectedIndex(i);
                return;
            }
        }
        cbTecnicoEdit.setSelectedIndex(-1);
    }

    private void seleccionarTipoServicioEnCombo(TipoServicio tipoActual) {
        if (tipoActual == null) {
            cbTipoServicioEdit.setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < cbTipoServicioEdit.getItemCount(); i++) {
            TipoServicio ts = cbTipoServicioEdit.getItemAt(i);
            if (ts.getIdTipoServicio() == tipoActual.getIdTipoServicio()) {
                cbTipoServicioEdit.setSelectedIndex(i);
                return;
            }
        }
        cbTipoServicioEdit.setSelectedIndex(-1);
    }

    private void pintarEstatus(EstatusOrden est) {
        if (est == null) {
            lblEstatusActual.setBackground(SECONDARY_COLOR);
            lblEstatusActual.setForeground(Color.WHITE);
            return;
        }
        switch (est) {
            case EN_ESPERA:
                lblEstatusActual.setBackground(WARNING_COLOR);
                lblEstatusActual.setForeground(Color.BLACK);
                break;
            case EN_PROCESO:
                lblEstatusActual.setBackground(PRIMARY_COLOR);
                lblEstatusActual.setForeground(Color.WHITE);
                break;
            case FINALIZADO:
                lblEstatusActual.setBackground(SUCCESS_COLOR);
                lblEstatusActual.setForeground(Color.WHITE);
                break;
        }
    }

    private String buildTicketFinalText(OrdenServicio o) {
        String clienteTxt = o.getCliente().getNombre() + " " + o.getCliente().getApellidos();
        String placaTxt   = (o.getVehiculo() != null ? o.getVehiculo().getPlaca() : "");
        String tecnicoTxt = (o.getTecnico() != null ? o.getTecnico().getNombre() : "");
        String tipoTxt    = (o.getTipoServicio() != null ? o.getTipoServicio().getNombre() : "");

        double manoObra   = o.getManoObra();
        double refTotal   = o.getTotalRefacciones();
        double granTotal  = manoObra + refTotal;

        StringBuilder desgloseRefs = new StringBuilder();
        desgloseRefs.append("Refacciones usadas:\n");
        if (o.getRefacciones() != null && !o.getRefacciones().isEmpty()) {
            for (OrdenRefaccion det : o.getRefacciones()) {
                Refaccion r = det.getRefaccion();
                desgloseRefs.append(
                        String.format(
                                "  - %s (%s) x%d @ $%.2f = $%.2f\n",
                                r.getDescripcion(),
                                r.getClave(),
                                det.getCantidad(),
                                det.getPrecioUnitario(),
                                det.getSubtotal()
                        )
                );
            }
        } else {
            desgloseRefs.append("  (Sin refacciones)\n");
        }

        return
                "         TALLER AUTOMOTRIZ\n" +
                "        ORDEN FINALIZADA\n" +
                "--------------------------------------\n" +
                "Folio: " + o.getFolio() + "\n" +
                "Fecha cierre: " + LocalDate.now() + "\n" +
                "\n" +
                "Cliente: " + clienteTxt + "\n" +
                "Vehiculo (placa): " + placaTxt + "\n" +
                "Tecnico responsable: " + tecnicoTxt + "\n" +
                "Servicio realizado: " + tipoTxt + "\n" +
                "\n" +
                "---- COSTOS FINALES ----\n" +
                String.format("Mano de obra:     $%.2f\n", manoObra) +
                String.format("Refacciones:      $%.2f\n", refTotal) +
                String.format("TOTAL A PAGAR:    $%.2f\n", granTotal) +
                "\n" +
                desgloseRefs.toString() +
                "\n" +
                "Proximo servicio: " +
                (o.getProximoServicio() != null ? o.getProximoServicio().toString() : "N/A") + "\n" +
                "\n" +
                "Notas finales:\n" +
                (o.getNotas() != null ? o.getNotas() : "") + "\n" +
                "--------------------------------------\n" +
                "   Gracias por su preferencia!\n";
    }


    private void guardarTicketComoPNG(String ticketText, String folio) {
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

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        int x = padding;
        int y = padding + fm.getAscent();
        for (String linea : lineas) {
            g2.drawString(linea, x, y);
            y += lineHeight;
        }

        g2.dispose();

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar ticket como imagen");
        chooser.setFileFilter(new FileNameExtensionFilter("Imagen PNG", "png"));

        String sugerido = "TICKET_" +
                (folio != null ? folio : "orden") + "_" +
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                        .format(java.time.LocalDateTime.now()) +
                ".png";

        chooser.setSelectedFile(new File(sugerido));

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

    private void setModoEdicion(boolean edit) {
        modoEdicion = edit;

        cbTecnicoEdit.setEnabled(edit);
        cbTipoServicioEdit.setEnabled(edit);
        txtManoObra.setEditable(edit);
        txtProximoServicio.setEditable(edit);
        txtNotas.setEditable(edit);

        txtFolio.setEditable(false);
        txtCliente.setEditable(false);
        txtVehiculo.setEditable(false);
        txtTotalRef.setEditable(false);

        btnMoverProceso.setEnabled(!edit && puedeAdminMover());
        btnMoverFinalizado.setEnabled(!edit && puedeAdminMover());
        btnAgregarRefaccion.setEnabled(!edit && puedeAdminEditar());
        btnQuitarRefaccion.setEnabled(!edit && puedeAdminEditar());
        btnEliminar.setEnabled(!edit && puedeAdminEditar());

        btnModificar.setText(edit ? "Guardar Cambios" : "Modificar / Guardar");
    }

    private boolean puedeAdminEditar() {
        Usuario u = AuthController.getUsuarioActual();
        return (u != null && "ADMIN".equalsIgnoreCase(u.getRol()));
    }

    private boolean puedeAdminMover() {
        return puedeAdminEditar();
    }

    private void mostrarComprobanteFinal(OrdenServicio o) {
        if (o == null) return;

        String ticketText = buildTicketFinalText(o);

        JTextArea area = new JTextArea(ticketText);
        area.setEditable(false);
        area.setFont(new Font("monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(480, 380));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Comprobante Final - Folio " + o.getFolio(),
                JOptionPane.INFORMATION_MESSAGE
        );

        int opc = JOptionPane.showConfirmDialog(
                this,
                "Quieres guardar este comprobante como imagen (PNG)?",
                "Exportar ticket",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opc == JOptionPane.YES_OPTION) {
            guardarTicketComoPNG(ticketText, o.getFolio());
        }
    }

    private void initActions() {
        btnCerrar.addActionListener(e -> dispose());

        btnBuscarFolio.addActionListener(e -> {
            String folio = txtBuscarFolio.getText().trim();
            if (folio.isEmpty()) return;
            OrdenServicio o = ordenDAO.buscarPorFolio(folio);
            if (o != null) {
                refrescarTablaResultados(java.util.Arrays.asList(o));
                mostrarOrdenEnPantalla(o);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontro orden con folio " + folio,
                        "Sin resultados",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        
        
        btnBuscarNombre.addActionListener(e -> {
            String nombre = txtBuscarNombre.getText().trim();
            if (nombre.isEmpty()) return;
            List<OrdenServicio> lista = ordenDAO.buscarPorNombreCliente(nombre);
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontraron ordenes para cliente que contenga: " + nombre,
                        "Sin resultados",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            refrescarTablaResultados(lista);
        });

        tblResultados.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tblResultados.getSelectedRow();
            if (row == -1) return;
            String folioSel = (String) tmResultados.getValueAt(row, 1);
            if (folioSel == null) return;
            OrdenServicio o = ordenDAO.buscarPorFolio(folioSel);
            if (o != null) {
                mostrarOrdenEnPantalla(o);
            }
        });

        btnModificar.addActionListener(e -> {
            if (ordenActual == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Primero selecciona una orden.",
                        "Sin orden",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!modoEdicion) {
                if (!puedeAdminEditar()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Solo ADMIN puede modificar la orden.",
                            "Sin permiso",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                setModoEdicion(true);
                return;
            }

            double manoObraVal;
            try {
                manoObraVal = Double.parseDouble(txtManoObra.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Mano de obra invalida.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            LocalDate prox = null;
            String proxTxt = txtProximoServicio.getText().trim();
            if (!proxTxt.isEmpty()) {
                try {
                    prox = LocalDate.parse(proxTxt);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Fecha de proximo servicio invalida. Usa yyyy-MM-dd.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }

            Tecnico tecnicoSel = (Tecnico) cbTecnicoEdit.getSelectedItem();
            TipoServicio tipoSel = (TipoServicio) cbTipoServicioEdit.getSelectedItem();
            if (tecnicoSel == null || tipoSel == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecciona tecnico y tipo de servicio.",
                        "Faltan datos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String notasNuevas = txtNotas.getText().trim();

            boolean ok = ordenDAO.actualizarOrdenBasica(
                    ordenActual.getIdOrden(),
                    tecnicoSel.getIdTecnico(),
                    tipoSel.getIdTipoServicio(),
                    manoObraVal,
                    prox,
                    notasNuevas
            );

            if (!ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar la orden.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            OrdenServicio oActualizada = ordenDAO.buscarPorFolio(ordenActual.getFolio());
            mostrarOrdenEnPantalla(oActualizada);

            JOptionPane.showMessageDialog(
                    this,
                    "Cambios guardados.",
                    "Listo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        btnGuardarProximo.addActionListener(e -> {
            if (ordenActual == null) return;
            String fechaTxt = txtProximoServicio.getText().trim();
            LocalDate fecha = null;
            if (!fechaTxt.isEmpty()) {
                try {
                    fecha = LocalDate.parse(fechaTxt);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Formato de fecha invalido. Usa yyyy-MM-dd",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }

            boolean ok = ordenDAO.actualizarProximoServicio(ordenActual.getIdOrden(), fecha);
            if (ok) {
                ordenActual.setProximoServicio(fecha);
                JOptionPane.showMessageDialog(
                        this,
                        "Proximo servicio actualizado.",
                        "OK",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar en BD.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnMoverProceso.addActionListener(e -> {
            if (ordenActual == null || controlEstado == null) return;

            Usuario actual = AuthController.getUsuarioActual();
            if (actual == null) {
                JOptionPane.showMessageDialog(this, "No hay usuario en sesion.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nota = JOptionPane.showInputDialog(
                    this,
                    "Nota para bitacora (ej: 'Inicio de trabajo en taller'):",
                    "Cambiar a EN PROCESO",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (nota == null) return;

            String resultado = controlEstado.cambiarAEnProceso(
                    actual.getIdUsuario(),
                    nota
            );

            if ("OK".equals(resultado)) {
                mostrarOrdenEnPantalla(ordenDAO.buscarPorFolio(ordenActual.getFolio()));
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        resultado,
                        "No permitido",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        btnMoverFinalizado.addActionListener(e -> {
            if (ordenActual == null || controlEstado == null) return;

            Usuario actual = AuthController.getUsuarioActual();
            if (actual == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No hay usuario en sesion.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // ====== VALIDACIÓN DE FECHA ANTES DE FINALIZAR ======
            // 1. Leemos lo que está en el campo "Próximo Servicio"
            String proxTxt = txtProximoServicio.getText().trim();
            if (!proxTxt.isEmpty()) {
                try {
                    LocalDate proxDate = LocalDate.parse(proxTxt); // formato yyyy-MM-dd
                    LocalDate hoy = LocalDate.now();

                    // Si la fecha de próximo servicio es después de hoy,
                    // NO permitimos finalizar todavía.
                    if (proxDate.isAfter(hoy)) {
                        JOptionPane.showMessageDialog(
                                this,
                                "No puedes marcar FINALIZADO antes de la fecha programada (" + proxDate + ").",
                                "Acción no permitida",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                } catch (Exception exFecha) {
                    // Si hay una fecha mal escrita y no la puedo parsear, también bloqueo
                    JOptionPane.showMessageDialog(
                            this,
                            "La fecha de Próximo Servicio no es válida (usa yyyy-MM-dd). Corrígela antes de finalizar.",
                            "Fecha inválida",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }
            // ====== FIN VALIDACIÓN ======

            // Pedimos nota para la bitácora
            String nota = JOptionPane.showInputDialog(
                    this,
                    "Nota para bitacora (ej: 'Servicio terminado y entregado'):",
                    "Cambiar a FINALIZADO",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (nota == null) return;

            // Aquí sí intentamos cambiar a FINALIZADO usando el State controller
            String resultado = controlEstado.cambiarAFinalizado(
                    actual.getIdUsuario(),
                    nota
            );

            if ("OK".equals(resultado)) {
                OrdenServicio oFinal = ordenDAO.buscarPorFolio(ordenActual.getFolio());
                mostrarOrdenEnPantalla(oFinal);
                mostrarComprobanteFinal(oFinal);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        resultado,
                        "No permitido",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });


        btnAgregarRefaccion.addActionListener(e -> {
            if (ordenActual == null) return;

            Refaccion refSel = (Refaccion) cbRefaccion.getSelectedItem();
            if (refSel == null) return;

            int cant;
            double precio;
            try {
                cant = Integer.parseInt(txtCantidad.getText().trim());
                precio = Double.parseDouble(txtPrecioUnitario.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cantidad / precio invalidos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            boolean ok = ordenDAO.agregarRefaccionAOrden(
                    ordenActual.getIdOrden(),
                    refSel.getIdRefaccion(),
                    cant,
                    precio
            );

            if (ok) {
                OrdenServicio oActualizada = ordenDAO.buscarPorFolio(ordenActual.getFolio());
                mostrarOrdenEnPantalla(oActualizada);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo agregar refaccion.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnQuitarRefaccion.addActionListener(e -> {
            if (ordenActual == null) return;

            int fila = tblRefacciones.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecciona una refaccion de la tabla para eliminarla.",
                        "Sin seleccion",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            List<OrdenRefaccion> lista = ordenActual.getRefacciones();
            if (lista == null || fila >= lista.size()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No pude identificar la refaccion seleccionada.",
                        "Error interno",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            OrdenRefaccion det = lista.get(fila);
            Refaccion ref = det.getRefaccion();
            if (ref == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No pude obtener el ID de la refaccion.",
                        "Error interno",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int opt = JOptionPane.showConfirmDialog(
                    this,
                    "Quitar la refaccion:\n" +
                            ref.getClave() + " - " + ref.getDescripcion() +
                            "\nCantidad: " + det.getCantidad() +
                            "\nPrecio unitario: $" + det.getPrecioUnitario() +
                            "\nSubtotal: $" + det.getSubtotal() +
                            "\n\nEsto actualizara el total de la orden.",
                    "Confirmar eliminacion de refaccion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opt != JOptionPane.YES_OPTION) return;

            boolean ok = ordenDAO.eliminarRefaccionDeOrden(
                    ordenActual.getIdOrden(),
                    ref.getIdRefaccion()
            );

            if (!ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar la refaccion en BD.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            OrdenServicio oActualizada = ordenDAO.buscarPorFolio(ordenActual.getFolio());
            mostrarOrdenEnPantalla(oActualizada);

            JOptionPane.showMessageDialog(
                    this,
                    "Refaccion eliminada.",
                    "Listo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        btnEliminar.addActionListener(e -> eliminarOrdenSeleccionada());
    }

    private void eliminarOrdenSeleccionada() {
        if (ordenActual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona una orden.",
                    "Sin orden",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Usuario actual = AuthController.getUsuarioActual();
        if (actual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay usuario en sesion.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!"ADMIN".equalsIgnoreCase(actual.getRol())) {
            JOptionPane.showMessageDialog(
                    this,
                    "Solo ADMIN puede eliminar ordenes.",
                    "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Seguro que deseas eliminar la orden folio " + ordenActual.getFolio() + "?\nEsta accion no se puede deshacer.",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opt != JOptionPane.YES_OPTION) return;

        boolean ok = ordenDAO.eliminarOrden(ordenActual.getIdOrden());
        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar en BD.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Orden eliminada correctamente.",
                "Listo",
                JOptionPane.INFORMATION_MESSAGE
        );

        limpiarDetalle();
        tmResultados.setRowCount(0);
        tmRefacciones.setRowCount(0);
    }

    private void limpiarDetalle() {
        ordenActual = null;
        controlEstado = null;

        txtFolio.setText("");
        txtCliente.setText("");
        txtEntregadoPor.setText("");
        txtEntregadoPor.setForeground(TEXT_COLOR);
        txtEntregadoPor.setToolTipText(null);
        txtVehiculo.setText("");
        cbTecnicoEdit.setSelectedIndex(-1);
        cbTipoServicioEdit.setSelectedIndex(-1);
        txtManoObra.setText("");
        txtTotalRef.setText("");
        txtNotas.setText("");
        txtProximoServicio.setText("");
        lblEstatusActual.setText("------");
        lblEstatusActual.setBackground(SECONDARY_COLOR);
        lblEstatusActual.setForeground(Color.WHITE);

        tmRefacciones.setRowCount(0);

        setModoEdicion(false);
    }

    private void aplicarPermisosSegunRol() {
        Usuario u = AuthController.getUsuarioActual();
        boolean isAdmin = (u != null && "ADMIN".equalsIgnoreCase(u.getRol()));

        btnGuardarProximo.setEnabled(isAdmin);
        btnMoverProceso.setEnabled(isAdmin);
        btnMoverFinalizado.setEnabled(isAdmin);
        btnAgregarRefaccion.setEnabled(isAdmin);
        btnQuitarRefaccion.setEnabled(isAdmin);
        btnEliminar.setEnabled(isAdmin);
        btnModificar.setEnabled(isAdmin);

        txtCantidad.setEditable(isAdmin);
        txtPrecioUnitario.setEditable(isAdmin);

        setModoEdicion(false);
    }
}

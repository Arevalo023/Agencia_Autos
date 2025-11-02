package view;

import controller.AuthController;
import model.Cliente;
import model.OrdenServicio;
import model.dao.OrdenServicioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * ProximosServiciosView
 *
 * Muestra una lista de servicios próximos programados,
 * con filtros por folio o por nombre del cliente.
 *
 * Requisito 9
 */
public class ProximosServiciosView extends JFrame {

    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(31, 41, 55);
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);

    private final OrdenServicioDAO ordenDAO = new OrdenServicioDAO();

    private JTextField txtBuscarFolio;
    private JTextField txtBuscarNombre;
    private JButton btnBuscarFolio;
    private JButton btnBuscarNombre;
    private JButton btnVerTodos;
    private JTable tblResultados;
    private DefaultTableModel tmResultados;

    public ProximosServiciosView() {
        setTitle("Próximos Servicios Programados");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        // HEADER
        JLabel lblTitulo = new JLabel("Próximos Servicios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(TEXT_COLOR);

        JLabel lblSub = new JLabel("Vehículos que deben regresar a mantenimiento");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 116, 139));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(lblTitulo);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblSub);

        root.add(headerPanel, BorderLayout.NORTH);

        // CENTRO (CARD)
        JPanel card = new JPanel(null);
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(760, 360));

        root.add(card, BorderLayout.CENTER);

        // Controles de búsqueda
        JLabel lblFolio = new JLabel("Folio:");
        lblFolio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFolio.setBounds(15, 10, 80, 25);
        card.add(lblFolio);

        txtBuscarFolio = new JTextField();
        txtBuscarFolio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarFolio.setBounds(70, 10, 140, 28);
        card.add(txtBuscarFolio);

        btnBuscarFolio = crearBoton("Buscar Folio");
        btnBuscarFolio.setBounds(220, 10, 140, 28);
        card.add(btnBuscarFolio);

        JLabel lblNombre = new JLabel("Cliente:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNombre.setBounds(380, 10, 80, 25);
        card.add(lblNombre);

        txtBuscarNombre = new JTextField();
        txtBuscarNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarNombre.setBounds(440, 10, 150, 28);
        card.add(txtBuscarNombre);

        btnBuscarNombre = crearBoton("Buscar Cliente");
        btnBuscarNombre.setBounds(600, 10, 140, 28);
        card.add(btnBuscarNombre);

        btnVerTodos = crearBoton("Ver Todos");
        btnVerTodos.setBounds(600, 45, 140, 28);
        card.add(btnVerTodos);

        // Tabla
        tmResultados = new DefaultTableModel(
                new Object[]{"Folio", "Cliente", "Placa", "Próximo Servicio", "Estatus"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblResultados = new JTable(tmResultados);
        tblResultados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblResultados.setRowHeight(26);

        JScrollPane scroll = new JScrollPane(tblResultados);
        scroll.setBounds(15, 90, 725, 240);
        card.add(scroll);

        // Listeners
        btnBuscarFolio.addActionListener(e -> {
            String folio = txtBuscarFolio.getText().trim();
            if (folio.isEmpty()) return;
            List<OrdenServicio> lista = ordenDAO.listarProximosPorFolio(folio);
            refrescarTabla(lista);
        });

        btnBuscarNombre.addActionListener(e -> {
            String nombre = txtBuscarNombre.getText().trim();
            if (nombre.isEmpty()) return;
            List<OrdenServicio> lista = ordenDAO.listarProximosPorNombreCliente(nombre);
            refrescarTabla(lista);
        });

        btnVerTodos.addActionListener(e -> {
            List<OrdenServicio> lista = ordenDAO.listarProximosServicios();
            refrescarTabla(lista);
        });

        // carga inicial
        List<OrdenServicio> lista = ordenDAO.listarProximosServicios();
        refrescarTabla(lista);

        aplicarPermisosSegunRol();
    }

    private JButton crearBoton(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(PRIMARY_COLOR);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        return b;
    }

    private void refrescarTabla(List<OrdenServicio> lista) {
        tmResultados.setRowCount(0);
        for (OrdenServicio o : lista) {
            String clienteTxt = o.getCliente().getNombre() + " " + o.getCliente().getApellidos();
            String placaTxt = (o.getVehiculo() != null ? o.getVehiculo().getPlaca() : "");
            String prox = (o.getProximoServicio() != null ? o.getProximoServicio().toString() : "N/A");

            tmResultados.addRow(new Object[]{
                    o.getFolio(),
                    clienteTxt,
                    placaTxt,
                    prox,
                    o.getEstatus()
            });
        }
    }

    private void aplicarPermisosSegunRol() {
        // Aquí solo lectura, así que no bloqueamos nada.
        // Si quisieras que solo ADMIN pueda ver esto, aquí lo harías.
        // Por ahora cualquier sesión válida puede entrar.
        if (AuthController.getUsuarioActual() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay usuario en sesion. Inicia sesion nuevamente.",
                    "Sin sesion",
                    JOptionPane.WARNING_MESSAGE
            );
            dispose();
        }
    }
}

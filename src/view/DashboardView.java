package view;

import controller.AuthController;
import controller.DashboardController;
import controller.MenuPrincipalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DashboardView
 *
 * Muestra:
 * - Filtro (Hoy / Total histórico)
 * - Conteo de órdenes por estatus: EN_ESPERA / EN_PROCESO / FINALIZADO
 * - Barras dibujadas a mano
 */
public class DashboardView extends JFrame {

    // Paleta bonita consistente con tu app
    private static final Color BG_COLOR        = new Color(249, 250, 251); // gris clarito
    private static final Color CARD_BG         = Color.WHITE;
    private static final Color BORDER_COLOR    = new Color(229, 231, 235); // gris borde
    private static final Color TEXT_DARK       = new Color(15, 23, 42);    // gris casi negro
    private static final Color TEXT_MUTED      = new Color(100, 116, 139); // gris medio

    private static final Color COLOR_ESPERA    = new Color(37, 99, 235);   // azul
    private static final Color COLOR_PROCESO   = new Color(99, 102, 241);  // índigo
    private static final Color COLOR_FINAL     = new Color(34, 197, 94);   // verde

    private final DashboardController dashboardController = new DashboardController();
    private final MenuPrincipalController menuPrincipalController; // para info de sesión

    // UI
    private JComboBox<String> comboFiltro;
    private JLabel lblResumen;
    private GraficaPanel graficaPanel;

    // datos actuales
    private DashboardController.ConteoEstatus conteoActual;

    // ======= Constructor =======
    public DashboardView(MenuPrincipalController menuPrincipalController) {
        this.menuPrincipalController = menuPrincipalController;

        setTitle("Dashboard - Estatus de Órdenes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        setContentPane(crearContenido());
        recargarDatos(false); // arranca mostrando "Hoy"
    }

    // ===========================
    // Construcción de la interfaz
    // ===========================
    private JPanel crearContenido() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(new EmptyBorder(16,16,16,16));

        // TOP BAR: título + sesión + filtro
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Texto lado izq (titulo y usuario actual)
        JPanel tituloYUsuario = new JPanel();
        tituloYUsuario.setOpaque(false);
        tituloYUsuario.setLayout(new BoxLayout(tituloYUsuario, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Dashboard de Órdenes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(TEXT_DARK);

        JLabel lblSesion = new JLabel(menuPrincipalController.getTextoSesionHeader());
        lblSesion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSesion.setForeground(TEXT_MUTED);

        tituloYUsuario.add(lblTitulo);
        tituloYUsuario.add(lblSesion);

        // Filtro lado der
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filtroPanel.setOpaque(false);

        comboFiltro = new JComboBox<>(new String[]{"Hoy", "Total histórico"});
        comboFiltro.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton btnAplicar = new JButton("Aplicar");
        btnAplicar.setFocusPainted(false);
        btnAplicar.setBackground(new Color(37,99,235));
        btnAplicar.setForeground(Color.WHITE);
        btnAplicar.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        btnAplicar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAplicar.addActionListener(e -> {
            boolean total = comboFiltro.getSelectedIndex() == 1; // 0=Hoy, 1=Total
            recargarDatos(total);
        });

        filtroPanel.add(new JLabel("Ver:"));
        filtroPanel.add(comboFiltro);
        filtroPanel.add(btnAplicar);

        header.add(tituloYUsuario, BorderLayout.WEST);
        header.add(filtroPanel, BorderLayout.EAST);

        // CARD CENTRAL
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(16,16,16,16)
        ));

        // texto resumen arriba
        lblResumen = new JLabel("Cargando...");
        lblResumen.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblResumen.setForeground(TEXT_DARK);

        // panel de barras custom
        graficaPanel = new GraficaPanel();
        graficaPanel.setPreferredSize(new Dimension(600,250));
        graficaPanel.setBackground(Color.WHITE);

        // Pegamos dentro del card
        card.add(lblResumen, BorderLayout.NORTH);
        card.add(graficaPanel, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);

        return root;
    }

    // ===========================
    // Lógica de refrescar datos
    // ===========================
    private void recargarDatos(boolean totalHistorico) {
        // pedirle al controller los datos
        this.conteoActual = dashboardController.getConteos(totalHistorico);

        // actualizar texto resumen
        int suma = conteoActual.espera + conteoActual.proceso + conteoActual.finalizado;
        String modo = totalHistorico ? "Total histórico" : "Hoy";
        lblResumen.setText(
            "<html>" +
            "<b>"+modo+"</b><br>" +
            "En espera: " + conteoActual.espera + " &nbsp;&nbsp; " +
            "En proceso: " + conteoActual.proceso + " &nbsp;&nbsp; " +
            "Finalizado: " + conteoActual.finalizado + "<br>" +
            "Total de ordenes: " + suma +
            "</html>"
        );

        // pasar datos al panel de barras y repintar
        graficaPanel.setValores(
                conteoActual.espera,
                conteoActual.proceso,
                conteoActual.finalizado
        );

        graficaPanel.repaint();
    }

    // ===========================
    // Panel interno para dibujar barras
    // ===========================
    private class GraficaPanel extends JPanel {

        private int vEspera;
        private int vProceso;
        private int vFinal;

        public void setValores(int espera, int proceso, int fin) {
            this.vEspera = espera;
            this.vProceso = proceso;
            this.vFinal   = fin;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);

            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // fondo blanco ya está

            // margen interior
            int marginX = 60;
            int marginTop = 30;
            int marginBottom = 50;

            // calcular altura disponible
            int chartH = h - marginTop - marginBottom;
            int chartW = w - marginX*2;

            // valores max
            int maxVal = Math.max(vEspera, Math.max(vProceso, vFinal));
            if (maxVal < 1) maxVal = 1; // evitar /0

            // ancho de cada barra
            int barCount = 3;
            int gap = 30;
            int totalGap = gap * (barCount - 1);
            int barW = (chartW - totalGap) / barCount;

            int xBase = marginX;

            // dibujar barras
            dibujaBarra(g, "EN ESPERA",   vEspera,   COLOR_ESPERA,  xBase,               barW, chartH, marginTop, maxVal);
            dibujaBarra(g, "EN PROCESO",  vProceso,  COLOR_PROCESO, xBase + barW + gap,  barW, chartH, marginTop, maxVal);
            dibujaBarra(g, "FINALIZADO",  vFinal,    COLOR_FINAL,   xBase + (barW+gap)*2,barW, chartH, marginTop, maxVal);
        }

        private void dibujaBarra(
                Graphics2D g,
                String label,
                int valor,
                Color colorBarra,
                int x,
                int barW,
                int chartH,
                int top,
                int maxVal
        ) {
            // altura proporcional
            double ratio = (double) valor / (double) maxVal;
            int barH = (int) Math.round(chartH * ratio);

            int y = top + (chartH - barH);

            // rectángulo barra
            g.setColor(colorBarra);
            g.fillRoundRect(x, y, barW, barH, 12, 12);

            // borde leve
            g.setColor(colorBarra.darker());
            g.drawRoundRect(x, y, barW, barH, 12, 12);

            // valor encima de la barra
            g.setColor(TEXT_DARK);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            String valTxt = String.valueOf(valor);
            int strW = g.getFontMetrics().stringWidth(valTxt);
            g.drawString(valTxt, x + (barW - strW)/2, y - 8);

            // label abajo
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(TEXT_MUTED);
            int lblW = g.getFontMetrics().stringWidth(label);
            g.drawString(label, x + (barW - lblW)/2, top + chartH + 20);
        }
    }

    // opcional: para que desde afuera puedas forzar refresco
    public void refrescarHoy() {
        comboFiltro.setSelectedIndex(0);
        recargarDatos(false);
    }
}

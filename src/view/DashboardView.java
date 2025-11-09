package view;

import controller.DashboardController;
import controller.MenuPrincipalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class DashboardView extends JFrame {

    private static final Color BG_COLOR        = new Color(248, 250, 252);
    private static final Color CARD_BG         = Color.WHITE;
    private static final Color BORDER_COLOR    = new Color(226, 232, 240);
    private static final Color TEXT_DARK       = new Color(15, 23, 42);
    private static final Color TEXT_MUTED      = new Color(100, 116, 139);
    
    private static final Color PRIMARY_BLUE    = new Color(59, 130, 246);
    private static final Color COLOR_ESPERA    = new Color(251, 146, 60);   // naranja
    private static final Color COLOR_PROCESO   = new Color(168, 85, 247);   // púrpura
    private static final Color COLOR_FINAL     = new Color(34, 197, 94);    // verde

    private final DashboardController dashboardController = new DashboardController();
    private final MenuPrincipalController menuPrincipalController;
    
    private JComboBox<String> comboFiltro;
    private JLabel lblResumen;
    private GraficaPanel graficaPanel;
    private DashboardController.ConteoEstatus conteoActual;

    
    private JDateChooser dcFecha;   // calendario para filtrar por día



    public DashboardView(MenuPrincipalController menuPrincipalController) {
        this.menuPrincipalController = menuPrincipalController;

        setTitle("Dashboard - Estatus de Órdenes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1050, 600); // Tamaño más grande para mejor visualización
        setLocationRelativeTo(null);

        setContentPane(crearContenido());
        recargarDatos(false);
    }

    private JPanel crearContenido() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(new EmptyBorder(20, 20, 20, 20)); // Más padding

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel tituloYUsuario = new JPanel();
        tituloYUsuario.setOpaque(false);
        tituloYUsuario.setLayout(new BoxLayout(tituloYUsuario, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("📊 Dashboard de Órdenes");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setForeground(TEXT_DARK);

        JLabel lblSesion = new JLabel(menuPrincipalController.getTextoSesionHeader());
        lblSesion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSesion.setForeground(TEXT_MUTED);

        tituloYUsuario.add(lblTitulo);
        tituloYUsuario.add(Box.createVerticalStrut(4));
        tituloYUsuario.add(lblSesion);

        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filtroPanel.setOpaque(false);

        JLabel lblVer = new JLabel("Ver:");
        lblVer.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblVer.setForeground(TEXT_DARK);

        comboFiltro = new JComboBox<>(new String[]{"Hoy", "Total histórico"});
        comboFiltro.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboFiltro.setPreferredSize(new Dimension(140, 32));
        comboFiltro.setBackground(Color.WHITE);
        comboFiltro.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JButton btnAplicar = crearBotonModerno("Aplicar", PRIMARY_BLUE);
        btnAplicar.addActionListener(e -> {
            boolean total = comboFiltro.getSelectedIndex() == 1;
            recargarDatos(total);
        });

        dcFecha = new JDateChooser();
        dcFecha.setDateFormatString("yyyy-MM-dd");
        dcFecha.setPreferredSize(new Dimension(140, 32));
        dcFecha.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel lblFecha = new JLabel("    Fecha específica:");
        lblFecha.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblFecha.setForeground(TEXT_DARK);

        JButton btnPorFecha = crearBotonModerno("Por fecha", new Color(16, 185, 129));
        btnPorFecha.addActionListener(e -> aplicarFiltroPorFecha());

        filtroPanel.add(lblVer);
        filtroPanel.add(comboFiltro);
        filtroPanel.add(btnAplicar);
        filtroPanel.add(lblFecha);
        filtroPanel.add(dcFecha);
        filtroPanel.add(btnPorFecha);

        header.add(tituloYUsuario, BorderLayout.WEST);
        header.add(filtroPanel, BorderLayout.EAST);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(24, 24, 24, 24)
        ));

        lblResumen = new JLabel("Cargando...");
        lblResumen.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblResumen.setForeground(TEXT_DARK);
        lblResumen.setBorder(new EmptyBorder(0, 0, 16, 0));

        graficaPanel = new GraficaPanel();
        graficaPanel.setPreferredSize(new Dimension(800, 350));
        graficaPanel.setBackground(Color.WHITE);

        card.add(lblResumen, BorderLayout.NORTH);
        card.add(graficaPanel, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);

        return root;
    }

    private JButton crearBotonModerno(String texto, Color colorBase) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBackground(colorBase);
        btn.setForeground(Color.black);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorBase.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorBase);
            }
        });
        
        return btn;
    }


    private void recargarDatos(boolean totalHistorico) {
        this.conteoActual = dashboardController.getConteos(totalHistorico);
        int suma = conteoActual.espera + conteoActual.proceso + conteoActual.finalizado;

  
        String modo = totalHistorico ? "Total histórico" : "Hoy";
        
        lblResumen.setText(
            "<html>" +
            "<div style='font-size: 14px;'>" +
            "<span style='font-weight: bold; font-size: 16px; color: #0f172a;'>"+modo+"</span><br><br>" +
            "<span style='color: #f97316;'>⏳ En espera: <b>" + conteoActual.espera + "</b></span> &nbsp;&nbsp;&nbsp; " +
            "<span style='color: #a855f7;'>🔄 En proceso: <b>" + conteoActual.proceso + "</b></span> &nbsp;&nbsp;&nbsp; " +
            "<span style='color: #22c55e;'>✅ Finalizado: <b>" + conteoActual.finalizado + "</b></span><br><br>" +
            "<span style='color: #64748b;'>Total de órdenes: <b>" + suma + "</b></span>" +
            "</div>" +
            "</html>"
        );

        graficaPanel.setValores(
                conteoActual.espera,
                conteoActual.proceso,
                conteoActual.finalizado
        );

        graficaPanel.repaint();
    }
    

    private void aplicarFiltroPorFecha() {
        Date utilDate = dcFecha.getDate();
        if (utilDate == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una fecha en el calendario.",
                    "Sin fecha",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        LocalDate fecha = utilDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        recargarDatosPorFecha(fecha);
    }

    private void recargarDatosPorFecha(LocalDate fecha) {
        try {
            this.conteoActual = dashboardController.getConteosPorFecha(fecha);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error al obtener datos de la fecha " + fecha + ":\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
            return;
        }

        int suma = conteoActual.espera + conteoActual.proceso + conteoActual.finalizado;

        if (suma == 0) {
            lblResumen.setText(
                "<html><center><span style='color:#64748b;font-size:14px;'>"
                + "📭 No se encontraron órdenes para el día " + fecha + "."
                + "</span></center></html>"
            );
            graficaPanel.setValores(0, 0, 0);
            graficaPanel.repaint();
            return;
        }

        String modo = "Día: " + fecha.toString();

        lblResumen.setText(
            "<html>" +
            "<b>" + modo + "</b><br>" +
            "En espera: " + conteoActual.espera + " &nbsp;&nbsp; " +
            "En proceso: " + conteoActual.proceso + " &nbsp;&nbsp; " +
            "Finalizado: " + conteoActual.finalizado + "<br>" +
            "Total de órdenes: " + suma +
            "</html>"
        );

        graficaPanel.setValores(
                conteoActual.espera,
                conteoActual.proceso,
                conteoActual.finalizado
        );
        graficaPanel.repaint();
    }


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

            int marginX = 80;
            int marginTop = 40;
            int marginBottom = 60;

            int chartH = h - marginTop - marginBottom;
            int chartW = w - marginX*2;

            int maxVal = Math.max(vEspera, Math.max(vProceso, vFinal));
            if (maxVal < 1) maxVal = 1;

            int barCount = 3;
            int gap = 50; // Mayor separación entre barras
            int totalGap = gap * (barCount - 1);
            int barW = (chartW - totalGap) / barCount;

            int xBase = marginX;

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
            double ratio = (double) valor / (double) maxVal;
            int barH = (int) Math.round(chartH * ratio);

            int y = top + (chartH - barH);

            g.setColor(new Color(0, 0, 0, 20));
            g.fillRoundRect(x + 3, y + 3, barW, barH, 16, 16);

            g.setColor(colorBarra);
            g.fillRoundRect(x, y, barW, barH, 16, 16);

            g.setColor(colorBarra.darker());
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x, y, barW, barH, 16, 16);

            g.setColor(TEXT_DARK);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            String valTxt = String.valueOf(valor);
            int strW = g.getFontMetrics().stringWidth(valTxt);
            g.drawString(valTxt, x + (barW - strW)/2, y - 12);

            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.setColor(TEXT_MUTED);
            int lblW = g.getFontMetrics().stringWidth(label);
            g.drawString(label, x + (barW - lblW)/2, top + chartH + 30);
        }
    }

    public void refrescarHoy() {
        comboFiltro.setSelectedIndex(0);
        recargarDatos(false);
    }
}

package app;

import view.LoginView;

/**
 * App
 *
 * Punto de entrada de todo el sistema.
 * Inicia la ventana de login.
 */
public class app {
    public static void main(String[] args) {
        //  Asegura que la interfaz use el look and feel del sistema operativo
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar LookAndFeel: " + e.getMessage());
        }

        //  Arrancar la vista principal (Login)
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}

package controller;

import model.Usuario;

/**
 * Controlador para la pantalla principal (men�).
 * - Expone datos de sesi�n
 * - Valida permisos
 * - Hace logout
 * 
 * La vista lo usa para tomar decisiones, pero sin tocar l�gica interna.
 */
public class MenuPrincipalController {

    private final AuthController authController;

    public MenuPrincipalController(AuthController authController) {
        this.authController = authController;
    }

    /**
     * Regresa el usuario actualmente en sesi�n.
     * Puede ser null si ya no hay sesi�n v�lida.
     */
    public Usuario getUsuarioActual() {
        return AuthController.getUsuarioActual();
    }

    /**
     * Construye el texto bonito que se muestra en el header:
     * "- Juan P�rez � ADMIN"
     */
    public String getTextoSesionHeader() {
        Usuario u = getUsuarioActual();
        if (u == null) {
            return "Sin sesi�n activa";
        }
        return "- " + u.getNombreCompleto() + " � " + u.getRol();
    }

    /**
     * �El usuario logeado tiene rol ADMIN?
     * Si no hay usuario logeado, tambi�n da false.
     */
    public boolean usuarioEsAdmin() {
        Usuario u = getUsuarioActual();
        if (u == null) return false;
        return "ADMIN".equalsIgnoreCase(u.getRol());
    }

    /**
     * Intenta validar acceso al m�dulo "Servicios".
     * Devuelve null si todo ok.
     * Devuelve mensaje de error si NO tiene permiso o no hay sesi�n.
     */
    public String validarAccesoServicios() {
        Usuario actual = getUsuarioActual();

        if (actual == null) {
            return "No hay usuario en sesi�n. Inicia sesi�n nuevamente.";
        }

        if (!usuarioEsAdmin()) {
            return "Acceso denegado. Solo los administradores pueden gestionar �rdenes de servicio.";
        }

        return null; // significa permitido
    }

    /**
     * Cerrar sesi�n a nivel de aplicaci�n.
     */
    public void cerrarSesion() {
        authController.logout();
    }

    /**
     * Exponemos el AuthController porque otras vistas lo necesitan
     * (por ejemplo para constructor de CatalogosView, ClientesVehiculosView, etc.)
     */
    public AuthController getAuthController() {
        return authController;
    }
}

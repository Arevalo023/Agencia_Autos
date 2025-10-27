package controller;

import model.Usuario;

/**
 * Controlador para la pantalla principal (menú).
 * - Expone datos de sesión
 * - Valida permisos
 * - Hace logout
 * 
 * La vista lo usa para tomar decisiones, pero sin tocar lógica interna.
 */
public class MenuPrincipalController {

    private final AuthController authController;

    public MenuPrincipalController(AuthController authController) {
        this.authController = authController;
    }

    /**
     * Regresa el usuario actualmente en sesión.
     * Puede ser null si ya no hay sesión válida.
     */
    public Usuario getUsuarioActual() {
        return AuthController.getUsuarioActual();
    }

    /**
     * Construye el texto bonito que se muestra en el header:
     * "- Juan Pérez • ADMIN"
     */
    public String getTextoSesionHeader() {
        Usuario u = getUsuarioActual();
        if (u == null) {
            return "Sin sesión activa";
        }
        return "- " + u.getNombreCompleto() + " • " + u.getRol();
    }

    /**
     * ¿El usuario logeado tiene rol ADMIN?
     * Si no hay usuario logeado, también da false.
     */
    public boolean usuarioEsAdmin() {
        Usuario u = getUsuarioActual();
        if (u == null) return false;
        return "ADMIN".equalsIgnoreCase(u.getRol());
    }

    /**
     * Intenta validar acceso al módulo "Servicios".
     * Devuelve null si todo ok.
     * Devuelve mensaje de error si NO tiene permiso o no hay sesión.
     */
    public String validarAccesoServicios() {
        Usuario actual = getUsuarioActual();

        if (actual == null) {
            return "No hay usuario en sesión. Inicia sesión nuevamente.";
        }

        if (!usuarioEsAdmin()) {
            return "Acceso denegado. Solo los administradores pueden gestionar órdenes de servicio.";
        }

        return null; // significa permitido
    }

    /**
     * Cerrar sesión a nivel de aplicación.
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

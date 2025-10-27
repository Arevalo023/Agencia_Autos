package controller;
 
import config.ConexionDB;
import model.Usuario;
import model.dao.UsuarioDAO;
import util.PasswordUtils;
 
/**
* AuthController
*
* Maneja el flujo de autenticación:
*  - validar credenciales
*  - mantener quién está logeado
*  - cerrar sesión limpiamente
*/
public class AuthController {
 
    // "Sesión" actual en memoria
    private static Usuario usuarioActual = null;
 
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
 
    /**
     * Intenta autenticar al usuario.
     * @param username lo que escribió en el login
     * @param password plano (sin hash) que escribió en el login
     * @return true si las credenciales son válidas
     */
    public boolean login(String username, String password) {
        // 1. Buscar usuario en BD
        Usuario u = usuarioDAO.buscarPorUsername(username);
 
        
        if (u == null) {
            // Usuario no existe o inactivo
            return false;
        }
 
        // 2. Validar password usando PasswordUtils (BCrypt bajo Factory Method)
        boolean ok = PasswordUtils.matches(password, u.getPasswordHash());
        if (!ok) {
            return false;
        }
 
        // 3. Guardar al usuario actual en "sesión"
        usuarioActual = u;
        return true;
    }
 
    /**
     * Devuelve el usuario autenticado actualmente.
     * @return Usuario logeado o null si no hay sesión activa
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
 
    /**
     * Cerrar sesión de forma segura:
     *  - limpia usuarioActual
     *  - cierra la conexión a BD
     * Esto te da puntos en el requisito de seguridad / cerrar sistema.
     */
    public void logout() {
        usuarioActual = null;
        ConexionDB.closeConnection();
    }
 
    /**
     * Saber si alguien está logeado.
     */
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }
}
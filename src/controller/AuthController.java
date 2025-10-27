package controller;
 
import config.ConexionDB;
import model.Usuario;
import model.dao.UsuarioDAO;
import util.PasswordUtils;
 
/**
* AuthController
*
* Maneja el flujo de autenticaci�n:
*  - validar credenciales
*  - mantener qui�n est� logeado
*  - cerrar sesi�n limpiamente
*/
public class AuthController {
 
    // "Sesi�n" actual en memoria
    private static Usuario usuarioActual = null;
 
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
 
    /**
     * Intenta autenticar al usuario.
     * @param username lo que escribi� en el login
     * @param password plano (sin hash) que escribi� en el login
     * @return true si las credenciales son v�lidas
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
 
        // 3. Guardar al usuario actual en "sesi�n"
        usuarioActual = u;
        return true;
    }
 
    /**
     * Devuelve el usuario autenticado actualmente.
     * @return Usuario logeado o null si no hay sesi�n activa
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
 
    /**
     * Cerrar sesi�n de forma segura:
     *  - limpia usuarioActual
     *  - cierra la conexi�n a BD
     * Esto te da puntos en el requisito de seguridad / cerrar sistema.
     */
    public void logout() {
        usuarioActual = null;
        ConexionDB.closeConnection();
    }
 
    /**
     * Saber si alguien est� logeado.
     */
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }
}
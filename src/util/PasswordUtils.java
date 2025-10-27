package util;
import org.mindrot.jbcrypt.BCrypt;
 
/**
* PasswordUtils
*
* - Centraliza la verificaci�n de contrase�as.
* - Aplica la idea de Factory Method:
*   En lugar de validar contrase�as "a mano", pedimos un validador
*   a trav�s de un m�todo f�brica. Hoy usamos BCrypt, pero si ma�ana
*   cambiamos de algoritmo, s�lo cambiamos la "f�brica", no el resto
*   del sistema.
*/
public class PasswordUtils {
 
    /**
     * Interfaz gen�rica para validadores de contrase�a.
     * Si en el futuro cambias de BCrypt a otro algoritmo, s�lo creas
     * otra clase que implemente esta interfaz, sin tocar el controller.
     */
    public interface PasswordValidator {
        boolean matches(String rawPassword, String hashedPassword);
    }
 
    /**
     * Implementaci�n concreta usando BCrypt.
     */
    public static class BCryptPasswordValidator implements PasswordValidator {
        @Override
        public boolean matches(String rawPassword, String hashedPassword) {
            if (rawPassword == null || hashedPassword == null) {
                return false;
            }
            return BCrypt.checkpw(rawPassword, hashedPassword);
        }
    }
 
    /**
     * FACTORY METHOD:
     * Devuelve el validador de contrase�as que el sistema va a usar.
     * Hoy: BCrypt.
     *
     * Ma�ana podr�amos regresar, por ejemplo, un Argon2PasswordValidator,
     * sin tocar el resto del c�digo (AuthController no cambia).
     */
    public static PasswordValidator getPasswordValidator() {
        return new BCryptPasswordValidator();
    }
 
    /**
     * Helper directo si no quieres manejar la interfaz afuera.
     * �til para c�digo corto tipo: PasswordUtils.matches(plain, hashFromDB)
     */
    public static boolean matches(String rawPassword, String hashedPassword) {
        return getPasswordValidator().matches(rawPassword, hashedPassword);
    }
}
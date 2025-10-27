package util;
import org.mindrot.jbcrypt.BCrypt;
 
/**
* PasswordUtils
*
* - Centraliza la verificación de contraseñas.
* - Aplica la idea de Factory Method:
*   En lugar de validar contraseñas "a mano", pedimos un validador
*   a través de un método fábrica. Hoy usamos BCrypt, pero si mañana
*   cambiamos de algoritmo, sólo cambiamos la "fábrica", no el resto
*   del sistema.
*/
public class PasswordUtils {
 
    /**
     * Interfaz genérica para validadores de contraseña.
     * Si en el futuro cambias de BCrypt a otro algoritmo, sólo creas
     * otra clase que implemente esta interfaz, sin tocar el controller.
     */
    public interface PasswordValidator {
        boolean matches(String rawPassword, String hashedPassword);
    }
 
    /**
     * Implementación concreta usando BCrypt.
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
     * Devuelve el validador de contraseñas que el sistema va a usar.
     * Hoy: BCrypt.
     *
     * Mañana podríamos regresar, por ejemplo, un Argon2PasswordValidator,
     * sin tocar el resto del código (AuthController no cambia).
     */
    public static PasswordValidator getPasswordValidator() {
        return new BCryptPasswordValidator();
    }
 
    /**
     * Helper directo si no quieres manejar la interfaz afuera.
     * Útil para código corto tipo: PasswordUtils.matches(plain, hashFromDB)
     */
    public static boolean matches(String rawPassword, String hashedPassword) {
        return getPasswordValidator().matches(rawPassword, hashedPassword);
    }
}
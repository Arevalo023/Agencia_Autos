package controller;
 
public class LoginScreenController {
 
    private final AuthController authController;
 
    // DTO simple para regresar resultado del intento de login

    public static class ResultadoLogin {

        public final boolean exito;

        public final String mensaje;

        public final String nombreUsuario;
 
        public ResultadoLogin(boolean exito, String mensaje, String nombreUsuario) {

            this.exito = exito;

            this.mensaje = mensaje;

            this.nombreUsuario = nombreUsuario;

        }

    }
 
    public LoginScreenController(AuthController authController) {

        this.authController = authController;

    }
 
    /**

     * Intenta iniciar sesión con usuario y password.

     * Devuelve un objeto con:

     *  - exito = true si inició sesión

     *  - mensaje = texto para mostrar en JOptionPane

     *  - nombreUsuario = nombre completo (solo si exito == true)

     */

    public ResultadoLogin intentarLogin(String usuario, String password) {
 
        boolean ok = authController.login(usuario, password);
 
        if (ok) {

            String nombre = AuthController.getUsuarioActual().getNombreCompleto();

            return new ResultadoLogin(

                    true,

                    "Bienvenido/a " + nombre,

                    nombre

            );

        } else {

            return new ResultadoLogin(

                    false,

                    "Usuario o contraseña incorrectos.",

                    null

            );

        }

    }
 
    /**

     * Exponemos el AuthController real por si la vista necesita

     * pasarlo a otras pantallas (por ejemplo, MenuPrincipalView).

     */

    public AuthController getAuthController() {

        return authController;

    }

}

 
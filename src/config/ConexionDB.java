package config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
* ConexionDB
*
* Esta clase maneja la conexi�n a la base de datos MySQL.
* Aplica el Patr�n Singleton:
*  - Solo existe UNA instancia de la conexi�n viva durante la sesi�n.
*  - Todas las capas (DAO) piden la conexi�n desde aqu�.
*
* Tambi�n nos ayuda con:
*  - Cerrar la conexi�n al cerrar sesi�n / salir del sistema (requisito de seguridad).
*/
public class ConexionDB {
 
    // === Ajusta estos datos a tu entorno ===
   // private static final String URL      = "jdbc:mysql://localhost:3306/agencia_autos";
	private static final String URL = 
		    "jdbc:mysql://localhost:3306/agencia_autos"
		  + "?useSSL=false"
		  + "&allowPublicKeyRetrieval=true"
		  + "&serverTimezone=UTC";
 
	private static final String USER     = "root";
    private static final String PASSWORD = "!Clubamerica18itzel";
 
    // Instancia �nica (Singleton)
    private static Connection conexionUnica = null;
 
    // Constructor privado: evita que alguien haga new ConexionDB() desde fuera
    private ConexionDB() { }
 
    /**
     * getConnection()
     * Devuelve la conexi�n activa a la BD.
     * Si no existe o est� cerrada, intenta crearla.
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (conexionUnica == null || conexionUnica.isClosed()) {
 
                // 1) Cargar el driver del conector MySQL.
                //    (Necesitas tener el .jar del conector mysql a�adido al Build Path del proyecto)
                Class.forName("com.mysql.cj.jdbc.Driver");
 
                // 2) Crear la conexi�n real f�sica
                conexionUnica = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            // Esto pasa si el .jar del conector MySQL no est� agregado al proyecto
            throw new SQLException("No se encontr� el Driver JDBC de MySQL. Aseg�rate de agregar el .jar al Build Path.", e);
        }
 
        return conexionUnica;
    }
 
    /**
     * closeConnection()
     * Cierra la conexi�n activa a la BD manualmente.
     * Se debe llamar cuando el usuario CIERRE SESI�N o cuando el sistema sale.
     *
     * Este m�todo es importante para:
     *  - Punto 3 de la r�brica: "cerrar base de datos y destruir variables globales".
     */
    public static void closeConnection() {
        if (conexionUnica != null) {
            try {
                if (!conexionUnica.isClosed()) {
                    conexionUnica.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexi�n: " + e.getMessage());
            } finally {
                conexionUnica = null;
            }
        }
    }
 
    /**
     * isConnected()
     * �til para debug o para mostrar en la vista principal si est�s conectado.
     */
    public static boolean isConnected() {
        try {
            return (conexionUnica != null && !conexionUnica.isClosed());
        } catch (SQLException e) {
            return false;
        }
    }
}
package config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
* ConexionDB
*
* Esta clase maneja la conexión a la base de datos MySQL.
* Aplica el Patrón Singleton:
*  - Solo existe UNA instancia de la conexión viva durante la sesión.
*  - Todas las capas (DAO) piden la conexión desde aquí.
*
* También nos ayuda con:
*  - Cerrar la conexión al cerrar sesión / salir del sistema (requisito de seguridad).
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
 
    // Instancia única (Singleton)
    private static Connection conexionUnica = null;
 
    // Constructor privado: evita que alguien haga new ConexionDB() desde fuera
    private ConexionDB() { }
 
    /**
     * getConnection()
     * Devuelve la conexión activa a la BD.
     * Si no existe o está cerrada, intenta crearla.
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (conexionUnica == null || conexionUnica.isClosed()) {
 
                // 1) Cargar el driver del conector MySQL.
                //    (Necesitas tener el .jar del conector mysql añadido al Build Path del proyecto)
                Class.forName("com.mysql.cj.jdbc.Driver");
 
                // 2) Crear la conexión real física
                conexionUnica = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            // Esto pasa si el .jar del conector MySQL no está agregado al proyecto
            throw new SQLException("No se encontró el Driver JDBC de MySQL. Asegúrate de agregar el .jar al Build Path.", e);
        }
 
        return conexionUnica;
    }
 
    /**
     * closeConnection()
     * Cierra la conexión activa a la BD manualmente.
     * Se debe llamar cuando el usuario CIERRE SESIÓN o cuando el sistema sale.
     *
     * Este método es importante para:
     *  - Punto 3 de la rúbrica: "cerrar base de datos y destruir variables globales".
     */
    public static void closeConnection() {
        if (conexionUnica != null) {
            try {
                if (!conexionUnica.isClosed()) {
                    conexionUnica.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                conexionUnica = null;
            }
        }
    }
 
    /**
     * isConnected()
     * Útil para debug o para mostrar en la vista principal si estás conectado.
     */
    public static boolean isConnected() {
        try {
            return (conexionUnica != null && !conexionUnica.isClosed());
        } catch (SQLException e) {
            return false;
        }
    }
}
package model.dao;
 
import config.ConexionDB;
import model.Usuario;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
 
/**
* UsuarioDAO
*
* Patrón DAO:
* - Aísla TODA la lógica SQL para la entidad Usuario.
* - La vista/controlador NO hablan SQL directo.
*/
public class UsuarioDAO {
 
    /**
     * Busca un usuario por su username (solo si está activo = 1).
     * @param username nombre de usuario que escribió en el login
     * @return objeto Usuario lleno, o null si no existe / inactivo
     */
    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT id_usuario, username, password_hash, nombre_completo, " +
                     "email, rol, activo, creado_en " +
                     "FROM usuarios " +
                     "WHERE username = ? AND activo = 1";
 
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, username);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    u.setEmail(rs.getString("email"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
 
                    // rs.getTimestamp(...) -> LocalDateTime
                    java.sql.Timestamp ts = rs.getTimestamp("creado_en");
                    if (ts != null) {
                        u.setCreadoEn(ts.toLocalDateTime());
                    } else {
                        u.setCreadoEn((LocalDateTime) null);
                    }
 
                    return u;
                }
            }
 
        } catch (SQLException e) {
            System.err.println("Error en buscarPorUsername: " + e.getMessage());
        }
 
        return null;
    }
}
package model.dao;
 
import config.ConexionDB;
import model.TipoServicio;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class TipoServicioDAO {
 
    public List<TipoServicio> listar() {
        List<TipoServicio> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_servicio, nombre, descripcion, activo FROM tipos_servicio ORDER BY id_tipo_servicio";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                TipoServicio t = new TipoServicio();
                t.setIdTipoServicio(rs.getInt("id_tipo_servicio"));
                t.setNombre(rs.getString("nombre"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setActivo(rs.getBoolean("activo"));
                lista.add(t);
            }
 
        } catch (SQLException e) {
            System.err.println("Error listar(): " + e.getMessage());
        }
        return lista;
    }
 
    public boolean insertar(String nombre, String descripcion) {
        String sql = "INSERT INTO tipos_servicio (nombre, descripcion, activo) VALUES (?, ?, 1)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar(): " + e.getMessage());
            return false;
        }
    }
 
    public boolean actualizar(int id, String nombre, String descripcion) {
        String sql = "UPDATE tipos_servicio SET nombre = ?, descripcion = ? WHERE id_tipo_servicio = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar(): " + e.getMessage());
            return false;
        }
    }
 
    public boolean desactivar(int id) {
        String sql = "UPDATE tipos_servicio SET activo = 0 WHERE id_tipo_servicio = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error desactivar(): " + e.getMessage());
            return false;
        }
    }
 
    public boolean reactivar(int id) {
        String sql = "UPDATE tipos_servicio SET activo = 1 WHERE id_tipo_servicio = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error reactivar(): " + e.getMessage());
            return false;
        }
    }
}
package model.dao;
 
import config.ConexionDB;
import model.Cliente;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class ClienteDAO {
 
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, apellidos, telefono, email, activo FROM clientes ORDER BY id_cliente";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setApellidos(rs.getString("apellidos"));
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setActivo(rs.getBoolean("activo"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error listar(): " + e.getMessage());
        }
        return lista;
    }
 
    public boolean insertar(String nombre, String apellidos, String telefono, String email) {
        String sql = "INSERT INTO clientes (nombre, apellidos, telefono, email, activo) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellidos);
            ps.setString(3, telefono);
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar(): " + e.getMessage());
            return false;
        }
    }
 
    public boolean actualizar(int id, String telefono, String email) {
        String sql = "UPDATE clientes SET telefono = ?, email = ? WHERE id_cliente = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, telefono);
            ps.setString(2, email);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar(): " + e.getMessage());
            return false;
        }
    }
 
    public boolean desactivar(int id) {
        String sql = "UPDATE clientes SET activo = 0 WHERE id_cliente = ?";
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
        String sql = "UPDATE clientes SET activo = 1 WHERE id_cliente = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error reactivar(): " + e.getMessage());
            return false;
        }
    }
    public List<Cliente> buscarPorNombre(String filtro) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, apellidos, telefono, email, activo " +
                     "FROM clientes " +
                     "WHERE nombre LIKE ? OR apellidos LIKE ?";
 
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            String like = "%" + filtro + "%";
            ps.setString(1, like);
            ps.setString(2, like);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("id_cliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellidos(rs.getString("apellidos"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setEmail(rs.getString("email"));
                    c.setActivo(rs.getBoolean("activo"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscarPorNombre(): " + e.getMessage());
        }
        return lista;
    }
    public java.util.List<Cliente> buscarPorNombreExacto(String nombreBuscado) {
        java.util.List<Cliente> lista = new java.util.ArrayList<>();
 
        String sql = "SELECT id_cliente, nombre, apellidos, telefono, email, activo " +
                     "FROM clientes " +
                     "WHERE TRIM(LOWER(nombre)) = TRIM(LOWER(?))";
 
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, nombreBuscado);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("id_cliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellidos(rs.getString("apellidos"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setEmail(rs.getString("email"));
                    c.setActivo(rs.getBoolean("activo"));
                    lista.add(c);
                }
            }
 
        } catch (SQLException e) {
            System.err.println("Error en buscarPorNombreExacto(): " + e.getMessage());
        }
 
        return lista;
    }
    public void eliminarDefinitivo(int idCliente) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
 
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idCliente);
            ps.executeUpdate();
 
        } catch (SQLException e) {
            System.err.println("Error eliminarDefinitivo() Cliente: " + e.getMessage());
        }
    }
 
 
}
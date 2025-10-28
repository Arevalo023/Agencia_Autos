package model.dao;

import config.ConexionDB;
import model.Tecnico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TecnicoDAO {

    public List<Tecnico> listar() {
        List<Tecnico> lista = new ArrayList<>();
        String sql = "SELECT id_tecnico, no_empleado, nombre, telefono, email, activo FROM tecnicos ORDER BY id_tecnico";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tecnico t = new Tecnico();
                t.setIdTecnico(rs.getInt("id_tecnico"));
                t.setNoEmpleado(rs.getString("no_empleado"));
                t.setNombre(rs.getString("nombre"));
                t.setTelefono(rs.getString("telefono"));
                t.setEmail(rs.getString("email"));
                t.setActivo(rs.getBoolean("activo"));
                lista.add(t);
            }

        } catch (SQLException e) {
            System.err.println("Error listar(): " + e.getMessage());
        }

        return lista;
    }

    public boolean insertar(String noEmpleado, String nombre, String telefono, String email) {
        String sql = "INSERT INTO tecnicos (no_empleado, nombre, telefono, email, activo) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noEmpleado);
            ps.setString(2, nombre);
            ps.setString(3, telefono);
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar(): " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(int id, String telefono, String email) {
        String sql = "UPDATE tecnicos SET telefono = ?, email = ? WHERE id_tecnico = ?";
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
        String sql = "UPDATE tecnicos SET activo = 0 WHERE id_tecnico = ?";
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
        String sql = "UPDATE tecnicos SET activo = 1 WHERE id_tecnico = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error reactivar(): " + e.getMessage());
            return false;
        }
    }
    
    public List<Tecnico> buscarPorTexto(String filtro) {
        List<Tecnico> lista = new ArrayList<>();

        String sql = "SELECT id_tecnico, no_empleado, nombre, telefono, email, activo " +
                     "FROM tecnicos " +
                     "WHERE LOWER(no_empleado) LIKE ? " +
                     "   OR LOWER(nombre) LIKE ? " +
                     "   OR LOWER(telefono) LIKE ? " +
                     "   OR LOWER(email) LIKE ? " +
                     "ORDER BY id_tecnico";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String patron = "%" + filtro.toLowerCase() + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tecnico t = new Tecnico();
                    t.setIdTecnico(rs.getInt("id_tecnico"));
                    t.setNoEmpleado(rs.getString("no_empleado"));
                    t.setNombre(rs.getString("nombre"));
                    t.setTelefono(rs.getString("telefono"));
                    t.setEmail(rs.getString("email"));
                    t.setActivo(rs.getBoolean("activo"));
                    lista.add(t);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error buscarPorTexto() en TecnicoDAO: " + e.getMessage());
        }

        return lista;
    }
    
    public boolean eliminarDefinitivo(int idTecnico) {
        String sql = "DELETE FROM tecnicos WHERE id_tecnico = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTecnico);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarDefinitivo técnico: " + e.getMessage());
            return false;
        }
    }


}

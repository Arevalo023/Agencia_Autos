package model.dao;

import config.ConexionDB;
import model.Refaccion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RefaccionDAO {

    public List<Refaccion> listar() {
        List<Refaccion> lista = new ArrayList<>();
        String sql = "SELECT id_refaccion, clave, descripcion, precio_unitario, activo FROM refacciones ORDER BY id_refaccion";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Refaccion r = new Refaccion();
                r.setIdRefaccion(rs.getInt("id_refaccion"));
                r.setClave(rs.getString("clave"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setPrecioUnitario(rs.getDouble("precio_unitario"));
                r.setActivo(rs.getBoolean("activo"));
                lista.add(r);
            }

        } catch (SQLException e) {
            System.err.println("Error listar(): " + e.getMessage());
        }

        return lista;
    }

    public boolean insertar(String clave, String descripcion, double precio) {
        String sql = "INSERT INTO refacciones (clave, descripcion, precio_unitario, activo) VALUES (?, ?, ?, 1)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clave);
            ps.setString(2, descripcion);
            ps.setDouble(3, precio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar(): " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(int id, String descripcion, double precio) {
        String sql = "UPDATE refacciones SET descripcion = ?, precio_unitario = ? WHERE id_refaccion = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ps.setDouble(2, precio);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar(): " + e.getMessage());
            return false;
        }
    }

    public boolean desactivar(int id) {
        String sql = "UPDATE refacciones SET activo = 0 WHERE id_refaccion = ?";
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
        String sql = "UPDATE refacciones SET activo = 1 WHERE id_refaccion = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error reactivar(): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Buscar refacciones por clave o descripción (parcial, case-insensitive).
     * Ej: filtro = "filtro" devuelve coincidencias en clave o descripcion que contengan "filtro".
     */
    public List<Refaccion> buscarPorTexto(String filtro) {
        List<Refaccion> lista = new ArrayList<>();

        String sql = "SELECT id_refaccion, clave, descripcion, precio_unitario, activo " +
                     "FROM refacciones " +
                     "WHERE LOWER(clave) LIKE ? OR LOWER(descripcion) LIKE ? " +
                     "ORDER BY id_refaccion";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String patron = "%" + filtro.toLowerCase() + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Refaccion r = new Refaccion();
                    r.setIdRefaccion(rs.getInt("id_refaccion"));
                    r.setClave(rs.getString("clave"));
                    r.setDescripcion(rs.getString("descripcion"));
                    r.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    r.setActivo(rs.getBoolean("activo"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error buscarPorTexto(): " + e.getMessage());
        }

        return lista;
    }
    
    public boolean eliminarDefinitivo(int idRefaccion) {
        String sql = "DELETE FROM refacciones WHERE id_refaccion = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRefaccion);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminarDefinitivo refacción: " + e.getMessage());
            return false;
        }
    }
    

}

package model.dao;

import config.ConexionDB;
import model.Marca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MarcaDAO
 *
 * CRUD para el cat�logo de marcas.
 * Esto cubre el requisito de CRUD de cat�logos.
 */
public class MarcaDAO {

    /**
     * Regresa TODAS las marcas (activas e inactivas)
     * para que en la vista puedas mostrar tambi�n cu�les est�n deshabilitadas.
     */
    public List<Marca> listarMarcas() {
        List<Marca> lista = new ArrayList<>();

        String sql = "SELECT id_marca, nombre, activo FROM marcas ORDER BY id_marca ASC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Marca m = new Marca();
                m.setIdMarca(rs.getInt("id_marca"));
                m.setNombre(rs.getString("nombre"));
                m.setActivo(rs.getBoolean("activo"));
                lista.add(m);
            }

        } catch (SQLException e) {
            System.err.println("Error listarMarcas(): " + e.getMessage());
        }

        return lista;
    }

    /**
     * Inserta una nueva marca (por default activa = 1)
     * @return true si se insert� bien
     */
    public boolean insertarMarca(String nombre) {
        String sql = "INSERT INTO marcas (nombre, activo) VALUES (?, 1)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre.trim());
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error insertarMarca(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el nombre de una marca existente.
     */
    public boolean actualizarMarca(int idMarca, String nombre) {
        String sql = "UPDATE marcas SET nombre = ? WHERE id_marca = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre.trim());
            ps.setInt(2, idMarca);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizarMarca(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Desactiva la marca (activo = 0).
     * Esto es mejor que borrarla, porque mantiene consistencia hist�rica.
     */
    public boolean desactivarMarca(int idMarca) {
        String sql = "UPDATE marcas SET activo = 0 WHERE id_marca = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error desactivarMarca(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Reactiva marca (activo = 1).
     * �til si el usuario se arrepiente.
     */
    public boolean reactivarMarca(int idMarca) {
        String sql = "UPDATE marcas SET activo = 1 WHERE id_marca = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error reactivarMarca(): " + e.getMessage());
            return false;
        }
    }
}

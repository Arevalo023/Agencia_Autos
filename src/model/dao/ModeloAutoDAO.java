package model.dao;

import config.ConexionDB;
import model.ModeloAuto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ModeloAutoDAO
 *
 * CRUD para el catálogo de Modelos de Automóvil.
 * Cada modelo pertenece a una Marca.
 */
public class ModeloAutoDAO {

    /**
     * Lista todos los modelos de una marca específica
     */
    public List<ModeloAuto> listarModelosPorMarca(int idMarca) {
        List<ModeloAuto> lista = new ArrayList<>();

        String sql = "SELECT id_modelo, nombre, activo FROM modelos WHERE id_marca = ? ORDER BY id_modelo ASC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ModeloAuto m = new ModeloAuto();
                    m.setIdModelo(rs.getInt("id_modelo"));
                    m.setNombre(rs.getString("nombre"));
                    m.setActivo(rs.getBoolean("activo"));
                    lista.add(m);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error listarModelosPorMarca(): " + e.getMessage());
        }

        return lista;
    }

    /**
     * Inserta un nuevo modelo asociado a una marca
     */
    public boolean insertarModelo(int idMarca, String nombre) {
        String sql = "INSERT INTO modelos (id_marca, nombre, activo) VALUES (?, ?, 1)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            ps.setString(2, nombre.trim());
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error insertarModelo(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el nombre del modelo
     */
    public boolean actualizarModelo(int idModelo, String nombre) {
        String sql = "UPDATE modelos SET nombre = ? WHERE id_modelo = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre.trim());
            ps.setInt(2, idModelo);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizarModelo(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Desactiva un modelo
     */
    public boolean desactivarModelo(int idModelo) {
        String sql = "UPDATE modelos SET activo = 0 WHERE id_modelo = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idModelo);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error desactivarModelo(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Reactiva un modelo
     */
    public boolean reactivarModelo(int idModelo) {
        String sql = "UPDATE modelos SET activo = 1 WHERE id_modelo = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idModelo);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error reactivarModelo(): " + e.getMessage());
            return false;
        }
    }
}

package model.dao;

import config.ConexionDB;
import model.AnioModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnioModeloDAO {

    public List<AnioModelo> listarPorModelo(int idModelo) {
        List<AnioModelo> lista = new ArrayList<>();

        String sql = "SELECT id_anio_modelo, anio, activo FROM anios_modelo WHERE id_modelo = ? ORDER BY anio ASC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idModelo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AnioModelo a = new AnioModelo();
                a.setIdAnioModelo(rs.getInt("id_anio_modelo"));
                a.setAnio(rs.getInt("anio"));
                a.setActivo(rs.getBoolean("activo"));
                lista.add(a);
            }

        } catch (SQLException e) {
            System.err.println("Error listarPorModelo(): " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(int idModelo, int anio) {
        String sql = "INSERT INTO anios_modelo (id_modelo, anio, activo) VALUES (?, ?, 1)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idModelo);
            ps.setInt(2, anio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar(): " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(int idAnio, int anio) {
        String sql = "UPDATE anios_modelo SET anio = ? WHERE id_anio_modelo = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, anio);
            ps.setInt(2, idAnio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar(): " + e.getMessage());
            return false;
        }
    }

    public boolean desactivar(int idAnio) {
        String sql = "UPDATE anios_modelo SET activo = 0 WHERE id_anio_modelo = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error desactivar(): " + e.getMessage());
            return false;
        }
    }

    public boolean reactivar(int idAnio) {
        String sql = "UPDATE anios_modelo SET activo = 1 WHERE id_anio_modelo = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAnio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error reactivar(): " + e.getMessage());
            return false;
        }
    }
}

package model.dao;

import config.ConexionDB;
import model.Vehiculo;
import model.Cliente;
import model.ModeloAuto;
import model.AnioModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    /**
     * Lista todos los vehículos registrados
     * Vamos a armar objetos Cliente, ModeloAuto y AnioModelo
     * solo con lo básico necesario para mostrar en tablas.
     */
    public List<Vehiculo> listar() {
        List<Vehiculo> lista = new ArrayList<>();

        String sql = "SELECT id_vehiculo, id_cliente, id_modelo, id_anio_modelo, placa, vin, color, kilometraje, activo " +
                     "FROM vehiculos " +
                     "ORDER BY id_vehiculo";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vehiculo v = new Vehiculo();

                v.setIdVehiculo(rs.getInt("id_vehiculo"));

                // ---- Cliente ----
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                v.setCliente(c);

                // ---- Modelo ----
                ModeloAuto m = new ModeloAuto();
                m.setIdModelo(rs.getInt("id_modelo"));
                v.setModelo(m);

                // ---- Año ----
                AnioModelo a = new AnioModelo();
                a.setIdAnioModelo(rs.getInt("id_anio_modelo"));
                v.setAnioModelo(a);

                v.setPlaca(rs.getString("placa"));
                v.setVin(rs.getString("vin"));
                v.setColor(rs.getString("color"));
                v.setKilometraje(rs.getInt("kilometraje"));
                v.setActivo(rs.getBoolean("activo"));

                lista.add(v);
            }

        } catch (SQLException e) {
            System.err.println("Error listar(): " + e.getMessage());
        }

        return lista;
    }

    /**
     * Inserta un vehículo nuevo
     */
    public boolean insertar(
            int idCliente,
            int idModelo,
            int idAnioModelo,
            String placa,
            String vin,
            String color,
            int kilometraje
    ) {
        String sql = "INSERT INTO vehiculos " +
                     "(id_cliente, id_modelo, id_anio_modelo, placa, vin, color, kilometraje, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idModelo);
            ps.setInt(3, idAnioModelo);
            ps.setString(4, placa);
            ps.setString(5, vin);
            ps.setString(6, color);
            ps.setInt(7, kilometraje);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error insertar(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza color y kilometraje del vehículo
     * (podrías permitir actualizar placa, pero muchos talleres no lo hacen)
     */
    public boolean actualizar(
            int idVehiculo,
            String color,
            int kilometraje
    ) {
        String sql = "UPDATE vehiculos SET color = ?, kilometraje = ? WHERE id_vehiculo = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, color);
            ps.setInt(2, kilometraje);
            ps.setInt(3, idVehiculo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizar(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Baja lógica
     */
    public boolean desactivar(int idVehiculo) {
        String sql = "UPDATE vehiculos SET activo = 0 WHERE id_vehiculo = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error desactivar(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Reactivar
     */
    public boolean reactivar(int idVehiculo) {
        String sql = "UPDATE vehiculos SET activo = 1 WHERE id_vehiculo = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error reactivar(): " + e.getMessage());
            return false;
        }
    }
    public List<Vehiculo> buscarPorPlaca(String placaParcial) {
        List<Vehiculo> lista = new ArrayList<>();

        String sql = "SELECT id_vehiculo, id_cliente, id_modelo, id_anio_modelo, " +
                     "placa, vin, color, kilometraje, activo " +
                     "FROM vehiculos " +
                     "WHERE placa LIKE ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + placaParcial + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehiculo v = new Vehiculo();
                    v.setIdVehiculo(rs.getInt("id_vehiculo"));

                    // cliente
                    Cliente cli = new Cliente();
                    cli.setIdCliente(rs.getInt("id_cliente"));
                    v.setCliente(cli);

                    // modelo
                    ModeloAuto mo = new ModeloAuto();
                    mo.setIdModelo(rs.getInt("id_modelo"));
                    v.setModelo(mo);

                    // año
                    AnioModelo an = new AnioModelo();
                    an.setIdAnioModelo(rs.getInt("id_anio_modelo"));
                    v.setAnioModelo(an);

                    v.setPlaca(rs.getString("placa"));
                    v.setVin(rs.getString("vin"));
                    v.setColor(rs.getString("color"));
                    v.setKilometraje((Integer) rs.getObject("kilometraje")); // cuidado null
                    v.setActivo(rs.getBoolean("activo"));

                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscarPorPlaca(): " + e.getMessage());
        }
        return lista;
    }
    
    public void eliminarDefinitivo(int idVehiculo) {
        String sql = "DELETE FROM vehiculos WHERE id_vehiculo = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVehiculo);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error eliminarDefinitivo() Vehiculo: " + e.getMessage());
        }
    }

    public int contarVehiculosPorCliente(int idCliente) {
        String sql = "SELECT COUNT(*) AS total FROM vehiculos WHERE id_cliente = ?";
        int total = 0;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error contarVehiculosPorCliente(): " + e.getMessage());
        }

        return total;
    }
    public List<Vehiculo> listarPorCliente(int idCliente) {
        List<Vehiculo> lista = new ArrayList<>();

        String sql =
            "SELECT v.id_vehiculo, v.placa, v.vin, v.color, v.kilometraje, v.activo, " +
            "       mo.id_modelo, mo.nombre AS nombre_modelo, " +
            "       am.id_anio_modelo, am.anio AS anio_modelo " +
            "FROM vehiculos v " +
            "JOIN modelos mo          ON mo.id_modelo = v.id_modelo " +
            "JOIN anios_modelo am     ON am.id_anio_modelo = v.id_anio_modelo " +
            "WHERE v.id_cliente = ? " +
            "  AND v.activo = 1 " +
            "ORDER BY am.anio DESC, mo.nombre ASC";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehiculo v = new Vehiculo();
                    v.setIdVehiculo(rs.getInt("id_vehiculo"));
                    v.setPlaca(rs.getString("placa"));
                    v.setVin(rs.getString("vin"));
                    v.setColor(rs.getString("color"));
                    v.setKilometraje(rs.getInt("kilometraje"));
                    v.setActivo(rs.getInt("activo") == 1);

                    // modelo
                    ModeloAuto m = new ModeloAuto();
                    m.setIdModelo(rs.getInt("id_modelo"));
                    m.setNombre(rs.getString("nombre_modelo"));
                    v.setModelo(m);

                    // año
                    AnioModelo a = new AnioModelo();
                    a.setIdAnioModelo(rs.getInt("id_anio_modelo"));
                    a.setAnio(rs.getInt("anio_modelo"));
                    v.setAnioModelo(a);

                    // puedes opcionalmente asociar el cliente ya que lo conoces
                    Cliente c = new Cliente();
                    c.setIdCliente(idCliente);
                    v.setCliente(c);

                    lista.add(v);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

}

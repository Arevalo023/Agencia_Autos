package model.dao;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;


import config.ConexionDB;
import model.Cliente;
import model.EstatusOrden;
import model.OrdenRefaccion;
import model.OrdenServicio;
import model.Refaccion;
import model.Tecnico;
import model.TipoServicio;
import model.Usuario;
import model.Vehiculo;

public class OrdenServicioDAO {

	/**
	 * Convierte una fila del ResultSet (JOIN grande) en un objeto OrdenServicio.
	 * NO carga refacciones todav�a � eso va aparte con cargarRefaccionesDeOrden().
	 */
	private OrdenServicio mapearOrdenConJoins(ResultSet rs) throws SQLException {

		// --- Primero leemos TODO del ResultSet a variables locales simples ---

		int idOrden              = rs.getInt("id_orden");
		String folio             = rs.getString("folio");

		int idCliente            = rs.getInt("id_cliente");
		String cliNombre         = rs.getString("cli_nombre");
		String cliApellidos      = rs.getString("cli_apellidos");
		String cliTel            = rs.getString("cli_tel");
		String cliEmail          = rs.getString("cli_email");

		int idVehiculo           = rs.getInt("id_vehiculo");
		String placa             = rs.getString("placa");
		String vin               = rs.getString("vin");
		String color             = rs.getString("color");
		int km                   = rs.getInt("kilometraje");

		int idTecnico            = rs.getInt("id_tecnico");
		String tecNoEmpleado     = rs.getString("no_empleado");
		String tecNombre         = rs.getString("tec_nombre");
		String tecTel            = rs.getString("tec_tel");
		String tecEmail          = rs.getString("tec_email");

		int idTipoServ           = rs.getInt("id_tipo_servicio");
		String tipoNombre        = rs.getString("tipo_nombre");
		String tipoDesc          = rs.getString("tipo_desc");

		String estatusStr        = rs.getString("estatus");

		double manoObra          = rs.getDouble("mano_obra");
		double totalRef          = rs.getDouble("total_refacciones");

		java.sql.Date proxServDate = rs.getDate("proximo_servicio");
		String notas             = rs.getString("notas");

		int createdById          = rs.getInt("created_by");
		boolean createdByNull    = rs.wasNull(); // OJO: guardamos wasNull inmediatamente despu�s del getInt

		int updatedById          = rs.getInt("updated_by");
		boolean updatedByNull    = rs.wasNull(); // igual aqu�, guardamos ya

		java.sql.Timestamp tsCreacion = rs.getTimestamp("fecha_creacion");
		java.sql.Timestamp tsCierre   = rs.getTimestamp("fecha_cierre");

		// --- Ahora construimos el objeto con esas variables ---

		OrdenServicio o = new OrdenServicio();
		o.setIdOrden(idOrden);
		o.setFolio(folio);

		// Cliente
		Cliente cli = new Cliente();
		cli.setIdCliente(idCliente);
		cli.setNombre(cliNombre);
		cli.setApellidos(cliApellidos);
		cli.setTelefono(cliTel);
		cli.setEmail(cliEmail);
		o.setCliente(cli);

		// Veh�culo
		Vehiculo v = new Vehiculo();
		v.setIdVehiculo(idVehiculo);
		v.setPlaca(placa);
		v.setVin(vin);
		v.setColor(color);
		v.setKilometraje(km);
		o.setVehiculo(v);

		// T�cnico
		Tecnico t = new Tecnico();
		t.setIdTecnico(idTecnico);
		t.setNoEmpleado(tecNoEmpleado);
		t.setNombre(tecNombre);
		t.setTelefono(tecTel);
		t.setEmail(tecEmail);
		o.setTecnico(t);

		// Tipo de servicio
		TipoServicio ts = new TipoServicio();
		ts.setIdTipoServicio(idTipoServ);
		ts.setNombre(tipoNombre);
		ts.setDescripcion(tipoDesc);
		o.setTipoServicio(ts);

		// Estatus (enum)
		o.setEstatus(EstatusOrden.fromString(estatusStr));

		// Costos
		o.setManoObra(manoObra);
		o.setTotalRefacciones(totalRef);

		// Pr�ximo servicio
		if (proxServDate != null) {
			o.setProximoServicio(proxServDate.toLocalDate());
		} else {
			o.setProximoServicio(null);
		}

		// Notas
		o.setNotas(notas);

		// Creado por
		if (!createdByNull) {
			Usuario uC = new Usuario();
			uC.setIdUsuario(createdById);
			o.setCreadoPor(uC);
		} else {
			o.setCreadoPor(null);
		}

		// �ltima actualizaci�n
		if (!updatedByNull) {
			Usuario uU = new Usuario();
			uU.setIdUsuario(updatedById);
			o.setActualizadoPor(uU);
		} else {
			o.setActualizadoPor(null);
		}

		// Fechas
		if (tsCreacion != null) {
			o.setFechaCreacion(tsCreacion.toLocalDateTime());
		} else {
			o.setFechaCreacion(null);
		}

		if (tsCierre != null) {
			o.setFechaCierre(tsCierre.toLocalDateTime());
		} else {
			o.setFechaCierre(null);
		}

		return o;
	}

	/**
	 * Trae TODAS las refacciones asociadas a una orden.
	 */
	private List<OrdenRefaccion> cargarRefaccionesDeOrden(int idOrden) {

	    List<OrdenRefaccion> detalles = new ArrayList<>();

	    String sql =
	        "SELECT r.id_refaccion, r.clave, r.descripcion, " +
	        "       orf.cantidad, orf.precio_unitario, " +
	        "       (orf.cantidad * orf.precio_unitario) AS subtotal " +
	        "FROM orden_refaccion orf " +
	        "JOIN refacciones r ON r.id_refaccion = orf.id_refaccion " +
	        "WHERE orf.id_orden = ?";

	    try (Connection conn = ConexionDB.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, idOrden);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                OrdenRefaccion det = new OrdenRefaccion();

	                Refaccion ref = new Refaccion();
	                ref.setIdRefaccion(rs.getInt("id_refaccion"));
	                ref.setClave(rs.getString("clave"));
	                ref.setDescripcion(rs.getString("descripcion"));

	                det.setRefaccion(ref);
	                det.setCantidad(rs.getInt("cantidad"));
	                det.setPrecioUnitario(rs.getDouble("precio_unitario"));
	                det.setSubtotal(rs.getDouble("subtotal"));

	                detalles.add(det);
	            }
	        }

	    } catch (SQLException e) {
	        System.err.println("Error cargarRefaccionesDeOrden(): " + e.getMessage());
	    }

	    return detalles;
	}

	/**
	 * Busca 1 orden por folio EXACTO.
	 * Incluye: cliente, vehiculo, tecnico, tipo_servicio.
	 * Despu�s le mete las refacciones.
	 */


	/**
	 * B�squeda por nombre/apellido del due�o (LIKE).
	 * Esto es para el requisito:
	 * "buscar por folio y/o nombre del due�o".
	 *
	 * Puede regresar varias �rdenes.
	 */
	public List<OrdenServicio> buscarPorNombreCliente(String nombreLike) {

	    List<OrdenServicio> lista = new ArrayList<>();

	    String sql =
	        "SELECT o.id_orden, o.folio, " +
	        "       o.id_cliente, c.nombre AS cli_nombre, c.apellidos AS cli_apellidos, c.telefono AS cli_tel, c.email AS cli_email, " +
	        "       o.id_vehiculo, v.placa, v.vin, v.color, v.kilometraje, " +
	        "       o.id_tecnico, t.no_empleado, t.nombre AS tec_nombre, t.telefono AS tec_tel, t.email AS tec_email, " +
	        "       o.id_tipo_servicio, ts.nombre AS tipo_nombre, ts.descripcion AS tipo_desc, " +
	        "       o.estatus, o.mano_obra, o.total_refacciones, o.proximo_servicio, o.notas, " +
	        "       o.created_by, o.updated_by, o.fecha_creacion, o.fecha_cierre " +
	        "FROM ordenes o " +
	        "JOIN clientes c   ON c.id_cliente = o.id_cliente " +
	        "JOIN vehiculos v  ON v.id_vehiculo = o.id_vehiculo " +
	        "JOIN tecnicos t   ON t.id_tecnico = o.id_tecnico " +
	        "JOIN tipos_servicio ts ON ts.id_tipo_servicio = o.id_tipo_servicio " +
	        "WHERE CONCAT(c.nombre, ' ', c.apellidos) LIKE ?";

	    // paso 1: leer todas las �rdenes b�sicas (sin refacciones todav�a)
	    List<OrdenServicio> tmp = new ArrayList<>();

	    try (Connection conn = ConexionDB.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, "%" + nombreLike + "%");

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                OrdenServicio orden = mapearOrdenBasico(rs);
	                tmp.add(orden);
	            }
	        }

	    } catch (SQLException e) {
	        System.err.println("Error buscarPorNombreCliente(): " + e.getMessage());
	    }

	    // paso 2: por cada orden ya cargada del ResultSet (que ya est� cerrado),
	    // ahora s� traemos refacciones
	    for (OrdenServicio o : tmp) {
	        o.setRefacciones(cargarRefaccionesDeOrden(o.getIdOrden()));
	        lista.add(o);
	    }

	    return lista;
	}


	/**
	 * Actualiza el estatus de una orden
	 * y registra la bit�cora del cambio.
	 *
	 * MUY IMPORTANTE para punto 7 y punto 11.
	 */
	public boolean actualizarEstatusConBitacora(int idOrden,
			EstatusOrden estatusNuevo,
			int idUsuarioAccion) {

		String sqlUpdate;

		if (estatusNuevo == EstatusOrden.FINALIZADO) {
			sqlUpdate =
					"UPDATE ordenes " +
							"SET estatus = ?, " +
							"    updated_by = ?, " +
							"    fecha_cierre = CURRENT_TIMESTAMP " +
							"WHERE id_orden = ?";
		} else {
			sqlUpdate =
					"UPDATE ordenes " +
							"SET estatus = ?, " +
							"    updated_by = ? " +
							"WHERE id_orden = ?";
		}

		try (Connection conn = ConexionDB.getConnection();
				PreparedStatement ps1 = conn.prepareStatement(sqlUpdate)) {

			ps1.setString(1, estatusNuevo.toDbValue());
			ps1.setInt(2, idUsuarioAccion);
			ps1.setInt(3, idOrden);

			int rows = ps1.executeUpdate();
			return rows > 0;

		} catch (SQLException e) {
			System.err.println("Error actualizarEstatusConBitacora(): " + e.getMessage());
			return false;
		}
	}



	/**
	 * Actualiza el campo proximo_servicio de una orden.
	 * Punto 9 del enunciado.
	 */
	public boolean actualizarProximoServicio(int idOrden, LocalDate fecha) {
		String sql = "UPDATE ordenes SET proximo_servicio = ? WHERE id_orden = ?";
		try (Connection conn = ConexionDB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			if (fecha != null) {
				ps.setDate(1, Date.valueOf(fecha));
			} else {
				ps.setNull(1, Types.DATE);
			}

			ps.setInt(2, idOrden);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error actualizarProximoServicio(): " + e.getMessage());
			return false;
		}
	}

	/**
	 * Agregar una refacci�n usada en una orden espec�fica.
	 * Esto tambi�n afecta el total_refacciones de la orden.
	 *
	 * Nota: recalculamos total_refacciones despu�s de insertar.
	 */
	public boolean agregarRefaccionAOrden(int idOrden, int idRefaccion, int cantidad, double precioUnitario) {

		String sqlInsertDet =
				"INSERT INTO orden_refaccion (id_orden, id_refaccion, cantidad, precio_unitario) " +
						"VALUES (?, ?, ?, ?)";

		String sqlUpdateTotal =
				"UPDATE ordenes o " +
						"SET total_refacciones = (" +
						"   SELECT COALESCE(SUM(cantidad * precio_unitario),0) " +
						"   FROM orden_refaccion r WHERE r.id_orden = o.id_orden" +
						") " +
						"WHERE o.id_orden = ?";

		Connection conn = null;
		try {
			conn = ConexionDB.getConnection();
			conn.setAutoCommit(false);

			// 1) insertar la linea
			try (PreparedStatement ps1 = conn.prepareStatement(sqlInsertDet)) {
				ps1.setInt(1, idOrden);
				ps1.setInt(2, idRefaccion);
				ps1.setInt(3, cantidad);
				ps1.setDouble(4, precioUnitario);
				ps1.executeUpdate();
			}

			// 2) recalcular total_refacciones
			try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateTotal)) {
				ps2.setInt(1, idOrden);
				ps2.executeUpdate();
			}

			conn.commit();
			return true;

		} catch (SQLException e) {
			System.err.println("Error agregarRefaccionAOrden(): " + e.getMessage());
			if (conn != null) {
				try { conn.rollback(); } catch (SQLException ex) {}
			}
			return false;
		} finally {
			if (conn != null) {
				try { conn.setAutoCommit(true); } catch (SQLException ex) {}
			}
		}
	}
	
	private OrdenServicio mapearOrdenBasico(ResultSet rs) throws SQLException {

	    // leo TODO primero a variables locales
	    int idOrden              = rs.getInt("id_orden");
	    String folio             = rs.getString("folio");

	    int idCliente            = rs.getInt("id_cliente");
	    String cliNombre         = rs.getString("cli_nombre");
	    String cliApellidos      = rs.getString("cli_apellidos");
	    String cliTel            = rs.getString("cli_tel");
	    String cliEmail          = rs.getString("cli_email");

	    int idVehiculo           = rs.getInt("id_vehiculo");
	    String placa             = rs.getString("placa");
	    String vin               = rs.getString("vin");
	    String color             = rs.getString("color");
	    int km                   = rs.getInt("kilometraje");

	    int idTecnico            = rs.getInt("id_tecnico");
	    String tecNoEmpleado     = rs.getString("no_empleado");
	    String tecNombre         = rs.getString("tec_nombre");
	    String tecTel            = rs.getString("tec_tel");
	    String tecEmail          = rs.getString("tec_email");

	    int idTipoServ           = rs.getInt("id_tipo_servicio");
	    String tipoNombre        = rs.getString("tipo_nombre");
	    String tipoDesc          = rs.getString("tipo_desc");

	    String estatusStr        = rs.getString("estatus");

	    double manoObra          = rs.getDouble("mano_obra");
	    double totalRef          = rs.getDouble("total_refacciones");

	    java.sql.Date proxServDate = rs.getDate("proximo_servicio");
	    String notas             = rs.getString("notas");

	    int createdById          = rs.getInt("created_by");
	    boolean createdByNull    = rs.wasNull();

	    int updatedById          = rs.getInt("updated_by");
	    boolean updatedByNull    = rs.wasNull();

	    java.sql.Timestamp tsCreacion = rs.getTimestamp("fecha_creacion");
	    java.sql.Timestamp tsCierre   = rs.getTimestamp("fecha_cierre");

	    // construir objeto
	    OrdenServicio o = new OrdenServicio();
	    o.setIdOrden(idOrden);
	    o.setFolio(folio);

	    // Cliente
	    Cliente cli = new Cliente();
	    cli.setIdCliente(idCliente);
	    cli.setNombre(cliNombre);
	    cli.setApellidos(cliApellidos);
	    cli.setTelefono(cliTel);
	    cli.setEmail(cliEmail);
	    o.setCliente(cli);

	    // Veh�culo
	    Vehiculo v = new Vehiculo();
	    v.setIdVehiculo(idVehiculo);
	    v.setPlaca(placa);
	    v.setVin(vin);
	    v.setColor(color);
	    v.setKilometraje(km);
	    o.setVehiculo(v);

	    // T�cnico
	    Tecnico t = new Tecnico();
	    t.setIdTecnico(idTecnico);
	    t.setNoEmpleado(tecNoEmpleado);
	    t.setNombre(tecNombre);
	    t.setTelefono(tecTel);
	    t.setEmail(tecEmail);
	    o.setTecnico(t);

	    // Tipo de servicio
	    TipoServicio ts = new TipoServicio();
	    ts.setIdTipoServicio(idTipoServ);
	    ts.setNombre(tipoNombre);
	    ts.setDescripcion(tipoDesc);
	    o.setTipoServicio(ts);

	    // Estatus
	    o.setEstatus(EstatusOrden.fromString(estatusStr));

	    // Costos
	    o.setManoObra(manoObra);
	    o.setTotalRefacciones(totalRef);

	    // Pr�ximo servicio
	    if (proxServDate != null) {
	        o.setProximoServicio(proxServDate.toLocalDate());
	    } else {
	        o.setProximoServicio(null);
	    }

	    // Notas
	    o.setNotas(notas);

	    // Creado por
	    if (!createdByNull) {
	        Usuario uC = new Usuario();
	        uC.setIdUsuario(createdById);
	        o.setCreadoPor(uC);
	    } else {
	        o.setCreadoPor(null);
	    }

	    // �ltima actualizaci�n
	    if (!updatedByNull) {
	        Usuario uU = new Usuario();
	        uU.setIdUsuario(updatedById);
	        o.setActualizadoPor(uU);
	    } else {
	        o.setActualizadoPor(null);
	    }

	    // Fechas
	    if (tsCreacion != null) {
	        o.setFechaCreacion(tsCreacion.toLocalDateTime());
	    } else {
	        o.setFechaCreacion(null);
	    }

	    if (tsCierre != null) {
	        o.setFechaCierre(tsCierre.toLocalDateTime());
	    } else {
	        o.setFechaCierre(null);
	    }

	    // NOTA: aqu� NO cargamos las refacciones todav�a
	    o.setRefacciones(null);

	    return o;
	}
	
	 public OrdenServicio insertarNuevaOrden(
	            int idCliente,
	            int idVehiculo,
	            int idTecnico,
	            int idTipoServicio,
	            double manoObra,
	            String notas,
	            LocalDate proximoServicio,
	            int createdByUserId
	    ) {

	        Connection cn = null;
	        PreparedStatement psInsert = null;
	        PreparedStatement psUpdateFolio = null;
	        ResultSet rsKeys = null;

	        try {
	            cn = ConexionDB.getConnection();
	            cn.setAutoCommit(false); // vamos a transaccionar

	            // 1. Insert preliminar SIN folio definitivo.
	            //    Ponemos folio = '' temporalmente.
	            String sqlInsert =
	                "INSERT INTO ordenes " +
	                "(folio, id_cliente, id_vehiculo, id_tecnico, id_tipo_servicio, estatus, " +
	                " fecha_creacion, fecha_cierre, proximo_servicio, mano_obra, total_refacciones, notas, created_by, updated_by) " +
	                "VALUES (?, ?, ?, ?, ?, 'EN_ESPERA', NOW(), NULL, ?, ?, 0, ?, ?, NULL)";

	            psInsert = cn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
	            psInsert.setString(1, ""); // folio temporal vac�o
	            psInsert.setInt(2, idCliente);
	            psInsert.setInt(3, idVehiculo);
	            psInsert.setInt(4, idTecnico);
	            psInsert.setInt(5, idTipoServicio);

	            // proximo_servicio puede ser null
	            if (proximoServicio != null) {
	                psInsert.setDate(6, Date.valueOf(proximoServicio));
	            } else {
	                psInsert.setNull(6, Types.DATE);
	            }

	            psInsert.setDouble(7, manoObra);
	            psInsert.setString(8, (notas != null ? notas : ""));
	            psInsert.setInt(9, createdByUserId);

	            int filas = psInsert.executeUpdate();
	            if (filas == 0) {
	                cn.rollback();
	                return null;
	            }

	            // 2. obtener el id_orden autogenerado
	            rsKeys = psInsert.getGeneratedKeys();
	            int newIdOrden = -1;
	            if (rsKeys.next()) {
	                newIdOrden = rsKeys.getInt(1);
	            }
	            if (newIdOrden <= 0) {
	                cn.rollback();
	                return null;
	            }

	            // 3. generar folio a partir del id. Ejemplo: A + 3 d�gitos con ceros
	            //    id = 7  -> A007
	            //    id = 123 -> A123
	            String folioGenerado = generarFolio(newIdOrden);

	            // 4. actualizar el folio real
	            String sqlUpdFolio = "UPDATE ordenes SET folio = ? WHERE id_orden = ?";
	            psUpdateFolio = cn.prepareStatement(sqlUpdFolio);
	            psUpdateFolio.setString(1, folioGenerado);
	            psUpdateFolio.setInt(2, newIdOrden);
	            psUpdateFolio.executeUpdate();

	            // 5. commit
	            cn.commit();

	            // 6. regresar la orden ya completa usando tu m�todo existente buscarPorFolio
	            return buscarPorFolio(folioGenerado);

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            // intento rollback si algo fall�
	            if (cn != null) {
	                try { cn.rollback(); } catch (SQLException ex2) { ex2.printStackTrace(); }
	            }
	            return null;
	        } finally {
	            // cleanup
	            try { if (rsKeys != null) rsKeys.close(); } catch (SQLException ignored) {}
	            try { if (psInsert != null) psInsert.close(); } catch (SQLException ignored) {}
	            try { if (psUpdateFolio != null) psUpdateFolio.close(); } catch (SQLException ignored) {}
	            try {
	                if (cn != null) {
	                    cn.setAutoCommit(true);
	                    cn.close();
	                }
	            } catch (SQLException ignored) {}
	        }
	    }

	 /**
	     * helper para armar un folio tipo A001, A045, A1234 etc
	     */
	    private String generarFolio(int idOrden) {
	        // rellenar a 3 d�gitos m�nimo
	        // 1   -> A001
	        // 12  -> A012
	        // 123 -> A123
	        if (idOrden < 10) {
	            return "A00" + idOrden;
	        } else if (idOrden < 100) {
	            return "A0" + idOrden;
	        } else {
	            return "A" + idOrden;
	        }
	    }

	    // ---------------------------------------------------------
	    // ejemplo de c�mo deber�a lucir buscarPorFolio(...) m�nimo
	    // (ya dijiste que lo tienes; lo pongo pa' que veas la idea)
	    // ---------------------------------------------------------
	    public OrdenServicio buscarPorFolio(String folio) {
	        String sql =
	            "SELECT o.id_orden, o.folio, o.estatus, o.fecha_creacion, o.fecha_cierre, " +
	            "       o.mano_obra, o.total_refacciones, o.proximo_servicio, o.notas, " +
	            "       c.id_cliente, c.nombre, c.apellidos, " +
	            "       v.id_vehiculo, v.placa, v.color, v.kilometraje, " +
	            "       t.id_tecnico, t.nombre AS nom_tecnico, t.no_empleado, " +
	            "       ts.id_tipo_servicio, ts.nombre AS nom_tipo " +
	            "FROM ordenes o " +
	            "JOIN clientes c        ON c.id_cliente = o.id_cliente " +
	            "JOIN vehiculos v       ON v.id_vehiculo = o.id_vehiculo " +
	            "JOIN tecnicos t        ON t.id_tecnico = o.id_tecnico " +
	            "JOIN tipos_servicio ts ON ts.id_tipo_servicio = o.id_tipo_servicio " +
	            "WHERE o.folio = ?";

	        try (Connection cn = ConexionDB.getConnection();
	             PreparedStatement ps = cn.prepareStatement(sql)) {

	            ps.setString(1, folio);

	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    OrdenServicio o = new OrdenServicio();
	                    o.setIdOrden(rs.getInt("id_orden"));
	                    o.setFolio(rs.getString("folio"));
	                    o.setEstatus(EstatusOrden.valueOf(rs.getString("estatus")));
	                    o.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
	                    Timestamp fc = rs.getTimestamp("fecha_cierre");
	                    if (fc != null) {
	                        o.setFechaCierre(fc.toLocalDateTime());
	                    }
	                    o.setManoObra(rs.getDouble("mano_obra"));
	                    o.setTotalRefacciones(rs.getDouble("total_refacciones"));

	                    Date px = rs.getDate("proximo_servicio");
	                    if (px != null) {
	                        o.setProximoServicio(px.toLocalDate());
	                    }

	                    o.setNotas(rs.getString("notas"));

	                    // cliente
	                    Cliente c = new Cliente();
	                    c.setIdCliente(rs.getInt("id_cliente"));
	                    c.setNombre(rs.getString("nombre"));
	                    c.setApellidos(rs.getString("apellidos"));
	                    o.setCliente(c);

	                    // vehiculo
	                    Vehiculo v = new Vehiculo();
	                    v.setIdVehiculo(rs.getInt("id_vehiculo"));
	                    v.setPlaca(rs.getString("placa"));
	                    v.setColor(rs.getString("color"));
	                    v.setKilometraje(rs.getInt("kilometraje"));
	                    o.setVehiculo(v);

	                    // tecnico
	                    Tecnico tec = new Tecnico();
	                    tec.setIdTecnico(rs.getInt("id_tecnico"));
	                    tec.setNombre(rs.getString("nom_tecnico"));
	                    tec.setNoEmpleado(rs.getString("no_empleado"));
	                    o.setTecnico(tec);

	                    // tipo servicio
	                    TipoServicio ts = new TipoServicio();
	                    ts.setIdTipoServicio(rs.getInt("id_tipo_servicio"));
	                    ts.setNombre(rs.getString("nom_tipo"));
	                    o.setTipoServicio(ts);

	                    // cargar refacciones detalle tambi�n si quieres
	                    o.setRefacciones(listarRefaccionesDeOrden(o.getIdOrden(), cn));

	                    return o;
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }

	        return null;
	    }
	    
	    /**
	     * Trae los renglones de orden_refaccion para una orden,
	     * usando la conexi�n abierta (para no abrir otra).
	     */
	    private java.util.List<OrdenRefaccion> listarRefaccionesDeOrden(int idOrden, Connection cn) {
	        java.util.List<OrdenRefaccion> dets = new java.util.ArrayList<>();

	        String sql =
	            "SELECT orf.id_refaccion, orf.cantidad, orf.precio_unitario, " +
	            "       r.clave, r.descripcion " +
	            "FROM orden_refaccion orf " +
	            "JOIN refacciones r ON r.id_refaccion = orf.id_refaccion " +
	            "WHERE orf.id_orden = ?";

	        try (PreparedStatement ps = cn.prepareStatement(sql)) {
	            ps.setInt(1, idOrden);
	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    OrdenRefaccion det = new OrdenRefaccion();
	                    det.setCantidad(rs.getInt("cantidad"));
	                    det.setPrecioUnitario(rs.getDouble("precio_unitario"));

	                    Refaccion ref = new Refaccion();
	                    ref.setIdRefaccion(rs.getInt("id_refaccion"));
	                    ref.setClave(rs.getString("clave"));
	                    ref.setDescripcion(rs.getString("descripcion"));
	                    det.setRefaccion(ref);

	                    // subtotal es (cantidad * precio_unitario), lo puedes calcular al vuelo
	                    det.setSubtotal(det.getCantidad() * det.getPrecioUnitario());

	                    dets.add(det);
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return dets;
	    }
	    
	    public boolean eliminarOrden(int idOrden) {
	        String sql = "DELETE FROM ordenes WHERE id_orden = ?";

	        try (Connection conn = ConexionDB.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, idOrden);

	            int filas = ps.executeUpdate();
	            return filas > 0;

	        } catch (SQLException e) {
	            System.err.println("Error eliminarOrden(): " + e.getMessage());
	            return false;
	        }
	    }

	
	 // En OrdenServicioDAO

	    /**
	     * Elimina UNA refacci�n espec�fica de una orden.
	     * Borra el rengl�n de la tabla detalle usando (id_orden, id_refaccion),
	     * luego recalcula el total_refacciones y lo actualiza en la orden.
	     *
	     * @param idOrden       ID de la orden (orden_servicio.id_orden)
	     * @param idRefaccion   ID de la refacci�n a quitar
	     * @return true si todo sali� bien
	     */
	    public boolean eliminarRefaccionDeOrden(int idOrden, int idRefaccion) {
	        String sqlDeleteDet = "DELETE FROM orden_refaccion WHERE id_orden = ? AND id_refaccion = ?";
	        String sqlUpdateTot = "UPDATE ordenes " +
	                "SET total_refacciones = COALESCE(( " +
	                "    SELECT SUM(subtotal) FROM orden_refaccion WHERE id_orden = ? " +
	                "), 0) " +
	                "WHERE id_orden = ?";

	        try (Connection conn = ConexionDB.getConnection()) {
	            conn.setAutoCommit(false);

	            // 1. borrar la l�nea del detalle
	            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteDet)) {
	                ps.setInt(1, idOrden);
	                ps.setInt(2, idRefaccion);
	                ps.executeUpdate();
	            }

	            // 2. recalcular el total_refacciones de la orden
	            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateTot)) {
	                ps2.setInt(1, idOrden);
	                ps2.setInt(2, idOrden);
	                ps2.executeUpdate();
	            }

	            conn.commit();
	            return true;

	        } catch (SQLException e) {
	            System.err.println("Error eliminarRefaccionDeOrden(): " + e.getMessage());
	            return false;
	        }
	    }
	    
	    public boolean actualizarOrdenBasica(
	            int idOrden,
	            int idTecnico,
	            int idTipoServicio,
	            double manoObra,
	            LocalDate proximoServicio,
	            String notas
	    ) {
	        String sql = "UPDATE ordenes " +
	                     "SET id_tecnico = ?, " +
	                     "    id_tipo_servicio = ?, " +
	                     "    mano_obra = ?, " +
	                     "    proximo_servicio = ?, " +
	                     "    notas = ? " +
	                     "WHERE id_orden = ?";

	        try (Connection conn = ConexionDB.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, idTecnico);
	            ps.setInt(2, idTipoServicio);
	            ps.setDouble(3, manoObra);

	            if (proximoServicio != null) {
	                ps.setDate(4, java.sql.Date.valueOf(proximoServicio));
	            } else {
	                ps.setNull(4, java.sql.Types.DATE);
	            }

	            ps.setString(5, notas);
	            ps.setInt(6, idOrden);

	            return ps.executeUpdate() > 0;

	        } catch (SQLException e) {
	            System.err.println("Error actualizarOrdenBasica(): " + e.getMessage());
	            return false;
	        }
	    }

	   




}

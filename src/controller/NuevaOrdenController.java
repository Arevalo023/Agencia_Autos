package controller;

import model.*;
import model.dao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NuevaOrdenController {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final TecnicoDAO tecnicoDAO = new TecnicoDAO();
    private final TipoServicioDAO tipoServicioDAO = new TipoServicioDAO();
    private final OrdenServicioDAO ordenServicioDAO = new OrdenServicioDAO();

    // --- Sesión / permisos ---
    public Usuario getUsuarioActual() {
        return AuthController.getUsuarioActual();
    }

    public boolean esAdmin() {
        Usuario u = getUsuarioActual();
        return u != null && "ADMIN".equalsIgnoreCase(u.getRol());
    }

    // --- Clientes / Vehículos ---
    public List<Cliente> getClientesActivos() {
        List<Cliente> activos = new ArrayList<>();
        for (Cliente c : clienteDAO.listar()) {
            if (c.isActivo()) {
                activos.add(c);
            }
        }
        return activos;
    }

    public List<Vehiculo> getVehiculosActivosPorCliente(int idCliente) {
        List<Vehiculo> activos = new ArrayList<>();
        for (Vehiculo v : vehiculoDAO.listarPorCliente(idCliente)) {
            if (v.isActivo()) {
                activos.add(v);
            }
        }
        return activos;
    }

    // --- Técnicos / Tipos servicio ---
    public List<Tecnico> getTecnicosActivos() {
        List<Tecnico> activos = new ArrayList<>();
        for (Tecnico t : tecnicoDAO.listar()) {
            if (t.isActivo()) {
                activos.add(t);
            }
        }
        return activos;
    }

    public List<TipoServicio> getTiposServicioActivos() {
        List<TipoServicio> activos = new ArrayList<>();
        for (TipoServicio ts : tipoServicioDAO.listar()) {
            if (ts.isActivo()) {
                activos.add(ts);
            }
        }
        return activos;
    }

    // --- Guardar orden ---
    public OrdenServicio crearNuevaOrden(
            Cliente cli,
            Vehiculo veh,
            Tecnico tec,
            TipoServicio tipo,
            double manoObra,
            String notas,
            LocalDate proximoServicio
    ) {
        Usuario actual = getUsuarioActual();
        if (actual == null) {
            return null; // la vista mostrará el error
        }

        return ordenServicioDAO.insertarNuevaOrden(
                cli.getIdCliente(),
                veh.getIdVehiculo(),
                tec.getIdTecnico(),
                tipo.getIdTipoServicio(),
                manoObra,
                notas,
                proximoServicio,
                actual.getIdUsuario()
        );
    }

    // --- Ticket (texto resumen) ---
    public String buildTicketText(OrdenServicio o) {
        String clienteTxt = o.getCliente().getNombre() + " " + o.getCliente().getApellidos();
        String placaTxt   = (o.getVehiculo() != null ? o.getVehiculo().getPlaca() : "");
        String tecnicoTxt = (o.getTecnico() != null ? o.getTecnico().getNombre() : "");
        String tipoTxt    = (o.getTipoServicio() != null ? o.getTipoServicio().getNombre() : "");

        double manoObra   = o.getManoObra();
        double refTotal   = o.getTotalRefacciones(); // al crear casi siempre 0
        double total      = manoObra + refTotal;

        return
                "         TALLER AUTOMOTRIZ\n" +
                "      ORDEN DE SERVICIO INICIAL\n" +
                "--------------------------------------\n" +
                "Folio: " + o.getFolio() + "\n" +
                "Fecha: " + LocalDate.now() + "\n" +
                "\n" +
                "Cliente: " + clienteTxt + "\n" +
                "Vehículo (placa): " + placaTxt + "\n" +
                "Técnico asignado: " + tecnicoTxt + "\n" +
                "Tipo de servicio: " + tipoTxt + "\n" +
                "\n" +
                "---- COSTOS INICIALES ----\n" +
                String.format("Mano de obra:     $%.2f\n", manoObra) +
                String.format("Refacciones:      $%.2f\n", refTotal) +
                String.format("TOTAL ESTIMADO:   $%.2f\n", total) +
                "\n" +
                "Próximo servicio: " +
                (o.getProximoServicio() != null ? o.getProximoServicio() : "N/A") + "\n" +
                "\n" +
                "Notas:\n" +
                (o.getNotas() != null ? o.getNotas() : "") + "\n" +
                "--------------------------------------\n" +
                "     ¡Gracias por su preferencia!\n";
    }
}

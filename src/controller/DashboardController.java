package controller;

import java.time.LocalDate;
import model.dao.OrdenServicioDAO;

public class DashboardController {

    private final OrdenServicioDAO ordenDAO = new OrdenServicioDAO();

    public static class ConteoEstatus {
        public int espera;
        public int proceso;
        public int finalizado;
    }

    // total = true - histórico completo
    // total = false - solo HOY
    public ConteoEstatus getConteos(boolean total) {
        return ordenDAO.contarPorEstatus(total);
    }

    // NUEVO: conteos para una fecha específica
    public ConteoEstatus getConteosPorFecha(LocalDate fecha) {
        return ordenDAO.contarPorEstatusEnFecha(fecha);
    }
}

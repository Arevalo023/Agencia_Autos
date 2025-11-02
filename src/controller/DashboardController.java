package controller;

import model.dao.OrdenServicioDAO;

/**
 * DashboardController
 *
 * - Pide al DAO los conteos por estatus
 * - Los entrega listos para que la vista los pinte
 */
public class DashboardController {

    private final OrdenServicioDAO ordenDAO = new OrdenServicioDAO();

    // DTO para los conteos (lo usamos en la vista)
    public static class ConteoEstatus {
        public int espera;
        public int proceso;
        public int finalizado;
    }

    /**
     * Dame los conteos para la gráfica.
     * @param total true = historial total
     *              false = solo HOY
     */
    public ConteoEstatus getConteos(boolean total) {
        return ordenDAO.contarPorEstatus(total);
    }
}

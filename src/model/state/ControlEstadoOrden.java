package model.state;
 
import model.EstatusOrden;

import model.OrdenServicio;

import model.dao.OrdenServicioDAO;
 
/**

* ControlEstadoOrden

*

* Actúa como el "contexto" del patrón State.

* - Mantiene referencia a la OrdenServicio actual

* - Mantiene la implementación concreta de EstadoOrden

* - Expone métodos seguros para cambiar estatus

*/

public class ControlEstadoOrden {
 
    private final OrdenServicio orden;

    private final OrdenServicioDAO ordenDAO;

    private EstadoOrden estadoActual;
 
    public ControlEstadoOrden(OrdenServicio orden, OrdenServicioDAO ordenDAO) {

        this.orden = orden;

        this.ordenDAO = ordenDAO;

        this.estadoActual = crearEstadoDesde(orden.getEstatus());

    }
 
    /**

     * Crea el objeto de estado correcto según el enum actual de la orden.

     */

    private EstadoOrden crearEstadoDesde(EstatusOrden estatus) {

        if (estatus == null) return null;

        switch (estatus) {

            case EN_ESPERA:

                return new EstadoEnEspera();

            case EN_PROCESO:

                return new EstadoEnProceso();

            case FINALIZADO:

                return new EstadoFinalizado();

            default:

                return null;

        }

    }
 
    public EstadoOrden getEstadoActual() {

        return estadoActual;

    }
 
    public EstatusOrden getEstatusActual() {

        return estadoActual.getEstatus();

    }
 
    /**

     * Intenta avanzar la orden a EN_PROCESO.

     * Devuelve mensaje de resultado para la UI:

     *  - "OK" si se pudo

     *  - Mensaje de error si no se pudo

     */

    public String cambiarAEnProceso(int idUsuarioAccion, String nota) {
 
        if (!estadoActual.puedePasarAEnProceso()) {

            return estadoActual.getMensajeErrorTransicion();

        }
 
        // Ejecutar en BD + bitácora

        boolean ok = ordenDAO.actualizarEstatusConBitacora(

        	    orden.getIdOrden(),

        	    EstatusOrden.EN_PROCESO,

        	    idUsuarioAccion

        	);
 
 
        if (!ok) {

            return "Error al actualizar estatus en BD.";

        }
 
        // Actualizar en memoria

        orden.setEstatus(EstatusOrden.EN_PROCESO);

        this.estadoActual = new EstadoEnProceso();
 
        return "OK";

    }
 
    /**

     * Intenta avanzar la orden a FINALIZADO.

     */

    public String cambiarAFinalizado(int idUsuarioAccion, String nota) {
 
        if (!estadoActual.puedePasarAFinalizado()) {

            return estadoActual.getMensajeErrorTransicion();

        }
 
        boolean ok = ordenDAO.actualizarEstatusConBitacora(

        	    orden.getIdOrden(),

        	    EstatusOrden.FINALIZADO,

        	    idUsuarioAccion

        	);
 
 
        if (!ok) {

            return "Error al actualizar estatus en BD.";

        }
 
        orden.setEstatus(EstatusOrden.FINALIZADO);

        this.estadoActual = new EstadoFinalizado();
 
        return "OK";

    }

}

 